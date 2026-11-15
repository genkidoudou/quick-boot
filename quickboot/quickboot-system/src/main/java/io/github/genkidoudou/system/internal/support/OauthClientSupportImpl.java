package io.github.genkidoudou.system.internal.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.sign.OauthClientSupport;
import io.github.genkidoudou.common.sign.SysOauthClientVo;
import io.github.genkidoudou.system.internal.entity.SysOauthClient;
import io.github.genkidoudou.system.internal.mapper.SysOauthClientMapper;
import io.github.genkidoudou.system.internal.service.ISysOauthClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OauthClientSupportImpl implements OauthClientSupport {
    /**
     * 根据客户端id查询客户端名称
     *
     * @param clientId 客户端id
     * @return
     * @since 2026/11/12
     */
    private final ISysOauthClientService sysOauthClientService;

    @Override
    public SysOauthClientVo getClient(String clientId) {
        if (StrUtil.isBlank(clientId)) {
            return null;
        }
        SysOauthClient sysOauthClient =   sysOauthClientService.getByClientId(clientId);
        if (sysOauthClient == null) {
            return null;
        }
        return BeanUtil.copyProperties(sysOauthClient, SysOauthClientVo.class);
    }
}
