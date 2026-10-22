package io.github.genkidoudou.job.internal.quartz;

import io.github.genkidoudou.job.internal.vo.SysJobVo;

/**
 * 定时任务执行
 *
 * @author luyanan
 * @since 2026/10/21
 */

public interface JobSchedulerEngine {


    /**
     * 添加任务
     *
     * @param sysJobVo
     * @return
     * @since 2026/10/21
     */
    void addJob(SysJobVo sysJobVo);


    /**
     * 修改任务
     *
     * @param sysJobVo
     * @return
     * @since 2026/10/22
     */
    void updateJob(SysJobVo sysJobVo);


    /**
     * 删除任务
     *
     * @param jobVo
     * @return
     * @since 2026/10/22
     */
    void deleteJob(SysJobVo jobVo);


    /**
     * 暂停任务
     *
     * @param sysJobVo
     * @return
     * @since 2026/10/22
     */
    void pauseJob(SysJobVo sysJobVo);


    /**
     * 恢复调度
     *
     * @param sysJobVo
     * @return
     * @since 2026/10/22
     */
    void resumeJob(SysJobVo sysJobVo);


    /**
     * 立即触发一次
     *
     * @param sysJobVo
     * @return
     * @since 2026/10/22
     */
    void triggerOnce(SysJobVo sysJobVo);
}
