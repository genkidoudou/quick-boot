-- sys_job_log：建表 + 任务状态字典 + 日志菜单
-- 表结构以 sdd/ddl/sys_job_log.sql 为准。日志不提供新增、修改。
-- parent_id 请按实际父菜单修改（当前为 0）。

CREATE TABLE IF NOT EXISTS `sys_job_log` (
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

-- 字典类型 JOB_STATUS
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1917000000000000001, '0', '任务状态', 'JOB_STATUS', '0');

-- 字典数据 JOB_STATUS
INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1917000000000000101, '0', 'JOB_STATUS', '执行中', '0', 1, '0', '0'),
  (1917000000000000102, '0', 'JOB_STATUS', '成功', '1', 2, '0', '0'),
  (1917000000000000103, '0', 'JOB_STATUS', '失败', '2', 3, '0', '0'),
  (1917000000000000104, '0', 'JOB_STATUS', '跳过', '3', 4, '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1917000000000001000, '0', 0, '任务日志', 'C', 'jobLog', 'system/jobLog/index', 'system:jobLog:list', 16, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1917000000000001001, '0', 1917000000000001000, '任务日志列表', 'F', 'system:jobLog:list', 1, '0', '0', '0', '0'),
  (1917000000000001002, '0', 1917000000000001000, '任务日志详情', 'F', 'system:jobLog:query', 2, '0', '0', '0', '0'),
  (1917000000000001003, '0', 1917000000000001000, '任务日志删除', 'F', 'system:jobLog:remove', 3, '0', '0', '0', '0');
