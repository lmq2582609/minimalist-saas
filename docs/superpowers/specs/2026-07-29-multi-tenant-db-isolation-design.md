# 多租户数据库隔离改造设计

## 概述

将现有多租户架构从"字段隔离（所有租户数据在主库，通过 tenant_id 字段区分）"改造为"纯数据库隔离（每个租户拥有独立数据库）"。主库仅保留平台管理数据和系统租户业务数据，其他租户数据全部迁移至各自独立数据库。

## 设计决策汇总

| 决策项 | 结论 |
|--------|------|
| 隔离模式 | 纯数据库隔离，取消字段隔离选项 |
| 登录认证 | 主库索引路由 + 租户库验密 |
| 用户名 | 全局唯一，不可修改 |
| 主库索引 | 不存密码，仅 username + tenant_id + status |
| 索引同步时机 | 新增/删除/禁用/启用用户 |
| 数据源路由依据 | token 中的 tenant_id（非 URL） |
| 管理员切换租户 | 保持现有方式，菜单刷新为租户权限 |
| 权限下发 | 直接拷贝 m_perms 到租户库 |
| 租户库初始化 | 自动建表 + 代码初始化数据 |
| 数据迁移 | 提供迁移方案 |

## 一、数据库职责划分

### 主库（master）

| 表 | 说明 |
|---|---|
| m_tenant | 租户信息 |
| m_tenant_package | 租户套餐 |
| m_tenant_package_perm | 套餐-权限关联 |
| m_tenant_datasource | 租户数据源连接信息 |
| m_user_index（新增） | 全局用户账号索引 |
| m_config | 系统配置 |
| m_storage | 文件存储配置 |
| m_dict | 字典（系统级） |
| m_notice | 通知（系统级） |
| m_perms | 权限/菜单定义（系统级） |
| 系统租户业务表 | tenant_id=0 的用户、角色、部门、岗位、文件等 |

### 租户库（每个租户独立）

| 表 | 说明 |
|---|---|
| m_user | 用户（含 password、salt，无 tenant_id） |
| m_role | 角色（无 tenant_id） |
| m_role_perm | 角色-权限关联 |
| m_role_dept | 角色-部门关联 |
| m_user_role | 用户-角色关联 |
| m_dept | 部门（无 tenant_id） |
| m_user_dept | 用户-部门关联 |
| m_post | 岗位（无 tenant_id） |
| m_user_post | 用户-岗位关联 |
| m_file | 文件记录（无 tenant_id） |
| m_perms | 权限副本（从主库拷贝） |

### m_user_index 表结构（新增）

```sql
CREATE TABLE `m_user_index` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL COMMENT '用户账号',
  `tenant_id` bigint NOT NULL COMMENT '所属租户ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unq_username`(`username`)
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='全局用户账号索引';
```

### m_tenant 表变更

- 移除 `data_isolation` 字段（不再有两种模式）
- `datasource` 字段保留（存储数据源名称标识）

## 二、登录流程

```
用户输入 username + password + captcha
        │
        ▼
① 验证码校验（Redis）
        │
        ▼
② 查主库 m_user_index（WHERE username = ?）
   → 获取 tenant_id、status
   → 校验账号状态
        │
        ▼
③ 根据 tenant_id 确定数据源：
   ├─ tenant_id = 0 → 主库
   └─ tenant_id ≠ 0 → DynamicDataSourceContextHolder.push(tenantId)
        │
        ▼
④ 在目标库 m_user 中验证密码（password + salt）
   → 校验用户状态
        │
        ▼
⑤ 查主库 m_tenant 校验租户状态（禁用/过期）
        │
        ▼
⑥ StpUtil.login(userId)
   Session 缓存 tenant_id
        │
        ▼
⑦ 清除数据源上下文，返回 token
```

### 与现有代码差异

- 移除登录接口的 `@TenantIgnore` 注解
- 登录方法内部手动控制数据源切换（先主库查索引，再切租户库验密）
- 用户名唯一性由 m_user_index 唯一索引保证

## 三、数据源路由拦截器

### 核心逻辑

基于 token 解析出的 tenant_id 决定数据源，不基于 URL 路径。

```
请求进入（已认证）
    │
    ▼
