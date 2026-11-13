package io.github.genkidoudou.common.sign;


import lombok.Data;

@Data
public class SysOauthClientVo {
    /**
     * 客户端 id。
     */
    private String clientId;

    /**
     * 客户端密钥。
     */
    private String clientSecret;

    /**
     * 客户端名称。
     */
    private String clientName;

    /**
     * 客户端类型：public|confidential。
     */
    private String clientType;

    /**
     * 允许的 grant_type，逗号分隔。
     */
    private String grantTypes;

    /**
     * 允许访问的接口路径模式。
     */
    private String apiPathPatterns;

    /**
     * token 有效时间（秒）。
     */
    private Integer tokenTimeout;

    /**
     * 是否校验验证码(COMMON_IS)。
     */
    private String checkCaptcha;


    /**
     * 是否校验签名(COMMON_IS)
     *
     * @since 2026/11/12
     */

    private String checkSign;

    /**
     * 状态(COMMON_STATUS)。
     */
    private String status;
}
