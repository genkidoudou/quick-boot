# 消息中心设计

日期：2026-10-27  
状态：设计稿，对应当日讨论结论，尚未落地到业务模块  
预览：`docs/superpowers/specs/2026-10-27-message-center-preview.html`  
示例代码：`docs/superpowers/examples/message-center/`（只作阅读，不参与 Maven 编译）

## 1. 目标

提供一个消息中心：业务代码和管理端手工发送走同一条发送路径，并留下发送单和每条通道的明细。

通道按类型建实例，同一种类型可以有多条。发送时只提交通道 ID、标题和正文。接收人、机器人地址、Webhook、Bean 写在通道配置里。接口可以额外传入本次接收人，只影响这一次的站内信。

## 2. 已确认决策

| 项 | 结论 |
|----|------|
| 入口 | 业务接口和管理端手工发送都要，都写发送记录 |
| 通道模型 | 方案 1：类型化通道实例 |
| 通道标识 | 保存时生成 UUID，调用方不维护编码 |
| 站内端 | 不再区分 PC / H5，类型只有「站内信」 |
| 已读 | 只有站内信按人记录已读、未读 |
| 默认接收人 | 写在通道上。手工发送一律用默认接收人 |
| 接口接收人 | 可选。传入后，本次选中的站内信改用这批人，不写回通道，也不改变机器人、Webhook、Bean 的投递地址 |
| 业务地址 | 可选，挂在这一次发送上。只有站内信提供点击跳转 |
| 投递方式 | 调用线程内同步投递。返回时每条明细已是终态 |
| 短信、邮件 | 不在本设计内 |

## 3. 不在本期

- 消息模板、变量编排、附件
- 按人的爱语飞飞 token（token 只配置在通道上）
- 发送失败后的自动重试、退避、只补发失败通道
- 业务幂等键（相同业务单据重复调用会再发一遍）
- 手工发送时临时指定接收人
- 撤回已发出的第三方消息

这些限制的后果见第 12 节。

## 4. 模块与依赖

新建 Maven 模块 `quickboot-message`，包 `io.github.genkidoudou.message`，Modulith 类型 OPEN。`quickboot-web` 依赖该模块并加入组件扫描。Controller 放在 `message.internal.controller`。

依赖方向：

- `message` 依赖 `common`、`core`、`system`。用户和部门只通过 `system.api` 查询。
- `system`、`job` 不依赖 `message`，避免和「消息要查用户」形成循环。
- 业务发送入口放在 `common` 的 `MessageSendApi`。`message` 提供唯一实现。业务模块只注入该接口，源码不引用 `message` 包。

`common.message` 保留投递契约，并替换现有枚举。现有 `SMS`、`MAIL` 删除。六种通道的实现类放在 `message` 内部，不放在 `common`。`BEAN` 类型的实现只负责按名称取出 `MessageBeanSender`。

站内信按部门展开时，`SysDeptApi.listSelfAndChildIds` 取部门及下级。`SysUserApi` 需要新增：按部门 ID 列出状态正常的用户 ID。现有接口没有这个方法。

内部 Bean 的契约是 `common.message.MessageBeanSender`。任何已扫描的模块都可以注册实现。管理端下拉只列出这个类型的 Bean，做法与定时任务列出 `ITask` 相同。按名字取到的 Bean 如果不是该类型，发送明细记失败，不调用其他 Bean。

## 5. 通道

### 5.1 类型

| 枚举 | 界面名称 | 配置 | 一次发送的投递次数 |
|------|----------|------|--------------------|
| `INBOX` | 站内信 | `userIds`、`deptIds`，均可空 | 展开后每人一行收件箱 |
| `DINGTALK_ROBOT` | 钉钉机器人 | `webhook` 必填，`secret` 可空 | 1 次，发到该 Webhook |
| `WECOM_ROBOT` | 企业微信机器人 | `webhook` 必填 | 1 次，发到该 Webhook |
| `IYUU` | 爱语飞飞 | `tokens` 至少一个 | 通道内每个 token 发一次，明细仍只有一行 |
| `WEBHOOK` | 自定义 Webhook | `url` 必填，`method`、`headers`、`bodyTemplate` 可空 | 1 次 |
| `BEAN` | 内部 Bean | `beanName` 必填，`params` 可空 | 1 次 |

`userIds`、`deptIds` 存用户 ID 和部门 ID，不存姓名。界面可以用姓名展示，提交时转成 ID。

钉钉、企业微信、爱语飞飞的正文是标题加内容，不自动拼接业务地址。Webhook 的正文模板和 Bean 的上下文可以读取本次的接收人与业务地址。

### 5.2 配置示例

站内信，默认发给一个部门：

```json
{ "userIds": [], "deptIds": [100] }
```

