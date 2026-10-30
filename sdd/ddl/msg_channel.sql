CREATE TABLE `msg_channel` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `channel_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道类型(CHANNEL_TYPE)',
  `content_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容类型(CONTENT_TYPE)',
  `template_body` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板内容',
  `webhook_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'WEBHOOK url',
  `bean_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'bean名称',
  `token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '访问token',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息渠道表';
