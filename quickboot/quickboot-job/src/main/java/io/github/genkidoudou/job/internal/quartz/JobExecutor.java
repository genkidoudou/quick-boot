package io.github.genkidoudou.job.internal.quartz;

/**
 * 任务执行器
 *
 * @author luyanan
 * @since 2026/10/22
 */
public interface JobExecutor {


    /**
     * 执行指定的方法
     *
     * @param jobId       任务id
     * @param triggerType 触发来源
     * @return
     * @since 2026/10/22
     */
    void execute(Long jobId, String triggerType);
}
