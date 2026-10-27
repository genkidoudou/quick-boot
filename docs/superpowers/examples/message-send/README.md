# 消息发送示例代码

对应设计：`docs/superpowers/specs/2026-10-27-message-send-design.md`。

这些文件只用来把规格里的分支写清楚，不加入 Maven，也不能直接编译。落地时放到 `quickboot-system`：`MessageSender` 进 api 包，其余进 internal。持久化换成 Mapper，Webhook 换成项目里的 HTTP 客户端，Bean 用 Spring 容器按名称取出。

| 文件 | 说明 |
|------|------|
| `schema.sql` | 渠道、群组、消息、快照、收件箱 |
| `MessageSender.java` | Bean 投递接口，以及 Webhook 和 Bean 共用的那份结果 |
| `MessageTypes.java` | 渠道类型、内容类型、消息状态 |
| `TemplateRenderer.java` | `{{name}}` 替换。缺变量只让当前渠道失败 |
| `JsonTexts.java` | 对象和数组变成 JSON 文本，字符串替换时不加引号 |
| `RecipientResolver.java` | 用户、群组、部门展开、去重、排除停用用户 |
| `MessageSendService.java` | 管理端和外部调用共用的发送流程 |
| `ChannelAdminExample.java` | 保存渠道、生成和重置入站 token |
| `GroupAdminExample.java` | 保存群组。部门必须存在，用户和部门各自去重 |
| `AdminSendExample.java` | `POST message/message/send`，可以多选渠道 |
| `OpenSendExample.java` | `POST message/open/send`，token 只绑定一个渠道 |
| `InboxExample.java` | 只看自己的收件箱，打开详情才标已读 |
| `NoticeSender.java` | 一个 `MessageSender` 实现，演示 Bean 渠道以后怎么接 |

阅读顺序建议：`MessageSendService` 为主线，解析和渲染看另外两个类，入口看 `OpenSendExample` 和 `InboxExample`。
