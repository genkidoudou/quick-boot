package io.github.genkidoudou.msg.internal.api.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.msg.api.api.MsgTemplate;
import io.github.genkidoudou.msg.internal.enums.ChannelTypeEnums;
import io.github.genkidoudou.msg.internal.msg.SendMsgService;
import io.github.genkidoudou.msg.internal.service.IMsgChannelService;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordItemService;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordItemVo;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 消息发送模板实现
 *
 * @author luyanan
 * @since 2026/11/2
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MsgTemplateImpl implements MsgTemplate {

    /** 来源：内部调用。 */
    private static final String SOURCE_INTERNAL = "3";

    /** 主记录：1 成功，3 失败。 */
    private static final String RECORD_SUCCESS = "1";
    private static final String RECORD_FAIL = "3";

    /** 明细：1 成功，2 失败。 */
    private static final String ITEM_SUCCESS = "1";
    private static final String ITEM_FAIL = "2";

    /** 对象类型：1 用户，3 外部人。 */
    private static final String TARGET_USER = "1";
    private static final String TARGET_EXTERNAL = "3";

    private final IMsgChannelService msgChannelService;

    private final IMsgSendRecordService msgSendRecordService;

    private final IMsgSendRecordItemService msgSendRecordItemService;

    @Override

    public boolean send(Long channel, SendMessage sendMessage) {
        boolean success = false;
        String errorMsg = null;
        MsgChannelVo msgChannelVo = null;
        //1. 查询channel
        try {
            msgChannelVo = msgChannelService.getVoById(channel);
            if (null == msgChannelVo) {
                throw new WarningException(30001);
            }
            if ("1".equals(msgChannelVo.getStatus())) {
                throw new WarningException(30010);
            }
            String channelType = msgChannelVo.getChannelType();
            ChannelTypeEnums channelTypeEnums = Arrays.stream(ChannelTypeEnums.values())
                    .filter(a -> a.getChannelType().equals(channelType))
                    .findFirst()
                    .orElse(null);
            if (null == channelTypeEnums) {
                throw new WarningException(30008, channelType);
            }
            SendMsgService sendMsgService = resolve(channelTypeEnums, msgChannelVo);
            success = sendMsgService.send(sendMessage, msgChannelVo);
            if (!success) {
                errorMsg = "发送失败";
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.error("发送消息失败:{}", e.getMessage());
        } finally {
            saveMsgLog(msgChannelVo, sendMessage, success, errorMsg);
        }
        return success;
    }


    /**
     * 记录发送消息的日志
     *
     * @param msgChannelVo 渠道
     * @param sendMessage  消息
     * @param success      是否成功
     * @param errorMsg     错误消息
     * @return
     * @since 2026/11/7
     */
    private boolean saveMsgLog(MsgChannelVo msgChannelVo, SendMessage sendMessage, boolean success, String errorMsg) {
        if (msgChannelVo == null || sendMessage == null) {
            return false;
        }
        try {
            MsgSendRecordVo record = new MsgSendRecordVo();
            record.setTitle(cut(StrUtil.nullToEmpty(sendMessage.title()), 255));
            record.setContent(StrUtil.nullToEmpty(sendMessage.body()));
            record.setSource(SOURCE_INTERNAL);
            record.setStatus(success ? RECORD_SUCCESS : RECORD_FAIL);
            record.setUserId(currentUserId());
            Long recordId = msgSendRecordService.saveVo(record).getId();

            if (ChannelTypeEnums.INBOX.getChannelType().equals(msgChannelVo.getChannelType())) {
                saveInboxItems(msgChannelVo, sendMessage, recordId, success, errorMsg);
            } else {
                msgSendRecordItemService.saveVo(channelItem(msgChannelVo, sendMessage, recordId, success, errorMsg));
            }
            return true;
        } catch (Exception ex) {
            log.warn("发送记录写入失败, channel={}", msgChannelVo.getId(), ex);
            return false;
        }
    }

    /**
     * 站内信每个收件用户一行。地址不是数字时只把这一行记为失败。
     */
    private void saveInboxItems(MsgChannelVo channel, SendMessage message, Long recordId,
                                boolean success, String errorMsg) {
        List<String> address = message.address();
        if (CollectionUtil.isEmpty(address)) {
            return;
        }
        for (String raw : address) {
            MsgSendRecordItemVo item = baseItem(channel, message, recordId, success, errorMsg);
            item.setTargetType(TARGET_USER);
            Long userId = parseUserId(raw);
            if (userId == null) {
                item.setStatus(ITEM_FAIL);
                item.setDetail(cut("收件用户不是数字: " + raw, 512));
            } else {
                item.setUserId(userId);
            }
            msgSendRecordItemService.saveVo(item);
        }
    }

    /**
     * Webhook 和 Bean 一次调用一行。地址拼进 name，列长 64。
     */
    private MsgSendRecordItemVo channelItem(MsgChannelVo channel, SendMessage message, Long recordId,
                                             boolean success, String errorMsg) {
        MsgSendRecordItemVo item = baseItem(channel, message, recordId, success, errorMsg);
        item.setTargetType(TARGET_EXTERNAL);
        if (CollectionUtil.isNotEmpty(message.address())) {
            item.setName(cut(String.join(",", message.address()), 64));
        }
        return item;
    }

    private MsgSendRecordItemVo baseItem(MsgChannelVo channel, SendMessage message, Long recordId,
                                          boolean success, String errorMsg) {
        MsgSendRecordItemVo item = new MsgSendRecordItemVo();
        item.setRecordId(recordId);
        item.setChannelId(channel.getId());
        item.setContent(StrUtil.nullToEmpty(message.body()));
        item.setStatus(success ? ITEM_SUCCESS : ITEM_FAIL);
        if (!success) {
            item.setDetail(cut(errorMsg, 512));
        }
        return item;
    }

    private Long currentUserId() {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        return loginUser == null ? null : loginUser.getUserId();
    }

    private Long parseUserId(String address) {
        if (StrUtil.isBlank(address)) {
            return null;
        }
        try {
            return Long.valueOf(address.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String cut(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

    /**
     * 站内信和 Webhook 用固定实现。Bean 渠道按页面填写的 Bean 名称获取，例如钉钉机器人 dingDingRobotSendMsgServiceImpl。
     */
    private SendMsgService resolve(ChannelTypeEnums channelType, MsgChannelVo channel) {
        if (channelType != ChannelTypeEnums.BEAN) {
            return SpringUtil.getBean(channelType.getMsgImplClass());
        }
        String beanName = StrUtil.trim(channel.getBeanName());
        if (StrUtil.isBlank(beanName)) {
            throw new WarningException(30009, beanName);
        }
        try {
            Object bean = SpringUtil.getBean(beanName);
            if (bean instanceof SendMsgService sendMsgService) {
                return sendMsgService;
            }
        } catch (RuntimeException ex) {
            log.warn("渠道 Bean 不存在, bean={}", beanName, ex);
        }
        throw new WarningException(30009, beanName);
    }


}
