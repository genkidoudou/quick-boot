-- sys_role：建表 + USER_DATA_SCOPE 字典 + 角色管理菜单
-- 表结构以 sdd/ddl/sys_role.sql 为准。（历史文件名仍为 V5__sus_role，避免已执行环境 Flyway 版本号冲突）
-- COMMON_STATUS 已在 V2 维护，本文件不再重复插入。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `role_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_sort` int DEFAULT '0' COMMENT '角色排序',
  `data_scope` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据权限(USER_DATA_SCOPE)',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ---------- 字典 USER_DATA_SCOPE（ID 可按环境调整） ----------
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1905000000000000001, '0', '数据权限', 'USER_DATA_SCOPE', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1905000000000000101, '0', 'USER_DATA_SCOPE', '全部数据权限', '1', 0, '0', '0'),
  (1905000000000000102, '0', 'USER_DATA_SCOPE', '自定义数据权限', '2', 1, '0', '0'),
  (1905000000000000103, '0', 'USER_DATA_SCOPE', '本部门数据权限', '3', 2, '0', '0'),
  (1905000000000000104, '0', 'USER_DATA_SCOPE', '本部门及以下', '4', 3, '0', '0'),
  (1905000000000000105, '0', 'USER_DATA_SCOPE', '仅本人', '5', 4, '0', '0');

-- ---------- 菜单：C + F×5 ----------
INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1905000000000001000, '0', 0, '角色管理', 'C', 'role', 'system/role/index', 'system:role:list', 10, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1905000000000001001, '0', 1905000000000001000, '角色列表', 'F', 'system:role:list', 1, '0', '0', '0', '0'),
  (1905000000000001002, '0', 1905000000000001000, '角色详情', 'F', 'system:role:query', 2, '0', '0', '0', '0'),
  (1905000000000001003, '0', 1905000000000001000, '角色新增', 'F', 'system:role:add', 3, '0', '0', '0', '0'),
  (1905000000000001004, '0', 1905000000000001000, '角色修改', 'F', 'system:role:edit', 4, '0', '0', '0', '0'),
  (1905000000000001005, '0', 1905000000000001000, '角色删除', 'F', 'system:role:remove', 5, '0', '0', '0', '0');
