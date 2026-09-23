package io.github.genkidoudou.system.internal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统用户实体，与表 {@code sys_user} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 部门 ID。 */
    private Long deptId;

    /** 用户名。 */
    private String userName;

    /** 昵称。 */
    private String nickName;

    /** 用户类型(user_type)。 */
    private String userType;

    /** 邮箱。 */
    private String email;

    /** 手机号码。 */
    private String phonenumber;

    /** 性别(sex)。 */
    private String sex;

    /** 密码（加密存储）。 */
    private String password;

    /** 用户状态(user_status)。 */
    private String status;
}
