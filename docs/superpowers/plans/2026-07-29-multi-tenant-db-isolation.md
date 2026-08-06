# 多租户数据库隔离改造 实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 将多租户架构从字段隔离改造为纯数据库隔离，每个租户拥有独立数据库。

**架构：** 主库存储平台管理数据和系统租户业务数据，新增 m_user_index 索引表用于登录路由。租户库存储各自完整业务数据（无 tenant_id 字段）。拦截器基于 token 中的 tenant_id 动态切换数据源。

**技术栈：** SpringBoot 3.1 / MyBatis-Flex 1.9.7 / Sa-Token 1.39 / Dynamic-Datasource 4.3.1 / Vue 3 / Arco Design

---

## 文件结构

### 新建文件

| 文件 | 职责 |
|------|------|
| `minimalist-basic/src/main/java/com/minimalist/basic/entity/po/MUserIndex.java` | 用户索引实体 |
| `minimalist-basic/src/main/java/com/minimalist/basic/mapper/MUserIndexMapper.java` | 用户索引 Mapper |
| `minimalist-basic/src/main/java/com/minimalist/basic/service/TenantDbInitService.java` | 租户库初始化接口 |
| `minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantDbInitServiceImpl.java` | 租户库初始化实现 |
| `minimalist-basic/src/main/resources/sql/tenant_init_template.sql` | 租户库建表模板 |

### 修改文件

| 文件 | 变更 |
|------|------|
| `minimalist-basic/.../config/tenant/TenantDatasourceInterceptor.java` | 简化路由逻辑 |
| `minimalist-basic/.../config/tenant/TenantWebMvcConfig.java` | 调整拦截器配置 |
| `minimalist-basic/.../config/mybatis/MyBatisFlexConfiguration.java` | 移除 TenantFactory |
| `minimalist-basic/.../service/impl/UserServiceImpl.java` | 新登录流程 + 索引同步 |
| `minimalist-basic/.../service/impl/TenantServiceImpl.java` | 新创建租户流程 |
| `minimalist-basic/.../service/impl/TenantPackageServiceImpl.java` | 套餐变更同步租户库 |
| `minimalist-basic/.../service/impl/DictServiceImpl.java` | 添加 @DS("master") |
| `minimalist-basic/.../service/impl/ConfigServiceImpl.java` | 添加 @DS("master") |
| `minimalist-basic/.../service/impl/NoticeServiceImpl.java` | 添加 @DS("master") |
| `minimalist-basic/.../service/impl/StorageServiceImpl.java` | 添加 @DS("master") |
| `minimalist-basic/.../manager/TenantManager.java` | 调整权限更新逻辑 |
| `minimalist-basic/.../entity/po/MUser.java` | 移除 tenantId 的 @Column(tenantId=true) |
| `minimalist-basic/.../entity/po/MRole.java` | 同上 |
| `minimalist-basic/.../entity/po/MDept.java` | 同上 |
| `minimalist-basic/.../entity/po/MPost.java` | 同上 |
| `minimalist-basic/.../entity/po/MFile.java` | 同上 |
| `minimalist-basic/.../entity/po/MTenant.java` | 移除 dataIsolation 字段 |
| `minimalist-vue3/src/pages/basic/tenant/TenantEdit.vue` | 移除隔离方式选择 |

### 删除文件

| 文件 | 原因 |
|------|------|
| `minimalist-basic/.../config/tenant/TenantIgnoreAspect.java` | 不再需要字段过滤忽略 |

> 注：`TenantIgnore.java` 保留，仅作为常量定义（TENANT_ID、CHANGE_TENANT_ID）。

---

## 任务 1：~~主库 SQL 变更~~（未创建）

> 注：`migration_v2.0.sql` 暂未创建，待正式发布时再处理。包括：新增 m_user_index 表、填充索引数据、m_tenant 表移除 data_isolation 字段。

---

## 任务 2：租户库建表模板

**文件：**
- 创建：`minimalist-basic/src/main/resources/sql/tenant_init_template.sql`

- [ ] **步骤 1：编写租户库建表模板 SQL**

