-- 将 check_captcha / check_sign 并入 grant_types（逗号串），再删除独立列。
-- 约定：grant_types 可含 password,sms,...,captcha,sign；含 captcha/sign 即开启对应校验。

-- 1) 字典补充安全策略项（与授权方式同一 GRANT_TYPES）
INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1927000000000000105, '0', 'GRANT_TYPES', '校验验证码', 'captcha', 10, '0', '0'),
  (1927000000000000106, '0', 'GRANT_TYPES', '校验签名', 'sign', 11, '0', '0');

-- 2) 历史数据迁入 grant_types
UPDATE sys_oauth_client
SET grant_types = CONCAT(grant_types, ',captcha')
WHERE IFNULL(check_captcha, '1') = '0'
  AND FIND_IN_SET('captcha', REPLACE(IFNULL(grant_types, ''), ' ', '')) = 0;

UPDATE sys_oauth_client
SET grant_types = CONCAT(grant_types, ',sign')
WHERE IFNULL(check_sign, '1') = '0'
  AND FIND_IN_SET('sign', REPLACE(IFNULL(grant_types, ''), ' ', '')) = 0;

-- 3) 删除独立字段
ALTER TABLE `sys_oauth_client`
  DROP COLUMN `check_captcha`,
  DROP COLUMN `check_sign`;
