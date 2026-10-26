package io.github.genkidoudou.job.internal.quartz;

/**
 * 当前任务上下文
 *
 * @author luyanan
 * @since 2026/10/26
 */
public final class JobLogContext {

    private static final InheritableThreadLocal<JobLogContext> HOLDER = new InheritableThreadLocal<>();


    /**
     * 日志id
     *
     * @since 2026/10/26
     */

    private final Long logId;


    /**
     * 日志文件的绝对路径
     *
     * @since 2026/10/26
     */

    private final String logFileName;


    private JobLogContext(Long logId, String logFileName) {
        this.logId = logId;
        this.logFileName = logFileName;
    }

    public static void set(Long logId, String logFileName) {
        HOLDER.set(new JobLogContext(logId, logFileName));
    }


    public static JobLogContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public Long getLogId() {
        return this.logId;
    }

    public String getLogFileName() {
        return this.logFileName;
    }


}
