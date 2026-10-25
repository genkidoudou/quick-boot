package io.github.genkidoudou.job.internal.vo;

import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务日志视图对象。日志只查不改，因此没有新增、修改校验分组。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysJobLogVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    private Long id;

    /**
     * 任务 ID。
     */
    private Long jobId;

    /**
     * 触发来源（CRON / MANUAL）。
     */
    private String triggerType;

    /**
     * 开始时间。
     */
    private LocalDateTime startTime;

    /**
     * 结束时间。
     */
    private LocalDateTime endTime;

    /**
     * 耗时（毫秒）。
     */
    private Long costMs;

    /**
     * 状态（JOB_STATUS）。
     */
    private String status;

    /**
     * 返回结果。
     */
    private String result;

    /**
     * 错误异常。
     */
    private String errorMsg;

    /**
     * 任务参数。
     */
    private String jobParam;

    /**
     * 查询条件：开始日期，格式 yyyy-MM-dd。不入库。
     */
    private String startDate;

    /**
     * 执行节点实例 ID（排障：哪台 JVM；列表/详情展示，也可作查询条件）。
     */
    private String instanceId;

    /**
     * 最近一次租约续约时间（心跳）。
     */
    private LocalDateTime heartbeatTime;
}
