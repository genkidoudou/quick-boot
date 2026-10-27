package io.github.genkidoudou.system.internal.message;

import io.github.genkidoudou.system.internal.message.MessageTypes.ContentType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录用户的收件箱。示例，不参与编译。
 * <p>
 * 不另设按钮权限。只能看到自己的行。列表按消息创建时间倒序，打开详情才写 read_at。
 * 没选站内渠道，或站内渠道投递失败，这里不会有行。
 */
public class InboxExample {

    private final InboxStore store;

    public InboxExample(InboxStore store) {
        this.store = store;
    }

    /** 列表本身不改为已读。 */
    public List<InboxItem> page(long currentUserId) {
        return store.listOwn(currentUserId);
    }

    /**
     * 打开详情。别人的 id，或这条收件箱不存在，按不存在处理，不泄露标题。
     * 第一次打开把 read_at 写成当前时间；已经读过的不覆盖原来的时间。
     */
    public InboxDetail detail(long currentUserId, long inboxId) {
        InboxDetail detail = store.findOwn(currentUserId, inboxId).orElse(null);
        if (detail == null) {
            return null;
        }
        if (detail.readAt() == null) {
            LocalDateTime readAt = LocalDateTime.now();
            store.markRead(inboxId, readAt);
            return detail.withReadAt(readAt);
        }
        return detail;
    }

    public record InboxItem(long inboxId, String title, boolean unread, LocalDateTime messageTime) {
    }

    /**
     * 正文是站内渠道那份已经渲染好的内容，不是渠道上的最新模板。
     * 展示方式：TEXT 纯文本，HTML 按 HTML，MARKDOWN 按 Markdown，JSON 按格式化文本且不执行。
     * 展示在前端做，这里把内容类型原样交出去。
     */
    public record InboxDetail(long inboxId,
                              String title,
                              ContentType contentType,
                              String renderedBody,
                              LocalDateTime readAt) {

        InboxDetail withReadAt(LocalDateTime readAt) {
            return new InboxDetail(inboxId, title, contentType, renderedBody, readAt);
        }
    }

    public interface InboxStore {

        List<InboxItem> listOwn(long userId);

        java.util.Optional<InboxDetail> findOwn(long userId, long inboxId);

        void markRead(long inboxId, LocalDateTime readAt);
    }
}
