package io.github.genkidoudou.msg.internal.api.impl;

import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.service.IMsgChannelService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 消息发送模板实现
 *
 * @author luyanan
 * @since 2026/11/2
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MsgTemplateImpl implements MsgTemplate {

    private final IMsgChannelService msgChannelService;

    @Override

    public boolean send(Long channel, SendMessage sendMessage) {
        //1. 查询channel
        MsgChannelVo msgChannelVo = msgChannelService.getVoById(channel);
        if (null == msgChannelVo) {
            throw new WarningException(30001);
        }
        return false;
    }
}
