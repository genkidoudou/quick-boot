-- sys_menu：建表 + MENU_TYPE 字典 + 菜单管理页
-- 表结构以 sdd/ddl/sys_menu.sql 为准。
-- COMMON_STATUS 已在 V2 维护，本文件不再重复插入。
-- 若库中已有 sys_menu，请注释掉下方 CREATE TABLE 段后再执行。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父id',
  `menu_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单类型(MENU_TYPE)',
  `path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由地址',
  `route_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由名称',
  `component` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件地址',
  `perms` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单图标',
  `order_num` int DEFAULT '0' COMMENT '菜单排序',
  `query` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由 query',
  `is_frame` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '是否外链(0:否,1:是)',
  `is_cache` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '是否缓存(0:缓存, 1:不缓存)',
  `visible` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '是否显示(0:显示,1:隐藏)',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- ---------- 字典 MENU_TYPE（ID 可按环境调整） ----------
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1904000000000000001, '0', '菜单类型', 'MENU_TYPE', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1904000000000000101, '0', 'MENU_TYPE', '目录', 'M', 0, '0', '0'),
  (1904000000000000102, '0', 'MENU_TYPE', '菜单', 'C', 1, '0', '0'),
  (1904000000000000103, '0', 'MENU_TYPE', '按钮', 'F', 2, '0', '0');

-- ---------- 菜单：C + F×5 ----------
INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1904000000000001000, '0', 0, '菜单管理', 'C', 'menu', 'system/menu/index', 'system:menu:list', 1, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1904000000000001001, '0', 1904000000000001000, '菜单列表', 'F', 'system:menu:list', 1, '0', '0', '0', '0'),
  (1904000000000001002, '0', 1904000000000001000, '菜单详情', 'F', 'system:menu:query', 2, '0', '0', '0', '0'),
  (1904000000000001003, '0', 1904000000000001000, '菜单新增', 'F', 'system:menu:add', 3, '0', '0', '0', '0'),
  (1904000000000001004, '0', 1904000000000001000, '菜单修改', 'F', 'system:menu:edit', 4, '0', '0', '0', '0'),
  (1904000000000001005, '0', 1904000000000001000, '菜单删除', 'F', 'system:menu:remove', 5, '0', '0', '0', '0');
