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
 * 站内信收件箱视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgInboxVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "站内信ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 收件用户 ID。 */
    @NotNull(message = "收件用户不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long userId;

    /** 标题。 */
    @NotBlank(message = "标题不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 255, message = "标题不能超过255个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String title;

    /** 内容。 */
    @NotBlank(message = "内容不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String content;

    /** 已读标记：0 未读，1 已读。 */
    @Size(max = 3, message = "已读标记不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String readFlag;

    /** 来源渠道 ID。 */
    private Long channelId;

    /** 发送记录 ID。 */
    private Long recordId;
}
