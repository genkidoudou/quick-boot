package io.github.genkidoudou.msg.api.api;

import java.util.List;

/**
 * 消息发送模板
 * 站内信 : 用户/部门
 * 外部:
 * - webhook 钉钉 企业微信
 * - email
 * - sms
 *
 * @author luyanan
 * @since 2026/11/1
 */
public interface MsgTemplate {

    /**
     * 消息发送
     *
     * @param channel     消息渠道
     * @param sendMessage 消息体
     * @return
     * @since 2026/11/2
     */
    boolean send(Long channel, SendMessage sendMessage);


    /**
     * 发送的消息体
     *
     * @param title   消息标题
     * @param body    消息内容
     * @param address 地址列表
     * @return
     * @since 2026/11/2
     */
    public record SendMessage(String title, String body, List<String> address) {

    }

}
