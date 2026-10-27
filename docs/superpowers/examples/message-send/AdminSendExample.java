package io.github.genkidoudou.system.internal.message;

import io.github.genkidoudou.system.internal.message.MessageSendService.SendCommand;
import io.github.genkidoudou.system.internal.message.MessageSendService.SendOutcome;
import io.github.genkidoudou.system.internal.message.MessageTypes.MessageSource;

import java.util.List;
import java.util.Map;

/**
 * 管理端发送。示例，不参与编译。
 * <p>
 * 对应 {@code POST message/message/send}。需要登录，并具备 {@code message:message:send}。
 * 和外部调用进入同一个 {@link MessageSendService}。差别只在于渠道可以多选，而且没有 token。
 */
public class AdminSendExample {

    private final MessageSendService sendService;

    public AdminSendExample(MessageSendService sendService) {
        this.sendService = sendService;
    }

    /**
     * 点发送即发送，没有草稿。
     * 未选渠道或站内超过一个时，{@link SendOutcome#accepted()} 为 false，库里没有这条消息。
     */
    public SendOutcome send(String title,
                            Map<String, Object> variables,
                            List<Long> directUserIds,
                            List<Long> groupIds,
                            List<Long> channelIds) {
        if (title == null || title.isBlank()) {
            return SendOutcome.rejected("标题不能为空白");
        }
        return sendService.send(new SendCommand(
                MessageSource.ADMIN,
                title.trim(),
                variables == null ? Map.of() : variables,
                directUserIds == null ? List.of() : directUserIds,
                groupIds == null ? List.of() : groupIds,
                channelIds == null ? List.of() : channelIds));
    }
}
