package io.github.genkidoudou.common.sign;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;

/**
 * 默认 HMAC-SHA256 实现。
 *
 * @author luyanan
 * @since 2026/11/13
 */
public class HmacSha256SignAlgorithm implements SignAlgorithm {
    public static final String NAME = "hmac-sha256";

    /**
     * 算法名称
     *
     * @since 2026/11/13
     */
    @Override
    public String algorithmName() {
        return NAME;
    }

    /**
     * 签名
     *
     * @param secret  密钥
     * @param payload 签名原文
     * @return
     * @since 2026/11/13
     */
    @Override
    public String sign(String secret, String payload) {
        HMac hMac = SecureUtil.hmacSha256(secret);
        return hMac.digestHex(payload);
    }

    /**
     * 验证签名
     *
     * @param sign    签名
     * @param secret  密钥
     * @param payload 签名原文
     * @return
     * @since 2026/11/13
     */
    @Override
    public boolean verify(String sign, String secret, String payload) {
        String actual = sign(secret, payload);
        // 忽略大小写
        return actual.toLowerCase().equals(sign.toUpperCase());
    }
}
