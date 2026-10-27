# 消息中心示例代码

对应设计：`docs/superpowers/specs/2026-10-27-message-center-design.md`。

这些文件只用于说明投递规则，不加入 Maven，也不能直接编译。落地时按设计文档放入 `quickboot-common` 与 `quickboot-message`，持久化和 HTTP 换成项目里的 Mapper、用户 API 和 HTTP 客户端。

| 文件 | 说明 |
|------|------|
| `schema.sql` | 四张表的 DDL |
| `ChannelType.java` | 六种通道类型 |
| `MessageSendCommand.java` | 发送参数和接收人 |
| `MessageSendResult.java` | 返回给调用方的总状态和明细 |
| `MessageSendApi.java` | 业务模块注入的入口 |
| `MessageChannel.java` | 单通道投递契约 |
| `MessageChannelRequest.java` | 编排层交给实现的参数 |
| `MessageChannelResult.java` | 一个通道的成功或失败 |
| `MessageBeanSender.java` | 内部 Bean 的扩展点 |
| `MessageSendOrchestrator.java` | 校验、跳过、接收人选择和状态汇总 |
| `InboxMessageChannel.java` | 按人写收件箱 |
| `DingtalkRobotChannel.java` | 钉钉机器人。地址不跟 receivers 变化 |
| `WebhookMessageChannel.java` | 只替换固定占位符的自定义 Webhook |
| `BeanMessageChannel.java` | 只调用 MessageBeanSender |
| `BusinessSendExample.java` | 审批场景的调用方式，以及一个 Bean 实现 |
