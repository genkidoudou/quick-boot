ALTER TABLE msg_channel
    ADD COLUMN webhook_method varchar(32) NULL COMMENT 'Webhook请求方式：GET-FORM、POST-FORM、POST-JSON' AFTER webhook_url;
