package io.github.genkidoudou.job.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.job.internal.entity.SysJobLog;
import io.github.genkidoudou.job.internal.mapper.SysJobLogMapper;
import io.github.genkidoudou.job.internal.service.ISysJobLogService;
import io.github.genkidoudou.job.internal.vo.SysJobLogVo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 定时任务日志服务实现。不提供新增和修改。
 */
@Service
public class SysJobLogServiceImpl extends BaseVoServiceImpl<SysJobLogMapper, SysJobLog, SysJobLogVo>
        implements ISysJobLogService {

    /**
     * 按查询条件组装 Wrapper。开始日期按当天范围过滤。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysJobLog> query, SysJobLogVo param) {
        if (param != null) {
            query.eq(param.getJobId() != null, SysJobLog::getJobId, param.getJobId())
                    .eq(StrUtil.isNotBlank(param.getStatus()), SysJobLog::getStatus, param.getStatus());
            if (StrUtil.isNotBlank(param.getStartDate())) {
                LocalDate day = parseDay(param.getStartDate());
                query.ge(SysJobLog::getStartTime, day.atStartOfDay())
                        .lt(SysJobLog::getStartTime, day.plusDays(1).atStartOfDay());
            }
        }
        query.orderByDesc(SysJobLog::getStartTime);
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysJobLogVo getVoById(Long id) {
        SysJobLogVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务日志不存在");
        }
        return vo;
    }

    /**
     * 按主键批量删除。表无逻辑删除列，删除为物理删除。
     *
     * @param ids 主键列表
     */
    @Override
    public void removeByIds(List<Long> ids) {
        deleteByIds(ids);
    }

    /**
     * @param startDate 开始日期
     * @return 日期
     */
    private LocalDate parseDay(String startDate) {
        try {
            return LocalDate.parse(startDate.trim());
        } catch (DateTimeParseException ex) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "开始日期格式应为 yyyy-MM-dd");
        }
    }
}
