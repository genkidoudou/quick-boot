-- 授权类型字典：OAuth 客户端 grant_types 多选。

INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1927000000000000001, '0', '授权类型', 'GRANT_TYPES', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1927000000000000101, '0', 'GRANT_TYPES', '密码登录', 'password', 1, '1', '0'),
  (1927000000000000102, '0', 'GRANT_TYPES', '短信登录', 'sms', 2, '0', '0'),
  (1927000000000000103, '0', 'GRANT_TYPES', '刷新令牌', 'refresh_token', 3, '0', '0'),
  (1927000000000000104, '0', 'GRANT_TYPES', '客户端凭证', 'client_credentials', 4, '0', '0');
