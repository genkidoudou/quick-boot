CREATE TABLE `sys_job_log` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `job_id` bigint NOT NULL COMMENT '任务id',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `cost_ms` bigint DEFAULT NULL COMMENT '耗时',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(JOB_STATUS)',
  `result` text COLLATE utf8mb4_unicode_ci COMMENT '返回结果',
  `error_msg` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误异常',
  `job_param` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务日志';
