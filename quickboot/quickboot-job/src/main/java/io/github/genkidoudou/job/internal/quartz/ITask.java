package io.github.genkidoudou.job.internal.quartz;

/**
 * 定时任务统一接口
 *
 * @author luyanan
 * @since 2026/10/22
 */
public interface ITask {

    /**
     * 执行
     *
     * @param param 参数
     * @return
     * @since 2026/10/22
     */
    String execute(String param);


}
