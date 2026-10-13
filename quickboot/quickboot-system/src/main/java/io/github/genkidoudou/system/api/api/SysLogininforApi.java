package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.system.api.vo.SysLogininforRecordVo;

/**
 * 登录日志对外 API。
 */
public interface SysLogininforApi {

    /**
     * 记录一条登录日志。
     *
     * @param record 登录访问信息
     */
    void record(SysLogininforRecordVo record);
}