站内信，默认接收人为空，只接受接口传入的接收人：

```json
{ "userIds": [], "deptIds": [] }
```

钉钉机器人：

```json
{ "webhook": "https://oapi.dingtalk.com/robot/send?access_token=...", "secret": "SEC..." }
```

自定义 Webhook。模板只替换四个固定占位符，不执行表达式：

```json
{
  "url": "https://ops.example/hook",
  "method": "POST",
  "headers": { "Authorization": "Bearer ***" },
  "bodyTemplate": "{\"title\":\"${title}\",\"content\":\"${content}\",\"bizUrl\":\"${bizUrl}\",\"receivers\":\"${receivers}\"}"
}
```

`bodyTemplate` 为空时，默认 POST JSON：`title`、`content`、`bizUrl`、`receivers`。`method` 只允许 `GET` 或 `POST`，默认 `POST`。

### 5.3 管理端

通道列表展示名称、通道 ID、类型、摘要、状态。摘要由服务端生成，不包含密钥、完整 Webhook 和 token。列表接口不返回 `channelConfig`。详情接口返回完整配置，供编辑。

新增时服务端生成 UUID。编辑不能改通道 ID，也不能改通道类型。停用使用 `COMMON_STATUS`：`0` 正常，`1` 停用。

## 6. 发送

### 6.1 调用参数

业务侧调用 `MessageSendApi.send`。来源由实现固定为 `API`，调用方不能自行标成手工发送。

| 字段 | 必填 | 含义 |
|------|------|------|
| `channelIds` | 是 | 通道 UUID 列表，按传入顺序投递，先去重 |
| `title` | 是 | 标题，最长 200 |
| `content` | 是 | 正文 |
| `receivers` | 否 | `userIds`、`deptIds`。空对象视为未传 |
| `bizType` | 否 | 追溯类型，如 `flow`、`job` |
| `bizId` | 否 | 追溯单号 |
| `bizUrl` | 否 | 站内信点击地址 |

`bizUrl` 只允许两种：

- 站内路径：以单个 `/` 开头，且不是 `//`
- 外部地址：以 `http://` 或 `https://` 开头

其他值拒绝本次调用，不落发送单。

管理端手工发送的字段是标题、正文、通道、可选业务地址。没有接收人参数。来源记为 `ADMIN`。管理端按钮使用现有 `@Idempotent` 防重复点击。该注解只挡住短时间重复提交，不是第 12 节所说的业务幂等。

### 6.2 接收人怎么生效

对本次选中的每一条通道分别判断。

站内信：

1. 请求里的 `receivers` 非空：本次只用这批用户和部门，来源记为「接口传入」。通道上的默认接收人不用。
2. 未传 `receivers`：使用通道配置，来源记为「通道配置」。
3. 部门展开为自身及全部下级，再查出这些部门里状态正常的用户。
4. 与直接指定的用户合并、去重。停用用户去掉。
5. 结果为空：该明细状态为 `SKIPPED`，原因「未指定接收人」。不写收件箱。

同一条站内信通道内，一个用户只写一行。同一次发送选了两条站内信通道，并且两边都包含这个用户，则写两行。接口传入接收人时，每一条被选中的站内信都使用这同一批人。

钉钉、企业微信、爱语飞飞、Webhook、Bean：

- 投递地址始终来自通道配置。
- `receivers` 不改变地址，明细的「接收人来源」为空。
- Webhook 模板和 Bean 仍能读到原始 `receivers`、`bizUrl`。

通道不存在：该 ID 对应明细 `FAIL`，原因「通道不存在」。通道停用：明细 `SKIPPED`，原因「通道已停用」。这两种情况都不中断其他通道。

### 6.3 发送单状态

| 明细终态 | 含义 |
|----------|------|
| `SUCCESS` | 该通道投递完成 |
| `FAIL` | 投递失败，或通道不存在，或 Bean 类型不对 |
| `SKIPPED` | 通道停用，或站内信没有接收人 |

爱语飞飞：一个 token 失败则整条明细 `FAIL`。`errorMsg` 写失败 token 的脱敏值（只保留前后少量字符）。已成功的 token 不在本期单独重试。

发送单状态在全部明细结束后计算：

- 全部 `SUCCESS`：`SUCCESS`
- 至少一条 `SUCCESS`，且存在 `FAIL` 或 `SKIPPED`：`PARTIAL`
- 没有 `SUCCESS`：`FAIL`

调用返回 `MessageSendResult`：发送单 ID、发送单状态、每条明细的通道 ID、状态和错误原因。调用方不能只根据「没抛异常」判断全部成功。

## 7. 站内信、已读与业务地址

