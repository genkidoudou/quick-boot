CREATE TABLE `chat2bi_domain` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据域名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `datasource_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '绑定数据源id（jimu_report_data_source.id，空=应用默认库）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态 enabled/启用,disabled/停用',
  `sort_no` int DEFAULT NULL COMMENT '排序号',
  `joins_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '表关联JSON [{id,left,right,leftCol,rightCol,type}]',
  `perm_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '权限/安全护栏配置JSON {grantRoles,grantUsers,sensWords,sensCats,rowRules,colRules,sensitivity,isPublic}',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` int DEFAULT '0' COMMENT '多租户标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Chat2BI 数据域';
