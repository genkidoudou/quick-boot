CREATE TABLE `sus_role` (
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