展开后的每个用户写一行 `msg_inbox`。字段包括发送单 ID、通道 ID、用户 ID、标题和正文快照、`bizUrl` 快照、`readFlag`（`0` 未读 / `1` 已读）、`readTime`。

收件箱查询只使用当前登录用户，不接受调用方传入的用户 ID。

已读数不写进发送明细。打开发送明细时，按该发送单和该通道下的收件箱行实时统计已读、未读。非站内信明细不返回已读数。

点击规则：

- 打开一条站内信时标为已读。
- `bizUrl` 为空：只展示内容。
- 站内路径：在当前应用内打开。
- `http` / `https`：新窗口打开。
- 点「查看业务」同样先标已读，再打开地址。

钉钉、企业微信、爱语飞飞没有点击跳转。它们的消息体不自动附带 `bizUrl`。

## 8. 表

字符集与现有表一致：`utf8mb4` / `utf8mb4_unicode_ci`。`msg_channel`、`msg_inbox` 带审计字段和逻辑删除。`msg_send`、`msg_send_detail` 按日志表处理，只追加、不逻辑删除。

完整 DDL 见 `docs/superpowers/examples/message-center/schema.sql`。落地时同步写入 `sdd/ddl`，并由 `quickboot-web` 的 Flyway 迁移。

### msg_channel

| 字段 | 含义 |
|------|------|
| `id` | `char(36)` UUID，主键 |
| 审计字段、`del_flag`、`remark`、`digest` | 与 `BaseEntity` 及现有表一致 |
| `channel_name` | 名称 |
| `channel_type` | 字典 `MSG_CHANNEL_TYPE` |
| `channel_config` | JSON 文本 |
| `status` | 字典 `COMMON_STATUS` |

### msg_send

| 字段 | 含义 |
|------|------|
| `id` | 雪花主键。原型里的 `20261027001` 只是示意 |
| `create_by`、`create_time` | 触发人。接口调用可以没有登录用户，`create_by` 可空 |
| `title`、`content` | 标题、正文 |
| `source` | `API` / `ADMIN`，字典 `MSG_SEND_SOURCE` |
| `biz_type`、`biz_id` | 追溯，不唯一 |
| `biz_url` | 业务地址，可空 |
| `receivers_json` | 请求传入的接收人原文。空表示未传 |
| `status` | `PENDING` / `SUCCESS` / `PARTIAL` / `FAIL` |
| `start_time`、`end_time` | 投递起止 |

插入时状态为 `PENDING`。全部通道处理完后改为终态。同步实现里，调用方拿到返回值时已是终态。

### msg_send_detail

一行对应一条通道实例。保存当时的通道名称、类型、目标摘要。目标摘要脱敏。

| 字段 | 含义 |
|------|------|
| `send_id` | 发送单 |
| `channel_id` | 通道 UUID。通道后来被逻辑删除，明细仍保留快照 |
| `channel_name`、`channel_type` | 快照 |
| `target_digest` | 如「86 人」「Webhook 末尾 a3f9」「2 个 token」 |
| `audience` | 仅站内信：`CHANNEL` 或 `REQUEST`。其他类型为空 |
| `status` | `SUCCESS` / `FAIL` / `SKIPPED`。同步投递直接写终态 |
| `error_msg` | 失败或跳过原因 |
| `start_time`、`end_time`、`cost_ms` | 耗时 |

### msg_inbox

| 字段 | 含义 |
|------|------|
| `send_id`、`channel_id` | 来源 |
| `user_id` | 接收用户 |
| `title`、`content`、`biz_url` | 快照 |
| `read_flag`、`read_time` | `0` / `1` |

索引：`(user_id, read_flag)`、`send_id`。

字典：

- `MSG_CHANNEL_TYPE`：六种通道类型
- `MSG_SEND_SOURCE`：`API`、`ADMIN`
- `MSG_SEND_STATUS`：`PENDING`、`SUCCESS`、`PARTIAL`、`FAIL`
- `MSG_DETAIL_STATUS`：`SUCCESS`、`FAIL`、`SKIPPED`

## 9. 处理顺序

1. 校验标题、正文、通道 ID、`bizUrl`。
2. 插入 `msg_send`，状态 `PENDING`。有 `receivers` 时写入 `receivers_json`。
3. 按通道 ID 去重后逐条处理，互不影响。每条处理完插入一行终态明细：
   - 通道不存在或已删除：`FAIL`。
   - 停用：`SKIPPED`。
   - 站内信先展开用户。展开结果为空则 `SKIPPED`，不调用通道实现。
   - 其余情况按类型调用 `MessageChannel`。站内信实现为每个用户插入未读的 `msg_inbox`。
   - 该通道抛出的异常记为这条明细的 `FAIL`，然后继续下一条。
