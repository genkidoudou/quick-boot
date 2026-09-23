CREATE TABLE `sys_logininfor` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `user_id` bigint NOT NULL COMMENT '登录的用户id',
  `user_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录的用户名',
  `client_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的客户端id',
  `ip_addr` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的ip',
  `browser` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器类型',
  `login_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的地址',
  `os` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作系统',
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录状态(SYS_LOGIN_STATUS)',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录访问日志表';
