package io.github.genkidoudou.job.internal.quartz;

import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动后，把库里启用中的任务重新注册到 Quartz。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobStartupLoader implements ApplicationRunner {

    private final ISysJobService sysJobService;
    private final JobSchedulerEngine jobSchedulerEngine;

    @Override
    public void run(ApplicationArguments args) {
        List<SysJobVo> jobs = sysJobService.listEnabled();
        int ok = 0;
        for (SysJobVo job : jobs) {
            try {
                jobSchedulerEngine.addJob(job);
                ok++;
            } catch (Exception ex) {
                log.error("启动时装载定时任务失败 id={} name={}", job.getId(), job.getJobName(), ex);
            }
        }
        log.info("定时任务装载完成：成功 {} / 总计 {}", ok, jobs.size());
    }
}