4. 汇总发送单状态，写 `end_time`，返回结果。进程若在中途退出，发送单会停在 `PENDING`，已经插入的明细保持各自终态。

站内信的数据库写入和本次发送单在同一事务里。第三方 HTTP 也在这条调用线程上执行。因此业务方如果包在自己的事务里调用，可能出现第 12 节描述的提交顺序问题。

## 10. HTTP 接口

只使用 `GET` 和 `POST`。现有 `quick-h5` 把标已读写为 `PUT /message/inbox/read`，落地时改为下面的 `POST`，并增加 `bizUrl` 字段。

管理端权限前缀 `message:`。

| 方法 | 路径 | 权限 | 作用 |
|------|------|------|------|
| POST | `/message/channel/page` | `message:channel:list` | 通道分页，不含配置原文 |
| GET | `/message/channel/{id}` | `message:channel:query` | 通道详情，含配置 |
| POST | `/message/channel/add` | `message:channel:add` | 新增，返回生成的 UUID |
| POST | `/message/channel/edit` | `message:channel:edit` | 修改名称、状态、配置，不改 ID 和类型 |
| POST | `/message/channel/changeStatus` | `message:channel:edit` | 启用或停用 |
| GET | `/message/bean/list` | `message:channel:query` | `MessageBeanSender` 列表 |
| POST | `/message/send` | `message:send:add` | 手工发送 |
| POST | `/message/record/page` | `message:record:list` | 发送单分页 |
| GET | `/message/record/{id}` | `message:record:query` | 发送单及明细；站内信明细附带实时已读数 |
| POST | `/message/inbox/page` | 登录即可 | 当前用户收件箱 |
| GET | `/message/inbox/{id}` | 登录即可 | 详情，非本人返回无权限 |
| POST | `/message/inbox/read` | 登录即可 | 将当前用户的指定收件箱行标已读 |
| GET | `/message/inbox/unread-count` | 登录即可 | 当前用户未读数 |

业务进程内调用不走 `/message/send`，直接调用 `MessageSendApi`。本文不提供给外部系统的开放 HTTP。若以后要开放，需要单独的调用凭证，不能复用登录用户权限。

## 11. 页面

与预览页一致，四个页面：

- 通道管理：按类型切换配置表单。通道 ID 只读。
- 手工发送：标题、正文、可选业务地址、勾选通道。停用通道可以勾选，结果为跳过。
- 发送记录：区分来源和接收人是「通道配置」「未传」还是「接口传入」。明细里只有站内信有已读数和接收人来源。
- 收件箱：未读 / 已读标识。有 `bizUrl` 时显示「查看业务」。

`quick-ui` 使用收件箱页面。`quick-h5` 沿用现有收件箱，去掉对 PC / H5 通道的区分，改为读取全部站内信，并改为 `POST` 标已读。

## 12. 已知限制

以下是本设计的当前行为，不是待实现的隐藏需求。

1. 没有业务幂等键。调用超时后再次 `send`，会新开一张发送单，已经成功的站内信和机器人消息会再发一次。
2. 一次调用选中多条站内信，并且传入了 `receivers` 时，每条站内信都发给这同一批人。用户会收到多条内容相同的站内信。
3. 投递在调用线程内同步完成。业务事务若在发送之后回滚，机器人消息已经发出；若在事务提交前发出，对方打开 `bizUrl` 时单据可能还不存在。
4. `bizUrl` 允许任意 `http` / `https`。能调用发送能力的代码可以把用户带到外部站点。
5. 自定义 Webhook 由服务器按配置发起请求，本期不限制目标是否为内网地址。
6. 爱语飞飞只认通道上的 token，不会按 `receivers` 去找每个人自己的 token。
7. 手工发送不能临时改接收人。只发给三个人时，需要事先建好通道，或走接口并传入 `receivers`。
8. 不处理第三方频率限制，失败后不会自动重试。
9. `bizUrl` 只有一个。管理端和 H5 路由不一致时，另一端可能没有这个页面。
10. 按大部门展开时，本期不限制写入收件箱的行数。

## 13. 与现有代码的关系

`quickboot-common` 里已有 `ChannelType`、`MessageChannel`、`MessageChannelRequest`、`MessageChannelResult`，枚举含 `INBOX`、`IYUU`、`SMS`、`MAIL`。落地时按本文替换，不保留短信和邮件。

`quick-ui/src/utils/messageRecipientTargets.js` 仍按用户、部门编解码，可以继续用于站内信配置。它里面的联系人分组不在本设计使用。

`quick-h5` 已有收件箱页面和 `/message/inbox` 调用，标已读目前是 `PUT`。落地时改成第 10 节的 `POST`，响应增加 `bizUrl`。

预览页里的单号、已读人数是界面示意。正式已读人数以 `msg_inbox` 实时统计为准。
