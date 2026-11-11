CREATE TABLE `ins_claim` (
  `id` bigint NOT NULL COMMENT '理赔ID',
  `claim_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '理赔单号',
  `policy_id` bigint DEFAULT NULL COMMENT '保单ID',
  `policy_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '保单号',
  `cust_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户姓名',
  `product_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '险种类型',
  `report_date` date DEFAULT NULL COMMENT '报案日期',
  `claim_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '理赔类型（医疗费用/身故赔付/车辆损失/意外伤害/重疾给付）',
  `claim_amount` decimal(12,2) DEFAULT NULL COMMENT '申请理赔金额（元）',
  `settle_amount` decimal(12,2) DEFAULT NULL COMMENT '实际赔付金额（元）',
  `status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '理赔状态（待审核/已赔付/已拒赔）',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '出险省份',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='保险理赔表';
