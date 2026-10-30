-- ============================================================
-- 消息中心 · 经典后台（方案 A）
-- 渠道 INBOX/WEBHOOK/BEAN；群组=用户+部门+内嵌外部人
-- 开放调用：用户级 sys_api_key（非 OAuth 客户端）；消息渠道白名单见 msg_api_key_channel
-- ============================================================

-- 1. 消息渠道
DROP TABLE IF EXISTS `msg_channel`;
CREATE TABLE `msg_channel` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道名称',
  `channel_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道类型(MSG_CHANNEL_TYPE:1站内信 2Webhook 3Bean)',
  `webhook_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Webhook URL（仅 WEBHOOK）',
  `bean_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Spring Bean 名称（仅 BEAN）',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`),
  KEY `idx_msg_channel_type` (`channel_type`),
  KEY `idx_msg_channel_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息渠道';

-- 2. 消息群组
DROP TABLE IF EXISTS `msg_group`;
CREATE TABLE `msg_group` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群组名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息群组';

-- 3. 群组成员（用户 / 部门 / 内嵌外部人；无独立联系人表）
-- member_type=1 填 user_id；=2 填 dept_id；=3 填 name，且 phone/email 至少一项
DROP TABLE IF EXISTS `msg_group_user`;
DROP TABLE IF EXISTS `msg_group_dept`;
DROP TABLE IF EXISTS `msg_group_external`;
DROP TABLE IF EXISTS `msg_group_member`;
CREATE TABLE `msg_group_member` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `member_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成员类型(MSG_GROUP_MEMBER_TYPE:1用户 2部门 3外部人)',
  `user_id` bigint DEFAULT NULL COMMENT '用户id(sys_user.id)，type=1',
  `dept_id` bigint DEFAULT NULL COMMENT '部门id(sys_dept.id)，type=2',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人名称，type=3',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人手机号，type=3',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人邮箱，type=3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_group_member_user` (`group_id`, `member_type`, `user_id`),
  UNIQUE KEY `uk_msg_group_member_dept` (`group_id`, `member_type`, `dept_id`),
  KEY `idx_msg_group_member_gid` (`group_id`),
  KEY `idx_msg_group_member_uid` (`user_id`),
  KEY `idx_msg_group_member_did` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组成员';

-- 4. 用户级 API Key（平台能力；归属 sys_user，与 OAuth 客户端区分）
-- 可被多模块复用；消息渠道限制见 msg_api_key_channel
DROP TABLE IF EXISTS `msg_open_token`;
DROP TABLE IF EXISTS `sys_api_key`;
CREATE TABLE `sys_api_key` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `user_id` bigint NOT NULL COMMENT '归属用户id(sys_user.id)',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Key 名称（如个人脚本、本机工具）',
  `api_key` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密钥（一期明文；生产建议改哈希）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间，空=不过期',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_api_key` (`api_key`),
  KEY `idx_sys_api_key_user` (`user_id`),
  KEY `idx_sys_api_key_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户API Key';

-- 5. API Key 允许的消息渠道（无行=该 Key 可用全部消息渠道）
DROP TABLE IF EXISTS `msg_open_token_channel`;
DROP TABLE IF EXISTS `msg_api_key_channel`;
CREATE TABLE `msg_api_key_channel` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `api_key_id` bigint NOT NULL COMMENT 'sys_api_key.id',
  `channel_id` bigint NOT NULL COMMENT 'msg_channel.id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_api_key_channel` (`api_key_id`, `channel_id`),
  KEY `idx_msg_api_key_channel_cid` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API Key消息渠道白名单';

-- 6. 发送记录（主）
DROP TABLE IF EXISTS `msg_send_record`;
CREATE TABLE `msg_send_record` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `recipient_summary` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收人摘要',
  `source` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '来源(MSG_SEND_SOURCE:1管理端 2开放接口 3内部调用)',
  `api_key_id` bigint DEFAULT NULL COMMENT '调用所用 API Key，可空',
  `user_id` bigint DEFAULT NULL COMMENT '操作/归属用户（开放调用时取 Key 的 user_id）',
  `request_json` text COLLATE utf8mb4_unicode_ci COMMENT '请求快照JSON（渠道/群组/用户等）',
  PRIMARY KEY (`id`),
  KEY `idx_msg_send_record_time` (`create_time`),
  KEY `idx_msg_send_record_api_key` (`api_key_id`),
  KEY `idx_msg_send_record_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送记录';

-- 7. 发送记录 · 按渠道结果
DROP TABLE IF EXISTS `msg_send_record_item`;
CREATE TABLE `msg_send_record_item` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `record_id` bigint NOT NULL COMMENT '发送记录id',
  `channel_id` bigint NOT NULL COMMENT '渠道id',
  `status` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '结果(MSG_SEND_STATUS:SUCCESS/FAIL/SKIPPED)',
  `detail` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '结果说明',
  PRIMARY KEY (`id`),
  KEY `idx_msg_send_record_item_rid` (`record_id`),
  KEY `idx_msg_send_record_item_cid` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发送记录渠道明细';

-- 8. 站内信收件箱
DROP TABLE IF EXISTS `msg_inbox`;
CREATE TABLE `msg_inbox` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `user_id` bigint NOT NULL COMMENT '收件用户id',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `read_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '已读(0未读 1已读)',
  `channel_id` bigint DEFAULT NULL COMMENT '来源渠道id',
  `record_id` bigint DEFAULT NULL COMMENT '发送记录id',
  PRIMARY KEY (`id`),
  KEY `idx_msg_inbox_user_read` (`user_id`, `read_flag`),
  KEY `idx_msg_inbox_time` (`create_time`),
  KEY `idx_msg_inbox_record` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信收件箱';

-- ------------------------------------------------------------
-- 字典说明（未自动插入，按需写入 sys_dict_*）
-- MSG_CHANNEL_TYPE:       1=站内信 2=Webhook 3=Bean
-- MSG_GROUP_MEMBER_TYPE:  1=用户 2=部门 3=外部人
-- MSG_SEND_SOURCE:        1=管理端 2=开放接口 3=内部调用
-- MSG_SEND_STATUS:        SUCCESS / FAIL / SKIPPED
-- ------------------------------------------------------------

-- 内置站内信渠道示例（id 按雪花/序列自行替换）
-- INSERT INTO msg_channel (id, create_time, del_flag, name, channel_type, status, remark)
-- VALUES (1, NOW(), '0', '系统站内信', '1', '1', '内置');
