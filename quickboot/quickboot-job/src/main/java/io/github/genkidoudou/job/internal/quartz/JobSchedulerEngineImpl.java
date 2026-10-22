package io.github.genkidoudou.job.internal.quartz;

import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
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
            // 停用的任务不需要进入调度器
            if (sysJobVo.getStatus().equals(CommonEnums.STATUS_DISABLE.getValue())) {
                return;
            }
            JobDetail detail = JobBuilder.newJob(DispatchQuartzJob.class)
                    .withIdentity(jobKey)
                    // 只带 jobId，脚本每次从库里现读
                    .usingJobData("jobId", String.valueOf(sysJobVo.getId()))
                    .storeDurably()
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey(sysJobVo.getId()))
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(sysJobVo.getCronExpression())
                            // 错过触发：默认不立刻补跑，由业务 concurrent 控制重叠
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

    @Override
    public void triggerOnce(SysJobVo sysJobVo) {
        try {
            // 立即触发一次；triggerType 由执行器侧标成 MANUAL
            JobDataMap map = new JobDataMap();
            map.put("jobId", String.valueOf(sysJobVo.getId()));
            map.put("triggerType", JobTriggerType.MANUAL.getValue());
            scheduler.triggerJob(jobKey(sysJobVo.getId()), map);
        } catch (SchedulerException e) {
            throw WarningException.literal(20008, "立即执行失败");
        }
    }

    private JobKey jobKey(Long id) {
        return JobKey.jobKey("job-" + id, GROUP);
    }

    private TriggerKey triggerKey(Long id) {
        return TriggerKey.triggerKey("trigger-" + id, GROUP);
    }
}
