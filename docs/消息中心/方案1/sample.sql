-- 方案 1 的示例数据，对应 LeaveScenario。
-- 前半是发送前要读到的通道和群组。后半是站内信发出后的表结果。
-- 其他通道的回执不写表，写在本文件末尾的注释里。

INSERT INTO `msg_channel`
(`id`, `create_time`, `del_flag`, `name`, `channel_type`, `status`,
 `smtp_host`, `smtp_port`, `smtp_username`, `smtp_password`, `mail_from`,
 `sms_access_key`, `sms_sign`, `sms_endpoint`,
 `wecom_corp_id`, `wecom_agent_id`, `wecom_secret`,
 `webhook_url`, `webhook_secret`, `iyuu_token`)
VALUES
(101, NOW(), '0', 'mail-office', 'EMAIL', '0',
 'smtp.corp.example', 465, 'notice', 'secret', 'notice@corp.example',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(102, NOW(), '0', 'sms-notice', 'SMS', '0',
 NULL, NULL, NULL, NULL, NULL,
 'sms-key', '人事通知', 'https://sms.example/send',
 NULL, NULL, NULL, NULL, NULL, NULL),
(103, NOW(), '0', 'inbox', 'INBOX', '0',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(104, NOW(), '0', 'wecom-hr', 'WECOM_APP', '0',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
 'ww-corp', '1000024', 'agent-secret', NULL, NULL, NULL),
(105, NOW(), '0', 'ding-ops', 'DINGTALK_ROBOT', '0',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
 'https://oapi.dingtalk.com/robot/send?access_token=demo', 'ding-secret', NULL),
(106, NOW(), '0', 'wecom-bot', 'WECOM_ROBOT', '0',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=demo', NULL, NULL),
(107, NOW(), '0', 'iyuu-ops', 'IYUU', '0',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'iyuu-token'),
(108, NOW(), '0', 'mail-paused', 'EMAIL', '1',
 'smtp.corp.example', 465, 'notice', 'secret', 'notice@corp.example',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO `msg_group` (`id`, `create_time`, `del_flag`, `name`, `channel_type`) VALUES
(201, NOW(), '0', 'g-hr-mail', 'EMAIL'),
(202, NOW(), '0', 'g-empty-mail', 'EMAIL'),
(203, NOW(), '0', 'g-empty-sms', 'SMS');

-- 201 有三个邮箱。202 是空的邮件群组，用来演示「群组为空」。
-- 203 是空的短信群组。用邮件通道发送它时，先报类型不符，而不是群组为空。
INSERT INTO `msg_group_address` (`id`, `create_time`, `group_id`, `address`, `sort_no`) VALUES
(301, NOW(), 201, 'wangmin@corp.example', 1),
(302, NOW(), 201, 'hr-dept@corp.example', 2),
(303, NOW(), 201, 'guest@partner.example', 3);

-- 站内信 send(103, ["U10086","U10087"], 消息) 之后的表结果。
INSERT INTO `msg_inbox`
(`id`, `create_time`, `channel_id`, `user_address`, `title`, `content`, `link`)
VALUES
(401, NOW(), 103, 'U10086', '请假已通过', '王敏 11 月 3 日的年假已通过。', '/leave/2041'),
(402, NOW(), 103, 'U10087', '请假已通过', '王敏 11 月 3 日的年假已通过。', '/leave/2041');

-- 不落库的回执，与 LeaveScenario 打印的一致。
--
-- 邮件 101，直接传地址或群组 201，同一张回执：
-- wangmin@corp.example    已送达
-- hr-dept@corp.example    已送达
-- guest@partner.example    未送达    对方服务器拒收
--
-- 短信 102：
-- 13800001111             已送达
-- abc                     未送达    地址格式不正确
--
-- 企业微信应用 104：
-- ZhangSan                已送达
-- LiSi                    未送达    不在应用可见范围
--
-- 钉钉机器人 105、企业微信机器人 106、IYUU 107 各一行，地址分别是 105、106、107，已送达。
