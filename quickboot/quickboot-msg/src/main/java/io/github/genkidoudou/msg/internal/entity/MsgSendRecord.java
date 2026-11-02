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
 * 消息发送记录实体，与表 {@code msg_send_record} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("msg_send_record")
public class MsgSendRecord extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 标题。 */
    private String title;

    /** 提交时的正文原文。 */
    private String content;

    /** 来源（MSG_SEND_SOURCE）。 */
    private String source;

    /** 总状态（MSG_SEND_RECORD_STATUS）。 */
    private String status;

    /** 调用所用 API Key，可空。 */
    private Long apiKeyId;

    /** 操作或归属用户。开放调用时取 Key 的用户。 */
    private Long userId;
}
