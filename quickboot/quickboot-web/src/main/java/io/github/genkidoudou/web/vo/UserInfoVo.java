package io.github.genkidoudou.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {


    /**
     * 部门id
     *
     * @since 2026/10/13
     */

    private Long deptId;

    /**
     * 昵称
     *
     * @since 2026/10/13
     */

    private String nickName;


    /**
     * 权限
     *
     * @since 2026/10/13
     */

    private Set<String> permissions;

    /**
     * 用户id
     *
     * @since 2026/10/13
     */

    private Long userId;

    /**
     * 用户名
     *
     * @since 2026/10/13
     */

    private String username;

    /**
     * 是否需要修改密码
     *
     * @since 2026/10/13
     */

    private boolean needUpdatePassword;
}
