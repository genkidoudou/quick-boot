-- sys_logininfor：建表 + SYS_LOGIN_STATUS 字典 + 只读菜单（列表/详情）
-- 表结构以 sdd/ddl/sys_logininfor.sql 为准（已补 update_by/update_time 以对齐 BaseEntity）。
-- parent_id 请按实际父菜单修改（当前为 0）。
-- SYS_LOGIN_STATUS 采用若依惯例：0 成功 / 1 失败。

CREATE TABLE IF NOT EXISTS `sys_logininfor` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
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

INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1909000000000000001, '0', '登录状态', 'SYS_LOGIN_STATUS', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1909000000000000101, '0', 'SYS_LOGIN_STATUS', '成功', '0', 0, '1', '0'),
  (1909000000000000102, '0', 'SYS_LOGIN_STATUS', '失败', '1', 1, '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1909000000000001000, '0', 0, '登录日志', 'C', 'logininfor', 'system/logininfor/index', 'system:logininfor:list', 13, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1909000000000001001, '0', 1909000000000001000, '登录日志列表', 'F', 'system:logininfor:list', 1, '0', '0', '0', '0'),
  (1909000000000001002, '0', 1909000000000001000, '登录日志详情', 'F', 'system:logininfor:query', 2, '0', '0', '0', '0');
