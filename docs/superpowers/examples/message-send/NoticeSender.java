package io.github.genkidoudou.system.internal.message;

import io.github.genkidoudou.system.api.message.MessageSender;
import io.github.genkidoudou.system.api.message.MessageSender.MessageDelivery;

/**
 * Bean 渠道的一个实现示例。不参与编译。
 * <p>
 * 落地时加上 Spring 的 {@code @Component("noticeSender")}，渠道上的 Bean 名称就填 {@code noticeSender}。
 * 邮件、企微、钉钉以后都按这个接口加，不在第一版做成内置渠道。
 * <p>
 * 方法拿到的 content 已经渲染完成。不要再读渠道上的模板，也不要再展开部门。
 */
public class NoticeSender implements MessageSender {

    @Override
    public void send(MessageDelivery delivery) {
        // 这里换成真实投递。抛出的异常会被发送层截到 500 字，记在这一条渠道快照上。
        if (delivery.recipientUserIds().isEmpty()) {
            throw new IllegalStateException("Bean 收到的收件人为空");
        }
        // delivery.title()、delivery.contentType()、delivery.content()、delivery.recipientUserIds()
    }
}
