package io.github.genkidoudou.system.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.core.constants.PatternConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePasswordRequestVo {


    /**
     * 旧密码
     *
     * @since 2026/10/13
     */

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     *
     * @since 2026/10/13
     */
    @Pattern(regexp = PatternConstants.PASS_WORD,
            message = "密码至少8位，且须包含大小写字母、数字与特殊字符",
            groups = AddGroup.class)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
