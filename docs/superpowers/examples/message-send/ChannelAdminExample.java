package io.github.genkidoudou.system.internal.message;

import io.github.genkidoudou.system.api.message.MessageSender;
import io.github.genkidoudou.system.internal.message.MessageTypes.ChannelType;
import io.github.genkidoudou.system.internal.message.MessageTypes.ContentType;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * 渠道保存和重置 token。示例，不参与编译。
 * <p>
 * token 是入站凭证，给外部系统调用。创建时生成，明文保存，管理端可以再次查看。
 * 重置后旧值立即失效，已经发出的消息不受影响。
 */
public class ChannelAdminExample {

    private static final int TOKEN_BYTES = 32;

    private final SecureRandom random = new SecureRandom();
    private final ChannelRepository repository;
    private final BeanCatalog beanCatalog;

    public ChannelAdminExample(ChannelRepository repository, BeanCatalog beanCatalog) {
        this.repository = repository;
        this.beanCatalog = beanCatalog;
    }

    /**
     * 新增或修改渠道。
     * 站内不保留地址和 Bean 名称。Webhook 必须有地址。Bean 必须能在容器里找到同名的 {@link MessageSender}。
     */
    public long save(ChannelForm form) {
        if (form.name() == null || form.name().isBlank()) {
            throw new IllegalArgumentException("名称必填");
        }
        if (form.templateBody() == null || form.templateBody().isBlank()) {
            throw new IllegalArgumentException("模板正文必填");
        }
        String webhookUrl = null;
        String beanName = null;
        if (form.type() == ChannelType.WEBHOOK) {
            if (form.webhookUrl() == null || form.webhookUrl().isBlank()) {
                throw new IllegalArgumentException("Webhook 要填地址");
            }
            webhookUrl = form.webhookUrl().trim();
        } else if (form.type() == ChannelType.BEAN) {
            if (form.beanName() == null || form.beanName().isBlank()) {
                throw new IllegalArgumentException("Bean 要填名称");
            }
            beanName = form.beanName().trim();
            if (!beanCatalog.isMessageSender(beanName)) {
                throw new IllegalArgumentException("容器中没有这个 MessageSender：" + beanName);
            }
        }
        if (form.id() == null) {
            return repository.insert(form, webhookUrl, beanName, newToken());
        }
        repository.update(form, webhookUrl, beanName);
        return form.id();
    }

    /** 换一个新 token。之后拿旧 token 调用，按 token 对不上拒绝，不产生消息。 */
    public String resetToken(long channelId) {
        String token = newToken();
        repository.replaceToken(channelId, token);
        return token;
    }

    /** 32 字节随机数的十六进制，共 64 个字符。 */
    private String newToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public record ChannelForm(Long id,
                              String name,
                              ChannelType type,
                              ContentType contentType,
                              String templateBody,
                              String webhookUrl,
                              String beanName,
                              boolean enabled) {
    }

    public interface ChannelRepository {

        long insert(ChannelForm form, String webhookUrl, String beanName, String token);

        void update(ChannelForm form, String webhookUrl, String beanName);

        void replaceToken(long channelId, String token);
    }

    /** 保存 Bean 渠道时用。落地时问 Spring 容器：这个名字在不在，类型是不是 MessageSender。 */
    public interface BeanCatalog {

        boolean isMessageSender(String beanName);
    }
}
