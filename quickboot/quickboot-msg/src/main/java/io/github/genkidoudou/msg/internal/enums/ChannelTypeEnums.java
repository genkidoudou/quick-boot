package io.github.genkidoudou.msg.internal.enums;

import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.msg.impl.CommonWebHookSendMsgServiceImpl;
import io.github.genkidoudou.msg.internal.msg.impl.InboxSendMsgService;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 渠道类型，取值与页面字典 MSG_CHANNEL_TYPE 一致。
 * Bean 渠道没有固定实现，发送时按渠道上的 Bean 名称获取。
 *
 * @author luyanan
 * @since 2026/11/4
 */
@AllArgsConstructor
@Getter
public enum ChannelTypeEnums {

    INBOX("1", InboxSendMsgService.class),
    WEBHOOK("2", CommonWebHookSendMsgServiceImpl.class),
    BEAN("3", null);

    /**
     * 渠道类型，对应 msg_channel.channel_type。
     */
    private final String channelType;

    /**
     * 固定实现。Bean 渠道为 null。
     */
    private final Class<? extends SendMsgService> msgImplClass;
}
