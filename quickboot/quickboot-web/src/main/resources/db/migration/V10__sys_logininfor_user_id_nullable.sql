-- 登录失败且无法解析用户时允许 user_id 为空
ALTER TABLE `sys_logininfor`
  MODIFY COLUMN `user_id` bigint NULL COMMENT '登录的用户id';
