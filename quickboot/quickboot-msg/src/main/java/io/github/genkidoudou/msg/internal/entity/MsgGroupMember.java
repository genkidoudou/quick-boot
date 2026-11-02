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
 * 群组成员实体，与表 {@code msg_group_member} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("msg_group_member")
public class MsgGroupMember extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 群组 ID。 */
    private Long groupId;

    /** 成员类型（MSG_GROUP_MEMBER_TYPE）。 */
    private String memberType;

    /** 用户 ID，成员类型为用户时填写。 */
    private Long userId;

    /** 部门 ID，成员类型为部门时填写。 */
    private Long deptId;

    /** 外部人名称。 */
    private String name;

    /** 外部人手机号。 */
    private String phone;

    /** 外部人邮箱。 */
    private String email;
}
