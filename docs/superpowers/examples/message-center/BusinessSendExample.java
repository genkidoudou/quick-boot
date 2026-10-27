package io.github.genkidoudou.message.example;

import io.github.genkidoudou.common.message.MessageBeanSender;
import io.github.genkidoudou.common.message.MessageSendApi;
import io.github.genkidoudou.common.message.MessageSendCommand;
import io.github.genkidoudou.common.message.MessageSendCommand.Receivers;
import io.github.genkidoudou.common.message.MessageSendResult;

import java.util.List;

/**
 * 业务侧调用和内部 Bean 的写法示例。
 * <p>
 * 不参与编译。审批场景只把这一次的接收人传进去，不修改「运营站内信」通道上的默认用户。
 * 同时选中的企业微信仍发到通道里配置的群，不会改成发给张三。
 */
public class BusinessSendExample {

    private final MessageSendApi messageSendApi;

    public BusinessSendExample(MessageSendApi messageSendApi) {
        this.messageSendApi = messageSendApi;
    }

    /**
     * 采购单提交后通知审批人，并给客服群一条消息。
     * 站内信点击后打开审批页。群机器人的正文不会自动带上这个地址。
     */
    public void afterPurchaseSubmitted(Long approverUserId, String purchaseNo) {
        MessageSendResult result = messageSendApi.send(new MessageSendCommand(
                List.of(
                        "6f1c2a80-1b34-4c2e-9a11-0e5d7c8b1002",
                        "6f1c2a80-1b34-4c2e-9a11-0e5d7c8b1004"),
                "审批待处理",
                "采购申请 " + purchaseNo + " 等待审批。",
                new Receivers(List.of(approverUserId), List.of()),
                "flow",
                purchaseNo,
                "/flow/approve/" + purchaseNo));
        // 返回成功只表示发送单处理完。客服群失败时总状态是 PARTIAL，站内信明细仍可能是 SUCCESS。
        if (!"SUCCESS".equals(result.status())) {
            // 落地时按业务决定是记录日志，还是把失败明细提示给操作者。不要再次 send，否则站内信会再写一行。
        }
    }

    /**
     * 自定义通道的 Bean。管理端内部 Bean 通道的 beanName 填 orderFailNotifier。
     * params 来自通道配置。receivers、bizUrl 来自这一次发送，可空。
     */
    public static class OrderFailNotifier implements MessageBeanSender {

        @Override
        public void send(Context context) {
            // 在这里读 context.params()、context.title()、context.content()。
            // 抛出运行时异常会被记成这一条通道的 FAIL，其他通道继续。
        }
    }
}
