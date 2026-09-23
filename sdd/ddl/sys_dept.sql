CREATE TABLE `sys_dept` (
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
