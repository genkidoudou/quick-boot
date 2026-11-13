# 第一方应用注册与 Grant 登录设计

日期：2026-11-11  
状态：已确认  
关联示例：已移除；请求签名示例见 `docs/认证与客户端/hmac-anti-replay-示例/` 与 `2026-11-12-request-hmac-anti-replay-design.md`

## 1. 目标

在 **不实现标准 OAuth2 授权服务器（AS）** 的前提下，借用 OAuth2 的 **客户端登记 + `grant_type`** 语义，理清本项目三层认证，并支持按客户端配置：

- 允许哪些登录方式（`grant_types`）
- 是否校验验证码（已有 `check_captcha`）
- 各端 token 超时、启停与审计用的 `clientId`

同时废除「前端藏 `client_secret` + 混淆 Basic」作为安全边界的做法。

## 2. 已确认决策

| 项 | 选择 |
|----|------|
| 落地路径 | 方案一：渐进演进，保留表名 `sys_oauth_client` |
| 登录方式控制 | 字段 **`grant_types`**（不用 `allow_*` 布尔，不用 scope） |
| 验证码 | 保留 **`check_captcha`**（验证码不是 grant_type） |
| 客户端类型 | 新增 **`client_type`**：`public` \| `confidential` |
| 前端 secret | 登录鉴权不依赖 secret；**请求 HMAC 验签**需前端持有 secret（见 2026-11-12 设计，弱约束） |
| 开放调用 | 继续 **`sys_api_key`**，不走登录 grant |
| OAuth2 AS | 本期不做；无明确第三方接入需求前不预实现 |

## 3. 三层边界

```text
① 应用注册 sys_oauth_client
   client_id + grant_types + check_captcha + token_timeout + client_type
② 用户会话 Sa-Token Bearer
   登录成功后的人机身份与 RBAC
③ 开放调用 sys_api_key
   脚本 / 开放消息等；与 password/sms grant 无关
```

| 层 | 凭证 | 职责 | 不做什么 |
|----|------|------|----------|
| 应用 | `client_id`（+ confidential 时服务端 secret） | 识别端、限制 grant、是否验证码、超时 | 不靠 SPA/H5 里的 secret |
| 用户 | Bearer | 登录后权限 | 不替代 grant 校验 |
| 开放 | API Key | 机器调用与范围控制 | 不调用 `/login` 的 password/sms |

## 4. 数据模型

### 4.1 `sys_oauth_client` 变更

| 字段 | 变更 | 说明 |
|------|------|------|
| `client_id` / `client_name` | 保留 | 稳定标识 |
| `client_secret` | 保留 | **仅 confidential 使用**；public 可空，校验时忽略 |
| `client_type` | **新增** | `public` \| `confidential` |
| `grant_types` | **新增** | 逗号分隔，如 `password,sms` |
| `check_captcha` | 保留 | `1` 时 password/sms 必须先验验证码 |
| `token_timeout` | 保留 | 该端登录后超时（秒）；空则全局默认 |
| `api_path_patterns` | 保留降级 | 本期不强制 ACL |
| `status` | 保留 | 停用则拒绝登录 |

### 4.2 支持的 grant_type

| grant_type | 含义 | 适用 |
|------------|------|------|
| `password` | 用户名 + 密码 | 第一方人机端 |
| `sms` | 手机号 + 短信码 | 扩展 grant；按端开放 |
| `refresh_token` | 刷新会话 | 可选，可后期再开 |
| `client_credentials` | 客户端凭证换票 | 仅 confidential；**本期默认不实现换票** |

种子示例：

- `quick-ui`：`public`，`grant_types=password`，`check_captcha=1`
- `quick-h5`：`public`，`grant_types=password,sms`，`check_captcha=1`

### 4.3 与现有代码的关系

仓库已有 `LoginGrantDispatcher` + `PasswordGrantHandler`（按请求参数 `grantType` 分发）。本设计在分发 **之前** 增加：解析 `client_id` → 校验客户端状态 → `grant_type ∈ grant_types` → 按 `check_captcha` 决定是否走验证码；成功后写入 `LoginUser.clientId`。

## 5. 接口形状

### 5.1 登录元数据（供前端渲染 Tab）

`GET /oauth/client/{clientId}/login-meta`（`@SaIgnore`）

响应示例：

```json
{
  "clientId": "quick-h5",
  "clientName": "Quick H5",
  "grantTypes": ["password", "sms"],
  "checkCaptcha": true
}
```

### 5.2 登录

`POST /login`（`@SaIgnore`），参数（form 或 JSON，与现网 `grantType` 风格对齐即可）：

- 公共：`clientId`（或 `client_id`）、`grantType`（或 `grant_type`）
- `password`：`username`、`password`、验证码字段（若需要）
- `sms`：`mobile`、`smsCode`、验证码字段（若需要）

校验顺序：

1. 客户端存在且启用  
2. `grantType` 在 `grant_types` 列表中  
3. 若 `check_captcha=1`，验证码通过  
4. 执行对应 `LoginGrantHandler`  
5. `LoginHelper.login`；会话与登录日志带 `clientId`；超时优先用客户端 `token_timeout`

兼容期（可配置开关，默认开一段时间）：

- 缺 `clientId` → 默认 `quick-ui`  
- 缺 `grantType` → 默认 `password`  
兼容关闭后二者必填。

### 5.3 业务请求

仅 Bearer。无 Token 时 **不再** 注入 Client Basic。公开接口靠 `ignore-url` + 验证码/限流。

## 6. 前端改造要点

| 项 | 动作 |
|----|------|
| env | 保留 client id；删除 `VITE_OAUTH_CLIENT_SECRET` |
| 请求拦截 | 删除混淆 Basic 分支 |
| 登录页 | 先 login-meta，再按 `grantTypes` / `checkCaptcha` 渲染 |
| 客户端管理页 | 维护 `client_type`、`grant_types`、`check_captcha`、`token_timeout` |

## 7. 迁移

1. Flyway 增加 `client_type`、`grant_types`，回填种子。  
2. 旧 Basic 头：忽略不校验。  
3. 更新 README 与各端 `.env*` 说明。  
4. 兼容期结束后改为 `clientId`/`grantType` 必填。

## 8. 非目标（本期不做）

- 完整授权码流程 / 第三方开放平台 AS  
- `client_credentials` 换票落地  
- 强制 `api_path_patterns` 路径 ACL  
- `sys_api_key` 存储改哈希（另项）  
- 用 scope 替代 `grant_types`

## 9. 成功标准与测试

见实现核对清单：

- [ ] 未签约 grant → 拒绝  
- [ ] `check_captcha` 开关行为正确  
- [ ] public 端无 secret 可登录  
- [ ] 无 Client Basic 仍可拉验证码并登录  
- [ ] 会话/登录日志 `clientId` 正确  
- [ ] API Key 开放调用不受登录 grant 影响  
- [ ] 前端只展示允许的登录方式  

## 10. 示例代码位置

完整带注释的改造示例（非直接编译进工程，供对照实现）：

`docs/认证与客户端/grant-auth-改造示例/`
