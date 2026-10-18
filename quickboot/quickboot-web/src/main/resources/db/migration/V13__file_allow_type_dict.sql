-- 文件允许类型字典：标签为类型名，字典值为逗号分隔后缀（小写、不带点）。
-- 页面勾选类型后展开为 sys_file_classify.allow_ext。
-- dict_value 最长 255，后缀列表需控制在该长度内。

INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES (1913000000000000001, '0', '文件允许类型', 'FILE_ALLOW_TYPE', '0');

INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  (1913000000000000101, '0', 'FILE_ALLOW_TYPE', '图片', 'jpg,jpeg,png,gif,webp,bmp', 1, '0', '0'),
  (1913000000000000102, '0', 'FILE_ALLOW_TYPE', '视频', 'mp4,avi,mov,wmv,flv,mkv,webm', 2, '0', '0'),
  (1913000000000000103, '0', 'FILE_ALLOW_TYPE', 'Excel', 'xls,xlsx,csv', 3, '0', '0'),
  (1913000000000000104, '0', 'FILE_ALLOW_TYPE', 'Word', 'doc,docx', 4, '0', '0'),
  (1913000000000000105, '0', 'FILE_ALLOW_TYPE', 'PPT', 'ppt,pptx', 5, '0', '0'),
  (1913000000000000106, '0', 'FILE_ALLOW_TYPE', 'PDF', 'pdf', 6, '0', '0'),
  (1913000000000000107, '0', 'FILE_ALLOW_TYPE', '文本', 'txt', 7, '0', '0'),
  (1913000000000000108, '0', 'FILE_ALLOW_TYPE', '压缩包', 'zip,rar,7z', 8, '0', '0');
