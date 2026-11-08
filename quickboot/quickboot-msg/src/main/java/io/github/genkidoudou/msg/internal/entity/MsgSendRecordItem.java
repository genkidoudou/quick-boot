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
 * 消息发送明细实体，与表 {@code msg_send_record_item} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("msg_send_record_item")
public class MsgSendRecordItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发送记录 ID。
     */
    private Long recordId;

    /**
     * 渠道 ID。
     */
    private Long channelId;

    /**
     * 对象类型（MSG_SEND_TARGET_TYPE）。
     */
    private String targetType;

    /**
     * 群组 ID，对象类型为群组时填写。
     */
    private Long groupId;

    /**
     * 用户 ID，对象类型为用户时填写。
     */
    private Long userId;

    /**
     * 部门 ID，对象类型为部门时填写。
     */
    private Long deptId;

    /**
     * 外部人名称。
     */
    private String name;

    /**
     * 外部人手机号。
     */
    private String phone;

    /**
     * 外部人邮箱。
     */
    private String email;

    /**
     * 该对象在该渠道实际发出的内容。
     */
    private String content;

    /**
     * 结果（MSG_SEND_ITEM_STATUS）。
     */
    private String status;

    /**
     * 失败原因。
     */
    private String detail;

    /**
     * 标题
     *
     * @since 2026/11/8
     */

    private String title;
}
