package io.github.genkidoudou.job.internal.quartz;

import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

/**
 * 把 sys_job 同步到 Quartz。
 * 集群下各节点共用 QRTZ_*，同一 JobKey 全局唯一。
 */
@Component
@RequiredArgsConstructor
public class JobSchedulerEngineImpl implements JobSchedulerEngine {

    /**
     * Quartz 分组，避免和系统其它 Job 撞名
     */
    public static final String GROUP = "SYS_JOB";
    private final Scheduler scheduler;

    @Override
    public void addJob(SysJobVo sysJobVo) {

        JobKey jobKey = jobKey(sysJobVo.getId());
        // 先判断任务是否存在,存在就删除
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            // 停用的任务不进入 Cron 调度
            if (sysJobVo.getStatus().equals(CommonEnums.STATUS_DISABLE.getValue())) {
                return;
            }
            JobDetail detail = buildJobDetail(sysJobVo);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey(sysJobVo.getId()))
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(sysJobVo.getCronExpression())
                            // 错过触发不立刻补跑；重叠策略由 Job 类注解决定
                            .withMisfireHandlingInstructionDoNothing())
                    .build();
            scheduler.scheduleJob(detail, trigger);
        } catch (Exception e) {
            throw WarningException.literal(20005, "注册定时任务失败: " + e.getMessage());
        }

    }

    @Override
    public void updateJob(SysJobVo sysJobVo) {
        // 修改 = 删旧 + 按新配置再建
        deleteJob(sysJobVo);
        addJob(sysJobVo);
    }

    @Override
    public void deleteJob(SysJobVo jobVo) {
        try {
            scheduler.deleteJob(jobKey(jobVo.getId()));
        } catch (SchedulerException e) {
            throw WarningException.literal(20006, "删除定时任务失败");
        }
    }

    @Override
    public void pauseJob(SysJobVo sysJobVo) {
        try {
            scheduler.pauseJob(jobKey(sysJobVo.getId()));
        } catch (SchedulerException e) {
            throw WarningException.literal(20009, "暂停定时任务失败");
        }
    }

    @Override
    public void resumeJob(SysJobVo sysJobVo) {
        try {
            scheduler.resumeJob(jobKey(sysJobVo.getId()));
        } catch (SchedulerException e) {
            throw WarningException.literal(20007, "恢复定时任务失败");
        }

    }

    /**
     * 执行一次（正常 / 停用均可）。
     * <p>
     * 停用任务没有 Cron 注册时，只挂 durable {@link JobDetail}（不挂 Trigger），
     * 再 {@code triggerJob}，避免重新启用定时调度。
     */
    @Override
    public void triggerOnce(SysJobVo sysJobVo) {
        try {
            JobKey key = jobKey(sysJobVo.getId());
            if (!scheduler.checkExists(key)) {
                // 启用任务理应已注册；停用或不存在时补 durable JobDetail
                scheduler.addJob(buildJobDetail(sysJobVo), true);
            }
            JobDataMap map = new JobDataMap();
            map.put("jobId", String.valueOf(sysJobVo.getId()));
            map.put("triggerType", JobTriggerType.MANUAL.getValue());
            // 同一 JobKey：禁止重叠类会等当前执行结束；允许重叠类可并行
            scheduler.triggerJob(key, map);
        } catch (SchedulerException e) {
            throw WarningException.literal(20008, "立即执行失败: " + e.getMessage());
        }
    }

    /**
     * 按 concurrent 选择 Job 实现类并构建 JobDetail。
     *
     * @param sysJobVo 任务
     * @return JobDetail
     */
    private JobDetail buildJobDetail(SysJobVo sysJobVo) {
        // concurrent：COMMON_IS —— "0"=允许重叠，"1"=禁止重叠（默认按禁止处理）
        Class<? extends org.quartz.Job> jobClass =
                "0".equals(sysJobVo.getConcurrent())
                        ? DispatchQuartzJobAllowConcurrent.class
                        : DispatchQuartzJob.class;
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey(sysJobVo.getId()))
                .usingJobData("jobId", String.valueOf(sysJobVo.getId()))
                .storeDurably()
                .build();
    }

    private JobKey jobKey(Long id) {
        return JobKey.jobKey("job-" + id, GROUP);
    }

    private TriggerKey triggerKey(Long id) {
        return TriggerKey.triggerKey("trigger-" + id, GROUP);
    }
}
