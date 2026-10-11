-- sys_config：建表 + 参数配置菜单
-- 表结构以 sdd/ddl/sys_config.sql 为准。
-- COMMON_IS 已在 V3 维护，本文件不再重复插入。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `config_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数键名',
  `config_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名称',
  `config_value` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数值',
  `config_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否系统内置(COMMON_IS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数配置表';

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1908000000000001000, '0', 0, '参数配置', 'C', 'config', 'system/config/index', 'system:config:list', 12, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1908000000000001001, '0', 1908000000000001000, '参数列表', 'F', 'system:config:list', 1, '0', '0', '0', '0'),
  (1908000000000001002, '0', 1908000000000001000, '参数详情', 'F', 'system:config:query', 2, '0', '0', '0', '0'),
  (1908000000000001003, '0', 1908000000000001000, '参数新增', 'F', 'system:config:add', 3, '0', '0', '0', '0'),
  (1908000000000001004, '0', 1908000000000001000, '参数修改', 'F', 'system:config:edit', 4, '0', '0', '0', '0'),
  (1908000000000001005, '0', 1908000000000001000, '参数删除', 'F', 'system:config:remove', 5, '0', '0', '0', '0');
