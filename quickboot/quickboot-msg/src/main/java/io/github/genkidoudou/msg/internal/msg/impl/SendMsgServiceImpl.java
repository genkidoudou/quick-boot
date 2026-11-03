package io.github.genkidoudou.msg.internal.msg.impl;

import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.entity.MsgInbox;
import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.service.IMsgInboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendMsgServiceImpl implements SendMsgService {



    @Override
    public boolean sendInbox(MsgTemplate.SendMessage sendMessage, Long channelId) {

        return false;
    }

    @Override
    public boolean sendWebHook(String webHookUrl, MsgTemplate.SendMessage sendMessage) {
//        https://open.dingtalk.com/document/orgapp/custom-robots-send-group-messages
        // 发送webHook消息

        return false;
    }
}
