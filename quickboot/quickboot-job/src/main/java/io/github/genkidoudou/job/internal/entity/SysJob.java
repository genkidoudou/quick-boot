package io.github.genkidoudou.job.internal.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务实体，与表 {@code sys_job} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_job")
public class SysJob extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 任务名称。 */
    private String jobName;

    /** 任务类型（JOB_TYPE）。 */
    private String jobType;

    /** Bean 名称。切换任务类型时需要写空。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String invokeTarget;

    /** 任务参数。 */
    private String jobParam;

    /** 任务脚本。切换任务类型时需要写空。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String scriptContent;

    /** Python 解释器，仅 Python 任务使用：python2 或 python3。切换任务类型时需要写空。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String pythonBin;

    /** Cron 表达式。 */
    private String cronExpression;

    /** 是否允许同一任务重叠执行（COMMON_IS）。 */
    private String concurrent;

    /** 状态（COMMON_STATUS）。 */
    private String status;
}
