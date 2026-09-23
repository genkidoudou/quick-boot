CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `dict_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `dict_label` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典值',
  `dict_sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `css_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'css 样式类',
  `list_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回显样式',
  `is_default` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '是否默认(COMMON_IS)',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据';
