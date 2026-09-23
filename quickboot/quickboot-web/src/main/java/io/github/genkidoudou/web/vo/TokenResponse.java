package io.github.genkidoudou.web.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录token 返回
 *
 * @author luyanan
 * @since 2026/9/18
 */
@Data
@Builder
public class TokenResponse {

    /**
     * token
     *
     * @since 2026/9/18
     */

    private String accessToken;


    /**
     * tokenType
     *
     * @since 2026/9/18
     */

    private String tokenType;


    /**
     * 有效时间
     *
     * @since 2026/9/18
     */

    private Long expiresIn;

}
