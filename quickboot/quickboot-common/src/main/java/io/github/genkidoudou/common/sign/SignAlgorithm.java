package io.github.genkidoudou.common.sign;

/**
 * 算法策略
 *
 * @since 2026/11/13
 */

public interface SignAlgorithm {

    /**
     * 算法名称
     *
     * @since 2026/11/13
     */

    String algorithmName();


    /**
     * 签名
     *
     * @param secret  密钥
     * @param payload 签名原文
     * @return
     * @since 2026/11/13
     */
    String sign(String secret, String payload);


    /**
     * 验证签名
     *
     * @param sign    签名
     * @param secret  密钥
     * @param payload 签名原文
     * @return
     * @since 2026/11/13
     */
    boolean verify(String sign, String secret, String payload);
}
