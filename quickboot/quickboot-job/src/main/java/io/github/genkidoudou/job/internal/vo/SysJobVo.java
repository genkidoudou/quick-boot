package io.github.genkidoudou.job.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysJobVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "定时任务ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 任务名称。 */
    @NotBlank(message = "任务名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String jobName;

    /** 任务分组（JOB_GROUP）。 */
    @NotBlank(message = "任务分组不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String jobGroup;

    /** 任务类型（JOB_TYPE）。 */
    @NotBlank(message = "任务类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String jobType;

    /** Bean 名称。 */
    private String invokeTarget;

    /** 任务参数。 */
    private String jobParam;

    /** 任务脚本。 */
    private String scriptContent;

    /** Python 解释器：python2 或 python3。 */
    private String pythonBin;

    /** Cron 表达式。 */
    @NotBlank(message = "Cron 不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String cronExpression;

    /** 是否允许同一任务重叠执行（COMMON_IS）。 */
    private String concurrent;

    /** 状态（COMMON_STATUS）。 */
    private String status;

    /**
     * 接下来若干次预计执行时间（仅详情回显，不入库）。
     * 由 Cron 从「当前时刻」起向后推算。
     */
    private List<LocalDateTime> nextFireTimes;
}
