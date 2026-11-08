package io.github.genkidoudou.msg.internal.msg.impl;

import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.entity.MsgInbox;
import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.service.IMsgInboxService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 站内信
 *
 * @author luyanan
 * @since 2026/11/3
 */
@RequiredArgsConstructor
@Slf4j
@Component("inboxSendMsgService")
public class InboxSendMsgService implements SendMsgService {

    private final IMsgInboxService msgInboxService;

    @Override
    public boolean send(MsgTemplate.SendMessage sendMessage, MsgChannelVo msgChannelVo) {
        List<String> userIds = sendMessage.address();
        List<MsgInbox> msgInboxes = new ArrayList<>();
        for (Long userId : userIds.stream().map(Long::valueOf).toList()) {
            // 发送站内信
            MsgInbox msgInbox = new MsgInbox();
            msgInbox.setUserId(userId);
            msgInbox.setTitle(sendMessage.title());
            msgInbox.setContent(sendMessage.body());
            msgInbox.setReadFlag("0");
            msgInbox.setChannelId(msgChannelVo.getId());
            msgInboxes.add(msgInbox);
        }
        msgInboxService.saveBatch(msgInboxes);
        return true;
    }

}