多租户开关是否开启？
   └─ 否 → 主库，放行
    │
    ▼
获取有效 tenant_id（TenantUtil.getTenantId()）
   ├─ tenant_id = 0 → 主库
   ├─ tenant_id ≠ 0 → 租户数据源
   └─ 系统管理员切换租户 → 切换后的租户数据源
    │
    ▼
afterCompletion → 清除数据源上下文
```

### 拦截器配置

```java
registry.addInterceptor(new TenantDatasourceInterceptor())
    .addPathPatterns("/**")
    .excludePathPatterns(
        "/basic/user/login",
        "/basic/user/getImageCaptcha"
    );
```

仅排除匿名接口（登录、验证码），其余所有已认证请求均走数据源路由。

### 管理员切换租户

- 保持现有 cookie 机制（change_tenant_id）
- 切换后前端刷新菜单为该租户的权限范围
- 系统管理菜单不可见，不会调用平台管理接口
- 无需 @MasterDS 注解或 URL 排除

## 四、创建租户流程

```
管理员填写：租户名、套餐、账号额度、过期时间、数据源连接信息、管理员用户信息
        │
        ▼
① 校验：租户名唯一、用户账号全局唯一（查 m_user_index）
        │
        ▼
② 连接目标数据库，执行 tenant_init_template.sql 建表
        │
        ▼
③ 在租户库中初始化数据：
   - 创建管理员用户（m_user）
   - 创建管理员角色（m_role）
   - 拷贝套餐权限到租户库（m_perms）
   - 角色-权限关联（m_role_perm）
   - 用户-角色关联（m_user_role）
        │
        ▼
④ 写入主库：
   - m_tenant（租户信息）
   - m_tenant_datasource（数据源连接）
   - m_user_index（用户账号索引）
        │
        ▼
⑤ 动态注册数据源（TenantManager.dynamicAddDatasource）
        │
        ▼
