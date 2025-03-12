-- 修改为mybatis flex框架后，deleted字段由 "0已删除 1未删除" 调整为 "0未删除  1已删除"
ALTER TABLE `minimalist`.`m_config` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_dept` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_dict` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_file` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_notice` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_perms` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_post` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_role` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_storage` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_tenant_package` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;
ALTER TABLE `minimalist`.`m_user` MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除' AFTER `update_time`;


-- 文件表删除 storage_type 存储类型 字段
ALTER TABLE `minimalist`.`m_file` DROP COLUMN `storage_type`;
-- 文件表增加 storage_id 存储ID字段
ALTER TABLE `minimalist`.`m_file` ADD COLUMN `storage_id` bigint(0) NULL COMMENT '存储ID' AFTER `file_th_url`;
-- 存储表删除 storage_default 默认存储字段
ALTER TABLE `minimalist`.`m_storage` DROP COLUMN `storage_default`;


-- 租户表增加 data_isolation(数据隔离方式)、 datasource(数据源名称)、storage_id(存储ID) 字段
ALTER TABLE m_tenant
ADD COLUMN `data_isolation` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'column' COMMENT '数据隔离方式  column字段隔离(默认) db数据库隔离' AFTER `account_count`,
ADD COLUMN `datasource` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'master' COMMENT '数据源名称 master(默认使用主库) ' AFTER `data_isolation`,
ADD COLUMN `storage_id` bigint(0) NULL DEFAULT NULL COMMENT '存储ID 表示该租户使用哪个文件存储' AFTER `datasource`;

-- 租户表字段顺序、字段默认值、注释调整
ALTER TABLE m_tenant
MODIFY COLUMN `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `storage_id`,
MODIFY COLUMN `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态 0禁用 1正常' AFTER `remark`,
MODIFY COLUMN `deleted` bit(1) NULL DEFAULT b'0' COMMENT '逻辑删除  0未删除  1已删除',
MODIFY COLUMN `version` int(0) NULL DEFAULT 0 COMMENT '版本号';


-- 新增 m_tenant_datasource 租户数据源表，目的是同时支持租户字段隔离和数据源隔离
DROP TABLE IF EXISTS `m_tenant_datasource`;
CREATE TABLE `m_tenant_datasource`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `datasource_id` bigint(0) NOT NULL COMMENT '数据源ID',
  `tenant_id` bigint(0) NOT NULL COMMENT '租户ID',
  `datasource_name` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源名称',
  `datasource_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源连接',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户数据源表' ROW_FORMAT = Dynamic;



-- 新增七牛云存储数据
INSERT INTO `m_storage` VALUES (4, 1891827317205766144, '七牛云2', 'qiniu', NULL, '{\"accessKey\":\"2222\",\"secretKey\":\"2222\",\"endPoint\":\"2222\",\"bucketName\":\"2222\",\"regionId\":\"2222\"}', 1, 0, '2025-02-18 20:29:15.000000', 0, '2025-02-18 20:29:56.000000', b'0', 2);


-- 新增字典数据
INSERT INTO `m_dict` VALUES (133, 1890393477224509440, 'tenant-data-isolation', 'column', '字段隔离', '租户数据隔离方式', '多租户隔离方式：字段隔离 或 数据库隔离', 1, 'green', 1, 0, '2025-02-14 21:31:41.000000', 0, '2025-02-14 21:31:41.000000', b'0', 0);
INSERT INTO `m_dict` VALUES (134, 1890393477329367040, 'tenant-data-isolation', 'db', '数据库隔离', '租户数据隔离方式', '多租户隔离方式：字段隔离 或 数据库隔离', 2, 'arcoblue', 1, 0, '2025-02-14 21:31:41.000000', 0, '2025-02-14 21:31:41.000000', b'0', 0);
INSERT INTO `m_dict` VALUES (136, 1891825581577605120, 'storage-type', 'qiniu', '七牛云', '存储类型', '存储平台，如阿里云oss、腾讯云oss、七牛云oss等', 3, 'gold', 1, 0, '2025-02-18 20:22:21.000000', 0, '2025-02-18 20:29:34.000000', b'0', 2);
INSERT INTO `m_dict` VALUES (137, 1899098996038688768, 'file-type', 'image', '图片', '文件类型', '文件类型，与file表中file_type字段对应', 0, NULL, 1, 0, '2025-03-10 22:04:18.000000', 0, '2025-03-10 22:04:18.000000', b'0', 0);
INSERT INTO `m_dict` VALUES (138, 1899098996160323584, 'file-type', 'video', '视频', '文件类型', '文件类型，与file表中file_type字段对应', 0, NULL, 1, 0, '2025-03-10 22:04:18.000000', 0, '2025-03-10 22:04:18.000000', b'0', 0);
INSERT INTO `m_dict` VALUES (139, 1899780153331810304, 'file-source-path', '-1', 'common/', '文件来源-存储位置', '文件来源对应的存储位置，配合\"文件来源\"一起使用', 0, NULL, 1, 0, '2025-03-12 19:11:03.000000', 0, '2025-03-12 19:11:03.000000', b'0', 0);


