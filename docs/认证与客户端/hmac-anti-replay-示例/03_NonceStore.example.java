package docs.example.sign;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * 示例：Redis 防重放。
 * <p>
 * 键：{@code sign:nonce:{clientId}:{nonce}}，SET NX + TTL。
 * 返回 false 表示 nonce 已用过（重放）。
 */
public class NonceStore {

    private final StringRedisTemplate redis;
    private final long ttlSeconds;

    public NonceStore(StringRedisTemplate redis, long ttlSeconds) {
        this.redis = redis;
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * @return true=首次使用并占坑成功；false=重复 nonce
     */
    public boolean tryAcquire(String clientId, String nonce) {
        String key = "sign:nonce:" + clientId + ":" + nonce;
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(ttlSeconds));
        return Boolean.TRUE.equals(ok);
    }
}
