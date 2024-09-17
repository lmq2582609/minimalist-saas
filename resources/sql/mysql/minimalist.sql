SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for m_config
-- ----------------------------
DROP TABLE IF EXISTS `m_config`;
CREATE TABLE `m_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `config_id` bigint(0) NOT NULL COMMENT '参数ID',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参数键值',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_config
-- ----------------------------
INSERT INTO `m_config` VALUES (1, 1833741434936000512, '系统参数-多租户开关', 'system.config.tenant', 'true', 1, 0, '2024-09-11 13:36:42.050638', 0, '2024-09-11 15:47:16.751385', b'1', 6);
INSERT INTO `m_config` VALUES (2, 1833772827577573376, '系统参数-多租户忽略的表', 'system.config.tenant.ignore.table', 'm_config,m_dict,m_perms,m_notice,m_role_dept,m_role_perm,m_tenant,m_tenant_package,m_tenant_package_perm,m_user_dept,m_user_post,m_user_role', 1, 0, '2024-09-11 15:41:26.637164', 0, '2024-09-11 15:44:11.337426', b'1', 1);
INSERT INTO `m_config` VALUES (3, 1833773478936207360, '系统参数-用户头像大小限制(kb)', 'system.config.user.avatar.size', '102400', 1, 0, '2024-09-11 15:44:01.932583', 0, '2024-09-11 15:44:17.760034', b'1', 1);
INSERT INTO `m_config` VALUES (4, 1833773744087523328, '系统参数-系统验证码是否开启', 'system.config.captcha.enable', 'false', 1, 0, '2024-09-11 15:45:05.149415', 0, '2024-09-11 17:11:15.866456', b'1', 2);
INSERT INTO `m_config` VALUES (5, 1834053538905784320, '系统参数-密码加密盐', 'system.config.password.salt', 'QulUIF2e3yBgzas0', 1, 0, '2024-09-12 10:16:53.432421', 0, '2024-09-12 10:16:53.432421', b'1', 0);

-- ----------------------------
-- Table structure for m_dept
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
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户编号',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `allow_delete` bit(1) NULL DEFAULT b'1' COMMENT '数据是否允许删除 0否 1是',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_dept
-- ----------------------------
INSERT INTO `m_dept` VALUES (5, 1677964029214371840, 0, NULL, '极简多租户', 0, 0, '10086', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-09 16:52:54.285322', 0, '2024-09-12 13:42:59.939494', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (6, 1677964231673425920, 1677964029214371840, '1677964029214371840', '北京总部', 0, 0, '10010', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-09 16:53:42.555301', 0, '2024-09-12 13:55:20.471072', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (7, 1677964435873116160, 1677964231673425920, '1677964029214371840,1677964231673425920', '基础架构部', 0, 20, '123456', '12121212', 1, 0, 1655941826117689345, '2023-07-09 16:54:31.240378', 0, '2024-09-12 13:48:55.319535', b'1', b'1', 3);
INSERT INTO `m_dept` VALUES (8, 1677964531410972672, 1677964231673425920, '1677964029214371840,1677964231673425920', '产品设计部', 0, 10, '123456', '233223', 1, 0, 1655941826117689345, '2023-07-09 16:54:54.018152', 0, '2024-09-12 13:48:49.612112', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (9, 1677964682431082496, 1677964231673425920, '1677964029214371840,1677964231673425920', '人力资源部', 0, 0, '456789', '4565346', 1, 0, 1655941826117689345, '2023-07-09 16:55:30.024647', 0, '2024-09-12 13:48:16.133344', b'1', b'1', 4);
INSERT INTO `m_dept` VALUES (10, 1686015040692744192, 1677964231673425920, '1677964029214371840,1677964231673425920', '市场营销部', 0, 30, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-31 22:04:45.032834', 0, '2024-09-12 13:48:59.471250', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (11, 1686019822887170048, 1677964231673425920, '1677964029214371840,1677964231673425920', '销售部', 0, 5, '1234567', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-31 22:23:45.167050', 0, '2024-09-12 13:48:43.809374', b'1', b'1', 3);
INSERT INTO `m_dept` VALUES (12, 1686021322711560192, 1677964231673425920, '1677964029214371840,1677964231673425920', '财务部', 0, 40, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-31 22:29:42.752069', 0, '2024-09-12 13:49:04.612000', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (13, 1686021452114227200, 1677964231673425920, '1677964029214371840,1677964231673425920', '客户服务部', 0, 50, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-31 22:30:13.605403', 0, '2024-09-12 13:49:08.625494', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (14, 1686021621496999936, 1677964231673425920, '1677964029214371840,1677964231673425920', '运营部', 0, 60, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-31 22:30:53.989690', 0, '2024-09-12 13:49:16.259154', b'1', b'1', 2);
INSERT INTO `m_dept` VALUES (15, 1686021776396840960, 1677964029214371840, '1677964029214371840', '河北分部', 0, 10, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-07-31 22:31:30.920687', 0, '2024-09-12 13:49:24.009569', b'1', b'1', 1);
INSERT INTO `m_dept` VALUES (16, 1686349179535036416, 1686021776396840960, '1677964029214371840,1686021776396840960', '销售部', 0, 10, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-08-01 20:12:29.912108', 0, '2024-09-12 13:49:36.360331', b'1', b'1', 1);
INSERT INTO `m_dept` VALUES (17, 1686349332929122304, 1686021776396840960, '1677964029214371840,1686021776396840960', '财务部', 0, 20, '123456', '123456@qq.com', 1, 0, 1655941826117689345, '2023-08-01 20:13:06.483608', 0, '2024-09-12 13:49:42.360847', b'1', b'1', 1);
INSERT INTO `m_dept` VALUES (18, 1688102484086906880, 1686021776396840960, '1677964029214371840,1686021776396840960', '运营部', 0, 30, '13888888888', '13888888888@phone.com', 1, 0, 1655941826117689345, '2023-08-06 16:19:30.292609', 0, '2024-09-12 13:49:46.840120', b'1', b'1', 1);
INSERT INTO `m_dept` VALUES (24, 1834773502369406976, 0, NULL, '东东胡辣汤', 1834763884364705792, 0, '11111', '11111', 1, 1834763883194494976, 1834763884364705792, '2024-09-14 09:57:46.111124', 1834763884364705792, '2024-09-14 10:01:30.581557', b'1', b'1', 1);
INSERT INTO `m_dept` VALUES (25, 1834774513448329216, 1834773502369406976, '1834773502369406976', '东东胡辣汤一部', 1834763884364705792, 0, '111', '111', 1, 1834763883194494976, 1834763884364705792, '2024-09-14 10:01:48.475618', 1834763884364705792, '2024-09-14 10:01:48.475618', b'1', b'1', 0);
INSERT INTO `m_dept` VALUES (26, 1834774974033240064, 1834773502369406976, '1834773502369406976', '东东胡辣汤二部', 1834763884364705792, 0, '222', '222', 1, 1834763883194494976, 1834763884364705792, '2024-09-14 10:03:37.111552', 1834763884364705792, '2024-09-14 10:03:37.111552', b'1', b'1', 0);
INSERT INTO `m_dept` VALUES (27, 1836060155556687872, 0, NULL, '测试租户111', 1836055978990497792, 100, '1111111', '1111111', 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:10:28.128713', 1836055978990497792, '2024-09-17 23:10:28.128713', b'1', b'1', 0);
INSERT INTO `m_dept` VALUES (28, 1836060210875363328, 1836060155556687872, '1836060155556687872', '一部', 1836059921829097472, 11, '11', '11', 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:10:41.436305', 1836055978990497792, '2024-09-17 23:10:41.436305', b'1', b'1', 0);
INSERT INTO `m_dept` VALUES (29, 1836060257859956736, 1836060155556687872, '1836060155556687872', '二部', 1836060007078326272, 22, '22', '22', 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:10:52.640320', 1836055978990497792, '2024-09-17 23:10:52.640320', b'1', b'1', 0);

-- ----------------------------
-- Table structure for m_dict
-- ----------------------------
DROP TABLE IF EXISTS `m_dict`;
CREATE TABLE `m_dict`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dict_id` bigint(0) NULL DEFAULT NULL COMMENT '字典ID',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `dict_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典key',
  `dict_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典value',
  `dict_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `dict_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典描述',
  `dict_order` int(0) NULL DEFAULT NULL COMMENT '字典排序值',
  `dict_class` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典样式，对应前端Tag组件的type',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `allow_delete` bit(1) NULL DEFAULT b'1' COMMENT '数据是否允许删除 0否 1是',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_dict
