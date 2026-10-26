package io.github.genkidoudou.job.internal.quartz;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务过程中写入日志的入口
 *
 * @author luyanan
 * @since 2026/10/26
 */
public class JobLogHelper {

    private static final ConcurrentHashMap<String, Object> LOCKS = new ConcurrentHashMap<>();

    private JobLogHelper() {

    }


    public static void info(String pattern, Object... args) {
        log("INFO", format(pattern, args), null);
    }

    public static void error(String msg, Throwable t) {
        log("ERROR", msg, t);
    }


    public static void warn(String pattern, Object... args) {
        log("WARN", format(pattern, args), null);
    }


    public static void log(String level, String msg, Throwable t) {
        JobLogContext jobLogContext = JobLogContext.get();
        if (jobLogContext == null) {
            return;
        }

        String body = msg == null ? "" : msg;

        if (t != null) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            body += "\n" + sw.toString();
        }
        String logFileName = jobLogContext.getLogFileName();
        Object lock = LOCKS.computeIfAbsent(logFileName, k -> new Object());
        synchronized (lock) {
            JobFileAppender.appendLog(logFileName, level, body);
        }
    }

    /**
     * 极简 {} 占位，生产可换 MessageFormatter 或 StrUtil.format
     */
    private static String format(String pattern, Object... args) {
        if (pattern == null) {
            return "";
        }
        if (args == null || args.length == 0) {
            return pattern;
        }
        String s = pattern;
        for (Object a : args) {
            s = s.replaceFirst("\\{\\}", a == null ? "null" : a.toString());
        }
        return s;
    }
}
