package io.github.genkidoudou.job.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.job.internal.enums.JobLogStatus;
import io.github.genkidoudou.job.internal.vo.SysJobLogVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务日志服务。管理端只查删；执行器可写 RUNNING / SKIP / 收尾。
 */
public interface ISysJobLogService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysJobLogVo> pageVo(PageRequest<SysJobLogVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysJobLogVo getVoById(Long id);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 当前任务是否还有执行中的日志。
     *
     * @param jobId 任务 id
     * @return RUNNING 条数
     */
    long countRunning(Long jobId);

    /**
     * 写入执行中日志，并返回主键。
     *
     * @param jobId       任务 id
     * @param jobParam    任务参数
     * @param triggerType 触发来源 CRON / MANUAL
     * @param start       开始时间
     * @return 日志主键
     */
    Long saveRunning(Long jobId, String jobParam, String triggerType, LocalDateTime start);

    /**
     * 禁止重叠时写入一条跳过日志。
     *
     * @param jobId       任务 id
     * @param jobParam    任务参数
     * @param triggerType 触发来源
     * @param start       开始时间
     * @param reason      跳过原因
     */
    void saveSkip(Long jobId, String jobParam, String triggerType, LocalDateTime start, String reason);

    /**
     * 收尾：写状态、结果、错误、结束时间、耗时。
     *
     * @param logId    日志主键
     * @param status   最终状态
     * @param result   成功结果
     * @param errorMsg 失败信息
     * @param costMs   耗时毫秒
     */
    void finish(Long logId, JobLogStatus status, String result, String errorMsg, long costMs);
}
