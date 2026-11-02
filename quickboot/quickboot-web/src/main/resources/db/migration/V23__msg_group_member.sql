-- 群组成员。开发库表已存在时 CREATE 跳过，再补逻辑删除列。
-- 成员类型取值来自列注释。菜单挂在消息中心目录下，parent_id 可按实际菜单调整。

CREATE TABLE IF NOT EXISTS `msg_group_member` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `member_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成员类型(MSG_GROUP_MEMBER_TYPE:1用户 2部门 3外部人)',
  `user_id` bigint DEFAULT NULL COMMENT '用户id(sys_user.id)，type=1',
  `dept_id` bigint DEFAULT NULL COMMENT '部门id(sys_dept.id)，type=2',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人名称，type=3',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人手机号，type=3',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部人邮箱，type=3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_group_member_user` (`group_id`,`member_type`,`user_id`),
  UNIQUE KEY `uk_msg_group_member_dept` (`group_id`,`member_type`,`dept_id`),
  KEY `idx_msg_group_member_gid` (`group_id`),
  KEY `idx_msg_group_member_uid` (`user_id`),
  KEY `idx_msg_group_member_did` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组成员';

ALTER TABLE `msg_group_member`
  ADD COLUMN `del_flag` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)' AFTER `update_time`;

INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1922100000000000006, '0', '成员类型', 'MSG_GROUP_MEMBER_TYPE', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, list_class, is_default, status)
VALUES
  (1922100000000000601, '0', 'MSG_GROUP_MEMBER_TYPE', '用户', '1', 1, 'primary', '0', '0'),
  (1922100000000000602, '0', 'MSG_GROUP_MEMBER_TYPE', '部门', '2', 2, 'info', '0', '0'),
  (1922100000000000603, '0', 'MSG_GROUP_MEMBER_TYPE', '外部人', '3', 3, 'warning', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1922000000000001600, '0', 1922000000000000001, '群组成员', 'C', 'groupMember', 'msg/groupMember/index', 'msg:groupMember:list', 6, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1922000000000001601, '0', 1922000000000001600, '群组成员列表', 'F', 'msg:groupMember:list', 1, '0', '0', '0', '0'),
  (1922000000000001602, '0', 1922000000000001600, '群组成员详情', 'F', 'msg:groupMember:query', 2, '0', '0', '0', '0'),
  (1922000000000001603, '0', 1922000000000001600, '群组成员新增', 'F', 'msg:groupMember:add', 3, '0', '0', '0', '0'),
  (1922000000000001604, '0', 1922000000000001600, '群组成员修改', 'F', 'msg:groupMember:edit', 4, '0', '0', '0', '0'),
  (1922000000000001605, '0', 1922000000000001600, '群组成员删除', 'F', 'msg:groupMember:remove', 5, '0', '0', '0', '0');
