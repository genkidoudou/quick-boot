-- sys_dict_type：建表 + COMMON_STATUS 字典 + 菜单
-- 表结构以 sdd/ddl/sys_dict_type.sql 为准。
-- 若库中已有 sys_dict_type，请注释掉下方 CREATE TABLE 段后再执行。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_dict_type` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `dict_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `dict_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ---------- 字典 COMMON_STATUS ----------
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1902000000000000001, '0', '通用状态', 'COMMON_STATUS', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1902000000000000101, '0', 'COMMON_STATUS', '正常', '0', 0, '1', '0'),
  (1902000000000000102, '0', 'COMMON_STATUS', '停用', '1', 1, '0', '0');

-- ---------- 菜单：C + F×5 ----------
INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1902000000000001000, '0', 0, '字典类型', 'C', 'dictType', 'system/dictType/index', 'system:dictType:list', 2, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1902000000000001001, '0', 1902000000000001000, '字典类型列表', 'F', 'system:dictType:list', 1, '0', '0', '0', '0'),
  (1902000000000001002, '0', 1902000000000001000, '字典类型详情', 'F', 'system:dictType:query', 2, '0', '0', '0', '0'),
  (1902000000000001003, '0', 1902000000000001000, '字典类型新增', 'F', 'system:dictType:add', 3, '0', '0', '0', '0'),
  (1902000000000001004, '0', 1902000000000001000, '字典类型修改', 'F', 'system:dictType:edit', 4, '0', '0', '0', '0'),
  (1902000000000001005, '0', 1902000000000001000, '字典类型删除', 'F', 'system:dictType:remove', 5, '0', '0', '0', '0');
