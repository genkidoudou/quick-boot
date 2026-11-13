package io.github.genkidoudou.common.sign;

/**
 * nonce 去重存储。
 *
 * @author luyanan
 * @since 2026/11/13
 */
public interface NonceStore {


    /**
     * 尝试占用nonce
     *
     * @param nonce      客户端nonce
     * @param ttlSeconds 存货秒数
     * @return true: 首次占用成功 false: 重复
     * @since 2026/11/13
     */
    boolean tryAcquire(String nonce, long ttlSeconds);
}
