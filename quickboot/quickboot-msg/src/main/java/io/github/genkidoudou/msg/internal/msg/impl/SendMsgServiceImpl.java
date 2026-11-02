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

    private final IMsgInboxService msgInboxService;

    @Override
    public boolean sendInbox(MsgTemplate.SendMessage sendMessage, Long channelId) {
        List<String> userIds =
                sendMessage.address();
        List<MsgInbox> msgInboxes = new ArrayList<>();
        for (Long userId : userIds.stream().map(Long::valueOf).toList()) {
            // 发送站内信
            MsgInbox msgInbox = new MsgInbox();
            msgInbox.setUserId(userId);
            msgInbox.setTitle(sendMessage.title());
            msgInbox.setContent(sendMessage.body());
            msgInbox.setChannelId(channelId);
            msgInboxes.add(msgInbox);
        }
        msgInboxService.saveBatch(msgInboxes);
        return false;
    }

    @Override
    public boolean sendWebHook(String webHookUrl, MsgTemplate.SendMessage sendMessage) {
//        https://open.dingtalk.com/document/orgapp/custom-robots-send-group-messages
        // 发送webHook消息

        return false;
    }
}
