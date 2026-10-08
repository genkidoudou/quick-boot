-- sys_user 业务表迁移：建表 + 字典 + 菜单
-- 表结构以 sdd/ddl/sys_user.sql 为准。
-- 若库中已有 sys_user，请注释掉下方 CREATE TABLE 段后再执行。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `dept_id` bigint DEFAULT NULL COMMENT '部门id',
  `user_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `nick_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `user_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '用户类型(user_type)',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phonenumber` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `sex` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别(sex)',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户状态(user_status)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ---------- 字典类型 ----------
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES
  (1901000000000000001, '0', '用户类型', 'user_type', '0'),
  (1901000000000000002, '0', '性别', 'sex', '0'),
  (1901000000000000003, '0', '用户状态', 'user_status', '0');

-- ---------- 字典数据 ----------
INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1901000000000000101, '0', 'user_type', '系统用户', '0', 0, '1', '0'),
  (1901000000000000201, '0', 'sex', '女', '0', 0, '0', '0'),
  (1901000000000000202, '0', 'sex', '男', '1', 1, '0', '0'),
  (1901000000000000301, '0', 'user_status', '正常', '0', 0, '1', '0'),
  (1901000000000000302, '0', 'user_status', '停用', '1', 1, '0', '0');

-- ---------- 菜单：C + F×5 ----------
INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1901000000000001000, '0', 0, '用户管理', 'C', 'user', 'system/user/index', 'system:user:list', 1, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1901000000000001001, '0', 1901000000000001000, '用户列表', 'F', 'system:user:list', 1, '0', '0', '0', '0'),
  (1901000000000001002, '0', 1901000000000001000, '用户详情', 'F', 'system:user:query', 2, '0', '0', '0', '0'),
  (1901000000000001003, '0', 1901000000000001000, '用户新增', 'F', 'system:user:add', 3, '0', '0', '0', '0'),
  (1901000000000001004, '0', 1901000000000001000, '用户修改', 'F', 'system:user:edit', 4, '0', '0', '0', '0'),
  (1901000000000001005, '0', 1901000000000001000, '用户删除', 'F', 'system:user:remove', 5, '0', '0', '0', '0');
