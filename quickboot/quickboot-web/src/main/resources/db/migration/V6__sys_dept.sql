-- sys_dept：建表 + 部门管理菜单
-- 表结构以 sdd/ddl/sys_dept.sql 为准。
-- COMMON_STATUS 已在 V2 维护，本文件不再重复插入。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_dept` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '上级部门',
  `dept_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门id',
  `order_num` int DEFAULT '0' COMMENT '排序',
  `leader` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统部门表';

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1907000000000001000, '0', 0, '部门管理', 'C', 'dept', 'system/dept/index', 'system:dept:list', 11, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1907000000000001001, '0', 1907000000000001000, '部门列表', 'F', 'system:dept:list', 1, '0', '0', '0', '0'),
  (1907000000000001002, '0', 1907000000000001000, '部门详情', 'F', 'system:dept:query', 2, '0', '0', '0', '0'),
  (1907000000000001003, '0', 1907000000000001000, '部门新增', 'F', 'system:dept:add', 3, '0', '0', '0', '0'),
  (1907000000000001004, '0', 1907000000000001000, '部门修改', 'F', 'system:dept:edit', 4, '0', '0', '0', '0'),
  (1907000000000001005, '0', 1907000000000001000, '部门删除', 'F', 'system:dept:remove', 5, '0', '0', '0', '0');
