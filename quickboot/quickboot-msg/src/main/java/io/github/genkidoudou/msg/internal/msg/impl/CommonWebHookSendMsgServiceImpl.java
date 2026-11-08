package io.github.genkidoudou.msg.internal.msg.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用的 Webhook 消息发送。
 * 请求方式：GET-FORM、POST-FORM、POST-JSON。注释里的 GET-FPRM 与 GET-FORM 相同。
 *
 * @author luyanan
 * @since 2026/11/3
 */
@RequiredArgsConstructor
@Slf4j
@Service("commonWebHookSendMsgServiceImpl")
public class CommonWebHookSendMsgServiceImpl implements SendMsgService {

    private static final int TIMEOUT_MILLIS = 10_000;

    /**
     * 模板占位符。{{title}}、{{content}} 是正文里用的写法，content 即消息正文。
     * ${title}、${body} 仍可用。只替换这一组，消息内容里再出现同名占位符不再展开。
     */
    private static final Pattern PLACEHOLDER = Pattern.compile(
            "\\{\\{(title|content|body|address|msgType)}}|\\$\\{(title|content|body|address|msgType)}");

    @Override
    public boolean send(MsgTemplate.SendMessage sendMessage, MsgChannelVo msgChannelVo) {
        String webhookUrl = msgChannelVo.getWebhookUrl();
        if (StrUtil.isBlank(webhookUrl)) {
            throw new WarningException(30002, msgChannelVo.getName());
        }
        String webhookMethod = StrUtil.trim(msgChannelVo.getWebhookMethod());
        if (StrUtil.isBlank(webhookMethod)) {
            throw WarningException.literal(30005, "Webhook请求方式不能为空");
        }
        Map<String, Object> params = templateParams(msgChannelVo.getTemplateContent(), sendMessage);
        HttpRequest request = HttpUtil.createRequest(httpMethod(webhookMethod), webhookUrl).timeout(TIMEOUT_MILLIS);
        if (jsonBody(webhookMethod)) {
            request.body(JSONUtil.toJsonStr(params)).contentType(ContentType.JSON.getValue());
        } else {
            request.form(params);
        }
        try (HttpResponse response = request.execute()) {
            String responseBody = response.body();
            log.info("webhook返回结果: status={}, body={}", response.getStatus(), responseBody);
            return succeeded(response.getStatus(), responseBody, msgChannelVo.getWebhookSuccess());
        } catch (WarningException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.warn("webhook请求失败, channel={}", msgChannelVo.getName(), ex);
            return false;
        }
    }

    /**
     * 模板是 JSON 对象，占位符写在字符串值里，例如 {"text":"{{title}}","desp":"{{content}}"}。
     * 模板为空时仍传 title、body，有地址时再传 address。
     */
    private Map<String, Object> templateParams(String templateContent, MsgTemplate.SendMessage sendMessage) {
        if (StrUtil.isBlank(templateContent)) {
            return messageParams(sendMessage);
        }
        String rendered = render(templateContent, builtinParams(sendMessage));
        try {
            return JSONUtil.parseObj(rendered);
        } catch (RuntimeException ex) {
            throw WarningException.literal(30006, "Webhook模板不是合法的JSON对象");
        }
    }

    /**
     * 三种方式都传 title、body。有地址时再传 address，多个地址用逗号拼接。
     */
    private Map<String, Object> messageParams(MsgTemplate.SendMessage sendMessage) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("title", StrUtil.nullToEmpty(sendMessage.title()));
        params.put("body", StrUtil.nullToEmpty(sendMessage.body()));
        return params;
    }

    private Map<String, String> builtinParams(MsgTemplate.SendMessage sendMessage) {
        Map<String, String> params = new LinkedHashMap<>();
        String body = StrUtil.nullToEmpty(sendMessage.body());
        params.put("title", StrUtil.nullToEmpty(sendMessage.title()));
        params.put("body", body);
        params.put("content", body);
        params.put("address", CollUtil.isEmpty(sendMessage.address()) ? "" : String.join(",", sendMessage.address()));
        params.put("msgType", sendMessage.msgType() == null ? "" : sendMessage.msgType().getMsgType());
        return params;
    }

    /** 占位符按 JSON 字符串转义后再写入，避免正文里的引号把模板截断。 */
    private String render(String template, Map<String, String> params) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            String value = jsonEscape(params.getOrDefault(name, ""));
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }

    private String jsonEscape(String value) {
        String wrapped = new JSONArray().set(value).toString();
        return wrapped.substring(2, wrapped.length() - 2);
    }

    /**
     * 规则为空时，HTTP 2xx 即成功。
     * 规则形如 errcode=0 或 data.success=true：状态码仍须为 2xx，并且 JSON 中该字段等于期望值。
     */
    private boolean succeeded(int status, String responseBody, String rule) {
        if (status < 200 || status >= 300) {
            return false;
        }
        if (StrUtil.isBlank(rule)) {
            return true;
        }
        String trimmed = rule.trim();
        int eq = trimmed.indexOf('=');
        if (eq <= 0 || eq == trimmed.length() - 1) {
            throw WarningException.literal(30007, "成功规则应为 字段=期望值，例如 errcode=0");
        }
        String path = trimmed.substring(0, eq).trim();
        String expected = trimmed.substring(eq + 1).trim();
        if (StrUtil.isBlank(responseBody) || !JSONUtil.isTypeJSONObject(responseBody)) {
            return false;
        }
        Object actual = JSONUtil.getByPath(JSONUtil.parseObj(responseBody), path);
        return actual != null && expected.equals(String.valueOf(actual));
    }

    private Method httpMethod(String webhookMethod) {
        return switch (normalize(webhookMethod)) {
            case "GET-FORM", "GET-FPRM" -> Method.GET;
            case "POST-FORM", "POST-JSON" -> Method.POST;
            default -> throw new WarningException(30005, webhookMethod);
        };
    }

    private boolean jsonBody(String webhookMethod) {
        return "POST-JSON".equals(normalize(webhookMethod));
    }

    private String normalize(String webhookMethod) {
        return webhookMethod.trim().toUpperCase();
    }
}
