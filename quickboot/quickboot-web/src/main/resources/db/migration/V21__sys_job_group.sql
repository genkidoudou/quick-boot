-- 定时任务：业务分组（字典 JOB_GROUP）；不改变 Quartz JobKey 分组

ALTER TABLE `sys_job`
  ADD COLUMN `job_group` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组(JOB_GROUP)' AFTER `job_name`;

UPDATE `sys_job` SET `job_group` = 'DEFAULT' WHERE `job_group` IS NULL OR `job_group` = '';

-- 字典类型 JOB_GROUP
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1916000000000000002, '0', '任务分组', 'JOB_GROUP', '0');

-- 字典数据 JOB_GROUP
INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1916000000000000201, '0', 'JOB_GROUP', '默认', 'DEFAULT', 1, '1', '0'),
  (1916000000000000202, '0', 'JOB_GROUP', '系统', 'SYSTEM', 2, '0', '0'),
  (1916000000000000203, '0', 'JOB_GROUP', '业务', 'BIZ', 3, '0', '0');
