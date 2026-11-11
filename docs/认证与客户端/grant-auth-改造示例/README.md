# Grant 登录改造示例

对应设计：[2026-11-11-app-client-grant-auth-design.md](../../superpowers/specs/2026-11-11-app-client-grant-auth-design.md)

本目录是 **对照实现的示例代码**（含注释），不是可直接编译的工程模块。包名/类名可按仓库现状微调；核心顺序不要变：

1. DDL 加 `client_type`、`grant_types`
2. 登录前解析客户端并校验 grant
3. 按 `check_captcha` 验验证码（在具体 GrantHandler 或统一前置）
4. 写入 `LoginUser.clientId`
5. 前端去掉 Basic secret，改为 `clientId` + login-meta

| 文件 | 说明 |
|------|------|
| `01_flyway_add_grant_types.sql` | 表字段与种子 |
| `02_ClientGrantSupport.example.java` | 解析 `grant_types`、校验客户端 |
| `03_LoginGrantDispatcher.example.java` | 在现有 Dispatcher 上插入客户端校验 |
| `04_PasswordGrantHandler.example.java` | 密码 grant + 按客户端验证码 |
| `05_SmsGrantHandler.example.java` | 扩展 sms grant |
| `06_LoginMetaController.example.java` | login-meta 接口 |
| `07_frontend_request.example.ts` | 去掉 Client Basic |
| `08_frontend_login.example.ts` | 登录与 meta |
| `09_env.example` | 环境变量 |
