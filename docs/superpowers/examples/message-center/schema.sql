-- 示例 DDL，对齐 docs/superpowers/specs/2026-10-27-message-center-design.md
-- 不参与 Flyway。落地时再同步到 sdd/ddl 与 quickboot-web 的迁移脚本。

CREATE TABLE `msg_channel` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通道ID，UUID，保存时生成',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '列表摘要，不含密钥',
  `channel_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通道名称',
  `channel_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通道类型(MSG_CHANNEL_TYPE)',
  `channel_config` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型配置 JSON',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通道';

CREATE TABLE `msg_send` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '触发人，接口调用可空',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文',
  `source` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源(MSG_SEND_SOURCE)',
  `biz_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务追溯类型',
  `biz_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务追溯单号，不唯一',
  `biz_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '站内信点击地址',
  `receivers_json` text COLLATE utf8mb4_unicode_ci COMMENT '接口传入的接收人原文，空表示未传',
  `status` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送单状态(MSG_SEND_STATUS)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送单';

CREATE TABLE `msg_send_detail` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `send_id` bigint NOT NULL COMMENT '发送单id',
  `channel_id` char(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通道ID，通道不存在时为空',
  `channel_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通道名称快照',
  `channel_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通道类型快照',
  `target_digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标摘要，已脱敏',
  `audience` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '站内信接收人来源：CHANNEL 或 REQUEST',
  `status` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '明细状态：SUCCESS、FAIL、SKIPPED',
  `error_msg` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败或跳过原因',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `cost_ms` bigint DEFAULT NULL COMMENT '耗时',
  PRIMARY KEY (`id`),
  KEY `idx_msg_send_detail_send_id` (`send_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送明细';

CREATE TABLE `msg_inbox` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `send_id` bigint NOT NULL COMMENT '发送单id',
  `channel_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通道ID',
  `user_id` bigint NOT NULL COMMENT '接收用户id',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题快照',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文快照',
  `biz_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务地址快照',
  `read_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '0未读 1已读',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  PRIMARY KEY (`id`),
  KEY `idx_msg_inbox_user_read` (`user_id`, `read_flag`),
  KEY `idx_msg_inbox_send_id` (`send_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信';
