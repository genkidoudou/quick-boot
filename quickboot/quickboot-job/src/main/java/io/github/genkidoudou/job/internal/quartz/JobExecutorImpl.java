package io.github.genkidoudou.job.internal.quartz;

import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.SysJobLogVo;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 默认的执行器
 *
 * @author luyanan
 * @since 2026/10/22
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JobExecutorImpl implements JobExecutor {
    private final ISysJobService sysJobService;

    /**
     * 执行指定的方法
     *
     * @param jobId       任务id
     * @param triggerType 触发来源
     * @return
     * @since 2026/10/22
     */
    @Override
    public void execute(Long jobId, String triggerType) {
        SysJobVo sysJobVo = sysJobService.getVoById(jobId);
        if (null == sysJobVo) {
            throw new WarningException(20001);
        }


        JobTriggerType jobTriggerType = JobTriggerType.get(triggerType);
        if (null == jobTriggerType) {
            throw new WarningException(20002, triggerType);
        }
        SysJobLogVo sysJobLogVo = new SysJobLogVo();
        sysJobLogVo.setJobId(jobId);
        sysJobLogVo.setJobParam(sysJobVo.getJobParam());
        sysJobLogVo.setStatus("0");
        sysJobLogVo.setStartTime(LocalDateTime.now());
        long begin = System.currentTimeMillis();

    }
}
