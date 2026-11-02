package io.github.genkidoudou.msg.internal.msg;

import io.github.genkidoudou.msg.api.api.MsgTemplate;

import java.util.List;

public interface SendMsgService {


    /**
     * 发送站内信
     *
     * @param sendMessage 消息体
     * @param channelId   渠道
     * @return
     * @since 2026/11/2
     */
    boolean sendInbox(MsgTemplate.SendMessage sendMessage, Long channelId);


    /**
     * 发送webHook消息
     *
     * @param webHookUrl  webHookUrl
     * @param sendMessage 消息
     * @return
     * @since 2026/11/2
     */
    boolean sendWebHook(String webHookUrl, MsgTemplate.SendMessage sendMessage);
}
