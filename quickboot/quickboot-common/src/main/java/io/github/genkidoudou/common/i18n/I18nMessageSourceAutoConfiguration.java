package io.github.genkidoudou.common.i18n;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 用 {@code classpath*:} 合并各模块同名 i18n 资源，避免 web 覆盖 common 导致文案丢失。
 */
@AutoConfiguration(before = MessageSourceAutoConfiguration.class)
@EnableConfigurationProperties(MessageSourceProperties.class)
public class I18nMessageSourceAutoConfiguration {

  private static final String PROPERTIES_SUFFIX = ".properties";

  /**
   * 覆盖 Boot 默认 {@code ResourceBundleMessageSource}（同 basename 只加载一份）。
   */
  @Bean(name = "messageSource")
  @ConditionalOnMissingBean(name = "messageSource")
  public MessageSource messageSource(MessageSourceProperties properties) throws IOException {
    List<String> configured = properties.getBasename();
    if (CollectionUtils.isEmpty(configured)) {
      configured = List.of("i18n/messages");
    }

    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Set<String> basenames = new LinkedHashSet<>();
    for (String base : configured) {
      if (!StringUtils.hasText(base)) {
        continue;
      }
      String pattern = toClasspathStarPattern(base.trim());
      for (Resource resource : resolver.getResources(pattern)) {
        if (!resource.exists()) {
          continue;
        }
        String url = resource.getURL().toString();
        if (url.endsWith(PROPERTIES_SUFFIX)) {
          url = url.substring(0, url.length() - PROPERTIES_SUFFIX.length());
        }
        basenames.add(url);
      }
    }

    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    if (basenames.isEmpty()) {
      messageSource.setBasenames(configured.toArray(String[]::new));
    } else {
      messageSource.setBasenames(basenames.toArray(String[]::new));
    }

    if (properties.getEncoding() != null) {
      messageSource.setDefaultEncoding(properties.getEncoding().name());
    } else {
      messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }
    messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
    Duration cacheDuration = properties.getCacheDuration();
    if (cacheDuration != null) {
      messageSource.setCacheMillis(cacheDuration.toMillis());
    }
    messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
    messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
    return messageSource;
  }

  private static String toClasspathStarPattern(String basename) {
    String base = basename;
    if (base.startsWith("classpath*:") || base.startsWith("classpath:")) {
      int idx = base.indexOf(':');
      base = base.substring(idx + 1);
    }
    if (base.endsWith(PROPERTIES_SUFFIX)) {
      base = base.substring(0, base.length() - PROPERTIES_SUFFIX.length());
    }
    return "classpath*:" + base + PROPERTIES_SUFFIX;
  }
}
