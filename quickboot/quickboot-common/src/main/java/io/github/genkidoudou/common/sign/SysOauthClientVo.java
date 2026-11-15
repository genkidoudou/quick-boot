package io.github.genkidoudou.common.sign;


import cn.hutool.core.util.StrUtil;
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
     * 能力串：授权方式（password/sms/...）与安全策略（captcha/sign），逗号分隔。
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
     * 状态(COMMON_STATUS)。
     */
    private String status;

    /**
     * grant_types 是否包含指定能力（忽略大小写与空白）。
     *
     * @param code 如 password / captcha / sign
     * @return 是否包含
     */
    public boolean hasGrant(String code) {
        if (StrUtil.isBlank(grantTypes) || StrUtil.isBlank(code)) {
            return false;
        }
        String target = code.trim();
        for (String part : grantTypes.split(",")) {
            if (target.equalsIgnoreCase(part.trim())) {
                return true;
            }
        }
        return false;
    }
}
