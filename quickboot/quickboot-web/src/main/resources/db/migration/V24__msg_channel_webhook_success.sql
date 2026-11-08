-- 通用 Webhook 用返回 JSON 判断业务是否成功。空则仍只看 HTTP 2xx。
ALTER TABLE `msg_channel`
  ADD COLUMN `webhook_success` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Webhook成功规则。空则HTTP 2xx即成功；否则为JSON字段=期望值，如 errcode=0' AFTER `template_content`;
