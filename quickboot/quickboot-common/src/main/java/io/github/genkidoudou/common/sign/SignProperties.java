package io.github.genkidoudou.common.sign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 签名配置
 *
 * @author luyanan
 * @since 2026/11/12
 */
@Data
@ConfigurationProperties(prefix = "qc.sign")
public class SignProperties {


    /**
     * 签名总开关
     *
     * @since 2026/11/12
     */

    private boolean enable = true;


    /**
     * 允许的时间偏差(毫秒)
     *
     * @since 2026/11/12
     */

    private long skewMs = 300_000L;


    /**
     * nonce 在 Redis 中的 TTL（秒），建议 ≥ skew
     *
     * @since 2026/11/12
     */

    private long nonceTtlSeconds = 600L;


    /**
     * 忽略url
     *
     * @since 2026/11/12
     */

    private List<String> ignoreUrls = new ArrayList<>();


    /**
     * 算法
     *
     * @since 2026/11/13
     */

    private String algorithm = HmacSha256SignAlgorithm.NAME;

    /**
     * nonce的redis key
     *
     * @author luyanan
     * @since 2026/11/13
     */
    private String redisKey = "sign:nonce";

}
