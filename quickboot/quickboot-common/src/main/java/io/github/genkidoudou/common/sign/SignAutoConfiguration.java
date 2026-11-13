package io.github.genkidoudou.common.sign;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 验签配置类
 *
 * @author luyanan
 * @since 2026/11/12
 */
@Configuration
@EnableConfigurationProperties(SignProperties.class)
public class SignAutoConfiguration {
}
