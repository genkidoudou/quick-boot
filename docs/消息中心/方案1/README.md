# 统一发送示例（方案 1）

这些文件只供阅读，不加入 Maven。`java` 文件都在默认包里，可以在本目录编译运行 `LeaveScenario`。

统一入口是 `MessageSender.send`：

```text
send(通道 ID, 地址列表或群组 ID, 消息) → 整单拒绝或回执列表
```

`addresses == null` 表示没传地址列表，空列表表示传了一个空列表。`groupId == null` 表示没传群组。空白字符串形式的群组 ID 要先变成 null 再调用。

## 文件

| 文件 | 作用 |
|------|------|
| `schema.sql` | 通道、群组、群组地址、站内信 |
| `sample.sql` | 与场景相同的主键，以及站内信发出后的行 |
| `MessageSender.java` | 统一发送接口 |
| `MessageSendService.java` | 按固定顺序做整单拒绝，然后交给适配器 |
| `RecipientAdapters.java` | 邮件、短信、站内信、企业微信应用 |
| `RobotAdapters.java` | 钉钉机器人、企业微信机器人、IYUU |
| `LeaveScenario.java` | 用内存数据跑完「请假已通过」并打印回执 |

邮件、短信、机器人和企业微信应用的回执不写表。站内信写入 `msg_inbox`。示例里的 `Outbound` 代替真实的 SMTP 和 HTTP。

同目录上一级的 `msg_schema.sql` 是另一套模型，不要和这里的表混用。
