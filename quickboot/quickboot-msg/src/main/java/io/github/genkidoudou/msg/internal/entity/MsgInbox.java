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
 * 站内信收件箱实体，与表 {@code msg_inbox} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("msg_inbox")
public class MsgInbox extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 收件用户 ID。 */
    private Long userId;

    /** 标题。 */
    private String title;

    /** 内容。 */
    private String content;

    /** 已读标记：0 未读，1 已读。 */
    private String readFlag;

    /** 来源渠道 ID。 */
    private Long channelId;

    /** 发送记录 ID。 */
    private Long recordId;
}
