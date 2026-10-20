-- sys_job：建表 + 任务类型字典 + 定时任务菜单
-- 表结构以 sdd/ddl/sys_job.sql 为准。
-- COMMON_STATUS、COMMON_IS 已存在，不重复写入。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_job` (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除(0:正常,1:删除)',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `digest` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `job_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务类型(JOB_TYPE)',
  `invoke_target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'bean名称',
  `job_param` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务参数',
  `script_content` text COLLATE utf8mb4_unicode_ci COMMENT '任务脚本',
  `python_bin` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'python2 或 python3，仅 Python 任务使用',
  `cron_expression` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'cron',
  `concurrent` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否允许同一任务重叠执行(COMMON_IS)',
  `status` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态(COMMON_STATUS)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务';

-- 字典类型 JOB_TYPE
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1916000000000000001, '0', '任务类型', 'JOB_TYPE', '0');

-- 字典数据 JOB_TYPE
INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1916000000000000101, '0', 'JOB_TYPE', 'Bean', 'BEAN', 1, '0', '0'),
  (1916000000000000102, '0', 'JOB_TYPE', 'Shell', 'SHELL', 2, '0', '0'),
  (1916000000000000103, '0', 'JOB_TYPE', 'Python', 'PYTHON', 3, '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1916000000000001000, '0', 0, '定时任务', 'C', 'job', 'system/job/index', 'system:job:list', 15, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1916000000000001001, '0', 1916000000000001000, '定时任务列表', 'F', 'system:job:list', 1, '0', '0', '0', '0'),
  (1916000000000001002, '0', 1916000000000001000, '定时任务详情', 'F', 'system:job:query', 2, '0', '0', '0', '0'),
  (1916000000000001003, '0', 1916000000000001000, '定时任务新增', 'F', 'system:job:add', 3, '0', '0', '0', '0'),
  (1916000000000001004, '0', 1916000000000001000, '定时任务修改', 'F', 'system:job:edit', 4, '0', '0', '0', '0'),
  (1916000000000001005, '0', 1916000000000001000, '定时任务删除', 'F', 'system:job:remove', 5, '0', '0', '0', '0');
