package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.system.api.api.SysLogininforApi;
import io.github.genkidoudou.system.api.vo.SysLogininforRecordVo;
import io.github.genkidoudou.system.internal.service.ISysLogininforService;
import io.github.genkidoudou.system.internal.vo.SysLogininforVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * {@link SysLogininforApi} 实现。
 */
@Service
@RequiredArgsConstructor
public class SysLogininforApiImpl implements SysLogininforApi {

    private final ISysLogininforService sysLogininforService;

    @Override
    public void record(SysLogininforRecordVo record) {
        if (record == null) {
            return;
        }
        SysLogininforVo vo = new SysLogininforVo();
        vo.setUserId(record.getUserId());
        vo.setUserName(record.getUserName());
        vo.setClientId(record.getClientId());
        vo.setIpAddr(record.getIpAddr());
        vo.setBrowser(record.getBrowser());
        vo.setLoginLocation(record.getLoginLocation());
        vo.setOs(record.getOs());
        vo.setStatus(record.getStatus());
        vo.setLoginTime(record.getLoginTime());
        vo.setTraceId(record.getTraceId());
        sysLogininforService.record(vo);
    }
}
