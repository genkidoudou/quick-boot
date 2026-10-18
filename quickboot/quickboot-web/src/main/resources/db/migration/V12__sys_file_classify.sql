-- sys_file_classify：建表 + 文件分类菜单
-- 表结构以 sdd/ddl/sys_file_classify.sql 为准。
-- COMMON_STATUS、COMMON_IS 已在 V2 / V3 维护，本文件不再重复插入。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_file_classify` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `classify_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类编码',
  `classify_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `allow_ext` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '允许的文件后缀',
  `max_size_bytes` bigint DEFAULT NULL COMMENT '单个文件大小上限',
  `max_count` int DEFAULT '1' COMMENT '每次可允许上传几个文件',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(COMMON_STATUS)',
  `anonymous` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否可以匿名(COMMON_IS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分类表';

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1912000000000001000, '0', 0, '文件分类', 'C', 'fileClassify', 'system/fileClassify/index', 'system:fileClassify:list', 13, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1912000000000001001, '0', 1912000000000001000, '文件分类列表', 'F', 'system:fileClassify:list', 1, '0', '0', '0', '0'),
  (1912000000000001002, '0', 1912000000000001000, '文件分类详情', 'F', 'system:fileClassify:query', 2, '0', '0', '0', '0'),
  (1912000000000001003, '0', 1912000000000001000, '文件分类新增', 'F', 'system:fileClassify:add', 3, '0', '0', '0', '0'),
  (1912000000000001004, '0', 1912000000000001000, '文件分类修改', 'F', 'system:fileClassify:edit', 4, '0', '0', '0', '0'),
  (1912000000000001005, '0', 1912000000000001000, '文件分类删除', 'F', 'system:fileClassify:remove', 5, '0', '0', '0', '0');
