package io.github.genkidoudou.msg.internal.msg;

import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;

import java.util.List;

public interface SendMsgService {


    /**
     * 发送消息
     *
     * @param sendMessage  消息体
     * @param msgChannelVo 渠道
     * @return
     * @since 2026/11/2
     */
    boolean send(MsgTemplate.SendMessage sendMessage, MsgChannelVo msgChannelVo);
}
