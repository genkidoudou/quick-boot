package io.github.genkidoudou.msg.internal.msg.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钉钉自定义机器人。
 * 报文类型见<a href="https://open.dingtalk.com/document/orgapp/custom-bot-send-message-type">自定义机器人发送消息的消息类型</a>。
 * text、markdown 的 address 写入 atMobiles；link、actionCard 用第一条地址做跳转地址；feedCard 每条地址一张卡片。
 *
 * @author luyanan
 * @since 2026/11/3
 */
@Slf4j
@RequiredArgsConstructor
@Component("dingDingRobotSendMsgServiceImpl")
public class DingDingRobotSendMsgServiceImpl implements SendMsgService {
    @Override
    public boolean send(MsgTemplate.SendMessage sendMessage, MsgChannelVo msgChannelVo) {

        String webhookUrl = msgChannelVo.getWebhookUrl();
        if (StrUtil.isBlank(webhookUrl)) {
            throw new WarningException(30002, msgChannelVo.getName());
        }
        if (sendMessage.msgType() == null) {
            throw new WarningException(30004);
        }
        try {
            Map<String, Object> payload = buildPayload(sendMessage);
            String response;
            try {
                response = HttpRequest.post(webhookUrl)
                        .body(JSONUtil.toJsonStr(payload))
                        .contentType(ContentType.JSON.getValue())
                        .execute()
                        .body();
                JSONObject jsonObject = JSONUtil.parseObj(response);
                Integer errcode = jsonObject.getInt("errcode", -1);
                if (errcode != 0) {
                    log.warn("钉钉机器人返回失败, channel={}, body={}", msgChannelVo.getName(), response);
                    throw new WarningException(30003, jsonObject.getStr("errmsg"));
                }
                return true;
            } catch (RuntimeException ex) {
                log.warn("钉钉机器人请求失败, channel={}", msgChannelVo.getName(), ex);
                throw WarningException.literal(30003, ex.getMessage());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建请求体
     *
     * @param sendMessage 消息内容
     * @return
     * @since 2026/11/3
     */
    private Map<String, Object> buildPayload(MsgTemplate.SendMessage sendMessage) throws JSONException {
        MapBuilder<String, Object> mapBuilder = MapBuilder
                .create(new HashMap<String, Object>());
        switch (sendMessage.msgType()) {
            case TEXT -> {
                mapBuilder
                        .put("text", MapBuilder.create(new HashMap<String, Object>())
                                .put("content", sendMessage.body())


                        )
                        .put("msgtype", "text");
            }
            case MARKDOWN -> {
                mapBuilder
                        .put("markdown", MapBuilder.create(new HashMap<String, Object>()
                                        ).put("title", sendMessage.title())
                                        .put("text", sendMessage.body())
                                        .build()
                        )
                        .put("msgtype", "markdown");

            }
            case LINK -> {
                mapBuilder
                        .put("link", MapBuilder.create(new HashMap<String, Object>())
                                        .put("title", sendMessage.title())
                                        .put("text", sendMessage.body())
//                                .put("messageUrl", sendMessage.messageUrl())
//                                .put("picUrl", sendMessage.picUrl())
                                        .build()
                        ).put("msgtype", "link");
            }
            case ACTION_CARD -> {
                mapBuilder
                        .put("actionCard", MapBuilder.create(new HashMap<String, Object>())
                                        .put("title", sendMessage.title())
                                        .put("text", sendMessage.body())
//                                        .put("singleTitle", sendMessage.singleTitle())
//                                        .put("singleURL", sendMessage.singleURL())
//                                        .put("btnOrientation", sendMessage.btnOrientation())
                                        .build()
                        ).put("msgtype", "actionCard");
            }
            default -> {
                mapBuilder
                        .put("text", MapBuilder.create(new HashMap<String, Object>())
                                .put("content", sendMessage.body())
                                .build()

                        ).put("msgtype", "text");
            }
        }

        List<String> address = sendMessage.address();
        if (CollectionUtil.isNotEmpty(address)) {
            mapBuilder.put("at", MapBuilder.create(new HashMap<String, Object>())
                    .put("atMobiles", address)
                    .put("isAtAll", false)
                    .build()
            );
        }
        return mapBuilder.build();
    }
}
