-- 消息中心：渠道、群组、站内信、发送记录、发送明细。
-- 表结构以 sdd/ddl 为准。下面三张表的建表语句先按手工库的旧结构创建，随后用 ALTER 补上 BaseEntity 需要的列。
-- COMMON_STATUS 已存在，本文件不再插入。
-- 字典取值来自列注释。parent_id：目录挂在 0，页面挂在消息中心目录下，可按实际菜单调整。

CREATE TABLE IF NOT EXISTS `msg_channel` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道名称',
  `channel_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道类型(MSG_CHANNEL_TYPE:1站内信 2Webhook 3Bean)',
  `webhook_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Webhook URL（仅 WEBHOOK）',
  `bean_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Spring Bean 名称（仅 BEAN）',
  `template_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '模板内容。空或空白则直接用发送正文；不为空则按该模板生成此渠道的发送内容',
  `status` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`),
  KEY `idx_msg_channel_type` (`channel_type`),
  KEY `idx_msg_channel_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息渠道';

CREATE TABLE IF NOT EXISTS `msg_group` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群组名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息群组';

CREATE TABLE IF NOT EXISTS `msg_inbox` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
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

CREATE TABLE IF NOT EXISTS `msg_send_record` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提交时的正文原文',
  `source` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '来源(MSG_SEND_SOURCE:1管理端 2开放接口 3内部调用)',
  `status` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '总状态(MSG_SEND_RECORD_STATUS:1成功 2部分成功 3失败)',
  `api_key_id` bigint DEFAULT NULL COMMENT '调用所用 API Key，可空',
  `user_id` bigint DEFAULT NULL COMMENT '操作/归属用户（开放调用时取 Key 的 user_id）',
  PRIMARY KEY (`id`),
  KEY `idx_msg_send_record_time` (`create_time`),
  KEY `idx_msg_send_record_status` (`status`),
  KEY `idx_msg_send_record_api_key` (`api_key_id`),
  KEY `idx_msg_send_record_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送记录';

CREATE TABLE IF NOT EXISTS `msg_send_record_item` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `record_id` bigint NOT NULL COMMENT '发送记录id',
  `channel_id` bigint NOT NULL COMMENT '渠道id',
  `target_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对象类型(MSG_SEND_TARGET_TYPE:1用户 2部门 3外部人 4群组)',
  `group_id` bigint DEFAULT NULL COMMENT '群组id（msg_group.id），type=4',
  `user_id` bigint DEFAULT NULL COMMENT '用户id(sys_user.id)，type=1',
  `dept_id` bigint DEFAULT NULL COMMENT '部门id(sys_dept.id)，type=2',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人名称，type=3',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人手机号，type=3',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人邮箱，type=3',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '该对象在该渠道实际发出的内容',
  `status` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '结果(MSG_SEND_ITEM_STATUS:1成功 2失败)',
  `detail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因',
  PRIMARY KEY (`id`),
  KEY `idx_msg_send_record_item_rid` (`record_id`),
  KEY `idx_msg_send_record_item_cid` (`channel_id`),
  KEY `idx_msg_send_record_item_uid` (`user_id`),
  KEY `idx_msg_send_record_item_gid` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送明细';

-- 补 BaseEntity 列。开发库这三张表是手工建的，没有这些列。
ALTER TABLE `msg_inbox`
  ADD COLUMN `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)' AFTER `update_time`;

ALTER TABLE `msg_send_record`
  ADD COLUMN `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人' AFTER `create_time`,
  ADD COLUMN `update_time` datetime DEFAULT NULL COMMENT '修改时间' AFTER `update_by`,
  ADD COLUMN `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)' AFTER `update_time`;

ALTER TABLE `msg_send_record_item`
  ADD COLUMN `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人' AFTER `create_time`,
  ADD COLUMN `update_time` datetime DEFAULT NULL COMMENT '修改时间' AFTER `update_by`,
  ADD COLUMN `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)' AFTER `update_time`;

