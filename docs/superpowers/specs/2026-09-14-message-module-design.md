# 消息模块（站内信 + 多通道发送）设计

日期：2026-09-14  
状态：待用户审阅

## 1. 背景与目标

业务需要统一的「消息发送」能力：支持站内信与外部通道（首期 IYUU，后续邮件/短信等），并提供可配置的接收人与渠道；系统用户可在管理端与 H5 查看收件箱。

参考：[爱语飞飞 GET 发送通知](https://iyuu.cn/article)  
`GET https://iyuu.cn/{token}.send?text={标题}&desp={内容}`，成功时 `errcode=0`。

### 1.1 目标

- 新建业务域模块 `quickboot-module-message`（Modulith 一对一）。
- `common` 定义通道 SPI，便于扩展邮件、短信、钉钉等。
- 独立联系人通讯录；接收人可从联系人或系统用户选择。
- 管理端可配置渠道与默认接收人；代码/HTTP 调用时可显式指定接收人与渠道（覆盖配置）。
- 系统用户站内信：列表、详情、已读未读、未读数；quick-ui + quick-h5。
- 发送方可来自系统代码或管理员手动发送（不做用户互发）。

### 1.2 非目标（一期不做）

- 用户之间互发站内信 / IM。
- 短信、邮件真实投递（仅通道骨架 + 联系人字段存储）。
- 钉钉等其它通道实现。
- 富文本模板引擎、定时群发、异步队列（一期同步发送，接收人有上限）。
- 联系人绑定系统用户。

## 2. 决策摘要

| 议题 | 选择 |
|------|------|
| 模块落点 | 新建 `quickboot-module-message`（方案 1） |
| 一期真发通道 | `INBOX` + `IYUU` |
| 一期骨架通道 | `SMS`、`MAIL`（明细记 SKIPPED） |
| 联系人 | 独立通讯录，不关联 `sys_user` |
| 站内信适用范围 | 仅系统用户；联系人不可选 INBOX |
| 发送方 | 系统/代码 + 管理端手动发 |
| 接收人来源 | 联系人列表 / 系统用户；配置默认 + 接口覆盖 |
| 前端 | quick-ui（管理 + 收件箱）+ quick-h5（收件箱） |
| 依赖 | `message` → `system.api`（如 `SysUserQueryFacade`）；禁止 `system` → `message.internal` |

## 3. 架构

```text
quickboot-common
  └─ message SPI：MessageChannel / ChannelType / MessageChannelRequest / Result

quickboot-module-message
  api/       MessageSendFacade + 对外 DTO/命令
  internal/
    contact/         联系人 CRUD
    profile/         通知配置（默认渠道与接收人）
    channelconfig/   渠道启用与 IYUU 默认令牌等
    dispatch/        发送编排
    channel/         Inbox / Iyuu / SmsStub / MailStub
    inbox/           站内信查询与已读
    record/          发送主单与明细

quickboot-module-system
  └─ 仅暴露用户查询 api，不依赖 message

前端
  quick-ui：联系人、消息配置、手动发送、发送记录、顶栏铃铛 + 收件箱
  quick-h5：收件箱列表/详情/已读/未读角标
```

### 3.1 发送编排

1. 解析渠道与接收人：请求显式参数优先，否则使用 `profileCode`（或缺省 `DEFAULT`）中的默认值。
2. 校验：
   - 至少一种渠道、至少一名接收人。
   - `CONTACT` + `INBOX` 拒绝。
   - 单次接收人数量上限（建议 50，可配置）。
3. 写 `msg_message` 主单。
4. 按「接收人 × 渠道」展开明细并投递：
   - `INBOX` → 写 `msg_inbox` + 明细 SUCCESS。
   - `IYUU` → HTTP GET；联系人用其 `iyuu_token`，否则用渠道配置默认令牌；无令牌则失败。
   - `SMS` / `MAIL` → Stub，明细 `SKIPPED`，不阻断其它渠道。
5. 汇总主单状态：`SUCCESS` / `PARTIAL` / `FAIL`。

站内信成功与 IYUU 失败相互独立（部分成功场景正常）。

## 4. 数据模型

表前缀 `msg_`，审计与逻辑删除对齐 `BaseEntity`。

### 4.1 `msg_contact`

| 列 | 说明 |
|----|------|
| `contact_id` | PK |
| `name` | 显示名 |
| `mobile` | 手机（短信用，一期仅存储） |
| `email` | 邮箱（一期仅存储） |
| `iyuu_token` | IYUU 令牌，可空 |
| `status` | 启用/停用（`sys_normal_disable`） |
| BaseEntity | |

### 4.2 `msg_message`

| 列 | 说明 |
|----|------|
| `message_id` | PK |
| `title` / `content` | 标题、正文 |
| `channels` | 本次渠道列表（如 `INBOX,IYUU`） |
| `send_status` | 汇总：成功/部分失败/失败（字典自建 `msg_send_status`） |
| `sender_type` | `SYSTEM` / `ADMIN` |
| `sender_id` | 管理员用户 ID，系统触发可空 |
| `profile_code` | 使用的配置编码，可空 |
| BaseEntity | |

### 4.3 `msg_message_recipient`

| 列 | 说明 |
|----|------|
| `id` | PK |
| `message_id` | 主单 |
| `recipient_type` | `USER` / `CONTACT` |
| `recipient_id` | 用户 ID 或联系人 ID |
| `channel` | `INBOX` / `IYUU` / `SMS` / `MAIL` |
| `target` | 实际投递目标（用户 ID / token / 手机 / 邮箱） |
| `status` | 成功/失败/跳过（`msg_recipient_status`） |
| `error_msg` | 失败原因 |
| `sent_time` | 投递时间 |

### 4.4 `msg_inbox`

| 列 | 说明 |
|----|------|
| `inbox_id` | PK |
| `message_id` | 关联主单 |
| `user_id` | 接收系统用户 |
| `title` / `content` | 列表冗余 |
| `read_flag` | `0` 未读 / `1` 已读 |
| `read_time` | 已读时间 |
| BaseEntity | |

### 4.5 `msg_channel_config`

按渠道一行：渠道类型、是否启用、IYUU 默认令牌（仅 IYUU 有意义）、扩展 JSON（预留邮件 SMTP 等）、备注。

### 4.6 `msg_notify_profile`

| 列 | 说明 |
|----|------|
| `profile_id` | PK |
| `profile_code` | 唯一编码，如 `DEFAULT`、`JOB_FAIL` |
| `profile_name` | 显示名 |
| `channels` | 默认渠道 |
| `default_user_ids` | JSON 数组 |
| `default_contact_ids` | JSON 数组 |
| `status` | 启用/停用 |
| BaseEntity | |

管理端「配置消息」主要维护 profile + channel_config。

Flyway：在 `quickboot-app` 新增迁移（如 `V37__msg_message_module.sql`），含表、字典、菜单权限。

## 5. API 与 SPI

### 5.1 通道 SPI（common）

```text
enum ChannelType { INBOX, IYUU, SMS, MAIL }

interface MessageChannel {
  ChannelType channelType();
  MessageChannelResult send(MessageChannelRequest request);
}
```

由 `MessageDispatcher` 按类型路由到对应 Bean；未知或未注册通道视为失败。

### 5.2 Facade

```text
MessageSendFacade.send(MessageSendCommand): MessageSendResult
```

`MessageSendCommand`：`title`、`content`、`channels`、`userIds`、`contactIds`、`profileCode`（均可选组合，受第 3.1 节规则约束）。

### 5.3 HTTP（示意）

| 能力 | 路径意向 |
|------|----------|
| 联系人 CRUD/分页 | `/message/contact/**` |
| 通知配置 | `/message/profile/**` |
| 渠道配置 | `/message/channel-config/**` |
| 手动发送 | `POST /message/send` |
| 发送记录 | `/message/record/**` |
| 收件箱分页 | `POST /message/inbox/page` |
| 收件箱详情 | `GET /message/inbox/{id}` |
| 批量已读 | `PUT /message/inbox/read` |
| 未读数 | `GET /message/inbox/unread-count` |

权限：管理类接口挂菜单权限；收件箱接口仅返回当前登录用户数据。

### 5.4 IYUU 实现要点

- URL：`https://iyuu.cn/{token}.send`
- Query：`text`（标题）、`desp`（内容）；参数过大时文档建议改 POST，一期先 GET，内容超长截断或失败并记明细。
- 成功：`errcode == 0`；否则记失败与 `errmsg`。
- Token 禁止写进日志明文（可脱敏后几位）。

## 6. 前端

### 6.1 quick-ui

1. 联系人管理（CRUD）
2. 消息配置（profile + 渠道）
3. 发送通知（选用户/联系人、渠道、标题内容）
4. 发送记录（主单 + 明细）
5. 布局顶栏铃铛（未读数）+ 收件箱页

### 6.2 quick-h5

1. 收件箱列表 / 详情 / 已读
2. 未读角标（挂入现有 H5 导航/首页入口习惯）

页面复用项目现有 CRUD / schema 模式；禁止页面裸调 axios（走 `api` 封装）。

## 7. 错误处理与约束

- 同步发送；超时/网络错误写入明细，不抛垮整个请求（除非参数校验失败）。
- 校验失败：业务警告异常（参数不合法），不写主单或写后立即标记失败（实现时选一种并保持一致，推荐**校验失败不写库**）。
- Stub 渠道：`SKIPPED`，主单汇总时若仅有 SKIPPED + SUCCESS 则仍可视为 SUCCESS；存在 FAIL 则为 PARTIAL/FAIL。
- 密钥类配置（IYUU token）存库；前端列表脱敏展示。

## 8. 测试与验收

- 单元：Dispatcher 校验（联系人+INBOX）、汇总状态、Stub 不阻断。
- 集成/手工：
  1. 管理员发站内信 → 目标用户 ui/h5 可见、未读数变化、已读生效。
  2. 联系人 + IYUU（有效 token）→ 明细成功；无效 token → 明细失败。
  3. 勾选 SMS/MAIL → SKIPPED，站内信仍成功。
  4. `profileCode` 默认接收人 vs 接口显式覆盖。
  5. Modulith `verify()` 仍通过；`message` 不依赖 `system.internal`。

## 9. 实现顺序（供后续 plan）

1. Maven 模块脚手架 + Modulith 注册 + app 依赖  
2. Flyway 表/字典/菜单  
3. common SPI + Inbox/Iyuu/Stub 实现 + Dispatcher + Facade  
4. 联系人 / profile / channel-config CRUD  
5. 发送记录与手动发送 API  
6. 收件箱 API  
7. quick-ui 页面与铃铛  
8. quick-h5 收件箱  

## 10. 已确认需求问答摘要

- 用途含业务用户站内信（收件箱）。
- 一期真发：站内信 + IYUU；手机/邮件骨架。
- 发送方：系统/代码 + 管理端；非用户互发。
- 配置可配渠道与接收人；调用时可指定接收人。
- 接收人：联系人或系统用户；方式：站内信/手机/邮件/IYUU。
- 联系人独立；联系人不可用站内信。
- 前端：ui 全套 + h5 收件箱。
- 架构：独立 `module-message`。
