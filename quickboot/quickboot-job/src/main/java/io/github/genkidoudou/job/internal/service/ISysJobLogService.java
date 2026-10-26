package io.github.genkidoudou.job.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.job.internal.enums.JobLogStatus;
import io.github.genkidoudou.job.internal.quartz.JobLogCatResult;
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
     * 写入执行中日志，并返回主键。
     *
     * @param jobId       任务 id
     * @param jobParam    任务参数
     * @param triggerType 触发来源 CRON / MANUAL
     * @param start       开始时间
     * @param instanceId  实例id
     * @return 日志主键
     */
    Long saveRunning(Long jobId, String jobParam, String triggerType, LocalDateTime start, String instanceId);


    /**
     * 续约：只更新「仍是 RUNNING」的本条日志心跳。
     * 若已被收尸改成 FAIL，update 影响行数为 0，调用方可忽略。
     */
    void touchHeartbeat(Long logId);

    /**
     * CAS 收尾：仅当仍是 RUNNING 才改为 SUCCESS/FAIL。
     * 防止：收尸已标 FAIL，业务线程稍后又把状态改回 SUCCESS。
     *
     * @return true=本次更新成功；false=已被别人收尾/收尸
     */
    boolean finishIfRunning(Long logId, JobLogStatus status,
                            String result, String errorMsg, long costMs);

    /**
     * 收尸：心跳超时仍 RUNNING → FAIL。
     * <p>
     * staleBefore = now - leaseTimeout。
     * 例：leaseTimeout=90s，则 90s 没人续约即视为节点挂了。
     * <p>
     * 误判窗口 ≈ 租约时长；比「启动清全部 RUNNING」安全得多。
     */
    int reclaimStaleRunning(LocalDateTime staleBefore);

    /**
     * 增量拉取日志
     *
     * @param id       日志id
     * @param fromLine 开始行
     * @return
     * @since 2026/10/26
     */
    JobLogCatResult cat(Long id, int fromLine);
}
