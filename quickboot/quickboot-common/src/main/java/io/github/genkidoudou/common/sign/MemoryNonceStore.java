package io.github.genkidoudou.common.sign;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存的nonce 去重存储
 *
 * @author luyanan
 * @since 2026/11/13
 */
public class MemoryNonceStore implements NonceStore {
    private final ConcurrentHashMap<String, Long> store = new ConcurrentHashMap<String, Long>();

    /**
     * 尝试占用nonce
     *
     * @param nonce      客户端nonce
     * @param ttlSeconds 存货秒数
     * @return true: 首次占用成功 false: 重复
     * @since 2026/11/13
     */
    @Override
    public boolean tryAcquire(String nonce, long ttlSeconds) {
        long now = System.currentTimeMillis();
        cleanExpiredNonce(now);
        long expireAt = now + TimeUnit.SECONDS.toMillis(ttlSeconds);
        // compute 保证同 key 并发下原子判定
        final boolean[] acquired = {false};
        store.compute(nonce, (key, existingExpireAt) -> {
            if (existingExpireAt != null && existingExpireAt > now) {
                // 未过期，拒绝重放
                acquired[0] = false;
                return existingExpireAt;
            }
            acquired[0] = true;
            return expireAt;
        });
        return acquired[0];
    }


    /**
     * 清理已经过期的nonce
     *
     * @param now 当前时间
     * @return
     * @since 2026/11/13
     */
    private void cleanExpiredNonce(long now) {
        Iterator<Map.Entry<String, Long>> iterator =
                store.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getValue() < now) {
                iterator.remove();
            }
        }
    }
}
