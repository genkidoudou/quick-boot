CREATE TABLE `chat2bi_table` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `domain_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属数据域id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名/英文标识',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '显示名称',
  `mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型 table/数据库表,sql/SQL虚拟表',
  `sql_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'SQL虚拟表定义（mode=sql时）',
  `columns_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '列定义JSON [{col,type,agg,alias,enabled}]',
  `sort_no` int DEFAULT NULL COMMENT '排序号',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` int DEFAULT '0' COMMENT '多租户标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_c2b_table_domain` (`domain_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Chat2BI 注册表';
