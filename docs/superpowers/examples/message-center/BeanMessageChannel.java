package io.github.genkidoudou.message.internal.channel;

import io.github.genkidoudou.common.message.ChannelType;
import io.github.genkidoudou.common.message.MessageBeanSender;
import io.github.genkidoudou.common.message.MessageChannel;
import io.github.genkidoudou.common.message.MessageChannelRequest;
import io.github.genkidoudou.common.message.MessageChannelResult;

import java.util.function.Function;

/**
 * 内部 Bean 通道示例。
 * <p>
 * 不参与编译。beanName 来自通道配置，但只接受 MessageBeanSender。
 * 容器里同名的其他 Bean 不能被调用，避免配置把一次发送变成任意方法执行。
 */
public class BeanMessageChannel implements MessageChannel {

    private final Function<String, Object> beans;

    public BeanMessageChannel(Function<String, Object> beans) {
        this.beans = beans;
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.BEAN;
    }

    @Override
    public MessageChannelResult send(MessageChannelRequest request) {
        String beanName = beanNameOf(request.channelConfig());
        Object bean = beans.apply(beanName);
        if (!(bean instanceof MessageBeanSender sender)) {
            return MessageChannelResult.fail(beanName, "Bean 未实现 MessageBeanSender");
        }
        sender.send(new MessageBeanSender.Context(
                request.channelId(),
                paramsOf(request.channelConfig()),
                request.title(),
                request.content(),
                request.receivers(),
                request.bizUrl()));
        return MessageChannelResult.success(beanName);
    }

    /** 落地时从通道 JSON 读取 beanName。示例不解析 JSON。 */
    private String beanNameOf(String channelConfig) {
        return channelConfig;
    }

    /** 落地时从通道 JSON 读取 params。没有则返回空字符串。 */
    private String paramsOf(String channelConfig) {
        return "";
    }
}
