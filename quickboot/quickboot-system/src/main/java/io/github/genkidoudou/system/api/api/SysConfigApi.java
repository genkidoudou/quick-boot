package io.github.genkidoudou.system.api.api;

import cn.hutool.core.lang.Opt;
import io.github.genkidoudou.common.exception.WarningException;

/**
 * 系统参数配置
 *
 * @author luyanan
 * @since 2026/10/13
 */
public interface SysConfigApi {


    /**
     * 根据配置key 获取配置项
     *
     * @param configKey 配置key
     * @return
     * @since 2026/10/13
     */
    String getConfigValue(String configKey);


    /**
     * 根据配置key 获取配置项
     *
     * @param configKey 配置key
     * @return
     * @since 2026/10/13
     */
    default String getConfigValue(String configKey, boolean require) {
        String configValue = getConfigValue(configKey);
        if (null == configValue && require) {
            throw new WarningException(10007);
        }
        return configValue;
    }

    /**
     * 根据配置获取配置项
     *
     * @param configKey    参数key
     * @param defaultValue 默认值
     * @return
     * @since 2026/10/13
     */
    default String getConfigValue(String configKey, String defaultValue) {
        String configValue = getConfigValue(configKey);
        return Opt.ofNullable(configValue).orElse(defaultValue);
    }

}
