package io.github.genkidoudou.msg.internal.msg.impl;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通用的webHook消息发送
 *
 * @author luyanan
 * @since 2026/11/3
 */
@RequiredArgsConstructor
@Slf4j
@Service("commonWebHookSendMsgServiceImpl")
public class CommonWebHookSendMsgServiceImpl implements SendMsgService {
    @Override
    public boolean send(MsgTemplate.SendMessage sendMessage, MsgChannelVo msgChannelVo) {
        String webhookUrl = msgChannelVo.getWebhookUrl();
        if (StrUtil.isBlank(webhookUrl)) {
            throw new WarningException(30002, msgChannelVo.getName());
        }

        return false;
    }
}