⑥ Redis Pub/Sub 通知其他节点同步
```

### 租户库建表模板

文件位置：`resources/sql/mysql/tenant_init_template.sql`

包含 11 张表的 DDL：m_user、m_role、m_dept、m_post、m_file、m_perms、m_role_perm、m_role_dept、m_user_role、m_user_dept、m_user_post。

业务表（m_user、m_role、m_dept、m_post、m_file）移除 tenant_id 字段。不含 INSERT 语句。

## 五、用户管理与主库索引同步

### 同步场景

| 操作 | 主库索引动作 |
|------|-------------|
| 新增用户 | INSERT m_user_index (username, tenant_id, status) |
| 删除用户 | DELETE m_user_index WHERE username = ? AND tenant_id = ? |
| 禁用用户 | UPDATE m_user_index SET status = 0 |
| 启用用户 | UPDATE m_user_index SET status = 1 |

### 同步方式

在用户管理 Service 中，操作租户库 m_user 后，切换数据源到主库同步 m_user_index。属于同一业务逻辑中的双写（非跨库事务，索引表操作失败可重试或补偿）。

### 用户名唯一性校验

新增用户时：先查主库 m_user_index 是否已存在该 username，存在则拒绝创建。

## 六、权限管理

### 权限定义（主库）

- m_perms 在主库统一维护（系统管理员操作）
- m_tenant_package / m_tenant_package_perm 在主库管理套餐权限

### 权限下发（拷贝到租户库）

- 创建租户时：根据选择的套餐，将对应 m_perms 记录拷贝到租户库
- 套餐权限变更时：遍历使用该套餐的所有租户，更新其租户库的 m_perms 和 m_role_perm

### 租户内权限使用

- 租户管理员在自己库内管理角色（m_role）和角色权限（m_role_perm）
- 加载菜单时：从租户库 m_role_perm → m_perms 查询，无需跨库

## 七、系统级数据访问

m_config、m_dict、m_notice、m_storage 为系统级数据，仅存于主库。

### 访问策略

租户用户也需要读取字典（前端下拉框渲染等），因此这些查询接口不能简单走租户数据源。

处理方式：在字典、配置、通知、存储相关的 Service/Mapper 层，使用 `DynamicDataSourceContextHolder.push("master")` 强制切换主库查询，查询完毕后恢复。

具体实现：
- 创建一个工具方法或 AOP 注解（如 `@UseMasterDS`），标记在需要访问主库系统级数据的 Service 方法上
- 方法执行前 push master，执行后 clear/恢复原数据源
- 适用于：DictService、ConfigService、NoticeService、StorageService 的查询方法

### 写入权限

- 系统级数据的增删改仅系统管理员可操作（通过 @SaCheckPermission 控制）
- 系统管理员 tenant_id=0，拦截器自然路由到主库，写入无问题

## 八、前端改动

### 登录页

- 无变化（用户名全局唯一，不需要选择租户）

### 租户管理页（TenantEdit.vue）

- 移除"数据隔离方式"选择（不再有字段隔离/数据库隔离选项）
- 数据源连接信息改为必填（不再是条件显示）

### 租户切换（MHeader.vue）

- 保持现有逻辑不变

### 字典/配置/通知页面

- 这些为系统级功能，仅系统管理员可见（通过权限控制）
- 无需前端改动

### API 路径

- 所有 API 路径保持不变，无破坏性变更

## 九、后端代码清理

### 移除

- `MyBatisFlexConfiguration` 中的 `TenantManager.setTenantFactory(...)` — 不再需要字段过滤
- 实体类上的 `@Column(tenantId = true)` 注解（MUser、MRole、MDept、MPost、MFile）
- `TenantIgnoreAspect` 切面
- `@TenantIgnore` 注解（或保留改作他用）
- `TenantEnum.DataIsolation` 枚举（不再有隔离方式选择）
- m_tenant 表的 `data_isolation` 字段相关逻辑

### 修改

- `TenantDatasourceInterceptor` — 简化逻辑，移除 `/basic/**` 排除
- `TenantWebMvcConfig` — 调整拦截器配置
- `UserServiceImpl.userLogin()` — 新登录流程
- `TenantServiceImpl.addTenant()` — 新创建租户流程（建表 + 初始化）
- `UserServiceImpl` 用户 CRUD — 增加主库索引同步
- `TenantManager.updateTenantPermission()` — 改为操作租户库
- `TenantInit` — 启动时加载所有租户数据源（逻辑基本不变）

### 新增

- `MUserIndexMapper` — 主库索引表 Mapper
- `TenantDbInitService` — 租户库初始化服务（执行建表 SQL + 初始数据）
- `tenant_init_template.sql` — 建表模板文件
- `@UseMasterDS` 注解 + AOP 切面 — 标记强制走主库的方法（用于系统级数据查询）

## 十、数据迁移方案

为已有用户提供从字段隔离到数据库隔离的迁移：

### 迁移步骤

1. **准备阶段**：为每个非系统租户创建独立数据库
2. **建表**：对每个租户库执行 tenant_init_template.sql
3. **数据迁移**：将主库中各租户的数据（按 tenant_id 筛选）插入到对应租户库
   - m_user (WHERE tenant_id = X) → 租户库 m_user（去掉 tenant_id 字段）
   - m_role、m_dept、m_post、m_file 同理
   - m_role_perm、m_user_role 等关联表按角色/用户 ID 筛选迁移
   - m_perms 按套餐权限拷贝
4. **生成索引**：将所有用户的 username + tenant_id + status 插入 m_user_index
5. **注册数据源**：在 m_tenant_datasource 中为每个租户插入连接信息
6. **清理主库**：删除主库中非系统租户的业务数据（可选，建议先备份）
7. **更新 m_tenant**：移除 data_isolation 字段值，更新 datasource 字段

### 迁移工具

提供 SQL 迁移脚本 + Java 迁移工具类（可选），在升级文档中说明步骤。

## 十一、系统租户（tenant_id=0）

系统租户的数据继续留在主库，包括：
- 系统管理员用户（admin）
- 系统级角色
- 系统级部门、岗位
- 系统管理员的文件

系统租户的表结构可以保留 tenant_id 字段（值为 0），也可以移除。建议保留以最小化改动。

## 十二、多节点同步

保持现有 Redis Pub/Sub 机制：
- 租户创建/修改/删除时发布消息
- 其他节点收到消息后动态添加/删除数据源
- 逻辑基本不变，只是不再需要处理"字段隔离"的分支
