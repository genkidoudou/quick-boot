package io.github.genkidoudou.msg.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息发送记录视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgSendRecordVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "发送记录ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 标题。 */
    @NotBlank(message = "标题不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 255, message = "标题不能超过255个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String title;

    /** 提交时的正文原文。 */
    @NotBlank(message = "正文不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String content;

    /** 来源（MSG_SEND_SOURCE）。 */
    @Size(max = 3, message = "来源不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String source;

    /** 总状态（MSG_SEND_RECORD_STATUS）。 */
    @NotBlank(message = "总状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 3, message = "总状态不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String status;

    /** 调用所用 API Key，可空。 */
    private Long apiKeyId;

    /** 操作或归属用户。 */
    private Long userId;
}
