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
 * 群组成员视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgGroupMemberVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @NotNull(message = "群组成员ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 群组 ID。
     */
    @NotNull(message = "群组不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long groupId;

    /**
     * 成员类型（MSG_GROUP_MEMBER_TYPE）。
     */
    @NotBlank(message = "成员类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 3, message = "成员类型不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String memberType;

    /**
     * 用户 ID，成员类型为用户时填写。
     */
    private Long userId;

    /**
     * 部门 ID，成员类型为部门时填写。
     */
    private Long deptId;

    /**
     * 外部人名称。
     */
    @Size(max = 64, message = "外部人名称不能超过64个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 外部人手机号。
     */
    @Size(max = 20, message = "手机号不能超过20个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;

    /**
     * 外部人邮箱。
     */
    @Size(max = 128, message = "邮箱不能超过128个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String email;


    /**
     * 用户名
     *
     * @since 2026/11/1
     */

    private String userName;


    /**
     * 用户状态
     *
     * @since 2026/11/1
     */
    private String userStatus;

    /**
     * 部门名称，成员类型为部门时由查询填充。
     */
    private String deptName;

    /**
     * 部门状态（COMMON_STATUS），成员类型为部门时由查询填充。
     */
    private String deptStatus;
}
