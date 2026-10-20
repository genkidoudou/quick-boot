-- 定时任务迁到 job 模块后，菜单组件和权限字与新模块对齐。
-- 接口前缀改为 job/job、job/jobLog。

UPDATE sys_menu
SET component = 'job/job/index', perms = 'job:job:list'
WHERE id = 1916000000000001000;

UPDATE sys_menu SET perms = 'job:job:list' WHERE id = 1916000000000001001;
UPDATE sys_menu SET perms = 'job:job:query' WHERE id = 1916000000000001002;
UPDATE sys_menu SET perms = 'job:job:add' WHERE id = 1916000000000001003;
UPDATE sys_menu SET perms = 'job:job:edit' WHERE id = 1916000000000001004;
UPDATE sys_menu SET perms = 'job:job:remove' WHERE id = 1916000000000001005;

UPDATE sys_menu
SET component = 'job/jobLog/index', perms = 'job:jobLog:list'
WHERE id = 1917000000000001000;

UPDATE sys_menu SET perms = 'job:jobLog:list' WHERE id = 1917000000000001001;
UPDATE sys_menu SET perms = 'job:jobLog:query' WHERE id = 1917000000000001002;
UPDATE sys_menu SET perms = 'job:jobLog:remove' WHERE id = 1917000000000001003;
