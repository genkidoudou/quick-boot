package io.github.genkidoudou.job.internal.quartz;

import cn.hutool.extra.spring.SpringUtil;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 允许重叠的调度入口（sys_job.concurrent = "0"）。
 *
 * @author luyanan
 * @since 2026/10/25
 */
public class DispatchQuartzJobAllowConcurrent extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getMergedJobDataMap();
        Long jobId = Long.valueOf(data.getString("jobId"));
        String triggerType = data.containsKey("triggerType")
                ? data.getString("triggerType")
                : JobTriggerType.CRON.getValue();
        SpringUtil.getBean(JobExecutor.class).execute(jobId, triggerType);
    }
}