从全量 SQL 中提取 12 张表的 DDL，移除 tenant_id 字段，不含 INSERT（m_func_config 除外，含预置数据）：

```sql
-- 租户库初始化建表模板
-- 创建租户时由系统自动执行

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- m_user（无 tenant_id）
DROP TABLE IF EXISTS `m_user`;
CREATE TABLE `m_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(30) NOT NULL COMMENT '用户账号',
  `password` varchar(128) NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) NOT NULL COMMENT '盐值',
  `nickname` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_real_name` varchar(20) NULL DEFAULT NULL COMMENT '用户真实姓名',
  `email` varchar(50) NULL DEFAULT NULL COMMENT '用户邮箱',
  `phone` varchar(12) NULL DEFAULT NULL COMMENT '手机号码',
  `user_sex` tinyint NULL DEFAULT 1 COMMENT '用户性别 0未知 1男 2女',
  `user_avatar` longtext NULL COMMENT '头像base64编码',
  `remark` varchar(255) NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_unq`(`username`),
  INDEX `user_id_idx`(`user_id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- m_role（无 tenant_id）
DROP TABLE IF EXISTS `m_role`;
CREATE TABLE `m_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) NOT NULL COMMENT '角色编码',
  `role_sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `remark` varchar(255) NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- m_dept（无 tenant_id）
DROP TABLE IF EXISTS `m_dept`;
CREATE TABLE `m_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dept_id` bigint NOT NULL COMMENT '部门id',
  `parent_dept_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(2048) NULL DEFAULT NULL COMMENT '祖级列表',
  `dept_name` varchar(30) NULL DEFAULT NULL COMMENT '部门名称',
  `dept_leader` bigint NULL DEFAULT NULL COMMENT '部门负责人',
  `dept_sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `phone` varchar(11) NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- m_post（无 tenant_id）
DROP TABLE IF EXISTS `m_post`;
CREATE TABLE `m_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `remark` varchar(255) NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位表';

