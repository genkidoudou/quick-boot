package io.github.genkidoudou.job.internal.quartz;

import cn.hutool.extra.spring.SpringUtil;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 所有业务任务共用这一个 Quartz Job。
 * 集群里同一 JobKey 同一时刻只会有一个节点进入。
 */
@DisallowConcurrentExecution
public class DispatchQuartzJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap data = context.getMergedJobDataMap();
        Long jobId = Long.valueOf(data.getString("jobId"));
        // Cron 触发没有 triggerType 时默认 CRON；手动 triggerOnce 会带 MANUAL
        String triggerType = data.containsKey("triggerType")
                ? data.getString("triggerType")
                : JobTriggerType.CRON.getValue();

        // 从 Spring 取 Bean，避免 Quartz 自己 new 出来拿不到依赖
        SpringUtil.getBean(JobExecutor.class).execute(jobId, triggerType);
    }
}