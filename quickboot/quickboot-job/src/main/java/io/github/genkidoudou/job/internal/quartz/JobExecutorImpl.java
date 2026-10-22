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
import java.util.concurrent.TimeUnit;

/**
 * 默认执行器：按任务类型执行 Bean / Shell / Python，并写入执行日志。
 */
@Component
@Slf4j
public class JobExecutorImpl implements JobExecutor {

    private static final Set<String> ALLOWED_BIN = Set.of("bash", "python2", "python3");
    private static final long TIMEOUT_SEC = 60;
    private static final int MAX_OUTPUT = 64 * 1024;

    private final ISysJobService sysJobService;
    private final ISysJobLogService sysJobLogService;
    private final ApplicationContext applicationContext;

    /**
     * @param sysJobService      任务服务（Lazy，打断与 Service 的循环依赖）
     * @param sysJobLogService   日志服务
     * @param applicationContext Spring 上下文
     */
    public JobExecutorImpl(@Lazy ISysJobService sysJobService,
                           ISysJobLogService sysJobLogService,
                           ApplicationContext applicationContext) {
        this.sysJobService = sysJobService;
        this.sysJobLogService = sysJobLogService;
        this.applicationContext = applicationContext;
    }

    /**
     * 执行指定任务。
     *
     * @param jobId       任务 id
     * @param triggerType 触发来源：CRON / MANUAL
     */
    @Override
    public void execute(Long jobId, String triggerType) {
        LocalDateTime start = LocalDateTime.now();
        long beginMs = System.currentTimeMillis();

        SysJobVo job = sysJobService.getVoById(jobId);
        // 停用任务被触发时直接忽略，避免 Cron 侧反复抛错
        if (CommonEnums.STATUS_DISABLE.getValue().equals(job.getStatus())) {
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

        // COMMON_IS：是=0 允许重叠，否=1 不允许；禁止重叠且仍有 RUNNING 时记 SKIP
        if ("1".equals(job.getConcurrent()) && sysJobLogService.countRunning(jobId) > 0) {
            sysJobLogService.saveSkip(jobId, job.getJobParam(), trigger.getValue(), start, "上一次尚未结束");
            return;
        }

        Long logId = sysJobLogService.saveRunning(jobId, job.getJobParam(), trigger.getValue(), start);
        try {
            String result = switch (jobType) {
                case BEAN -> runBean(job);
                case PYTHON -> runScript(job, job.getPythonBin(), "py");
                case SHELL -> runScript(job, "bash", "sh");
            };
            sysJobLogService.finish(logId, JobLogStatus.SUCCESS, result, null, System.currentTimeMillis() - beginMs);
        } catch (Exception ex) {
            log.warn("任务执行失败 jobId={}", jobId, ex);
            sysJobLogService.finish(logId, JobLogStatus.FAIL, null,
                    StrUtil.maxLength(ex.getMessage(), 1000),
                    System.currentTimeMillis() - beginMs);
        }
    }

    /**
     * 从 Spring 容器按 Bean 名取 {@link ITask} 并执行。
     *
     * @param job 任务
     * @return 执行结果文本
     */
    private String runBean(SysJobVo job) {
        try {
            ITask task = applicationContext.getBean(job.getInvokeTarget(), ITask.class);
            return task.execute(job.getJobParam());
        } catch (NoSuchBeanDefinitionException ex) {
            throw new WarningException(20004, job.getInvokeTarget());
        }
    }

    /**
     * 执行脚本：写临时文件 → ProcessBuilder 参数数组启动 → 合并 stdout/stderr。
     *
     * @param job     任务
     * @param bin     解释器：bash / python2 / python3
     * @param fileExt 临时文件后缀
     * @return 标准输出
     */
    private String runScript(SysJobVo job, String bin, String fileExt) throws IOException, InterruptedException {
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
            boolean finished = process.waitFor(TIMEOUT_SEC, TimeUnit.SECONDS);
            String output = readCapped(process.getInputStream(), MAX_OUTPUT);
            if (!finished) {
                process.destroyForcibly();
                throw new IllegalStateException("执行超过 " + TIMEOUT_SEC + " 秒\n" + output);
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
