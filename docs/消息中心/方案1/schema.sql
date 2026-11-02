-- ============================================================
-- 统一发送 · 方案 1
-- 一次调用一条通道。收件人是最终地址，或一个同类型群组。
-- 发送层不查用户、部门、外部联系人。回执返回给调用方，不另建发送记录表。
-- 状态沿用 COMMON_STATUS：0 启用，1 停用。
-- ============================================================

DROP TABLE IF EXISTS `msg_inbox`;
DROP TABLE IF EXISTS `msg_group_address`;
DROP TABLE IF EXISTS `msg_group`;
DROP TABLE IF EXISTS `msg_channel`;

-- 通道：一条已配置的管道。密钥和机器人地址在这里，收件人不在这里。
CREATE TABLE `msg_channel` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `name` varchar(255) NOT NULL COMMENT '通道名称',
  `channel_type` varchar(32) NOT NULL COMMENT 'EMAIL/SMS/INBOX/WECOM_APP/DINGTALK_ROBOT/WECOM_ROBOT/IYUU',
  `status` varchar(3) NOT NULL DEFAULT '0' COMMENT '状态(COMMON_STATUS:0启用,1停用)',
  `smtp_host` varchar(255) DEFAULT NULL COMMENT '邮件 SMTP 主机',
  `smtp_port` int DEFAULT NULL COMMENT '邮件 SMTP 端口',
  `smtp_username` varchar(255) DEFAULT NULL COMMENT '邮件 SMTP 账号',
  `smtp_password` varchar(255) DEFAULT NULL COMMENT '邮件 SMTP 密码',
  `mail_from` varchar(255) DEFAULT NULL COMMENT '邮件发件人',
  `sms_access_key` varchar(255) DEFAULT NULL COMMENT '短信访问密钥',
  `sms_sign` varchar(64) DEFAULT NULL COMMENT '短信签名',
  `sms_endpoint` varchar(512) DEFAULT NULL COMMENT '短信接口地址',
  `wecom_corp_id` varchar(128) DEFAULT NULL COMMENT '企业微信企业 ID',
  `wecom_agent_id` varchar(64) DEFAULT NULL COMMENT '企业微信应用 ID',
  `wecom_secret` varchar(255) DEFAULT NULL COMMENT '企业微信应用密钥',
  `webhook_url` varchar(512) DEFAULT NULL COMMENT '机器人 Webhook 地址',
  `webhook_secret` varchar(255) DEFAULT NULL COMMENT '钉钉机器人加签密钥',
  `iyuu_token` varchar(255) DEFAULT NULL COMMENT 'IYUU 令牌',
  PRIMARY KEY (`id`),
  KEY `idx_msg_channel_type` (`channel_type`),
  KEY `idx_msg_channel_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一发送通道';

-- 群组：一份已经解析好的地址名单，只属于一种通道类型，不绑到某一条通道。
-- 机器人没有群组。
CREATE TABLE `msg_group` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `name` varchar(255) NOT NULL COMMENT '群组名称',
  `channel_type` varchar(32) NOT NULL COMMENT 'EMAIL/SMS/INBOX/WECOM_APP',
  PRIMARY KEY (`id`),
  KEY `idx_msg_group_type` (`channel_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一发送群组';

-- 群组地址。允许重复，发送时按 sort_no 各投递一次。
CREATE TABLE `msg_group_address` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `address` varchar(255) NOT NULL COMMENT '最终地址：邮箱、手机号、用户 ID 或企业微信 ID',
  `sort_no` int NOT NULL COMMENT '投递顺序，从 1 开始',
  PRIMARY KEY (`id`),
  KEY `idx_msg_group_address_order` (`group_id`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一发送群组地址';

-- 站内信是唯一会落库的投递结果。邮件、短信、机器人和企业微信应用只返回回执。
CREATE TABLE `msg_inbox` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `channel_id` bigint NOT NULL COMMENT '来源通道id',
  `user_address` varchar(64) NOT NULL COMMENT '调用方传入的用户 ID，不核对该用户是否存在',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '正文',
  `link` varchar(512) DEFAULT NULL COMMENT '链接，未传则为空',
  PRIMARY KEY (`id`),
  KEY `idx_msg_inbox_user` (`user_address`),
  KEY `idx_msg_inbox_channel` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信';
