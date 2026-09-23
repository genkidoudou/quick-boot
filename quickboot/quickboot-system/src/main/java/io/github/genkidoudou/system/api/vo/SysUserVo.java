package io.github.genkidoudou.system.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.constants.PatternConstants;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统用户视图对象（对外暴露，供登录等跨模块场景使用）。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "用户ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 部门 ID。 */
    private Long deptId;

    /** 用户名。 */
    @NotBlank(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Pattern(regexp = PatternConstants.USER_NAME, message = "用户名须大于6位且仅为字母或数字",
            groups = {AddGroup.class, UpdateGroup.class})
    private String userName;

    /** 昵称。 */
    @NotBlank(message = "昵称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String nickName;

    /** 用户类型(user_type)。 */
    private String userType;

    /** 邮箱。 */
    @Email
    private String email;

    /** 手机号码。 */
    @Pattern(regexp = PatternConstants.PHONE, message = "手机号格式不正确")
    private String phonenumber;

    /** 性别(sex)。 */
    private String sex;

    /**
     * 密码。仅写入，接口响应不序列化；登录校验仍可通过 getter 读取。
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "密码不能为空", groups = AddGroup.class)
    @Pattern(regexp = PatternConstants.PASS_WORD,
            message = "密码至少8位，且须包含大小写字母、数字与特殊字符",
            groups = AddGroup.class)
    private String password;

    /** 用户状态(user_status)。 */
    private String status;
}
