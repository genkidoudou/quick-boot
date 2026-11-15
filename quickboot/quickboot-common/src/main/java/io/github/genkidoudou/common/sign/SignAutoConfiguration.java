package io.github.genkidoudou.common.sign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 验签配置类
 *
 * @author luyanan
 * @since 2026/11/12
 */
@Configuration
@EnableConfigurationProperties(SignProperties.class)
public class SignAutoConfiguration {


    /**
     * 签名算法
     *
     * @return
     * @since 2026/11/14
     */
    @Bean
    @ConditionalOnMissingBean(SignAlgorithm.class)
    public SignAlgorithm signAlgorithm() {
        // 建议换成修过 verify 的 HmacSha256SignAlgorithmCompleteExample
        return new HmacSha256SignAlgorithm();
    }

    /**
     * nonce redis存储
     *
     * @param props
     * @param redis
     * @return
     * @since 2026/11/14
     */
    @Bean
    @ConditionalOnMissingBean(NonceStore.class)
    @ConditionalOnProperty(prefix = "qc.sign", name = "nonce-store", havingValue = "redis", matchIfMissing = true)
    public NonceStore redisNonceStore(SignProperties props, StringRedisTemplate redis) {
        return new RedisNonceStore(props, redis);
    }

    /**
     * nonce 内存存储
     *
     * @return
     * @since 2026/11/14
     */
    @Bean
    @ConditionalOnMissingBean(NonceStore.class)
    @ConditionalOnProperty(prefix = "qc.sign", name = "nonce-store", havingValue = "memory")
    public NonceStore memoryNonceStore() {
        return new MemoryNonceStore();
    }

    /**
     * 签名过滤器
     *
     * @param props
     * @param oauthClientSupport
     * @param nonceStore
     * @param signAlgorithm
     * @return
     * @since 2026/11/14
     */
    @Bean
    @ConditionalOnProperty(prefix = "qc.sign", name = "enable", havingValue = "true")
    public FilterRegistrationBean<RequestSignFilter> requestSignFilter(
            SignProperties props,
            OauthClientSupport oauthClientSupport,
            NonceStore nonceStore,
            SignAlgorithm signAlgorithm) {
        FilterRegistrationBean<RequestSignFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestSignFilter(props, oauthClientSupport, nonceStore, signAlgorithm));
        bean.addUrlPatterns("/*");
        // 建议早于业务 Filter，但晚于编码 / 安全基础 Filter
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        return bean;
    }
}
