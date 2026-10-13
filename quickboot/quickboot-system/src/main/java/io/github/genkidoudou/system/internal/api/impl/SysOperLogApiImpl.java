package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.system.api.api.SysOperLogApi;
import io.github.genkidoudou.system.api.vo.SysOperLogRecordVo;
import io.github.genkidoudou.system.internal.service.ISysOperLogService;
import io.github.genkidoudou.system.internal.vo.SysOperLogVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * {@link SysOperLogApi} 实现。
 */
@Service
@RequiredArgsConstructor
public class SysOperLogApiImpl implements SysOperLogApi {

    private final ISysOperLogService sysOperLogService;

    @Override
    public void record(SysOperLogRecordVo record) {
        if (record == null) {
            return;
        }
        SysOperLogVo vo = new SysOperLogVo();
        vo.setMethod(record.getMethod());
        vo.setRequestMethod(record.getRequestMethod());
        vo.setOperName(record.getOperName());
        vo.setOperUrl(record.getOperUrl());
        vo.setClientId(record.getClientId());
        vo.setIpAddr(record.getIpAddr());
        vo.setBrowser(record.getBrowser());
        vo.setLoginLocation(record.getLoginLocation());
        vo.setOs(record.getOs());
        vo.setParams(record.getParams());
        vo.setJsonResult(record.getJsonResult());
        vo.setStatus(record.getStatus());
        vo.setErrorMsg(record.getErrorMsg());
        vo.setOperTime(record.getOperTime());
        vo.setCostTime(record.getCostTime());
        vo.setTraceId(record.getTraceId());
        sysOperLogService.record(vo);
    }
}
