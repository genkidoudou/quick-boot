CREATE TABLE `sys_api_key` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `user_id` bigint NOT NULL COMMENT '归属用户id(sys_user.id)',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Key 名称（如个人脚本、本机工具）',
  `api_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密钥（一期明文；生产建议改哈希）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间，空=不过期',
  `status` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_api_key` (`api_key`),
  KEY `idx_sys_api_key_user` (`user_id`),
  KEY `idx_sys_api_key_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户API Key';
