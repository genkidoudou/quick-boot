package io.github.genkidoudou.system.internal.api.impl;

import cn.hutool.core.lang.Opt;
import io.github.genkidoudou.system.api.api.SysConfigApi;
import io.github.genkidoudou.system.internal.entity.SysConfig;
import io.github.genkidoudou.system.internal.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class SysConfigApiImpl implements SysConfigApi {

    private final ISysConfigService sysConfigService;

    /**
     * 根据配置key 获取配置项
     *
     * @param configKey 配置key
     * @return
     * @since 2026/10/13
     */
    @Override
    public String getConfigValue(String configKey) {
        SysConfig sysConfig = sysConfigService.getByConfigKey(configKey);
        return Opt.ofNullable(sysConfig).map(SysConfig::getConfigValue).orElse(null);
    }
}
