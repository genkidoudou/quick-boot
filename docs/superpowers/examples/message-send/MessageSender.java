package io.github.genkidoudou.system.api.message;

import java.util.List;

/**
 * Bean 渠道的投递接口。示例，不参与编译。
 * <p>
 * 落地时放在 {@code quickboot-system} 的 api 包。容器里同名 Bean 必须实现它。
 * 发送前已经按渠道模板渲染完毕，这里拿到的是渲染结果，不是原始变量。
 */
public interface MessageSender {

    /**
     * 投递一份已经渲染好的消息。
     * <p>
     * 抛出的异常只让当前渠道失败，不撤回其他渠道。异常信息会被截到 500 字写进渠道快照。
     *
     * @param delivery 标题、内容类型、渲染正文、冻结后的收件人
     */
    void send(MessageDelivery delivery);

    /**
     * Webhook 和 Bean 收到的是同一份结构。出站请求不带入站 token。
     *
     * @param title             消息标题
     * @param contentType       TEXT、HTML、JSON、MARKDOWN
     * @param content           该渠道渲染后的正文
     * @param recipientUserIds  本次冻结的收件人，顺序不作为验收条件
     */
    record MessageDelivery(String title, String contentType, String content, List<Long> recipientUserIds) {
    }
}
