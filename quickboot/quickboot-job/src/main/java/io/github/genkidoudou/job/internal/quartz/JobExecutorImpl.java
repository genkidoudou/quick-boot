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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 默认执行器：Bean / Shell / Python + 日志租约。
 * <p>
 * 并发：交给 Quartz（本类不再用 RUNNING 条数挡调度）。
 * 租约：RUNNING 写入 instanceId/heartbeat，执行中续约，结束 CAS 收尾。
 */
@Component
@Slf4j
public class JobExecutorImpl implements JobExecutor {

    /** 续约周期；须明显小于收尸阈值（20s vs 90s） */
    private static final long HEARTBEAT_PERIOD_SEC = 20;

    private static final Set<String> ALLOWED_BIN = Set.of("bash", "python2", "python3");

    /** 脚本进程超时 */
    private static final long SCRIPT_TIMEOUT_SEC = 60;

    /**
     * Bean 任务超时（中优先级修复）。
     * 与脚本对齐为 60s；长任务请自行在业务里分段/异步，或再调大此常量。
     */
    private static final long BEAN_TIMEOUT_SEC = 60;

    private static final int MAX_OUTPUT = 64 * 1024;

    /** 心跳池大小：避免单线程时多任务续约互相堵住导致误收尸 */
    private static final int HB_POOL_SIZE = Math.max(4, Runtime.getRuntime().availableProcessors());

    private final ISysJobService sysJobService;
    private final ISysJobLogService sysJobLogService;
    private final ApplicationContext applicationContext;
    private final JobInstanceId jobInstanceId;

    /** 多线程续约池（守护线程，进程退出可不管） */
    private final ScheduledExecutorService hbPool = new ScheduledThreadPoolExecutor(
            HB_POOL_SIZE,
            r -> {
                Thread t = new Thread(r, "job-log-heartbeat");
                t.setDaemon(true);
                return t;
            });

    /** Bean 超时专用池；与 Quartz 工作线程解耦，才能 Future.get(timeout) */
    private final ExecutorService beanPool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "job-bean-exec");
        t.setDaemon(true);
        return t;
    });

    public JobExecutorImpl(@Lazy ISysJobService sysJobService,
                           ISysJobLogService sysJobLogService,
                           ApplicationContext applicationContext,
                           JobInstanceId jobInstanceId) {
        this.sysJobService = sysJobService;
        this.sysJobLogService = sysJobLogService;
        this.applicationContext = applicationContext;
        this.jobInstanceId = jobInstanceId;
    }

    @PreDestroy
    public void shutdownPools() {
        hbPool.shutdownNow();
        beanPool.shutdownNow();
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

        /*
         * 不要再写：
         *   if (concurrent=1 && countRunning>0) saveSkip...
         * 节点崩溃后 RUNNING 永不结束会永久 SKIP。
         * 禁止重叠请依赖 Quartz Job 类上的 @DisallowConcurrentExecution。
         */

        Long logId = sysJobLogService.saveRunning(
                jobId, job.getJobParam(), trigger.getValue(), start, jobInstanceId.get());

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
            // 【严重修复】恢复真实执行，禁止再写死 "ok"
            String result = switch (jobType) {
                case BEAN -> runBean(job);
                case PYTHON -> runScript(job, job.getPythonBin(), "py");
                case SHELL -> runScript(job, "bash", "sh");
            };
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
        }
    }

    /**
     * 执行 Spring Bean，并施加超时（中：Bean 无超时修复）。
     * 超时后 cancel(true) 中断工作线程；ITask 若忽略中断，仍可能继续跑，
     * 但本方法会向调度返回失败，并由租约在节点崩溃时收尸。
     */
    private String runBean(SysJobVo job) throws Exception {
        ITask task;
        try {
            task = applicationContext.getBean(job.getInvokeTarget(), ITask.class);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new WarningException(20004, job.getInvokeTarget());
        }

        Callable<String> call = () -> task.execute(job.getJobParam());
        Future<String> future = beanPool.submit(call);
        try {
            return future.get(BEAN_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new IllegalStateException("Bean 任务超过 " + BEAN_TIMEOUT_SEC + " 秒");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if (cause instanceof Exception e) {
                throw e;
            }
            throw new IllegalStateException(cause);
        }
    }

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
            boolean finished = process.waitFor(SCRIPT_TIMEOUT_SEC, TimeUnit.SECONDS);
            String output = readCapped(process.getInputStream(), MAX_OUTPUT);
            if (!finished) {
                process.destroyForcibly();
                throw new IllegalStateException("执行超过 " + SCRIPT_TIMEOUT_SEC + " 秒\n" + output);
            }
            if (process.exitValue() != 0) {
                throw new IllegalStateException("退出码 " + process.exitValue() + "\n" + output);
            }
            return output;
        } finally {
            FileUtil.del(tmpDir.toFile());
        }
    }

    private String readCapped(InputStream in, int max) throws IOException {
        byte[] buf = in.readNBytes(max + 1);
        String text = new String(buf, 0, Math.min(buf.length, max), StandardCharsets.UTF_8);
        return buf.length > max ? text + "\n...(truncated)" : text;
    }
}