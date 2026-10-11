-- sys_oper_log：建表 + OPER_STATUS 字典 + 只读菜单（列表/详情）
-- 表结构以 sdd/ddl/sys_oper_log.sql 为准（已补 del_flag/update_by/update_time 以对齐 BaseEntity）。
-- parent_id 请按实际父菜单修改（当前为 0）。
-- OPER_STATUS 采用若依惯例：0 正常 / 1 异常。

CREATE TABLE IF NOT EXISTS `sys_oper_log` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `method` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方法名',
  `request_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'http请求方式',
  `oper_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `oper_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作url',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的客户端id',
  `ip_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的ip',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器类型',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录的地址',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作系统',
  `params` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数',
  `json_result` text COLLATE utf8mb4_unicode_ci COMMENT '返回结果',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(OPER_STATUS)',
  `error_msg` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT NULL COMMENT '耗时',
  `trace_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链路id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1910000000000000001, '0', '操作状态', 'OPER_STATUS', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1910000000000000101, '0', 'OPER_STATUS', '正常', '0', 0, '1', '0'),
  (1910000000000000102, '0', 'OPER_STATUS', '异常', '1', 1, '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1910000000000001000, '0', 0, '操作日志', 'C', 'operLog', 'system/operLog/index', 'system:operLog:list', 14, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1910000000000001001, '0', 1910000000000001000, '操作日志列表', 'F', 'system:operLog:list', 1, '0', '0', '0', '0'),
  (1910000000000001002, '0', 1910000000000001000, '操作日志详情', 'F', 'system:operLog:query', 2, '0', '0', '0', '0');
