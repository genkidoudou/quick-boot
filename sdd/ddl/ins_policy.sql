CREATE TABLE `ins_policy` (
  `id` bigint NOT NULL COMMENT '保单ID',
  `policy_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '保单号',
  `cust_id` bigint DEFAULT NULL COMMENT '客户ID',
  `cust_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户姓名',
  `product_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '险种名称',
  `product_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '险种类型（寿险/健康险/车险/意外险/财产险）',
  `channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '销售渠道（线上直销/代理人/银保/电销/经纪）',
  `premium` decimal(12,2) DEFAULT NULL COMMENT '年保费（元）',
  `coverage_amount` decimal(14,2) DEFAULT NULL COMMENT '保额（元）',
  `term_years` int DEFAULT NULL COMMENT '保障年限',
  `sign_date` date DEFAULT NULL COMMENT '投保日期',
  `effect_date` date DEFAULT NULL COMMENT '生效日期',
  `expire_date` date DEFAULT NULL COMMENT '到期日期',
  `status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '保单状态（生效/已过期/退保）',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出单省份',
  `region` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '大区',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='保险保单表';
