package io.github.genkidoudou.job.internal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务日志实体，与表 {@code sys_job_log} 对应。
 * <p>
 * 表上没有逻辑删除和完整审计列，因此不继承 {@code BaseEntity}。
 */
@Data
@TableName("sys_job_log")
public class SysJobLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 摘要。
     */
    private String digest;

    /**
     * 任务 ID。
     */
    private Long jobId;

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
     * 触发来源
     *
     * @since 2026/10/22
     */

    private String triggerType;

    /**
     * 执行本条日志的 JVM 实例 ID。
     */
    private String instanceId;

    /**
     * 租约续约时间（心跳）。
     * 执行中定期刷新；收尸任务用 now - heartbeat_time > 阈值 判定僵尸。
     */
    private LocalDateTime heartbeatTime;
}
