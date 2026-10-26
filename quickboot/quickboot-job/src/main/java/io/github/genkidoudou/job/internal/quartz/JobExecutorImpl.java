package io.github.genkidoudou.job.internal.quartz;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.job.internal.enums.JobLogStatus;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import io.github.genkidoudou.job.internal.enums.JobType;
import io.github.genkidoudou.job.internal.service.ISysJobLogService;
import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 默认执行器：Bean / Shell / Python + 日志租约。
 * <p>
 * 并发：交给 Quartz（本类不再用 RUNNING 条数挡调度）。
 * 租约：RUNNING 写入 instanceId/heartbeat，执行中续约，结束 CAS 收尾。
 * 执行时长：不设超时，由业务自行结束；节点失联靠租约收尸。
 */
@Component
@Slf4j
public class JobExecutorImpl implements JobExecutor {

    /**
     * 续约周期；须明显小于收尸阈值（20s vs 90s）
     */
    private static final long HEARTBEAT_PERIOD_SEC = 20;

    private static final Set<String> ALLOWED_BIN = Set.of("bash", "python2", "python3");

    /**
     * 心跳池大小：避免单线程时多任务续约互相堵住导致误收尸
     */
    private static final int HB_POOL_SIZE = Math.max(4, Runtime.getRuntime().availableProcessors());

    private final ISysJobService sysJobService;
    private final ISysJobLogService sysJobLogService;
    private final ApplicationContext applicationContext;
    private final JobInstanceId jobInstanceId;

    private final JobLogProperties jobLogProperties;

    /**
     * 多线程续约池（守护线程，进程退出可不管）
     */
    private final ScheduledExecutorService hbPool = new ScheduledThreadPoolExecutor(
            HB_POOL_SIZE,
            r -> {
                Thread t = new Thread(r, "job-log-heartbeat");
                t.setDaemon(true);
                return t;
            });

    public JobExecutorImpl(@Lazy ISysJobService sysJobService,
                           ISysJobLogService sysJobLogService,
                           ApplicationContext applicationContext,
                           JobInstanceId jobInstanceId,
                           JobLogProperties jobLogProperties) {
        this.sysJobService = sysJobService;
        this.sysJobLogService = sysJobLogService;
        this.applicationContext = applicationContext;
        this.jobInstanceId = jobInstanceId;
        this.jobLogProperties = jobLogProperties;

    }

    @PreDestroy
    public void shutdownPools() {
        hbPool.shutdownNow();
    }

    @Override
    public void execute(Long jobId, String triggerType) {
        LocalDateTime start = LocalDateTime.now();
        long beginMs = System.currentTimeMillis();

        SysJobVo job = sysJobService.getVoById(jobId);
        if (CommonEnums.STATUS_DISABLE.getValue().equals(job.getStatus())) {
            // Cron 侧偶发：停用后触发器未卸干净；静默忽略避免刷错
            return;
        }

        JobTriggerType trigger = JobTriggerType.from(triggerType);
        if (trigger == null) {
            throw new WarningException(20002, triggerType);
        }

        JobType jobType = JobType.get(job.getJobType());
        if (jobType == null) {
            throw WarningException.literal(20002, "不支持的任务类型: " + job.getJobType());
        }


        Long logId = sysJobLogService.saveRunning(
                jobId, job.getJobParam(), trigger.getValue(), start, jobInstanceId.get());

        // 生成当日文件, 并绑定到当前线程
        String logFileName = JobFileAppender.makeLogFileName(jobLogProperties.getLogPath(), logId);
        JobLogContext.set(logId,logFileName);
        JobLogHelper.info("----------- job start ----------- jobId={} trigger={} type={}",
                jobId, trigger.getValue(), jobType);

        ScheduledFuture<?> hb = hbPool.scheduleAtFixedRate(
                () -> {
                    try {
                        sysJobLogService.touchHeartbeat(logId);
                    } catch (Exception e) {
                        // 续约失败不打断业务；连续失败超过租约会被收尸
                        log.debug("续约失败 logId={}", logId, e);
                    }
                },
                HEARTBEAT_PERIOD_SEC, HEARTBEAT_PERIOD_SEC, TimeUnit.SECONDS);

        try {
            String result = switch (jobType) {
                case BEAN -> runBean(job);
                case PYTHON -> runScript(job, job.getPythonBin(), "py");
                case SHELL -> runScript(job, "bash", "sh");
            };
            JobLogHelper.info("----------- job success ----------- jobId={}", jobId);
            boolean ok = sysJobLogService.finishIfRunning(
                    logId, JobLogStatus.SUCCESS, result, null,
                    System.currentTimeMillis() - beginMs);
            if (!ok) {
                log.warn("任务已成功但日志已被收尸覆盖 logId={}", logId);
            }
        } catch (Exception ex) {
            log.warn("任务执行失败 jobId={}", jobId, ex);
            sysJobLogService.finishIfRunning(
                    logId, JobLogStatus.FAIL, null,
                    StrUtil.maxLength(ex.getMessage(), 1000),
                    System.currentTimeMillis() - beginMs);
        } finally {
            hb.cancel(false);
            JobLogContext.clear(); // 必须清，避免线程池脏上下文
        }
    }

    /** 同步执行 Spring Bean，不限制时长。 */
    private String runBean(SysJobVo job) {
        ITask task;
        try {
            task = applicationContext.getBean(job.getInvokeTarget(), ITask.class);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new WarningException(20004, job.getInvokeTarget());
        }
        return task.execute(job.getJobParam());
    }

    /** 启动脚本并等到进程结束，不限制时长。 */
    private String runScript(SysJobVo job, String bin, String fileExt)
            throws IOException, InterruptedException {
        if (!ALLOWED_BIN.contains(bin)) {
            throw new IllegalStateException("解释器不在允许列表: " + bin);
        }
        Path tmpDir = Files.createTempDirectory("qc-job-");
        Path file = tmpDir.resolve(IdUtil.simpleUUID() + "." + fileExt);
        try {
            Files.writeString(file, job.getScriptContent(), StandardCharsets.UTF_8);
            List<String> cmd = new ArrayList<>();
            cmd.add(bin);
            cmd.add(file.toAbsolutePath().toString());
            if (StrUtil.isNotBlank(job.getJobParam())) {
                cmd.add(job.getJobParam());
            }
            Process process = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .directory(tmpDir.toFile())
                    .start();
            // 边读边 append；同时拼一份截断摘要给 DB
            StringBuilder summary = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    JobLogHelper.info("[script] {}", line);
                    if (summary.length() < 64 * 1024) {
                        summary.append(line).append('\n');
                    }
                }
            }
            int exit = process.waitFor();
            if (exit != 0) {
                throw new IllegalStateException("退出码 " + exit);
            }
            return summary.toString();
        } finally {
            FileUtil.del(tmpDir.toFile());
        }
    }
}