# quick-ui 登录 + 菜单精简清理设计

日期：2026-09-16  
范围：`quick-ui` 前端  
目标：只保留登录与菜单管理相关能力，删除其余业务页及对应 API。

## 背景

`quick-ui` 当前包含系统管理、监控、消息、代码生成等多套业务视图。需要收敛为「能登录 + 能管菜单」的最小壳，便于后续按需加回功能。

已确认方案：**views + 对应 api 一起清**（方案 2），保留范围对应选项 A。

## 成功标准

1. 应用可启动，访问 `/login` 可登录。
2. 登录后可进首页 `/index`，侧栏仍按后端动态菜单渲染。
3. 菜单管理页（`system/menu`）可正常列表/增删改（依赖字典、积木报表选项仍可用）。
4. 已删除业务页与对应 API 文件不存在；路由不再引用已删页面。
5. 顶栏不再请求站内信未读接口（避免运行时报错）。

## 保留

### views

- `src/views/login.vue`
- `src/views/system/menu/**`
- `src/views/index.vue`
- `src/views/redirect/**`
- `src/views/error/401.vue`
- `src/views/error/404.vue`

### api

- `src/api/login.js`
- `src/api/captcha.js`
- `src/api/menu.js`（动态路由）
- `src/api/system/menu.js`
- `src/api/system/dict/data.js`（菜单页 `useDict`）
- `src/api/report/jimu.js`（菜单表单积木报表/BI 选项）
- `src/api/_factory/createCrudApi.js`
- `src/api/common/file.js`（通用上传组件仍引用）

### 不动

- `layout`、`permission.js`、登录相关 store、通用 `components` / `packages`

## 删除

### views

- `src/views/system/` 下除 `menu` 外全部（user、role、dept、dict、config、file、fileClassify、oauthClient 等）
- `src/views/monitor/**`
- `src/views/message/**`
- `src/views/tool/**`
- `src/views/oauth/**`
- `src/views/_schemas/**`
- `src/views/social-bind.vue`

### api

- `src/api/message/**`
- `src/api/monitor/**`
- `src/api/tool/**`
- `src/api/system/user.js`、`role.js`、`dept.js`、`config.js`、`file.js`、`fileClassify.js`、`oauthClient.js`、`oauthProvider.js`、`notice.js`
- `src/api/system/dict/type.js`
- `src/api/oauth/authorize.js`

## 连带改动

1. **路由**（`src/router/index.js`）  
   移除 `/social-bind`、`/oauth/callback` 常量路由，避免 lazy import 指向已删文件。

2. **Navbar**（`src/layout/components/Navbar.vue`）  
   移除站内信铃铛、未读数轮询、`getInboxUnreadCount` 引用及跳转 `/message/inbox`。

## 非目标（本轮不做）

- 不清理无引用的业务专用 components（如 `components/monitor`），除非构建直接失败。
- 不改后端菜单数据；侧栏点到已删页面时出现 404/缺组件属预期。
- 不新增用户/角色/部门管理页。

## 风险与处理

| 风险 | 处理 |
|------|------|
| 菜单页依赖字典 API | 保留 `system/dict/data.js` |
| 菜单表单依赖积木报表 API | 保留 `report/jimu.js` |
| Navbar 仍调 message API | 删除铃铛相关逻辑 |
| 动态路由仍指向已删 view | 由后端菜单配置决定；前端不拦截，404 可接受 |

## 验证

1. 启动 `quick-ui`，打开登录页无控制台报错。
2. 登录后进入首页。
3. 打开菜单管理，搜索/展开/打开新增或编辑抽屉可加载。
4. 顶栏无站内信铃铛，无周期性 inbox 请求。