-- 字典。ID 可按环境调整。
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES
  (1922100000000000001, '0', '渠道类型', 'MSG_CHANNEL_TYPE', '0'),
  (1922100000000000002, '0', '来源', 'MSG_SEND_SOURCE', '0'),
  (1922100000000000003, '0', '总状态', 'MSG_SEND_RECORD_STATUS', '0'),
  (1922100000000000004, '0', '结果', 'MSG_SEND_ITEM_STATUS', '0'),
  (1922100000000000005, '0', '对象类型', 'MSG_SEND_TARGET_TYPE', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, list_class, is_default, status)
VALUES
  (1922100000000000101, '0', 'MSG_CHANNEL_TYPE', '站内信', '1', 1, 'primary', '0', '0'),
  (1922100000000000102, '0', 'MSG_CHANNEL_TYPE', 'Webhook', '2', 2, 'info', '0', '0'),
  (1922100000000000103, '0', 'MSG_CHANNEL_TYPE', 'Bean', '3', 3, 'warning', '0', '0'),
  (1922100000000000201, '0', 'MSG_SEND_SOURCE', '管理端', '1', 1, 'primary', '0', '0'),
  (1922100000000000202, '0', 'MSG_SEND_SOURCE', '开放接口', '2', 2, 'info', '0', '0'),
  (1922100000000000203, '0', 'MSG_SEND_SOURCE', '内部调用', '3', 3, 'warning', '0', '0'),
  (1922100000000000301, '0', 'MSG_SEND_RECORD_STATUS', '成功', '1', 1, 'success', '0', '0'),
  (1922100000000000302, '0', 'MSG_SEND_RECORD_STATUS', '部分成功', '2', 2, 'warning', '0', '0'),
  (1922100000000000303, '0', 'MSG_SEND_RECORD_STATUS', '失败', '3', 3, 'danger', '0', '0'),
  (1922100000000000401, '0', 'MSG_SEND_ITEM_STATUS', '成功', '1', 1, 'success', '0', '0'),
  (1922100000000000402, '0', 'MSG_SEND_ITEM_STATUS', '失败', '2', 2, 'danger', '0', '0'),
  (1922100000000000501, '0', 'MSG_SEND_TARGET_TYPE', '用户', '1', 1, 'primary', '0', '0'),
  (1922100000000000502, '0', 'MSG_SEND_TARGET_TYPE', '部门', '2', 2, 'info', '0', '0'),
  (1922100000000000503, '0', 'MSG_SEND_TARGET_TYPE', '外部人', '3', 3, 'warning', '0', '0'),
  (1922100000000000504, '0', 'MSG_SEND_TARGET_TYPE', '群组', '4', 4, 'success', '0', '0');

