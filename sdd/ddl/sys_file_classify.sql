CREATE TABLE `sys_file_classify` (
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
