-- 示例：Flyway 迁移（版本号按仓库实际顺延，如 Vxx__oauth_client_grant_types.sql）
-- 目标：为 sys_oauth_client 增加 client_type、grant_types，并回填第一方种子。

ALTER TABLE `sys_oauth_client`
  ADD COLUMN `client_type` varchar(16) NOT NULL DEFAULT 'public'
    COMMENT 'public|confidential；SPA/H5 用 public' AFTER `client_name`,
  ADD COLUMN `grant_types` varchar(255) NOT NULL DEFAULT 'password'
    COMMENT '允许的 grant_type，逗号分隔，如 password,sms' AFTER `client_type`;

-- 管理端：仅密码 + 要验证码
UPDATE `sys_oauth_client`
SET `client_type` = 'public',
    `grant_types` = 'password',
    `check_captcha` = '1',
    `status` = '0'
WHERE `client_id` = 'quick-ui';

-- H5：密码 + 短信；要验证码（若尚无行则 INSERT）
UPDATE `sys_oauth_client`
SET `client_type` = 'public',
    `grant_types` = 'password,sms',
    `check_captcha` = '1',
    `status` = '0'
WHERE `client_id` = 'quick-h5';

-- 若 quick-h5 不存在，示例插入（id/secret 按你们发号与加密策略替换）
-- INSERT INTO sys_oauth_client (id, del_flag, client_id, client_secret, client_name, client_type, grant_types,
--   api_path_patterns, check_captcha, status, create_time)
-- VALUES (..., '0', 'quick-h5', '', 'Quick H5', 'public', 'password,sms', '/**', '1', '0', NOW());

-- 说明：
-- 1) public 客户端 client_secret 可空；即使有值也不在浏览器侧使用。
-- 2) check_captcha 已存在则不必再加列。
-- 3) status 取值以项目 COMMON_STATUS 为准（示例假设 0=正常，请按实际字典改）。
