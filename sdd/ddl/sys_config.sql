CREATE TABLE `sys_config` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `config_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数键名',
  `config_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名称',
  `config_value` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数值',
  `config_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否系统内置(COMMON_IS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数配置表';
