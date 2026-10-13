-- 登录日志增加链路 trace_id，便于与操作日志/响应体对齐排查
ALTER TABLE `sys_logininfor`
  ADD COLUMN `trace_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链路id' AFTER `login_time`;
