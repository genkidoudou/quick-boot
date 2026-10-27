-- 消息发送表示例。对应设计文档第 4 节。
-- 不参与 Flyway。落地时再补菜单和字典。
-- 主表带上项目统一的审计列和逻辑删除；关联表只存关系。

CREATE TABLE `msg_channel` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `channel_type` varchar(20) NOT NULL COMMENT 'IN_APP、WEBHOOK、BEAN',
  `content_type` varchar(20) NOT NULL COMMENT 'TEXT、HTML、JSON、MARKDOWN',
  `template_body` text NOT NULL COMMENT '模板正文，占位符 {{name}}',
  `inbound_token` varchar(64) NOT NULL COMMENT '入站 token，32 字节十六进制，明文可再次查看',
  `webhook_url` varchar(500) DEFAULT NULL COMMENT '仅 WEBHOOK 使用',
  `bean_name` varchar(100) DEFAULT NULL COMMENT '仅 BEAN 使用，容器中的 MessageSender 名称',
  `status` varchar(3) NOT NULL DEFAULT '0' COMMENT '0 启用，1 停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_channel_token` (`inbound_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息渠道';

CREATE TABLE `msg_group` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` varchar(3) NOT NULL DEFAULT '0',
  `remark` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL COMMENT '群组名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息群组';

CREATE TABLE `msg_group_user` (
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`group_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组直接用户';

CREATE TABLE `msg_group_dept` (
  `group_id` bigint NOT NULL,
  `dept_id` bigint NOT NULL,
  `include_children` varchar(1) NOT NULL COMMENT '1 包含下级，0 只取本部门',
  PRIMARY KEY (`group_id`, `dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组部门';

CREATE TABLE `msg_message` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL COMMENT '外部调用可以为空',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` varchar(3) NOT NULL DEFAULT '0',
  `remark` varchar(100) DEFAULT NULL,
  `title` varchar(200) NOT NULL COMMENT '标题',
  `variables_json` json NOT NULL COMMENT '变量，缺省 {}',
  `source` varchar(20) NOT NULL COMMENT 'ADMIN 或 API',
  `status` varchar(20) NOT NULL COMMENT 'SUCCESS、PARTIAL、FAILED',
  `fail_reason` varchar(500) DEFAULT NULL COMMENT '只在整次未投递时填写',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

CREATE TABLE `msg_message_user` (
  `message_id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '发送时指定的直接用户，含后来被忽略的停用用户',
  PRIMARY KEY (`message_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息直接用户';

CREATE TABLE `msg_message_group` (
  `message_id` bigint NOT NULL,
  `group_id` bigint NOT NULL COMMENT '请求中的群组 id，群组不存在时也保留',
  PRIMARY KEY (`message_id`, `group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息所选群组';

CREATE TABLE `msg_message_channel` (
  `id` bigint NOT NULL,
  `message_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL COMMENT '请求中的渠道 id，渠道删除后快照仍保留',
  `content_type` varchar(20) DEFAULT NULL,
  `template_body` text COMMENT '渠道不存在时为空',
  `rendered_body` text COMMENT '渲染成功才有',
  `webhook_url` varchar(500) DEFAULT NULL,
  `bean_name` varchar(100) DEFAULT NULL,
  `delivery_status` varchar(20) NOT NULL COMMENT 'SUCCESS 或 FAILED',
  `error_message` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_message_channel` (`message_id`, `channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息渠道快照';

CREATE TABLE `msg_message_recipient` (
  `message_id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '发送当时冻结的收件人',
  PRIMARY KEY (`message_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息收件人快照';

CREATE TABLE `msg_inbox` (
  `id` bigint NOT NULL,
  `message_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `read_at` datetime DEFAULT NULL COMMENT '空表示未读。列表不写这个字段',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_inbox_user` (`message_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内收件箱';
