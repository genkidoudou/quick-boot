CREATE TABLE `msg_inbox` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `user_id` bigint NOT NULL COMMENT '收件用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `read_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '已读(0未读 1已读)',
  `channel_id` bigint DEFAULT NULL COMMENT '来源渠道id',
  `record_id` bigint DEFAULT NULL COMMENT '发送记录id',
  PRIMARY KEY (`id`),
  KEY `idx_msg_inbox_user_read` (`user_id`,`read_flag`),
  KEY `idx_msg_inbox_time` (`create_time`),
  KEY `idx_msg_inbox_record` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信收件箱';
