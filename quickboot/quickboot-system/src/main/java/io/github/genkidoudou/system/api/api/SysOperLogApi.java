package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.system.api.vo.SysOperLogRecordVo;

/**
 * 操作日志对外 API。
 */
public interface SysOperLogApi {

    /**
     * 记录一条操作日志。
     *
     * @param record 操作访问信息
     */
    void record(SysOperLogRecordVo record);
}
