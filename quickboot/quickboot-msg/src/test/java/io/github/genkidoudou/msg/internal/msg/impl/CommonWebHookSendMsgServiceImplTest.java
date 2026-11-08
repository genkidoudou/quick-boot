package io.github.genkidoudou.msg.internal.msg.impl;

import io.github.genkidoudou.msg.api.api.MsgTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Webhook 模板占位符替换。
 */
class CommonWebHookSendMsgServiceImplTest {

    @Test
    @DisplayName("{{title}} 与 {{content}} 替换为标题和正文")
    void replacesMustacheTitleAndContent() throws Exception {
        String template = """
                {
                    "text": "{{title}}",
                    "desp": "{{content}}"
                }
                """;
        Map<String, Object> params = templateParams(template, new MsgTemplate.SendMessage(
                "你好", "这是\"正文", List.of(), MsgTemplate.MsgType.TEXT));
        assertEquals("你好", params.get("text"));
        assertEquals("这是\"正文", params.get("desp"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> templateParams(String template, MsgTemplate.SendMessage message) throws Exception {
        CommonWebHookSendMsgServiceImpl service = new CommonWebHookSendMsgServiceImpl();
        Method method = CommonWebHookSendMsgServiceImpl.class.getDeclaredMethod(
                "templateParams", String.class, MsgTemplate.SendMessage.class);
        method.setAccessible(true);
        return (Map<String, Object>) method.invoke(service, template, message);
    }
}
