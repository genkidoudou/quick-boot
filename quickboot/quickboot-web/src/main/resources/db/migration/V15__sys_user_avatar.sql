-- sys_user 增加头像，并准备 avatar 上传分类（图片，单文件 2MB）。
-- COMMON_IS：否 = 1，头像上传需要登录。

ALTER TABLE `sys_user`
  ADD COLUMN `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像' AFTER `sex`;

INSERT INTO sys_file_classify (id, del_flag, classify_code, classify_name, allow_ext, max_size_bytes, max_count, status, anonymous)
SELECT 1915000000000000001, '0', 'avatar', '用户头像', 'jpg,jpeg,png,gif,webp', 2097152, 1, '0', '1'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_file_classify WHERE classify_code = 'avatar' AND del_flag = '0'
);
