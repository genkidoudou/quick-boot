-- OAuth 客户端管理菜单。表已存在，本迁移仅插入菜单。
-- COMMON_IS / COMMON_STATUS 已存在，跳过字典 INSERT。
-- parent_id 请按实际父菜单修改（当前为 0）。

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1926000000000001000, '0', 0, 'OAuth客户端', 'C', 'oauthClient', 'system/oauthClient/index', 'system:oauthClient:list', 80, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  (1926000000000001001, '0', 1926000000000001000, 'OAuth客户端列表', 'F', 'system:oauthClient:list', 1, '0', '0', '0', '0'),
  (1926000000000001002, '0', 1926000000000001000, 'OAuth客户端详情', 'F', 'system:oauthClient:query', 2, '0', '0', '0', '0'),
  (1926000000000001003, '0', 1926000000000001000, 'OAuth客户端新增', 'F', 'system:oauthClient:add', 3, '0', '0', '0', '0'),
  (1926000000000001004, '0', 1926000000000001000, 'OAuth客户端修改', 'F', 'system:oauthClient:edit', 4, '0', '0', '0', '0'),
  (1926000000000001005, '0', 1926000000000001000, 'OAuth客户端删除', 'F', 'system:oauthClient:remove', 5, '0', '0', '0', '0');
