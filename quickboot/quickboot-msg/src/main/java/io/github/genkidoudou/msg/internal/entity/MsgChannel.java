package io.github.genkidoudou.msg.internal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息渠道实体，与表 {@code msg_channel} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("msg_channel")
public class MsgChannel extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 渠道名称。 */
    private String name;

    /** 渠道类型（MSG_CHANNEL_TYPE）。 */
    private String channelType;

    /** Webhook URL，仅 Webhook 渠道使用。 */
    private String webhookUrl;

    /** Spring Bean 名称，仅 Bean 渠道使用。 */
    private String beanName;

    /** 模板内容。空或空白则发送时直接用正文。 */
    private String templateContent;

    /** 状态（COMMON_STATUS）。 */
    private String status;
}
