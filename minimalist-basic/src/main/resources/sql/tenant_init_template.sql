-- =============================================
-- 租户库初始化建表模板
-- 创建租户时由系统自动执行
-- 注意：所有表均不含 tenant_id 字段
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `m_user`;
CREATE TABLE `m_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '盐值',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_real_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户真实姓名',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `phone` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `user_sex` tinyint(0) NULL DEFAULT 1 COMMENT '用户性别  0未知 1男 2女',
  `user_avatar` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像base64编码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_unq`(`username`) USING BTREE COMMENT '用户名唯一索引',
  INDEX `user_id_idx`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `m_role`;
CREATE TABLE `m_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_sort` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS `m_dept`;
CREATE TABLE `m_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dept_id` bigint(0) NOT NULL COMMENT '部门id',
  `parent_dept_id` bigint(0) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `dept_leader` bigint(0) NULL DEFAULT NULL COMMENT '部门负责人',
  `dept_sort` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 岗位表
-- ----------------------------
DROP TABLE IF EXISTS `m_post`;
CREATE TABLE `m_post`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(0) NOT NULL COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(0) NOT NULL COMMENT '显示顺序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 文件表
-- ----------------------------
DROP TABLE IF EXISTS `m_file`;
CREATE TABLE `m_file`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `file_id` bigint(0) NOT NULL COMMENT '文件ID',
  `file_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原文件名',
  `new_file_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '现文件名',
  `file_size` bigint(0) NOT NULL DEFAULT 0 COMMENT '文件大小',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_base_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件基础路径',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件相对路径',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件url',
  `file_source` int(0) NULL DEFAULT NULL COMMENT '文件来源',
  `file_th_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件缩略图url',
  `storage_id` bigint(0) NULL DEFAULT NULL COMMENT '存储ID',
  `file_th_filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件缩略图文件名',
  `file_th_size` bigint(0) NULL DEFAULT NULL COMMENT '缩略图文件大小',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态  0未使用 1已使用',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id`) USING BTREE COMMENT '文件ID索引',
  INDEX `idx_file_url`(`file_url`) USING BTREE COMMENT '文件URL索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 权限表（从主库拷贝的权限副本）
-- ----------------------------
DROP TABLE IF EXISTS `m_perms`;
CREATE TABLE `m_perms`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `perm_id` bigint(0) NOT NULL COMMENT '权限ID',
  `perm_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `perm_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `parent_perm_id` bigint(0) NULL DEFAULT 0 COMMENT '父权限ID',
  `perm_sort` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `perm_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址',
  `perm_icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限图标',
  `perm_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'M' COMMENT '权限类型  M菜单 B按钮',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `external_link` bit(1) NULL DEFAULT b'0' COMMENT '是否为外部链接  0否 1是',
  `visible` bit(1) NULL DEFAULT b'1' COMMENT '是否可见 0隐藏 1显示',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 角色与权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `m_role_perm`;
CREATE TABLE `m_role_perm`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `perm_id` bigint(0) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `perm_id_idx`(`perm_id`) USING BTREE COMMENT '权限ID索引',
  INDEX `role_id_idx`(`role_id`) USING BTREE COMMENT '角色ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与权限关联表 1角色-N权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 角色与部门关联表
-- ----------------------------
DROP TABLE IF EXISTS `m_role_dept`;
CREATE TABLE `m_role_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id_idx`(`dept_id`) USING BTREE COMMENT '部门ID索引',
  INDEX `role_id_idx`(`role_id`) USING BTREE COMMENT '角色ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与部门关联表 1角色-N部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 用户与角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `m_user_role`;
CREATE TABLE `m_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role_id_idx`(`role_id`) USING BTREE COMMENT '角色ID索引',
  INDEX `user_id_idx`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与角色关联表 1用户-N角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 用户与部门关联表
-- ----------------------------
DROP TABLE IF EXISTS `m_user_dept`;
CREATE TABLE `m_user_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE COMMENT '部门ID索引',
  INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 用户与岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS `m_user_post`;
CREATE TABLE `m_user_post`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `post_id` bigint(0) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `post_id_idx`(`post_id`) USING BTREE COMMENT '岗位ID索引',
  INDEX `user_id_idx`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表 1用户-N岗位' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
