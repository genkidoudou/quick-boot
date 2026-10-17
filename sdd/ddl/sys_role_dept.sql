CREATE TABLE `sys_role_dept` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `role_id` bigint DEFAULT NULL COMMENT '角色id',
  `dept_id` bigint DEFAULT NULL COMMENT '部门id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和部门关联表';
