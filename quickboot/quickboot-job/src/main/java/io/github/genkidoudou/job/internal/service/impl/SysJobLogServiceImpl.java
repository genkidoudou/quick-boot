package io.github.genkidoudou.job.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.job.internal.entity.SysJobLog;
import io.github.genkidoudou.job.internal.enums.JobLogStatus;
import io.github.genkidoudou.job.internal.mapper.SysJobLogMapper;
import io.github.genkidoudou.job.internal.quartz.JobFileAppender;
import io.github.genkidoudou.job.internal.quartz.JobLogCatResult;
import io.github.genkidoudou.job.internal.quartz.JobLogProperties;
import io.github.genkidoudou.job.internal.service.ISysJobLogService;
import io.github.genkidoudou.job.internal.vo.SysJobLogVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 定时任务日志服务实现。
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysJobLogServiceImpl extends BaseVoServiceImpl<SysJobLogMapper, SysJobLog, SysJobLogVo>
        implements ISysJobLogService {

    private final JobLogProperties jobLogProperties;

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
                    .like(StrUtil.isNotBlank(param.getInstanceId()), SysJobLog::getInstanceId, param.getInstanceId())
                    .eq(StrUtil.isNotBlank(param.getStatus()), SysJobLog::getStatus, param.getStatus());
            if (StrUtil.isNotBlank(param.getStartDate())) {
                LocalDate day = parseDay(param.getStartDate());
                query.ge(SysJobLog::getStartTime, day.atStartOfDay())
                        .lt(SysJobLog::getStartTime, day.plusDays(1).atStartOfDay());
            }
        }
        query.orderByDesc(SysJobLog::getStartTime);
    }

    @Override
    public SysJobLogVo getVoById(Long id) {
        SysJobLogVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务日志不存在");
        }
        return vo;
    }

    @Override
    public void removeByIds(List<Long> ids) {
        deleteByIds(ids);
    }


    @Override
    public Long saveRunning(Long jobId, String jobParam, String triggerType, LocalDateTime start, String instanceId) {
        SysJobLog row = new SysJobLog();
        row.setJobId(jobId);
        row.setJobParam(jobParam);
        row.setTriggerType(triggerType);
        row.setStatus(JobLogStatus.RUNNING.getValue());
        row.setStartTime(start);
        row.setInstanceId(instanceId);
        row.setHeartbeatTime(start);
        this.baseMapper.insert(row);
        return row.getId();
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

    /**
     * 续约：只更新「仍是 RUNNING」的本条日志心跳。
     * 若已被收尸改成 FAIL，update 影响行数为 0，调用方可忽略。
     */
    public void touchHeartbeat(Long logId) {
        super.baseMapper.update(null, new LambdaUpdateWrapper<SysJobLog>()
                .eq(SysJobLog::getId, logId)
                .eq(SysJobLog::getStatus, JobLogStatus.RUNNING.getValue())
                .set(SysJobLog::getHeartbeatTime, LocalDateTime.now()));
    }

    /**
     * CAS 收尾：仅当仍是 RUNNING 才改为 SUCCESS/FAIL。
     * 防止：收尸已标 FAIL，业务线程稍后又把状态改回 SUCCESS。
     *
     * @return true=本次更新成功；false=已被别人收尾/收尸
     */
    public boolean finishIfRunning(Long logId, JobLogStatus status,
                                   String result, String errorMsg, long costMs) {
        // 只允许终态
        if (status != JobLogStatus.SUCCESS && status != JobLogStatus.FAIL) {
            throw new IllegalArgumentException("finish 只能写 SUCCESS/FAIL");
        }
        int rows = baseMapper.update(null, new LambdaUpdateWrapper<SysJobLog>()
                .eq(SysJobLog::getId, logId)
                .eq(SysJobLog::getStatus, JobLogStatus.RUNNING.getValue())
                .set(SysJobLog::getStatus, status.getValue())
                .set(SysJobLog::getResult, result)
                .set(SysJobLog::getErrorMsg, errorMsg)
                .set(SysJobLog::getEndTime, LocalDateTime.now())
                .set(SysJobLog::getCostMs, costMs));
        return rows > 0;
    }

    /**
     * 收尸：心跳超时仍 RUNNING → FAIL。
     * <p>
     * staleBefore = now - leaseTimeout。
     * 例：leaseTimeout=90s，则 90s 没人续约即视为节点挂了。
     * <p>
     * 误判窗口 ≈ 租约时长；比「启动清全部 RUNNING」安全得多。
     */
    public int reclaimStaleRunning(LocalDateTime staleBefore) {
        LocalDateTime now = LocalDateTime.now();
        return baseMapper.update(null, new LambdaUpdateWrapper<SysJobLog>()
                .eq(SysJobLog::getStatus, JobLogStatus.RUNNING.getValue())
                .lt(SysJobLog::getHeartbeatTime, staleBefore)
                .set(SysJobLog::getStatus, JobLogStatus.FAIL.getValue())
                .set(SysJobLog::getErrorMsg, "执行节点失联，租约过期自动收尸")
                .set(SysJobLog::getEndTime, LocalDateTime.now())
                // 近似耗时：从开始到收尸时刻（毫秒）
                .setSql("cost_ms = TIMESTAMPDIFF(MICROSECOND, start_time, {0}) DIV 1000", now));
    }

    @Override
    public JobLogCatResult cat(Long id, int fromLine) {

        SysJobLogVo jobLogVo = getVoById(id);

        LocalDate day = jobLogVo.getStartTime() != null ? jobLogVo.getStartTime().toLocalDate()
                : LocalDate.now();

        String fileName = JobFileAppender.resolveFile(jobLogProperties.getLogPath(), day, id);

        JobLogCatResult jobLogCatResult = JobFileAppender.readLog(fileName, fromLine, 1000);
        // 任务已结束且读到文件末尾 → 前端可停止轮询
        boolean finished = JobLogStatus.SUCCESS.getValue().equals(jobLogVo.getStatus())
                || JobLogStatus.FAIL.getValue().equals(jobLogVo.getStatus());
        jobLogCatResult.setEnd(jobLogCatResult.isEnd() && finished);
        return jobLogCatResult;

    }

}