-- 菜单：目录 + 五个页面，每个页面 5 个按钮。
INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, icon, order_num, is_frame, is_cache, visible, status)
VALUES
  (1922000000000000001, '0', 0, '消息中心', 'M', 'msg', 'Layout', 'message', 90, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1922000000000001100, '0', 1922000000000000001, '消息渠道', 'C', 'channel', 'msg/channel/index', 'msg:channel:list', 1, '0', '0', '0', '0'),
  (1922000000000001200, '0', 1922000000000000001, '消息群组', 'C', 'group', 'msg/group/index', 'msg:group:list', 2, '0', '0', '0', '0'),
  (1922000000000001300, '0', 1922000000000000001, '站内信', 'C', 'inbox', 'msg/inbox/index', 'msg:inbox:list', 3, '0', '0', '0', '0'),
  (1922000000000001400, '0', 1922000000000000001, '发送记录', 'C', 'sendRecord', 'msg/sendRecord/index', 'msg:sendRecord:list', 4, '0', '0', '0', '0'),
  (1922000000000001500, '0', 1922000000000000001, '发送明细', 'C', 'sendRecordItem', 'msg/sendRecordItem/index', 'msg:sendRecordItem:list', 5, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1922000000000001101, '0', 1922000000000001100, '消息渠道列表', 'F', 'msg:channel:list', 1, '0', '0', '0', '0'),
  (1922000000000001102, '0', 1922000000000001100, '消息渠道详情', 'F', 'msg:channel:query', 2, '0', '0', '0', '0'),
  (1922000000000001103, '0', 1922000000000001100, '消息渠道新增', 'F', 'msg:channel:add', 3, '0', '0', '0', '0'),
  (1922000000000001104, '0', 1922000000000001100, '消息渠道修改', 'F', 'msg:channel:edit', 4, '0', '0', '0', '0'),
  (1922000000000001105, '0', 1922000000000001100, '消息渠道删除', 'F', 'msg:channel:remove', 5, '0', '0', '0', '0'),
  (1922000000000001201, '0', 1922000000000001200, '消息群组列表', 'F', 'msg:group:list', 1, '0', '0', '0', '0'),
  (1922000000000001202, '0', 1922000000000001200, '消息群组详情', 'F', 'msg:group:query', 2, '0', '0', '0', '0'),
  (1922000000000001203, '0', 1922000000000001200, '消息群组新增', 'F', 'msg:group:add', 3, '0', '0', '0', '0'),
  (1922000000000001204, '0', 1922000000000001200, '消息群组修改', 'F', 'msg:group:edit', 4, '0', '0', '0', '0'),
  (1922000000000001205, '0', 1922000000000001200, '消息群组删除', 'F', 'msg:group:remove', 5, '0', '0', '0', '0'),
  (1922000000000001301, '0', 1922000000000001300, '站内信列表', 'F', 'msg:inbox:list', 1, '0', '0', '0', '0'),
  (1922000000000001302, '0', 1922000000000001300, '站内信详情', 'F', 'msg:inbox:query', 2, '0', '0', '0', '0'),
  (1922000000000001303, '0', 1922000000000001300, '站内信新增', 'F', 'msg:inbox:add', 3, '0', '0', '0', '0'),
  (1922000000000001304, '0', 1922000000000001300, '站内信修改', 'F', 'msg:inbox:edit', 4, '0', '0', '0', '0'),
  (1922000000000001305, '0', 1922000000000001300, '站内信删除', 'F', 'msg:inbox:remove', 5, '0', '0', '0', '0'),
  (1922000000000001401, '0', 1922000000000001400, '发送记录列表', 'F', 'msg:sendRecord:list', 1, '0', '0', '0', '0'),
  (1922000000000001402, '0', 1922000000000001400, '发送记录详情', 'F', 'msg:sendRecord:query', 2, '0', '0', '0', '0'),
  (1922000000000001403, '0', 1922000000000001400, '发送记录新增', 'F', 'msg:sendRecord:add', 3, '0', '0', '0', '0'),
  (1922000000000001404, '0', 1922000000000001400, '发送记录修改', 'F', 'msg:sendRecord:edit', 4, '0', '0', '0', '0'),
  (1922000000000001405, '0', 1922000000000001400, '发送记录删除', 'F', 'msg:sendRecord:remove', 5, '0', '0', '0', '0'),
  (1922000000000001501, '0', 1922000000000001500, '发送明细列表', 'F', 'msg:sendRecordItem:list', 1, '0', '0', '0', '0'),
  (1922000000000001502, '0', 1922000000000001500, '发送明细详情', 'F', 'msg:sendRecordItem:query', 2, '0', '0', '0', '0'),
  (1922000000000001503, '0', 1922000000000001500, '发送明细新增', 'F', 'msg:sendRecordItem:add', 3, '0', '0', '0', '0'),
  (1922000000000001504, '0', 1922000000000001500, '发送明细修改', 'F', 'msg:sendRecordItem:edit', 4, '0', '0', '0', '0'),
  (1922000000000001505, '0', 1922000000000001500, '发送明细删除', 'F', 'msg:sendRecordItem:remove', 5, '0', '0', '0', '0');
