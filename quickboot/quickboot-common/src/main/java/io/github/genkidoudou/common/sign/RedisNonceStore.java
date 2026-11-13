package io.github.genkidoudou.common.sign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * &#x57FA;&#x4E8E;redis&#x7684; nonce &#x53BB;&#x91CD;&#x5B58;&#x50A8;
 *
 * @author luyanan
 * @since 2026/11/13
 */
@RequiredArgsConstructor
@Slf4j
public class RedisNonceStore implements NonceStore {

    private final SignProperties signProperties;

    private final StringRedisTemplate stringRedisTemplate;


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
        String redisKey = signProperties.getRedisKey() + nonce;
        // setIfAbsent 等价 SET key value NX EX ttl
        Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "1", ttlSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(ok);
    }
}
