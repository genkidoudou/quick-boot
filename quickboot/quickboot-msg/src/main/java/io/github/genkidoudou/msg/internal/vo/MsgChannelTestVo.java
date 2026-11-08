package io.github.genkidoudou.msg.internal.vo;

import io.github.genkidoudou.msg.api.api.MsgTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 渠道测试发送入参，对应 {@link MsgTemplate.SendMessage}。
 */
@Data
public class MsgChannelTestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 渠道主键。 */
    @NotNull(message = "渠道不能为空")
    private Long channelId;

    /** 标题。 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 正文。 */
    @NotBlank(message = "正文不能为空")
    private String body;

    /** 地址。站内信为用户 ID；机器人渠道可空。 */
    private List<String> address;

    /** 消息类型。空则按 text 发送。 */
    private MsgTemplate.MsgType msgType;
}
