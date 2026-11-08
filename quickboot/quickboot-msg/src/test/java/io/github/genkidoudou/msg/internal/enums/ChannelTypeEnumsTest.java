package io.github.genkidoudou.msg.internal.enums;

import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.msg.impl.CommonWebHookSendMsgServiceImpl;
import io.github.genkidoudou.msg.internal.msg.impl.InboxSendMsgService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ChannelTypeEnums} 单元测试
 *
 * @author luyanan
 * @since 2026/11/4
 */
@DisplayName("ChannelTypeEnums 渠道类型枚举测试")
class ChannelTypeEnumsTest {

    @Test
    @DisplayName("枚举应与页面字典 1站内信 2Webhook 3Bean 一致")
    void shouldContainAllChannelTypes() {
        ChannelTypeEnums[] values = ChannelTypeEnums.values();
        assertEquals(3, values.length);
        assertSame(ChannelTypeEnums.INBOX, values[0]);
        assertSame(ChannelTypeEnums.WEBHOOK, values[1]);
        assertSame(ChannelTypeEnums.BEAN, values[2]);
    }

    @Test
    @DisplayName("valueOf 应与枚举名称一一对应")
    void valueOfShouldResolveEachConstant() {
        assertSame(ChannelTypeEnums.INBOX, ChannelTypeEnums.valueOf("INBOX"));
        assertSame(ChannelTypeEnums.WEBHOOK, ChannelTypeEnums.valueOf("WEBHOOK"));
        assertSame(ChannelTypeEnums.BEAN, ChannelTypeEnums.valueOf("BEAN"));
    }

    @Test
    @DisplayName("站内信和 Webhook 使用固定实现，Bean 按名称解析")
    void shouldMapPageCodesToImplementations() {
        assertEquals("1", ChannelTypeEnums.INBOX.getChannelType());
        assertSame(InboxSendMsgService.class, ChannelTypeEnums.INBOX.getMsgImplClass());
        assertEquals("2", ChannelTypeEnums.WEBHOOK.getChannelType());
        assertSame(CommonWebHookSendMsgServiceImpl.class, ChannelTypeEnums.WEBHOOK.getMsgImplClass());
        assertEquals("3", ChannelTypeEnums.BEAN.getChannelType());
        assertNull(ChannelTypeEnums.BEAN.getMsgImplClass());
    }

    @Test
    @DisplayName("固定实现类均应为 SendMsgService 的子类型")
    void msgImplClassShouldImplementSendMsgService() {
        for (ChannelTypeEnums channelType : ChannelTypeEnums.values()) {
            Class<? extends SendMsgService> implClass = channelType.getMsgImplClass();
            if (implClass == null) {
                continue;
            }
            assertTrue(SendMsgService.class.isAssignableFrom(implClass),
                    "实现类必须实现 SendMsgService: " + implClass.getName());
        }
    }
}
