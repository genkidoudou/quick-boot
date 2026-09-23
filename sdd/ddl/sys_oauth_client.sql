CREATE TABLE `sys_oauth_client` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `client_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端id',
  `client_secret` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端密钥',
  `client_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端名称',
  `api_path_patterns` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '/**' COMMENT '允许访问的接口',
  `token_timeout` int DEFAULT NULL COMMENT 'token有效时间',
  `check_captcha` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '是否校验验证码(COMMON_IS)',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户端';
