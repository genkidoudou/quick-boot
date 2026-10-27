package io.github.genkidoudou.message.internal.channel;

import io.github.genkidoudou.common.message.ChannelType;
import io.github.genkidoudou.common.message.MessageChannel;
import io.github.genkidoudou.common.message.MessageChannelRequest;
import io.github.genkidoudou.common.message.MessageChannelResult;
import io.github.genkidoudou.common.message.MessageSendCommand.Receivers;

/**
 * 站内信通道示例。
 * <p>
 * 不参与编译。编排层已经确认接收人非空，并把部门展开成了 userIds。
 * 本类只为每个用户写一行收件箱。已读初始为 0。业务地址原样快照，为空就没有跳转。
 * <p>
 * 同一通道内的重复用户由编排层去掉。不同站内信通道各写各的行，这里不去跨通道合并。
 */
public class InboxMessageChannel implements MessageChannel {

    private final InboxWriter inboxWriter;

    public InboxMessageChannel(InboxWriter inboxWriter) {
        this.inboxWriter = inboxWriter;
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.INBOX;
    }

    @Override
    public MessageChannelResult send(MessageChannelRequest request) {
        Receivers receivers = request.receivers();
        for (Long userId : receivers.userIds()) {
            inboxWriter.insert(
                    request.sendId(),
                    request.channelId(),
                    userId,
                    request.title(),
                    request.content(),
                    request.bizUrl());
        }
        return MessageChannelResult.success(receivers.userIds().size() + " 人");
    }

    /**
     * 落地时写入 msg_inbox。查询收件箱只按当前登录用户过滤，不信任请求里的用户 ID。
     */
    public interface InboxWriter {
        void insert(Long sendId, String channelId, Long userId, String title, String content, String bizUrl);
    }
}
