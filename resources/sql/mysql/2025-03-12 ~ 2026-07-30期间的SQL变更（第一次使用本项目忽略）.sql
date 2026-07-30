-- =============================================
-- v2.0 多租户数据库隔离改造 - 主库变更
-- =============================================

-- 1. 新增全局用户账号索引表
CREATE TABLE
    IF
    NOT EXISTS `m_user_index` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `username` VARCHAR ( 30 ) NOT NULL COMMENT '用户账号',
    `tenant_id` BIGINT NOT NULL COMMENT '所属租户ID',
    `status` TINYINT NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
    PRIMARY KEY ( `id` ),
    UNIQUE INDEX `unq_username` ( `username` )
    ) ENGINE = INNODB CHARACTER
    SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局用户账号索引';

-- 2. 从现有 m_user 表填充索引数据
INSERT INTO m_user_index (username, tenant_id, status)
SELECT username, tenant_id, status FROM m_user WHERE deleted = 0;

-- 3. m_tenant 表移除 data_isolation 字段
ALTER TABLE m_tenant DROP COLUMN `data_isolation`;

-- 4. 移除字典中的 tenant-data-isolation 数据（可选）
DELETE FROM m_dict WHERE dict_type = 'tenant-data-isolation';









-- =============================================
-- v2.0 多租户数据库隔离改造 - 数据迁移参考
-- =============================================
-- 步骤1：为租户创建独立数据库
CREATE DATABASE IF NOT EXISTS `tenant_{租户ID}` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 步骤2：在租户库中执行建表模板
-- 使用项目中 minimalist-basic/src/main/resources/sql/tenant_init_template.sql

-- 步骤3：从主库导出租户数据到租户库（示例，需对每个租户重复）
-- 以下在主库执行，将数据导出后导入租户库

-- 用户数据（去掉 tenant_id 列）
INSERT INTO `tenant_{租户ID}`.m_user (id, user_id, username, password, salt, nickname, user_real_name, email, phone, user_sex, user_avatar, remark, status, create_id, create_time, update_id, update_time, deleted, version)
SELECT id, user_id, username, password, salt, nickname, user_real_name, email, phone, user_sex, user_avatar, remark, status, create_id, create_time, update_id, update_time, deleted, version
FROM m_user WHERE tenant_id = {租户ID};

-- 角色数据
INSERT INTO `tenant_{租户ID}`.m_role (id, role_id, role_name, role_code, role_sort, status, remark, create_id, create_time, update_id, update_time, deleted, version)
SELECT id, role_id, role_name, role_code, role_sort, status, remark, create_id, create_time, update_id, update_time, deleted, version
FROM m_role WHERE tenant_id = {租户ID};

-- 部门数据
INSERT INTO `tenant_{租户ID}`.m_dept (id, dept_id, parent_dept_id, ancestors, dept_name, dept_leader, dept_sort, phone, email, status, create_id, create_time, update_id, update_time, deleted, version)
SELECT id, dept_id, parent_dept_id, ancestors, dept_name, dept_leader, dept_sort, phone, email, status, create_id, create_time, update_id, update_time, deleted, version
FROM m_dept WHERE tenant_id = {租户ID};

-- 岗位数据
INSERT INTO `tenant_{租户ID}`.m_post (id, post_id, post_code, post_name, post_sort, remark, status, create_id, create_time, update_id, update_time, deleted, version)
SELECT id, post_id, post_code, post_name, post_sort, remark, status, create_id, create_time, update_id, update_time, deleted, version
FROM m_post WHERE tenant_id = {租户ID};

-- 文件数据
INSERT INTO `tenant_{租户ID}`.m_file (id, file_id, file_name, new_file_name, file_size, file_type, file_base_path, file_path, file_url, file_source, file_th_url, storage_id, file_th_filename, file_th_size, remark, status, create_id, create_time, update_id, update_time, deleted, version)
SELECT id, file_id, file_name, new_file_name, file_size, file_type, file_base_path, file_path, file_url, file_source, file_th_url, storage_id, file_th_filename, file_th_size, remark, status, create_id, create_time, update_id, update_time, deleted, version
FROM m_file WHERE tenant_id = {租户ID};

-- 关联表（按该租户的角色ID/用户ID筛选）
-- m_role_perm: WHERE role_id IN (SELECT role_id FROM m_role WHERE tenant_id = {租户ID})
-- m_user_role: WHERE user_id IN (SELECT user_id FROM m_user WHERE tenant_id = {租户ID})
-- m_user_dept: 同上
-- m_user_post: 同上
-- m_perms: 按套餐权限拷贝

-- 步骤4：在主库 m_tenant_datasource 中注册该租户的数据源连接
INSERT INTO m_tenant_datasource (datasource_id, tenant_id, datasource_name, datasource_url, username, password)
VALUES ({唯一ID}, {租户ID}, 'tenant_{租户ID}', 'jdbc:mysql://localhost:3306/tenant_{租户ID}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai', '用户名', '密码');

-- 步骤5（可选）：确认无误后，删除主库中该租户的业务数据
-- DELETE FROM m_user WHERE tenant_id = {租户ID};
-- DELETE FROM m_role WHERE tenant_id = {租户ID};
-- ... 其他表同理