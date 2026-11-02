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
 * 消息群组视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgGroupVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @NotNull(message = "消息群组ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 群组名称。
     */
    @NotBlank(message = "群组名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 255, message = "群组名称不能超过255个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String name;


    /**
     * 成员类型(MSG_GROUP_MEMBER_TYPE)
     *
     * @since 2026/10/31
     */
    @NotBlank(message = "成员类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String memberType;
}