-- ----------------------------
INSERT INTO `m_dict` VALUES (7, 1665042401249841152, 'dict-status', '0', '禁用', '字典状态', '字典管理中的字典状态', 0, '', 1, NULL, NULL, 1655941826117689345, '2023-06-20 09:22:39.302825', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (8, 1665042401249841153, 'dict-status', '1', '启用', '字典状态', '字典管理中的字典状态', 1, '', 1, NULL, NULL, 1655941826117689345, '2023-06-20 09:22:39.302825', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (61, 1666804273288323072, 'perm-status', '0', '禁用', '权限状态', '权限管理中的权限状态', 0, 'red', 1, NULL, NULL, 1655941826117689345, '2023-07-08 14:55:19.798029', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (62, 1666804273288323073, 'perm-status', '1', '启用', '权限状态', '权限管理中的权限状态', 1, 'arcoblue', 1, NULL, NULL, 1655941826117689345, '2023-07-08 14:55:19.798029', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (63, 1666983160652914688, 'yes-no', 'true', '是', '是/否', '公共字典，是/否', 0, 'arcoblue', 1, NULL, NULL, 1655941826117689345, '2023-06-16 09:55:02.189707', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (64, 1666983160652914689, 'yes-no', 'false', '否', '是/否', '公共字典，是/否', 1, 'red', 1, NULL, NULL, 1655941826117689345, '2023-06-16 09:55:02.189707', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (66, 1667413525115789313, 'perm-type', 'M', '菜单', '权限类型', '权限管理中的权限类型', 5, 'arcoblue', 1, NULL, NULL, 1655941826117689345, '2023-06-28 10:58:51.929748', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (67, 1667413525115789314, 'perm-type', 'B', '按钮', '权限类型', '权限管理中的权限类型', 10, 'magenta', 1, NULL, NULL, 1655941826117689345, '2023-06-28 10:58:51.929748', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (68, 1669515928599478272, 'dict-class', 'default', '默认', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 0, 'default', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (69, 1669515928603672576, 'dict-class', 'orange', '橙', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 10, 'orange', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (70, 1669515928603672577, 'dict-class', 'gold', '金', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 20, 'gold', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (71, 1669515928603672578, 'dict-class', 'lime', '绿黄', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 40, 'lime', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (72, 1669515928603672579, 'dict-class', 'green', '绿', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 50, 'green', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (73, 1669515928603672580, 'dict-class', 'cyan', '青', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 60, 'cyan', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (74, 1669515928603672581, 'dict-class', 'blue', '蓝', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 70, 'blue', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (75, 1669515928603672582, 'dict-class', 'arcoblue', '湖蓝', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 80, 'arcoblue', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (76, 1669515928603672583, 'dict-class', 'purple', '紫', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 90, 'purple', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (77, 1669515928603672584, 'dict-class', 'pinkpurple', '紫粉', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 100, 'pinkpurple', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (78, 1669515928603672585, 'dict-class', 'magenta', '洋红', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 110, 'magenta', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (79, 1669515928603672586, 'dict-class', 'gray', '灰', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 120, 'gray', 1, 1655941826117689345, '2023-06-16 09:23:10.142052', 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (80, 1669518820391731200, 'dict-class', 'red', '红', '字典样式', '字典管理中的字典样式，对应Tag标签的样式', 30, 'red', 1, NULL, NULL, 1655941826117689345, '2023-06-17 11:25:04.092472', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (88, 1670964037875548160, 'role-status', '0', '禁用', '角色状态', '角色管理中的角色状态', 0, 'red', 1, 1655941826117689345, '2023-06-20 09:17:26.298689', 1655941826117689345, '2023-06-21 14:03:01.385069', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (89, 1670964037879742464, 'role-status', '1', '启用', '角色状态', '角色管理中的角色状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-06-20 09:17:26.298689', 1655941826117689345, '2023-06-21 14:03:01.385069', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (90, 1673936932367593472, 'dept-status', '0', '禁用', '部门状态', '部门管理中的部门状态', 0, 'red', 0, 1655941826117689345, '2023-06-28 14:10:39.577940', 1655941826117689345, '2023-06-28 14:10:39.577940', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (91, 1673936932367593473, 'dept-status', '1', '启用', '部门状态', '部门管理中的部门状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-06-28 14:10:39.577940', 1655941826117689345, '2023-06-28 14:10:39.577940', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (92, 1676044708130308096, 'post-status', '0', '禁用', '岗位状态', '岗位管理中的岗位状态', 0, 'red', 1, 1655941826117689345, '2023-07-04 09:46:12.480261', 1655941826117689345, '2023-07-04 09:46:12.480261', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (93, 1676044708130308097, 'post-status', '1', '启用', '岗位状态', '岗位管理中的岗位状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-04 09:46:12.480261', 1655941826117689345, '2023-07-04 09:46:12.480261', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (94, 1676833934375645184, 'tenant-package-status', '0', '禁用', '套餐状态', '租户维护 -> 套餐管理中的套餐状态', 0, 'red', 1, 1655941826117689345, '2023-07-06 14:02:18.680351', 1655941826117689345, '2023-07-06 14:02:18.680351', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (95, 1676833934375645185, 'tenant-package-status', '1', '启用', '套餐状态', '租户维护 -> 套餐管理中的套餐状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-06 14:02:18.680351', 1655941826117689345, '2023-07-06 14:02:18.680351', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (96, 1676834354351304704, 'tenant-status', '0', '禁用', '租户状态', '租户维护 -> 租户管理中的租户状态', 0, 'red', 1, 1655941826117689345, '2023-07-06 14:03:58.810584', 1655941826117689345, '2023-07-06 14:03:58.810584', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (97, 1676834354351304705, 'tenant-status', '1', '启用', '租户状态', '租户维护 -> 租户管理中的租户状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-06 14:03:58.810584', 1655941826117689345, '2023-07-06 14:03:58.810584', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (98, 1677961643871744000, 'user-sex', '1', '男', '用户性别', '用户管理中的用户性别', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-09 16:43:25.575566', 1655941826117689345, '2023-07-09 16:43:25.575566', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (99, 1677961643871744001, 'user-sex', '2', '女', '用户性别', '用户管理中的用户性别', 2, 'orange', 1, 1655941826117689345, '2023-07-09 16:43:25.575566', 1655941826117689345, '2023-07-09 16:43:25.575566', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (100, 1678035488150446080, 'user-status', '0', '禁用', '用户状态', '用户管理中的用户状态', 0, 'red', 1, 1655941826117689345, '2023-07-09 21:36:51.423322', 1655941826117689345, '2023-07-09 21:36:51.423322', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (101, 1678035488150446081, 'user-status', '1', '启用', '用户状态', '用户管理中的用户状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-09 21:36:51.423322', 1655941826117689345, '2023-07-09 21:36:51.423322', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (102, 1681277991876071424, 'notice-status', '0', '禁用', '系统公告状态', '系统公告中的状态', 0, 'red', 1, 1655941826117689345, '2023-07-18 20:21:24.554865', 1655941826117689345, '2023-07-18 20:21:24.554865', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (103, 1681277991876071425, 'notice-status', '1', '启用', '系统公告状态', '系统公告中的状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-18 20:21:24.554865', 1655941826117689345, '2023-07-18 20:21:24.554865', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (104, 1681499349985316864, 'notice-type', '1', '公告', '系统公告类型', '系统公告中的公告类型', 0, 'arcoblue', 1, 1655941826117689345, '2023-07-19 11:01:00.440762', 1655941826117689345, '2023-07-23 22:51:26.805770', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (105, 1683127629625241600, 'notice-type', '2', '新闻', '系统公告类型', '系统公告中的公告类型', 5, 'orange', 1, NULL, NULL, 1655941826117689345, '2023-07-23 22:51:26.805770', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (106, 1683303597238255616, 'file-status', '0', '未使用', '文件状态', '文件管理中的文件状态', 0, 'red', 1, 1655941826117689345, '2023-07-24 10:30:26.507573', 1655941826117689345, '2023-07-24 10:30:26.507573', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (107, 1683303597238255617, 'file-status', '1', '已使用', '文件状态', '文件管理中的文件状态', 1, 'arcoblue', 1, 1655941826117689345, '2023-07-24 10:30:26.507573', 1655941826117689345, '2023-07-24 10:30:26.507573', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (108, 1683308098322038784, 'file-source', '1', '公告封面图片', '文件来源', '标识文件的用途', 1, 'orange', 1, 1655941826117689345, '2023-07-24 10:48:19.649197', 1655941826117689345, '2023-07-24 10:49:28.171164', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (109, 1683308098322038785, 'file-source', '2', '公告内容图片', '文件来源', '标识文件的用途', 2, 'lime', 1, 1655941826117689345, '2023-07-24 10:48:19.649197', 1655941826117689345, '2023-07-24 10:49:28.171164', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (110, 1683459693981552640, 'file-platform', 'local-1', '本地存储', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 1, '', 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (111, 1683459693981552641, 'file-platform', 'local-plus-1', '本地存储升级版', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 2, '', 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (112, 1683459693981552642, 'file-platform', 'huawei-obs-1', '华为云OBS', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 3, '', 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (113, 1683459693981552643, 'file-platform', 'aliyun-oss-1', '阿里云OSS', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 4, '', 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (114, 1683459693981552644, 'file-platform', 'qiniu-kodo-1', '七牛云KODO', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 5, '', 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (115, 1683459693981552645, 'file-platform', 'tencent-cos-1', '腾讯云COS', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 6, '', 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (116, 1683459693981552646, 'file-platform', 'baidu-bos-1', '百度云BOS', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 7, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (117, 1683459693981552647, 'file-platform', 'upyun-uss-1', '又拍云USS', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 8, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (118, 1683459693981552648, 'file-platform', 'minio-1', 'MINIO', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 9, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (119, 1683459693981552649, 'file-platform', 'aws-s3-1', 'AWS S3', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 10, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (120, 1683459693981552650, 'file-platform', 'ftp-1', 'FTP', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 11, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (121, 1683459693981552651, 'file-platform', 'sftp-1', 'SFTP', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 12, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (122, 1683459693981552652, 'file-platform', 'webdav-1', 'WEBDAV', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 13, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (123, 1683459693981552653, 'file-platform', 'google-1', '谷歌云', '文件存储平台', '上传文件的平台，开源项目：spring-file-storage', 14, NULL, 1, 1655941826117689345, '2023-07-24 20:50:42.870814', 1655941826117689345, '2023-07-24 20:50:42.870814', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (124, 1833704336749133824, 'config-status', '0', '禁用', '参数状态', '参数管理中的参数状态', 0, 'red', 1, NULL, NULL, 0, '2024-09-11 11:09:48.993133', b'1', b'0', 0);
INSERT INTO `m_dict` VALUES (125, 1833704336749133825, 'config-status', '1', '启用', '参数状态', '参数管理中的参数状态', 1, 'arcoblue', 1, NULL, NULL, 0, '2024-09-11 11:09:49.056086', b'1', b'0', 0);

-- ----------------------------
-- Table structure for m_file
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
  `file_platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件存储平台',
  `file_th_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件缩略图url',
  `file_th_filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件缩略图文件名',
  `file_th_size` bigint(0) NULL DEFAULT NULL COMMENT '文件缩略图大小',
  `file_th_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件缩略图文件类型',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户ID',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态  0未使用 1已使用，默认未使用，代码中控制修改为已使用，可以定期清理未使用的文件',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id`) USING BTREE COMMENT '文件ID索引',
  INDEX `idx_file_url`(`file_url`) USING BTREE COMMENT '文件URL索引'
) ENGINE = InnoDB AUTO_INCREMENT = 170 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_notice
-- ----------------------------
DROP TABLE IF EXISTS `m_notice`;
CREATE TABLE `m_notice`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `notice_id` bigint(0) NOT NULL COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` tinyint(0) NOT NULL COMMENT '公告类型（1公告）',
  `notice_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公告内容',
  `notice_pic_file_id` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图文件ID，多个使用 , 分割',
  `notice_top` bit(1) NULL DEFAULT b'0' COMMENT '是否置顶 0否 1是',
  `notice_time_interval` datetime(6) NULL DEFAULT NULL COMMENT '延时发布的时间',
  `notice_sort` int(0) NULL DEFAULT 0 COMMENT '排序',
  `notice_out_chain` bit(1) NULL DEFAULT NULL COMMENT '是否外链 0否 1是',
  `notice_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外部跳转链接',
  `publish_dept_id` bigint(0) NULL DEFAULT NULL COMMENT '发布部门',
  `publish_author_id` bigint(0) NULL DEFAULT NULL COMMENT '发布人',
  `publish_time` datetime(6) NULL DEFAULT NULL COMMENT '发布时间',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_perms
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
  `perm_icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限图标 菜单或目录时可传图标',
  `perm_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'M' COMMENT '权限类型  M菜单 B按钮',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `external_link` bit(1) NULL DEFAULT b'0' COMMENT '是否为外部链接  0否 1是',
  `visible` bit(1) NULL DEFAULT b'1' COMMENT '是否可见 0隐藏 1显示）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `allow_delete` bit(1) NULL DEFAULT b'1' COMMENT '数据是否允许删除 0否 1是',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_perms
-- ----------------------------
INSERT INTO `m_perms` VALUES (6, 1669336412647133184, NULL, '系统管理', 0, 20, NULL, 'IconSettings', 'M', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-06-15 21:29:50.202052', 1655941826117689345, '2023-07-26 21:42:24.834876', b'1', b'0', 4);
INSERT INTO `m_perms` VALUES (7, 1669338708936298496, NULL, '用户管理', 1669336412647133184, 10, '/basic/user/mgt', 'IconUser', 'M', '/basic/user/UserMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-06-15 21:38:57.680970', 1655941826117689345, '2023-07-31 21:29:03.510290', b'1', b'0', 6);
INSERT INTO `m_perms` VALUES (8, 1669339147886989312, NULL, '菜单权限', 1669336412647133184, 30, '/basic/perm/mgt', 'IconNav', 'M', '/basic/perm/PermMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-06-15 21:40:42.334745', 0, '2024-09-13 13:59:01.298846', b'1', b'0', 8);
INSERT INTO `m_perms` VALUES (9, 1669339375922909184, NULL, '字典管理', 1669336412647133184, 50, '/basic/dict/mgt', 'IconDice', 'M', '/basic/dict/DictMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-06-15 21:41:36.702313', 1655941826117689345, '2023-07-31 21:29:40.385520', b'1', b'0', 6);
INSERT INTO `m_perms` VALUES (15, 1673167646340120576, NULL, '角色管理', 1669336412647133184, 20, '/basic/role/mgt', 'IconUserGroup', 'M', '/basic/role/RoleMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-06-26 11:13:47.496133', 1655941826117689345, '2023-07-31 21:29:13.988209', b'1', b'0', 6);
INSERT INTO `m_perms` VALUES (16, 1673524365792530432, NULL, '部门管理', 1669336412647133184, 40, '/basic/dept/mgt', 'IconMindMapping', 'M', '/basic/dept/DeptMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-06-27 10:51:16.041741', 1655941826117689345, '2023-07-31 21:29:27.845462', b'1', b'0', 5);
INSERT INTO `m_perms` VALUES (17, 1673524591861321728, NULL, '岗位管理', 1669336412647133184, 45, '/basic/post/mgt', 'IconBulb', 'M', '/basic/post/PostMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-06-27 10:52:09.940810', 1655941826117689345, '2023-07-31 21:29:33.111096', b'1', b'0', 5);
INSERT INTO `m_perms` VALUES (19, 1673863803093557248, NULL, '开发文档', 0, 10, 'https://www.baidu.com', 'IconBookmark', 'M', NULL, b'1', b'1', '', 1, 1655941826117689345, '2023-06-28 09:20:04.200055', 1655941826117689345, '2023-07-26 21:42:06.019290', b'1', b'0', 4);
INSERT INTO `m_perms` VALUES (20, 1676499330464649216, NULL, '租户维护', 1669336412647133184, 60, NULL, 'IconCloud', 'M', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-05 15:52:42.891799', 1655941826117689345, '2023-07-26 21:53:05.189805', b'1', b'0', 4);
INSERT INTO `m_perms` VALUES (21, 1676499559247155200, NULL, '套餐管理', 1676499330464649216, 10, '/basic/tenantPackage/mgt', 'IconCommon', 'M', '/basic/tenantPackage/TenantPackageMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-07-05 15:53:37.437053', 1655941826117689345, '2023-07-31 21:29:59.129666', b'1', b'0', 5);
INSERT INTO `m_perms` VALUES (22, 1676501907755405312, NULL, '租户管理', 1676499330464649216, 20, '/basic/tenant/mgt', 'IconRelation', 'M', '/basic/tenant/TenantMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-07-05 16:02:57.365341', 1655941826117689345, '2023-07-31 21:30:04.168813', b'1', b'0', 4);
INSERT INTO `m_perms` VALUES (28, 1680749851857862656, 'basic:swagger', '接口文档', 1669336412647133184, 100, '/basic/swagger', 'IconBook', 'M', '/basic/system/Swagger.vue', b'0', b'1', '将swagger页面嵌入到系统', 1, 1655941826117689345, '2023-07-17 09:22:46.162278', 1655941826117689345, '2023-07-26 21:51:49.984333', b'1', b'0', 2);
INSERT INTO `m_perms` VALUES (29, 1681273378598850560, NULL, '系统公告', 1669336412647133184, 60, '/basic/notice/mgt', 'IconSubscribe', 'M', '/basic/notice/NoticeMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-07-18 20:03:04.663282', 1655941826117689345, '2023-07-31 21:30:08.721061', b'1', b'0', 2);
INSERT INTO `m_perms` VALUES (30, 1683285343149096960, NULL, '文件管理', 1669336412647133184, 55, '/basic/file/mgt', 'IconFile', 'M', '/basic/file/FileMgt.vue', b'0', b'1', '', 1, 1655941826117689345, '2023-07-24 09:17:54.393048', 1655941826117689345, '2023-07-31 21:29:48.832685', b'1', b'0', 3);
INSERT INTO `m_perms` VALUES (31, 1685985572129398784, 'basic:user:add', '添加用户', 1669338708936298496, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:07:39.152606', 1655941826117689345, '2023-07-31 20:07:39.152606', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (32, 1685985875515990016, 'basic:user:delete', '删除用户', 1669338708936298496, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:08:51.482954', 1655941826117689345, '2023-07-31 20:08:51.482954', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (33, 1685985977349496832, 'basic:user:update', '修改用户', 1669338708936298496, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:09:15.762162', 1655941826117689345, '2023-07-31 20:09:15.762162', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (34, 1685986276453703680, 'basic:user:get', '查询用户', 1669338708936298496, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:10:27.074176', 1655941826117689345, '2023-07-31 20:10:27.074176', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (35, 1685986566443687936, 'basic:role:add', '添加角色', 1673167646340120576, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:11:36.213812', 1655941826117689345, '2023-07-31 20:11:36.213812', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (36, 1685986702834065408, 'basic:role:delete', '删除角色', 1673167646340120576, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:12:08.733552', 1655941826117689345, '2023-07-31 20:12:08.733552', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (37, 1685986806387236864, 'basic:role:update', '修改角色', 1673167646340120576, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:12:33.419706', 1655941826117689345, '2023-07-31 20:12:33.419706', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (38, 1685986887102423040, 'basic:role:get', '查询角色', 1673167646340120576, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:12:52.664113', 1655941826117689345, '2023-07-31 20:12:52.664113', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (39, 1685987612687654912, 'basic:perm:add', '添加权限', 1669339147886989312, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:15:45.657682', 1655941826117689345, '2023-07-31 20:15:45.657682', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (40, 1685987708229705728, 'basic:perm:delete', '删除权限', 1669339147886989312, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:16:08.439529', 1655941826117689345, '2023-07-31 20:16:08.439529', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (41, 1685988419789185024, 'basic:perm:update', '修改权限', 1669339147886989312, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:18:58.085960', 1655941826117689345, '2023-08-06 10:29:38.265543', b'1', b'0', 1);
INSERT INTO `m_perms` VALUES (42, 1685988623951126528, 'basic:perm:get', '查询权限', 1669339147886989312, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:19:46.761240', 1655941826117689345, '2023-08-06 10:29:55.392998', b'1', b'0', 1);
INSERT INTO `m_perms` VALUES (43, 1685996318275985408, 'basic:dept:add', '添加部门', 1673524365792530432, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:50:21.231736', 1655941826117689345, '2023-07-31 20:50:21.231736', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (44, 1685996426530971648, 'basic:dept:delete', '删除部门', 1673524365792530432, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:50:47.041079', 1655941826117689345, '2023-07-31 20:50:47.041079', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (45, 1685996547448561664, 'basic:dept:update', '修改部门', 1673524365792530432, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:51:15.869518', 1655941826117689345, '2023-07-31 20:51:15.869518', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (46, 1685996623386435584, 'basic:dept:get', '查询部门', 1673524365792530432, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:51:33.974273', 1655941826117689345, '2023-07-31 20:51:33.974273', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (47, 1685996875623489536, 'basic:post:add', '添加岗位', 1673524591861321728, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:52:34.113986', 1655941826117689345, '2023-07-31 20:52:34.113986', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (48, 1685997536914235392, 'basic:post:delete', '删除岗位', 1673524591861321728, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:55:11.777212', 1655941826117689345, '2023-07-31 20:55:11.777212', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (49, 1685997640341577728, 'basic:post:update', '修改岗位', 1673524591861321728, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:55:36.435155', 1655941826117689345, '2023-07-31 20:55:36.435155', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (50, 1685997732956004352, 'basic:post:get', '查询岗位', 1673524591861321728, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:55:58.516359', 1655941826117689345, '2023-07-31 20:55:58.516359', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (51, 1685998041879076864, 'basic:dict:add', '添加字典', 1669339375922909184, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:57:12.169238', 1655941826117689345, '2023-07-31 20:57:12.169238', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (52, 1685998171407572992, 'basic:dict:delete', '删除字典', 1669339375922909184, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:57:43.052637', 1655941826117689345, '2023-07-31 20:57:43.052637', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (53, 1685998244715618304, 'basic:dict:update', '修改字典', 1669339375922909184, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:58:00.530266', 1655941826117689345, '2023-07-31 20:58:00.530266', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (54, 1685998340425441280, 'basic:dict:get', '查询字典', 1669339375922909184, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 20:58:23.349675', 1655941826117689345, '2023-07-31 20:58:23.349675', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (55, 1686001693859569664, 'basic:notice:add', '添加公告', 1681273378598850560, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:11:42.870516', 1655941826117689345, '2023-07-31 21:11:42.870516', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (56, 1686001770980237312, 'basic:notice:delete', '删除公告', 1681273378598850560, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:12:01.257352', 1655941826117689345, '2023-07-31 21:12:01.257352', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (57, 1686002193703165952, 'basic:notice:update', '修改公告', 1681273378598850560, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:13:42.042772', 1655941826117689345, '2023-07-31 21:13:42.042772', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (58, 1686002291338174464, 'basic:notice:get', '查询公告', 1681273378598850560, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:14:05.320984', 1655941826117689345, '2023-07-31 21:14:05.320984', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (59, 1686002657173757952, 'basic:tenantPackage:add', '添加套餐', 1676499559247155200, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:15:32.542913', 1655941826117689345, '2023-07-31 21:15:32.542913', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (60, 1686002750660599808, 'basic:tenantPackage:delete', '删除套餐', 1676499559247155200, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:15:54.831477', 1655941826117689345, '2023-07-31 21:15:54.831477', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (61, 1686002863185387520, 'basic:tenantPackage:update', '修改套餐', 1676499559247155200, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:16:21.658749', 1655941826117689345, '2023-07-31 21:16:21.658749', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (62, 1686002975089418240, 'basic:tenantPackage:get', '查询套餐', 1676499559247155200, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:16:48.338212', 1655941826117689345, '2023-07-31 21:16:48.338212', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (63, 1686004858881368064, 'basic:tenant:add', '添加租户', 1676501907755405312, 10, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:24:17.469065', 1655941826117689345, '2023-07-31 21:24:17.469065', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (64, 1686005292303966208, 'basic:tenant:delete', '删除租户', 1676501907755405312, 20, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:26:00.806007', 1655941826117689345, '2023-07-31 21:26:00.806007', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (65, 1686005483111243776, 'basic:tenant:update', '修改租户', 1676501907755405312, 30, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:26:46.297456', 1655941826117689345, '2023-07-31 21:26:46.297456', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (66, 1686005567790047232, 'basic:tenant:get', '查询租户', 1676501907755405312, 40, NULL, NULL, 'B', NULL, b'0', b'1', '', 1, 1655941826117689345, '2023-07-31 21:27:06.487964', 1655941826117689345, '2023-07-31 21:27:06.487964', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (73, 1833687152849211392, '', '参数配置', 1669336412647133184, 70, '/basic/config/mgt', 'IconSettings', 'M', '/basic/config/ConfigMgt.vue', b'0', b'1', NULL, 1, 0, '2024-09-11 10:01:00.193232', 0, '2024-09-11 10:02:35.271112', b'1', b'0', 5);
INSERT INTO `m_perms` VALUES (74, 1833688664656728064, 'basic:config:add', '添加参数', 1833687152849211392, 10, NULL, NULL, 'B', NULL, b'0', b'1', NULL, 1, 0, '2024-09-11 10:07:00.633835', 0, '2024-09-11 10:07:00.633835', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (75, 1833688838682595328, 'basic:config:delete', '删除参数', 1833687152849211392, 20, NULL, NULL, 'B', NULL, b'0', b'1', NULL, 1, 0, '2024-09-11 10:07:42.123295', 0, '2024-09-11 10:07:42.123295', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (76, 1833688910472302592, 'basic:config:update', '修改参数', 1833687152849211392, 30, NULL, NULL, 'B', NULL, b'0', b'1', NULL, 1, 0, '2024-09-11 10:07:59.239329', 0, '2024-09-11 10:07:59.239329', b'1', b'0', 0);
INSERT INTO `m_perms` VALUES (77, 1833688972682219520, 'basic:config:get', '查询参数', 1833687152849211392, 40, NULL, NULL, 'B', NULL, b'0', b'1', NULL, 1, 0, '2024-09-11 10:08:14.070779', 0, '2024-09-11 10:08:14.070779', b'1', b'0', 0);

-- ----------------------------
-- Table structure for m_post
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
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户编号',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_post
-- ----------------------------
INSERT INTO `m_post` VALUES (1, 1676044813214400512, 'super-admin', '超级管理员', 0, '超级无敌管理员', 1, 0, 1655941826117689345, '2023-07-04 09:46:37.534734', 1655941826117689345, '2023-07-27 23:23:36.287549', b'1', 3);
INSERT INTO `m_post` VALUES (2, 1676046456572071936, '11111111111', '11111111111111', 23, NULL, 1, 0, 1655941826117689345, '2023-07-04 09:53:09.341043', 1655941826117689345, '2023-07-04 09:53:35.392627', b'0', 2);
INSERT INTO `m_post` VALUES (3, 1676046492030717952, 'develop-post', '开发', 20, NULL, 1, 0, 1655941826117689345, '2023-07-04 09:53:17.795741', 1655941826117689345, '2023-07-12 13:55:35.942153', b'1', 3);
INSERT INTO `m_post` VALUES (4, 1679006310662467584, 'sell-post', '销售', 30, NULL, 1, 0, 1655941826117689345, '2023-07-12 13:54:33.547206', 1655941826117689345, '2023-07-12 13:55:38.805938', b'1', 1);
INSERT INTO `m_post` VALUES (5, 1679006401066496000, 'finance-post', '财务', 40, NULL, 1, 0, 1655941826117689345, '2023-07-12 13:54:55.102228', 1655941826117689345, '2023-07-12 13:54:55.102228', b'1', 0);
INSERT INTO `m_post` VALUES (6, 1685180496364257280, 'tester-post', '测试岗', 100, NULL, 1, 0, 1655941826117689345, '2023-07-29 14:48:37.544778', 1655941826117689345, '2023-07-31 22:32:33.193652', b'1', 1);
INSERT INTO `m_post` VALUES (7, 1685181450862055424, 'hr-post', '人力资源', 50, NULL, 1, 0, 1655941826117689345, '2023-07-29 14:52:21.704777', 1655941826117689345, '2023-07-31 22:32:40.815096', b'1', 2);
INSERT INTO `m_post` VALUES (8, 1836060404593487872, 'xiaoshou', '销售1', 1, NULL, 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:11:27.505296', 1836055978990497792, '2024-09-17 23:18:33.795853', b'1', 1);
INSERT INTO `m_post` VALUES (9, 1836060433332858880, 'caiwu', '财务2', 2, NULL, 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:11:34.356145', 1836055978990497792, '2024-09-17 23:18:37.233269', b'1', 1);
INSERT INTO `m_post` VALUES (10, 1836060483333156864, 'dongshizhang', '董事长', 0, NULL, 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:11:46.279929', 1836055978990497792, '2024-09-17 23:11:46.279929', b'1', 0);

-- ----------------------------
-- Table structure for m_role
-- ----------------------------
DROP TABLE IF EXISTS `m_role`;
CREATE TABLE `m_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_sort` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `perm_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '存储角色所关联的权限集合，使用英文逗号分割，此字段只用于角色管理，获取用户权限从角色与权限关联表中获取',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态  0禁用 1正常',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户编号',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `allow_delete` bit(1) NULL DEFAULT b'1' COMMENT '数据是否允许删除 0否 1是',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_role
-- ----------------------------
INSERT INTO `m_role` VALUES (101, 1671337763855073280, '系统管理员', 'system_admin', 0, '1673863803093557248,1683285343149096960,1680749851857862656,1685998041879076864,1685998171407572992,1685998244715618304,1685998340425441280,1685996318275985408,1685996426530971648,1685996547448561664,1685996623386435584,1685996875623489536,1685997536914235392,1685997640341577728,1685997732956004352,1686002657173757952,1686002750660599808,1686002863185387520,1686002975089418240,1686004858881368064,1686005292303966208,1686005483111243776,1686005567790047232,1686001693859569664,1686001770980237312,1686002193703165952,1686002291338174464,1685987612687654912,1685987708229705728,1685988419789185024,1685988623951126528,1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680,1685986566443687936,1685986702834065408,1685986806387236864,1685986887102423040,1833688664656728064,1833688838682595328,1833688910472302592,1833688972682219520', 1, NULL, 0, 1655941826117689345, '2023-06-21 10:02:29.514290', 0, '2024-09-11 10:08:24.345998', b'1', b'0', 32);
INSERT INTO `m_role` VALUES (104, 1671343260918325248, '销售人员', 'sell', 10, '1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680', 1, NULL, 0, 1655941826117689345, '2023-06-21 10:24:20.116648', 1655941826117689345, '2023-08-06 12:52:14.956493', b'1', b'1', 19);
INSERT INTO `m_role` VALUES (105, 1671389807492136960, '财务人员', 'financial', 20, '1673863803093557248', 1, NULL, 0, 1655941826117689345, '2023-06-21 13:29:17.684715', 1655941826117689345, '2023-08-06 12:52:19.880964', b'1', b'1', 7);
INSERT INTO `m_role` VALUES (106, 1677966412497596416, '开发人员', 'developer', 30, '1673863803093557248', 1, NULL, 0, 1655941826117689345, '2023-07-09 17:02:22.504978', 1655941826117689345, '2023-08-06 12:52:25.197098', b'1', b'1', 1);
INSERT INTO `m_role` VALUES (107, 1677966506420645888, '测试人员', 'tester', 40, '1673863803093557248', 1, NULL, 0, 1655941826117689345, '2023-07-09 17:02:44.898094', 1655941826117689345, '2023-08-06 12:52:30.282735', b'1', b'1', 1);
INSERT INTO `m_role` VALUES (108, 1677966630567849984, '运维人员', 'devopser', 50, '1673863803093557248', 1, NULL, 0, 1655941826117689345, '2023-07-09 17:03:14.496421', 1655941826117689345, '2023-08-06 12:52:34.732570', b'1', b'1', 1);
INSERT INTO `m_role` VALUES (109, 1834763883194494977, '管理员', 'admin', 0, '1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680,1685986566443687936,1685986702834065408,1685986806387236864,1685986887102423040,1685996318275985408,1685996426530971648,1685996547448561664,1685996623386435584,1685996875623489536,1685997536914235392,1685997640341577728,1685997732956004352,1683285343149096960,1686002291338174464', 1, '添加租户系统自动创建角色', 1834763883194494976, 0, '2024-09-14 09:19:32.826724', 0, '2024-09-14 09:19:32.826724', b'1', b'0', 0);
INSERT INTO `m_role` VALUES (110, 1834790150900002816, '普通用户', 'putongyonghu', 1, '1683285343149096960', 1, NULL, 1834763883194494976, 1834763884364705792, '2024-09-14 11:03:55.423220', 1834763884364705792, '2024-09-14 11:03:55.423220', b'1', b'1', 0);
INSERT INTO `m_role` VALUES (111, 1836055976973037568, '管理员', 'admin', 0, '1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680,1685986566443687936,1685986702834065408,1685986806387236864,1685986887102423040,1685996318275985408,1685996426530971648,1685996547448561664,1685996623386435584,1685996875623489536,1685997536914235392,1685997640341577728,1685997732956004352,1683285343149096960,1680749851857862656', 1, '添加租户系统自动创建角色', 1836055976968843264, 0, '2024-09-17 22:53:52.221775', 1836055978990497792, '2024-09-17 23:29:36.280452', b'1', b'0', 1);
INSERT INTO `m_role` VALUES (112, 1836056110565814273, '管理员', 'admin', 0, '1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680,1685986566443687936,1685986702834065408,1685986806387236864,1685986887102423040,1685996318275985408,1685996426530971648,1685996547448561664,1685996623386435584,1685996875623489536,1685997536914235392,1685997640341577728,1685997732956004352,1683285343149096960,1686002291338174464', 1, '添加租户系统自动创建角色', 1836056110565814272, 0, '2024-09-17 22:54:23.727486', 0, '2024-09-17 22:54:23.727486', b'1', b'0', 0);
INSERT INTO `m_role` VALUES (113, 1836057975659556864, '测试租户1普通', 'ceshi1-putong', 1, '1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680', 1, NULL, 1836055976968843264, 1836055978990497792, '2024-09-17 23:01:48.394988', 1836055978990497792, '2024-09-17 23:01:48.394988', b'1', b'1', 0);

-- ----------------------------
-- Table structure for m_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `m_role_dept`;
CREATE TABLE `m_role_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id_idx`(`dept_id`) USING BTREE COMMENT '部门ID索引',
  INDEX `role_id_idx`(`role_id`) USING BTREE COMMENT '角色ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与部门关联表 1角色-N部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for m_role_perm
-- ----------------------------
DROP TABLE IF EXISTS `m_role_perm`;
CREATE TABLE `m_role_perm`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `perm_id` bigint(0) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `perm_id_idx`(`perm_id`) USING BTREE COMMENT '权限ID索引',
  INDEX `role_id_idx`(`role_id`) USING BTREE COMMENT '角色ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 832 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与权限关联表 1角色-N权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_role_perm
-- ----------------------------
INSERT INTO `m_role_perm` VALUES (741, 1671343260918325248, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (742, 1671343260918325248, 1685985572129398784);
INSERT INTO `m_role_perm` VALUES (743, 1671343260918325248, 1685985875515990016);
INSERT INTO `m_role_perm` VALUES (744, 1671343260918325248, 1685985977349496832);
INSERT INTO `m_role_perm` VALUES (745, 1671343260918325248, 1685986276453703680);
INSERT INTO `m_role_perm` VALUES (746, 1671343260918325248, 1669338708936298496);
INSERT INTO `m_role_perm` VALUES (747, 1671389807492136960, 1673863803093557248);
INSERT INTO `m_role_perm` VALUES (748, 1677966412497596416, 1673863803093557248);
INSERT INTO `m_role_perm` VALUES (749, 1677966506420645888, 1673863803093557248);
INSERT INTO `m_role_perm` VALUES (750, 1677966630567849984, 1673863803093557248);
INSERT INTO `m_role_perm` VALUES (751, 1671337763855073280, 1673863803093557248);
INSERT INTO `m_role_perm` VALUES (752, 1671337763855073280, 1683285343149096960);
INSERT INTO `m_role_perm` VALUES (753, 1671337763855073280, 1680749851857862656);
INSERT INTO `m_role_perm` VALUES (754, 1671337763855073280, 1685998041879076864);
INSERT INTO `m_role_perm` VALUES (755, 1671337763855073280, 1685998171407572992);
INSERT INTO `m_role_perm` VALUES (756, 1671337763855073280, 1685998244715618304);
INSERT INTO `m_role_perm` VALUES (757, 1671337763855073280, 1685998340425441280);
INSERT INTO `m_role_perm` VALUES (758, 1671337763855073280, 1669339375922909184);
INSERT INTO `m_role_perm` VALUES (759, 1671337763855073280, 1685996318275985408);
INSERT INTO `m_role_perm` VALUES (760, 1671337763855073280, 1685996426530971648);
INSERT INTO `m_role_perm` VALUES (761, 1671337763855073280, 1685996547448561664);
INSERT INTO `m_role_perm` VALUES (762, 1671337763855073280, 1685996623386435584);
INSERT INTO `m_role_perm` VALUES (763, 1671337763855073280, 1673524365792530432);
INSERT INTO `m_role_perm` VALUES (764, 1671337763855073280, 1685996875623489536);
INSERT INTO `m_role_perm` VALUES (765, 1671337763855073280, 1685997536914235392);
INSERT INTO `m_role_perm` VALUES (766, 1671337763855073280, 1685997640341577728);
INSERT INTO `m_role_perm` VALUES (767, 1671337763855073280, 1685997732956004352);
INSERT INTO `m_role_perm` VALUES (768, 1671337763855073280, 1673524591861321728);
INSERT INTO `m_role_perm` VALUES (769, 1671337763855073280, 1686002657173757952);
INSERT INTO `m_role_perm` VALUES (770, 1671337763855073280, 1686002750660599808);
INSERT INTO `m_role_perm` VALUES (771, 1671337763855073280, 1686002863185387520);
INSERT INTO `m_role_perm` VALUES (772, 1671337763855073280, 1686002975089418240);
INSERT INTO `m_role_perm` VALUES (773, 1671337763855073280, 1676499559247155200);
INSERT INTO `m_role_perm` VALUES (774, 1671337763855073280, 1686004858881368064);
INSERT INTO `m_role_perm` VALUES (775, 1671337763855073280, 1686005292303966208);
INSERT INTO `m_role_perm` VALUES (776, 1671337763855073280, 1686005483111243776);
INSERT INTO `m_role_perm` VALUES (777, 1671337763855073280, 1686005567790047232);
INSERT INTO `m_role_perm` VALUES (778, 1671337763855073280, 1676501907755405312);
INSERT INTO `m_role_perm` VALUES (779, 1671337763855073280, 1676499330464649216);
INSERT INTO `m_role_perm` VALUES (780, 1671337763855073280, 1686001693859569664);
INSERT INTO `m_role_perm` VALUES (781, 1671337763855073280, 1686001770980237312);
INSERT INTO `m_role_perm` VALUES (782, 1671337763855073280, 1686002193703165952);
INSERT INTO `m_role_perm` VALUES (783, 1671337763855073280, 1686002291338174464);
INSERT INTO `m_role_perm` VALUES (784, 1671337763855073280, 1681273378598850560);
INSERT INTO `m_role_perm` VALUES (785, 1671337763855073280, 1685987612687654912);
INSERT INTO `m_role_perm` VALUES (786, 1671337763855073280, 1685987708229705728);
INSERT INTO `m_role_perm` VALUES (787, 1671337763855073280, 1685988419789185024);
INSERT INTO `m_role_perm` VALUES (788, 1671337763855073280, 1685988623951126528);
INSERT INTO `m_role_perm` VALUES (789, 1671337763855073280, 1669339147886989312);
INSERT INTO `m_role_perm` VALUES (790, 1671337763855073280, 1685985572129398784);
INSERT INTO `m_role_perm` VALUES (791, 1671337763855073280, 1685985875515990016);
INSERT INTO `m_role_perm` VALUES (792, 1671337763855073280, 1685985977349496832);
INSERT INTO `m_role_perm` VALUES (793, 1671337763855073280, 1685986276453703680);
INSERT INTO `m_role_perm` VALUES (794, 1671337763855073280, 1669338708936298496);
INSERT INTO `m_role_perm` VALUES (795, 1671337763855073280, 1685986566443687936);
INSERT INTO `m_role_perm` VALUES (796, 1671337763855073280, 1685986702834065408);
INSERT INTO `m_role_perm` VALUES (797, 1671337763855073280, 1685986806387236864);
INSERT INTO `m_role_perm` VALUES (798, 1671337763855073280, 1685986887102423040);
INSERT INTO `m_role_perm` VALUES (799, 1671337763855073280, 1673167646340120576);
INSERT INTO `m_role_perm` VALUES (800, 1671337763855073280, 1833687152849211392);
INSERT INTO `m_role_perm` VALUES (801, 1671337763855073280, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (802, 1671337763855073280, 1833688664656728064);
INSERT INTO `m_role_perm` VALUES (803, 1671337763855073280, 1833688838682595328);
INSERT INTO `m_role_perm` VALUES (804, 1671337763855073280, 1833688910472302592);
INSERT INTO `m_role_perm` VALUES (805, 1671337763855073280, 1833688972682219520);
INSERT INTO `m_role_perm` VALUES (830, 1834790150900002816, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (880, 1836057975659556864, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (881, 1836057975659556864, 1669338708936298496);
INSERT INTO `m_role_perm` VALUES (882, 1836057975659556864, 1685985572129398784);
INSERT INTO `m_role_perm` VALUES (883, 1836057975659556864, 1685985875515990016);
INSERT INTO `m_role_perm` VALUES (884, 1836057975659556864, 1685985977349496832);
INSERT INTO `m_role_perm` VALUES (885, 1836057975659556864, 1685986276453703680);
INSERT INTO `m_role_perm` VALUES (1041, 1834763883194494977, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (1042, 1834763883194494977, 1685985572129398784);
INSERT INTO `m_role_perm` VALUES (1043, 1834763883194494977, 1685985875515990016);
INSERT INTO `m_role_perm` VALUES (1044, 1834763883194494977, 1685985977349496832);
INSERT INTO `m_role_perm` VALUES (1045, 1834763883194494977, 1685986276453703680);
INSERT INTO `m_role_perm` VALUES (1046, 1834763883194494977, 1669338708936298496);
INSERT INTO `m_role_perm` VALUES (1047, 1834763883194494977, 1685986566443687936);
INSERT INTO `m_role_perm` VALUES (1048, 1834763883194494977, 1685986702834065408);
INSERT INTO `m_role_perm` VALUES (1049, 1834763883194494977, 1685986806387236864);
INSERT INTO `m_role_perm` VALUES (1050, 1834763883194494977, 1685986887102423040);
INSERT INTO `m_role_perm` VALUES (1051, 1834763883194494977, 1673167646340120576);
INSERT INTO `m_role_perm` VALUES (1052, 1834763883194494977, 1685996318275985408);
INSERT INTO `m_role_perm` VALUES (1053, 1834763883194494977, 1685996426530971648);
INSERT INTO `m_role_perm` VALUES (1054, 1834763883194494977, 1685996547448561664);
INSERT INTO `m_role_perm` VALUES (1055, 1834763883194494977, 1685996623386435584);
INSERT INTO `m_role_perm` VALUES (1056, 1834763883194494977, 1673524365792530432);
INSERT INTO `m_role_perm` VALUES (1057, 1834763883194494977, 1685996875623489536);
INSERT INTO `m_role_perm` VALUES (1058, 1834763883194494977, 1685997536914235392);
INSERT INTO `m_role_perm` VALUES (1059, 1834763883194494977, 1685997640341577728);
INSERT INTO `m_role_perm` VALUES (1060, 1834763883194494977, 1685997732956004352);
INSERT INTO `m_role_perm` VALUES (1061, 1834763883194494977, 1673524591861321728);
INSERT INTO `m_role_perm` VALUES (1062, 1836055976973037568, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (1063, 1836055976973037568, 1685985572129398784);
INSERT INTO `m_role_perm` VALUES (1064, 1836055976973037568, 1685985875515990016);
INSERT INTO `m_role_perm` VALUES (1065, 1836055976973037568, 1685985977349496832);
INSERT INTO `m_role_perm` VALUES (1066, 1836055976973037568, 1685986276453703680);
INSERT INTO `m_role_perm` VALUES (1067, 1836055976973037568, 1669338708936298496);
INSERT INTO `m_role_perm` VALUES (1068, 1836055976973037568, 1685986566443687936);
INSERT INTO `m_role_perm` VALUES (1069, 1836055976973037568, 1685986702834065408);
INSERT INTO `m_role_perm` VALUES (1070, 1836055976973037568, 1685986806387236864);
INSERT INTO `m_role_perm` VALUES (1071, 1836055976973037568, 1685986887102423040);
INSERT INTO `m_role_perm` VALUES (1072, 1836055976973037568, 1673167646340120576);
INSERT INTO `m_role_perm` VALUES (1073, 1836055976973037568, 1685996318275985408);
INSERT INTO `m_role_perm` VALUES (1074, 1836055976973037568, 1685996426530971648);
INSERT INTO `m_role_perm` VALUES (1075, 1836055976973037568, 1685996547448561664);
INSERT INTO `m_role_perm` VALUES (1076, 1836055976973037568, 1685996623386435584);
INSERT INTO `m_role_perm` VALUES (1077, 1836055976973037568, 1673524365792530432);
INSERT INTO `m_role_perm` VALUES (1078, 1836055976973037568, 1685996875623489536);
INSERT INTO `m_role_perm` VALUES (1079, 1836055976973037568, 1685997536914235392);
INSERT INTO `m_role_perm` VALUES (1080, 1836055976973037568, 1685997640341577728);
INSERT INTO `m_role_perm` VALUES (1081, 1836055976973037568, 1685997732956004352);
INSERT INTO `m_role_perm` VALUES (1082, 1836055976973037568, 1673524591861321728);
INSERT INTO `m_role_perm` VALUES (1083, 1836056110565814273, 1669336412647133184);
INSERT INTO `m_role_perm` VALUES (1084, 1836056110565814273, 1685985572129398784);
INSERT INTO `m_role_perm` VALUES (1085, 1836056110565814273, 1685985875515990016);
INSERT INTO `m_role_perm` VALUES (1086, 1836056110565814273, 1685985977349496832);
INSERT INTO `m_role_perm` VALUES (1087, 1836056110565814273, 1685986276453703680);
INSERT INTO `m_role_perm` VALUES (1088, 1836056110565814273, 1669338708936298496);
INSERT INTO `m_role_perm` VALUES (1089, 1836056110565814273, 1685986566443687936);
INSERT INTO `m_role_perm` VALUES (1090, 1836056110565814273, 1685986702834065408);
INSERT INTO `m_role_perm` VALUES (1091, 1836056110565814273, 1685986806387236864);
INSERT INTO `m_role_perm` VALUES (1092, 1836056110565814273, 1685986887102423040);
INSERT INTO `m_role_perm` VALUES (1093, 1836056110565814273, 1673167646340120576);
INSERT INTO `m_role_perm` VALUES (1094, 1836056110565814273, 1685996318275985408);
INSERT INTO `m_role_perm` VALUES (1095, 1836056110565814273, 1685996426530971648);
INSERT INTO `m_role_perm` VALUES (1096, 1836056110565814273, 1685996547448561664);
INSERT INTO `m_role_perm` VALUES (1097, 1836056110565814273, 1685996623386435584);
INSERT INTO `m_role_perm` VALUES (1098, 1836056110565814273, 1673524365792530432);
INSERT INTO `m_role_perm` VALUES (1099, 1836056110565814273, 1685996875623489536);
INSERT INTO `m_role_perm` VALUES (1100, 1836056110565814273, 1685997536914235392);
INSERT INTO `m_role_perm` VALUES (1101, 1836056110565814273, 1685997640341577728);
INSERT INTO `m_role_perm` VALUES (1102, 1836056110565814273, 1685997732956004352);
INSERT INTO `m_role_perm` VALUES (1103, 1836056110565814273, 1673524591861321728);

-- ----------------------------
-- Table structure for m_tenant
-- ----------------------------
DROP TABLE IF EXISTS `m_tenant`;
CREATE TABLE `m_tenant`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(0) NOT NULL COMMENT '租户ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `package_id` bigint(0) NOT NULL COMMENT '租户套餐ID',
  `tenant_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户名',
  `expire_time` datetime(6) NOT NULL COMMENT '租户过期时间',
  `account_count` int(0) NOT NULL DEFAULT 0 COMMENT '可创建账号数量',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_id` bigint(0) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除 0已删除 1未删除',
  `allow_delete` bit(1) NULL DEFAULT b'1' COMMENT '数据是否允许删除 0否 1是',
  `version` int(0) NULL DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_tenant
-- ----------------------------
INSERT INTO `m_tenant` VALUES (1, 0, 0, 0, '系统租户', '2030-12-31 23:59:59.000000', 20, 1, NULL, 1655941826117689345, '2023-07-08 14:35:22.000000', 0, '2024-09-12 14:21:32.569325', b'1', b'0', NULL);
INSERT INTO `m_tenant` VALUES (3, 1834763883194494976, 1834763884364705792, 1676850220841287680, '东东胡辣汤', '2024-09-30 09:15:21.000000', 2, 1, NULL, 0, '2024-09-14 09:19:33.195353', 0, '2024-09-14 09:19:33.195353', b'1', b'1', NULL);
INSERT INTO `m_tenant` VALUES (4, 1836055976968843264, 1836055978990497792, 1676850220841287680, '测试租户111', '2024-09-30 22:38:09.000000', 3, 1, NULL, 0, '2024-09-17 22:53:52.473378', 0, '2024-09-17 22:53:52.473378', b'1', b'1', NULL);
INSERT INTO `m_tenant` VALUES (5, 1836056110565814272, 1836056110997827584, 1676850220841287680, '测试租户222', '2024-09-30 22:54:04.000000', 2, 1, NULL, 0, '2024-09-17 22:54:23.953678', 0, '2024-09-17 22:54:23.953678', b'1', b'1', NULL);

-- ----------------------------
-- Table structure for m_tenant_package
-- ----------------------------
DROP TABLE IF EXISTS `m_tenant_package`;
CREATE TABLE `m_tenant_package`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `package_id` bigint(0) NOT NULL COMMENT '套餐ID',
  `package_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '套餐名称',
  `perm_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储套餐所关联的权限集合，使用英文逗号分割，此字段只用于套餐管理，获取租户套餐从套餐与权限关联表中获取',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_id` bigint(0) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除 0已删除 1未删除',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_package_id`(`package_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户套餐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_tenant_package
-- ----------------------------
INSERT INTO `m_tenant_package` VALUES (5, 1676850220841287680, '基础套餐', '1685985572129398784,1685985875515990016,1685985977349496832,1685986276453703680,1685986566443687936,1685986702834065408,1685986806387236864,1685986887102423040,1685996318275985408,1685996426530971648,1685996547448561664,1685996623386435584,1685996875623489536,1685997536914235392,1685997640341577728,1685997732956004352', 1, '基础套餐', 1655941826117689345, '2023-07-06 15:07:01.676599', 0, '2024-09-17 23:50:31.697911', b'1', 17);

-- ----------------------------
-- Table structure for m_tenant_package_perm
-- ----------------------------
DROP TABLE IF EXISTS `m_tenant_package_perm`;
CREATE TABLE `m_tenant_package_perm`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `package_id` bigint(0) NOT NULL COMMENT '租户套餐ID',
  `perm_id` bigint(0) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 332 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户套餐与权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_tenant_package_perm
-- ----------------------------
INSERT INTO `m_tenant_package_perm` VALUES (555, 1676850220841287680, 1669336412647133184);
INSERT INTO `m_tenant_package_perm` VALUES (556, 1676850220841287680, 1685985572129398784);
INSERT INTO `m_tenant_package_perm` VALUES (557, 1676850220841287680, 1685985875515990016);
INSERT INTO `m_tenant_package_perm` VALUES (558, 1676850220841287680, 1685985977349496832);
INSERT INTO `m_tenant_package_perm` VALUES (559, 1676850220841287680, 1685986276453703680);
INSERT INTO `m_tenant_package_perm` VALUES (560, 1676850220841287680, 1669338708936298496);
INSERT INTO `m_tenant_package_perm` VALUES (561, 1676850220841287680, 1685986566443687936);
INSERT INTO `m_tenant_package_perm` VALUES (562, 1676850220841287680, 1685986702834065408);
INSERT INTO `m_tenant_package_perm` VALUES (563, 1676850220841287680, 1685986806387236864);
INSERT INTO `m_tenant_package_perm` VALUES (564, 1676850220841287680, 1685986887102423040);
INSERT INTO `m_tenant_package_perm` VALUES (565, 1676850220841287680, 1673167646340120576);
INSERT INTO `m_tenant_package_perm` VALUES (566, 1676850220841287680, 1685996318275985408);
INSERT INTO `m_tenant_package_perm` VALUES (567, 1676850220841287680, 1685996426530971648);
INSERT INTO `m_tenant_package_perm` VALUES (568, 1676850220841287680, 1685996547448561664);
INSERT INTO `m_tenant_package_perm` VALUES (569, 1676850220841287680, 1685996623386435584);
INSERT INTO `m_tenant_package_perm` VALUES (570, 1676850220841287680, 1673524365792530432);
INSERT INTO `m_tenant_package_perm` VALUES (571, 1676850220841287680, 1685996875623489536);
INSERT INTO `m_tenant_package_perm` VALUES (572, 1676850220841287680, 1685997536914235392);
INSERT INTO `m_tenant_package_perm` VALUES (573, 1676850220841287680, 1685997640341577728);
INSERT INTO `m_tenant_package_perm` VALUES (574, 1676850220841287680, 1685997732956004352);
INSERT INTO `m_tenant_package_perm` VALUES (575, 1676850220841287680, 1673524591861321728);

-- ----------------------------
-- Table structure for m_user
-- ----------------------------
DROP TABLE IF EXISTS `m_user`;
CREATE TABLE `m_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `dept_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户所在部门ID集合，用于回显',
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
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户编号',
  `create_id` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'1' COMMENT '逻辑删除  0已删除  1未删除',
  `allow_delete` bit(1) NULL DEFAULT b'1' COMMENT '数据是否允许删除 0否 1是',
  `version` int(0) NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_unq`(`username`) USING BTREE COMMENT '用户名唯一索引',
  INDEX `user_id_idx`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_user
-- ----------------------------
INSERT INTO `m_user` VALUES (1, 0, '1677964682431082496,1677964531410972672,1686349179535036416,1686349332929122304', 'admin', '83fa7c38a4fcd74b1916682315137d67', 'admin', '无敌的我', '小太阳', '438562332@qq.com', '15011384542', 1, 'data:image/jpeg;base64,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', '232332323', 1, 0, 0, '2023-05-09 22:25:26.000000', 1655941826117689345, '2023-08-06 16:18:22.936302', b'1', b'0', 20);
INSERT INTO `m_user` VALUES (3, 1834763884364705792, '', 'dongdong', '70fd00a1490168dd76cb99320f551007', '8gb1qw', '东东', '东东', '11111111111', '11111111111', 1, NULL, NULL, 1, 1834763883194494976, 0, '2024-09-14 09:19:33.047847', 1834763884364705792, '2024-09-14 09:53:37.774605', b'1', b'0', 2);
INSERT INTO `m_user` VALUES (4, 1834817021968625664, '1834774513448329216', 'dongdong1', '3cd3cdce58ec356292051eae88030318', 'uwgrap', '东东1号', '东东1号', '111', '111', 1, NULL, '1', 1, 1834763883194494976, 1834763884364705792, '2024-09-14 12:50:42.041340', 1834763884364705792, '2024-09-14 12:50:42.041340', b'1', b'1', 0);
INSERT INTO `m_user` VALUES (5, 1836055978990497792, '1836060210875363328,1836060257859956736', 'ceshi111', '41e43c75e3426adb407c05000f6f8501', 'et1t8b', '测试租户111', '测试租户111', '111', '111', 1, NULL, NULL, 1, 1836055976968843264, 0, '2024-09-17 22:53:52.392398', 1836055978990497792, '2024-09-17 23:17:41.221513', b'1', b'0', 1);
INSERT INTO `m_user` VALUES (6, 1836056110997827584, NULL, 'ceshi222', 'c709119dd102a77fe92b972578ac226f', '5gxkja', '测试租户222', '测试租户222', '111', '111', 1, NULL, NULL, 1, 1836056110565814272, 0, '2024-09-17 22:54:23.866476', 0, '2024-09-17 22:54:23.866476', b'1', b'0', 0);
INSERT INTO `m_user` VALUES (7, 1836059921829097472, '1836060210875363328', 'ceshi111-1', '2dcc424ea713c122ae0dcf10ff90406f', 'sm74mg', 'ceshi111-1', '111', 'ceshi111-1', 'ceshi111-1', 1, NULL, NULL, 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:09:32.433005', 1836055978990497792, '2024-09-17 23:18:01.576329', b'1', b'1', 1);
INSERT INTO `m_user` VALUES (8, 1836060007078326272, '1836060257859956736', 'ceshi111-2', '8ffd827af99a14c8802e54ae1df6a5f2', '2l9538', 'ceshi111-2', 'ceshi111-2', 'ceshi111-2', 'ceshi111-2', 1, NULL, NULL, 1, 1836055976968843264, 1836055978990497792, '2024-09-17 23:09:52.749490', 1836055978990497792, '2024-09-17 23:18:09.068931', b'1', b'1', 1);

-- ----------------------------
-- Table structure for m_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `m_user_dept`;
CREATE TABLE `m_user_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE COMMENT '部门ID索引',
  INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_user_dept
-- ----------------------------
INSERT INTO `m_user_dept` VALUES (19, 1679006018092986368, 1677964231673425920);
INSERT INTO `m_user_dept` VALUES (20, 1679006018092986368, 1677964029214371840);
INSERT INTO `m_user_dept` VALUES (21, 1679006018092986368, 1686021621496999936);
INSERT INTO `m_user_dept` VALUES (22, 1679006018092986368, 1677964435873116160);
INSERT INTO `m_user_dept` VALUES (23, 0, 1677964682431082496);
INSERT INTO `m_user_dept` VALUES (24, 0, 1677964531410972672);
INSERT INTO `m_user_dept` VALUES (25, 0, 1686349179535036416);
INSERT INTO `m_user_dept` VALUES (26, 0, 1677964231673425920);
INSERT INTO `m_user_dept` VALUES (27, 0, 1677964029214371840);
INSERT INTO `m_user_dept` VALUES (28, 0, 1686349332929122304);
INSERT INTO `m_user_dept` VALUES (29, 0, 1686021776396840960);
INSERT INTO `m_user_dept` VALUES (30, 1834817021968625664, 1834774513448329216);
INSERT INTO `m_user_dept` VALUES (31, 1834817021968625664, 1834773502369406976);
INSERT INTO `m_user_dept` VALUES (32, 1836055978990497792, 1836060155556687872);
INSERT INTO `m_user_dept` VALUES (33, 1836055978990497792, 1836060210875363328);
INSERT INTO `m_user_dept` VALUES (34, 1836055978990497792, 1836060257859956736);
INSERT INTO `m_user_dept` VALUES (35, 1836059921829097472, 1836060155556687872);
INSERT INTO `m_user_dept` VALUES (36, 1836059921829097472, 1836060210875363328);
INSERT INTO `m_user_dept` VALUES (37, 1836060007078326272, 1836060155556687872);
INSERT INTO `m_user_dept` VALUES (38, 1836060007078326272, 1836060257859956736);

-- ----------------------------
-- Table structure for m_user_post
-- ----------------------------
DROP TABLE IF EXISTS `m_user_post`;
CREATE TABLE `m_user_post`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `post_id` bigint(0) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `post_id_idx`(`post_id`) USING BTREE COMMENT '岗位ID索引',
  INDEX `user_id_idx`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表 1用户-N岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_user_post
-- ----------------------------
INSERT INTO `m_user_post` VALUES (14, 1679006018092986368, 1676046492030717952);
INSERT INTO `m_user_post` VALUES (15, 0, 1676046492030717952);
INSERT INTO `m_user_post` VALUES (16, 0, 1676044813214400512);
INSERT INTO `m_user_post` VALUES (17, 1836055978990497792, 1836060483333156864);
INSERT INTO `m_user_post` VALUES (18, 1836059921829097472, 1836060404593487872);
INSERT INTO `m_user_post` VALUES (19, 1836060007078326272, 1836060433332858880);

-- ----------------------------
-- Table structure for m_user_role
-- ----------------------------
DROP TABLE IF EXISTS `m_user_role`;
CREATE TABLE `m_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role_id_idx`(`role_id`) USING BTREE COMMENT '角色ID索引',
  INDEX `user_id_idx`(`user_id`) USING BTREE COMMENT '用户ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与角色关联表 1用户-N角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_user_role
-- ----------------------------
INSERT INTO `m_user_role` VALUES (9, 1679006018092986368, 1677966412497596416);
INSERT INTO `m_user_role` VALUES (10, 0, 1671337763855073280);
INSERT INTO `m_user_role` VALUES (13, 1834763884364705792, 1834763883194494977);
INSERT INTO `m_user_role` VALUES (14, 1834817021968625664, 1834790150900002816);
INSERT INTO `m_user_role` VALUES (16, 1836056110997827584, 1836056110565814273);
INSERT INTO `m_user_role` VALUES (19, 1836055978990497792, 1836055976973037568);
INSERT INTO `m_user_role` VALUES (20, 1836059921829097472, 1836057975659556864);
INSERT INTO `m_user_role` VALUES (21, 1836060007078326272, 1836057975659556864);

SET FOREIGN_KEY_CHECKS = 1;
