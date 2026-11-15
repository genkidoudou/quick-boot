-- OAuth 客户端增加「是否校验签名」；COMMON_IS：0=是，1=否。
ALTER TABLE `sys_oauth_client`
  ADD COLUMN `check_sign` varchar(3) COLLATE utf8mb4_unicode_ci DEFAULT '1'
    COMMENT '是否校验签名(COMMON_IS)' AFTER `check_captcha`;