-- m_file（无 tenant_id）
DROP TABLE IF EXISTS `m_file`;
CREATE TABLE `m_file` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `file_name` varchar(64) NOT NULL COMMENT '原文件名',
  `new_file_name` varchar(64) NOT NULL COMMENT '现文件名',
  `file_size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小',
  `file_type` varchar(20) NULL DEFAULT NULL COMMENT '文件类型',
  `file_base_path` varchar(255) NULL DEFAULT NULL COMMENT '文件基础路径',
  `file_path` varchar(255) NULL DEFAULT NULL COMMENT '文件相对路径',
  `file_url` varchar(255) NULL DEFAULT NULL COMMENT '文件url',
  `file_source` int NULL DEFAULT NULL COMMENT '文件来源',
  `file_th_url` varchar(255) NULL DEFAULT NULL COMMENT '文件缩略图url',
  `storage_id` bigint NULL DEFAULT NULL COMMENT '存储ID',
  `file_th_filename` varchar(255) NULL DEFAULT NULL COMMENT '文件缩略图文件名',
  `file_th_size` bigint NULL DEFAULT NULL COMMENT '缩略图文件大小',
  `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0未使用 1已使用',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`),
  INDEX `idx_file_id`(`file_id`),
  INDEX `idx_file_url`(`file_url`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件表';

-- m_perms（权限副本）
DROP TABLE IF EXISTS `m_perms`;
CREATE TABLE `m_perms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `perm_id` bigint NOT NULL COMMENT '权限ID',
  `perm_code` varchar(100) NULL DEFAULT NULL COMMENT '权限标识',
  `perm_name` varchar(50) NOT NULL COMMENT '权限名称',
  `parent_perm_id` bigint NULL DEFAULT 0 COMMENT '父权限ID',
  `perm_sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `perm_path` varchar(200) NULL DEFAULT NULL COMMENT '路由地址',
  `perm_icon` varchar(100) NULL DEFAULT NULL COMMENT '权限图标',
  `perm_type` char(1) NULL DEFAULT 'M' COMMENT '权限类型 M菜单 B按钮',
  `component` varchar(255) NULL DEFAULT NULL COMMENT '组件路径',
  `external_link` bit(1) NULL DEFAULT b'0' COMMENT '是否为外部链接',
  `visible` bit(1) NULL DEFAULT b'1' COMMENT '是否可见',
  `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- m_role_perm
DROP TABLE IF EXISTS `m_role_perm`;
CREATE TABLE `m_role_perm` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `perm_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  INDEX `perm_id_idx`(`perm_id`),
  INDEX `role_id_idx`(`role_id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色与权限关联表';

-- m_role_dept
DROP TABLE IF EXISTS `m_role_dept`;
CREATE TABLE `m_role_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  INDEX `dept_id_idx`(`dept_id`),
  INDEX `role_id_idx`(`role_id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色与部门关联表';

-- m_user_role
DROP TABLE IF EXISTS `m_user_role`;
CREATE TABLE `m_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  INDEX `role_id_idx`(`role_id`),
  INDEX `user_id_idx`(`user_id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与角色关联表';

-- m_user_dept
DROP TABLE IF EXISTS `m_user_dept`;
CREATE TABLE `m_user_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  INDEX `idx_dept_id`(`dept_id`),
  INDEX `idx_user_id`(`user_id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与部门关联表';

-- m_user_post
DROP TABLE IF EXISTS `m_user_post`;
CREATE TABLE `m_user_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`),
  INDEX `post_id_idx`(`post_id`),
  INDEX `user_id_idx`(`user_id`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与岗位关联表';

-- m_func_config（功能配置表）
DROP TABLE IF EXISTS `m_func_config`;
CREATE TABLE `m_func_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_id` bigint NOT NULL COMMENT '配置ID',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `config_key` varchar(100) NOT NULL COMMENT '配置键名',
  `config_value` varchar(255) NOT NULL COMMENT '配置键值',
  `description` varchar(255) NULL DEFAULT NULL COMMENT '说明',
  `create_id` bigint NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除',
  `version` int NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `config_key_unq`(`config_key`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='功能配置表';

-- 功能配置预置数据
INSERT INTO `m_func_config` (`config_id`, `config_name`, `config_key`, `config_value`, `description`) VALUES (1, '部门管理开关', 'feature.dept.enable', 'true', '是否启用部门管理功能');
INSERT INTO `m_func_config` (`config_id`, `config_name`, `config_key`, `config_value`, `description`) VALUES (2, '岗位管理开关', 'feature.post.enable', 'true', '是否启用岗位管理功能');

SET FOREIGN_KEY_CHECKS = 1;
```

- [ ] **步骤 2：Commit**

```bash
git add minimalist-basic/src/main/resources/sql/tenant_init_template.sql
git commit -m "feat: 新增租户库建表模板SQL（12张表，无tenant_id，含m_func_config预置数据）"
```

---

## 任务 3：新增 MUserIndex 实体和 Mapper

**文件：**
- 创建：`minimalist-basic/src/main/java/com/minimalist/basic/entity/po/MUserIndex.java`
- 创建：`minimalist-basic/src/main/java/com/minimalist/basic/mapper/MUserIndexMapper.java`

- [ ] **步骤 1：创建 MUserIndex 实体**

```java
package com.minimalist.basic.entity.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(value = "m_user_index")
public class MUserIndex implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 用户账号 */
    private String username;

    /** 所属租户ID */
    private Long tenantId;

    /** 状态 0禁用 1正常 */
    private Integer status;
}
```

- [ ] **步骤 2：创建 MUserIndexMapper**

```java
package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.po.MUserIndex;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

public interface MUserIndexMapper extends BaseMapper<MUserIndex> {

    default MUserIndex selectByUsername(String username) {
        return selectOneByQuery(QueryWrapper.create().eq(MUserIndex::getUsername, username));
    }

    default void deleteByUsernameAndTenantId(String username, Long tenantId) {
        deleteByQuery(QueryWrapper.create()
                .eq(MUserIndex::getUsername, username)
                .eq(MUserIndex::getTenantId, tenantId));
    }

    default void updateStatusByUsernameAndTenantId(String username, Long tenantId, Integer status) {
        MUserIndex index = new MUserIndex();
        index.setStatus(status);
        updateByQuery(index, QueryWrapper.create()
                .eq(MUserIndex::getUsername, username)
                .eq(MUserIndex::getTenantId, tenantId));
    }
}
```

- [ ] **步骤 3：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 4：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/entity/po/MUserIndex.java
git add minimalist-basic/src/main/java/com/minimalist/basic/mapper/MUserIndexMapper.java
git commit -m "feat: 新增MUserIndex实体和Mapper（全局用户账号索引）"
```

---

## 任务 4：系统级数据服务添加 @DS("master")

**文件：**
- 修改：`DictServiceImpl.java`、`ConfigServiceImpl.java`、`NoticeServiceImpl.java`、`StorageServiceImpl.java`

> 注：原计划创建自定义 `@UseMasterDS` 注解和 AOP 切面，实际实现中改为直接使用 dynamic-datasource 提供的 `@DS("master")` 注解（类级别），更简洁且无需额外维护切面。

- [ ] **步骤 1：在字典/配置/通知/存储的 Service 类上添加 @DS("master")**

在 `DictServiceImpl`、`ConfigServiceImpl`、`NoticeServiceImpl`、`StorageServiceImpl` 的类声明上添加 `@DS("master")` 注解，确保这些系统级数据的所有操作均走主库。

```java
@Service
@DS("master")
public class DictServiceImpl implements DictService {
    // ...
}
```

- [ ] **步骤 2：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 3：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/service/impl/
git commit -m "feat: 系统级数据查询添加@DS(\"master\")（字典/配置/通知/存储）"
```

---

## 任务 5：新增 TenantDbInitService（租户库初始化）

**文件：**
- 创建：`minimalist-basic/src/main/java/com/minimalist/basic/service/TenantDbInitService.java`
- 创建：`minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantDbInitServiceImpl.java`

- [ ] **步骤 1：创建接口**

```java
package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;

public interface TenantDbInitService {

    /**
     * 初始化租户数据库（建表）
     * @param datasourceVO 数据源连接信息
     */
    void initTenantDatabase(TenantDatasourceVO datasourceVO);
}
```

- [ ] **步骤 2：创建实现类**

```java
package com.minimalist.basic.service.impl;

import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.service.TenantDbInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TenantDbInitServiceImpl implements TenantDbInitService {

    private static final String TEMPLATE_SQL_PATH = "sql/tenant_init_template.sql";

    @Override
    public void initTenantDatabase(TenantDatasourceVO datasourceVO) {
        String sql = loadTemplateSql();
        try (Connection conn = DriverManager.getConnection(
                datasourceVO.getDatasourceUrl(),
                datasourceVO.getUsername(),
                datasourceVO.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("租户数据库初始化建表成功，数据源：{}", datasourceVO.getDatasourceName());
        } catch (Exception e) {
            log.error("租户数据库初始化建表失败：", e);
            throw new BusinessException("租户数据库初始化失败：" + e.getMessage());
        }
    }

    private String loadTemplateSql() {
        try {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_SQL_PATH);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new BusinessException("读取租户建表模板SQL失败");
        }
    }
}
```

- [ ] **步骤 3：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 4：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/service/TenantDbInitService.java
git add minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantDbInitServiceImpl.java
git commit -m "feat: 新增TenantDbInitService（租户库自动建表）"
```

---

## 任务 6：改造登录流程

**文件：**
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/service/impl/UserServiceImpl.java`

- [ ] **步骤 1：修改 userLogin 方法**

将现有登录逻辑改为：查主库索引 → 切租户库验密 → 校验租户状态 → 登录。

关键变更：
- 注入 `MUserIndexMapper`
- 移除 `@TenantIgnore` 对登录的影响
- 登录方法内部手动控制数据源

```java
// 在 UserServiceImpl 中新增注入
@Autowired
private MUserIndexMapper userIndexMapper;

// userLogin 方法改造核心逻辑：
// 1. 验证码校验（不变）
// 2. 查主库 m_user_index
MUserIndex userIndex = userIndexMapper.selectByUsername(reqVO.getUsername());
Assert.notNull(userIndex, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(userIndex.getStatus()),
        () -> new BusinessException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));

// 3. 切换数据源到租户库（tenant_id=0 则不切换，走主库）
Long tenantId = userIndex.getTenantId();
boolean needSwitch = tenantId != 0;
if (needSwitch) {
    DynamicDataSourceContextHolder.push(String.valueOf(tenantId));
}
try {
    // 4. 在目标库验证密码
    MUser loginUser = userMapper.selectUserByUsername(reqVO.getUsername());
    Assert.notNull(loginUser, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
    String passwordEncrypt = userManager.passwordEncrypt(reqVO.getPassword(), loginUser.getSalt());
    Assert.isTrue(loginUser.getPassword().equals(passwordEncrypt),
            () -> new BusinessException(UserEnum.ErrorMsg.U_OR_P_INCORRECT.getDesc()));
    Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(loginUser.getStatus()),
            () -> new BusinessException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));
} finally {
    if (needSwitch) {
        DynamicDataSourceContextHolder.poll();
    }
}

// 5. 校验租户状态（主库 m_tenant）
TenantVO tenantVO = tenantService.getTenantByTenantId(tenantId);
Assert.notNull(tenantVO, () -> new BusinessException(UserEnum.ErrorMsg.USER_UNBOUND_TENANT.getDesc()));
Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(tenantVO.getStatus().intValue()),
        () -> new BusinessException(TenantEnum.ErrorMsg.DISABLED_TENANT.getDesc()));
tenantManager.checkTenantExpireTime(tenantVO.getExpireTime());

// 6. 登录
StpUtil.login(userIndex.getTenantId() == 0 ? 0L : loginUser.getUserId());
StpUtil.getSession().set(TenantIgnore.TENANT_ID, tenantId);
return StpUtil.getTokenInfo();
```

- [ ] **步骤 2：移除 UserController.login 上的 @TenantIgnore**

- [ ] **步骤 3：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 4：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/service/impl/UserServiceImpl.java
git add minimalist-basic/src/main/java/com/minimalist/basic/controller/UserController.java
git commit -m "feat: 改造登录流程（主库索引路由+租户库验密）"
```

---

## 任务 7：改造数据源路由拦截器

**文件：**
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/config/tenant/TenantDatasourceInterceptor.java`
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/config/tenant/TenantWebMvcConfig.java`

- [ ] **步骤 1：简化拦截器逻辑**

移除对 `TenantEnum.MASTER` 的判断（不再有字段隔离），所有非系统租户直接切换数据源：

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (!TenantUtil.checkTenantOnOff()) {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
    long tenantId = TenantUtil.getTenantId();
    if (CommonConstant.ZERO != tenantId) {
        DynamicDataSourceContextHolder.push(String.valueOf(tenantId));
    }
    return HandlerInterceptor.super.preHandle(request, response, handler);
}
```

- [ ] **步骤 2：修改 TenantWebMvcConfig**

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new TenantDatasourceInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/basic/user/login",
                "/basic/user/getImageCaptcha"
            );
}
```

- [ ] **步骤 3：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 4：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/config/tenant/TenantDatasourceInterceptor.java
git add minimalist-basic/src/main/java/com/minimalist/basic/config/tenant/TenantWebMvcConfig.java
git commit -m "feat: 简化数据源路由拦截器（基于tenant_id，移除/basic排除）"
```

---

## 任务 8：改造创建租户流程

**文件：**
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantServiceImpl.java`

- [ ] **步骤 1：重写 addTenant 方法**

核心变更：
- 数据源连接信息为必填
- 先执行建表（TenantDbInitService）
- 切换到租户库初始化数据（用户、角色、权限）
- 写入主库（m_tenant、m_tenant_datasource、m_user_index）
- 移除字段隔离分支

- [ ] **步骤 2：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 3：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantServiceImpl.java
git commit -m "feat: 改造创建租户流程（自动建表+初始化+主库索引）"
```

---

## 任务 9：用户 CRUD 同步主库索引

**文件：**
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/service/impl/UserServiceImpl.java`

- [ ] **步骤 1：addUser 方法增加索引同步**

新增用户后，切主库插入 m_user_index。新增前校验 username 全局唯一。

- [ ] **步骤 2：deleteUserByUserId 方法增加索引删除**

删除用户后，切主库删除对应 m_user_index 记录。

- [ ] **步骤 3：用户禁用/启用增加索引状态同步**

更新用户 status 后，切主库同步 m_user_index.status。

- [ ] **步骤 4：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 5：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/service/impl/UserServiceImpl.java
git commit -m "feat: 用户CRUD同步主库索引（新增/删除/禁用/启用）"
```

---

## 任务 10：~~系统级数据服务添加 @UseMasterDS~~ → 已合并至任务 4

> 注：此任务已合并到任务 4 中统一处理。实际实现中使用 `@DS("master")` 类级别注解替代自定义 `@UseMasterDS` 方法级别注解。

---

## 任务 11：套餐权限变更同步租户库

**文件：**
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantPackageServiceImpl.java`
- 修改：`minimalist-basic/src/main/java/com/minimalist/basic/manager/TenantManager.java`

- [ ] **步骤 1：修改 updateTenantPermission**

套餐权限变更时，切换到各租户数据源更新 m_perms 和 m_role_perm。

- [ ] **步骤 2：编译验证**

运行：`mvn compile -pl minimalist-basic -q`
预期：BUILD SUCCESS

- [ ] **步骤 3：Commit**

```bash
git add minimalist-basic/src/main/java/com/minimalist/basic/service/impl/TenantPackageServiceImpl.java
git add minimalist-basic/src/main/java/com/minimalist/basic/manager/TenantManager.java
git commit -m "feat: 套餐权限变更同步到租户库（m_perms + m_role_perm）"
```

---

## 任务 12：代码清理

**文件：**
- 修改：`MyBatisFlexConfiguration.java` — 移除 TenantManager.setTenantFactory
- 修改：`MUser.java`、`MRole.java`、`MDept.java`、`MPost.java`、`MFile.java` — 移除 @Column(tenantId=true)
- 修改：`MTenant.java` — 移除 dataIsolation 字段
- 删除：`TenantIgnoreAspect.java`
- 修改：`TenantEnum.java` — 移除 DataIsolation 枚举

- [ ] **步骤 1：移除 MyBatisFlexConfiguration 中的 TenantFactory**

- [ ] **步骤 2：移除实体类上的 @Column(tenantId=true)**

- [ ] **步骤 3：移除 MTenant.dataIsolation 字段**

- [ ] **步骤 4：删除 TenantIgnoreAspect，清理 @TenantIgnore 引用**

- [ ] **步骤 5：编译验证**

运行：`mvn compile -q`
预期：BUILD SUCCESS

- [ ] **步骤 6：Commit**

```bash
git add -A
git commit -m "refactor: 清理字段隔离相关代码（TenantFactory/tenantId注解/TenantIgnoreAspect）"
```

---

## 任务 13：前端改造

**文件：**
- 修改：`minimalist-vue3/src/pages/basic/tenant/TenantEdit.vue`

- [ ] **步骤 1：移除"数据隔离方式"选择框**

移除 `dataIsolation` 相关的表单项和逻辑，数据源连接信息改为始终显示（必填）。

- [ ] **步骤 2：移除 form 中的 dataIsolation 字段**

- [ ] **步骤 3：Commit**

```bash
git add minimalist-vue3/src/pages/basic/tenant/TenantEdit.vue
git commit -m "feat: 前端移除数据隔离方式选择（数据源信息改为必填）"
```

---

## 任务 14：~~数据迁移脚本~~（未创建）

> 注：数据迁移脚本 `migration_data_transfer.sql` 暂未创建，待实际需要数据迁移时再编写。迁移方案已在设计文档中描述（第十节）。

---

## 任务 15：~~更新全量 SQL 和验证~~（未执行）

> 注：全量 SQL 更新和 migration_v2.0.sql 暂未创建，待正式发布时再处理。包括：新增 m_user_index 表、m_tenant 表移除 data_isolation 字段、移除 tenant-data-isolation 字典数据等。
