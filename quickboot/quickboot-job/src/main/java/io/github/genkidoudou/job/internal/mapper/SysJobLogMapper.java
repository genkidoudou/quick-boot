package io.github.genkidoudou.job.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.job.internal.entity.SysJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志 Mapper。
 */
@Mapper
public interface SysJobLogMapper extends BaseBaseMapper<SysJobLog> {
}
