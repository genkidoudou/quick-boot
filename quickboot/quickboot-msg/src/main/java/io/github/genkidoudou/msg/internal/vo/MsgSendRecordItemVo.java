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
 * 消息发送明细视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgSendRecordItemVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "发送明细ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 发送记录 ID。 */
    @NotNull(message = "发送记录不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long recordId;

    /** 渠道 ID。 */
    @NotNull(message = "渠道不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long channelId;

    /** 渠道名称，列表展示用。 */
    private String channelName;

    /** 对象类型（MSG_SEND_TARGET_TYPE）。 */
    @NotBlank(message = "对象类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 3, message = "对象类型不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String targetType;

    /** 群组 ID，对象类型为群组时填写。 */
    private Long groupId;

    /** 用户 ID，对象类型为用户时填写。 */
    private Long userId;

    /** 部门 ID，对象类型为部门时填写。 */
    private Long deptId;

    /** 外部人名称。 */
    @Size(max = 64, message = "外部人名称不能超过64个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /** 外部人手机号。 */
    @Size(max = 20, message = "手机号不能超过20个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;

    /** 外部人邮箱。 */
    @Size(max = 128, message = "邮箱不能超过128个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String email;

    /** 该对象在该渠道实际发出的内容。 */
    @NotBlank(message = "发送内容不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String content;

    /** 结果（MSG_SEND_ITEM_STATUS）。 */
    @NotBlank(message = "结果不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 3, message = "结果不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String status;

    /** 失败原因。 */
    @Size(max = 512, message = "失败原因不能超过512个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String detail;
}
