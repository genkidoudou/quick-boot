package io.github.genkidoudou.job.internal.quartz;

import cn.hutool.core.date.DatePattern;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志按行追加
 *
 * @author luyanan
 * @since 2026/10/26
 */
@Slf4j
public final class JobFileAppender {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private final static DateTimeFormatter TS = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN);


    private JobFileAppender() {

    }

    /**
     * 生成日志文件路径(base/yyyy-MM-dd/{logId}.log)
     *
     * @param baseFilePath 日志文件根目录
     * @param logId        日志id
     * @return
     * @since 2026/10/26
     */
    public static String makeLogFileName(String baseFilePath, Long logId) {
        // 目录按日：必须用 DAY；TS 含 HourOfDay，LocalDate 不支持会抛 UnsupportedTemporalTypeException
        Path dayDir = Paths.get(baseFilePath, LocalDate.now().format(DAY));
        try {
            Files.createDirectories(dayDir);
        } catch (IOException e) {
            throw new IllegalStateException("创建任务日志目录失败: " + dayDir, e);
        }
        return dayDir.resolve(logId + ".log").toAbsolutePath().toString();
    }


    /**
     * 追加日志
     *
     * @param logFileName 日志文件
     * @param level       日志级别
     * @param content     日志内容
     * @return
     * @since 2026/10/26
     */
    public static void appendLog(String logFileName, String level, String content) {
        if (null == logFileName || null == content) {
            return;
        }

        String line = LocalDateTime.now().format(TS)
                + " [" + level + "] "
                + content.replace("\r\n", "\n").replace('\r', '\n')
                + "\n";

        try {
            Path path = Paths.get(logFileName);
            Files.createDirectories(path.getParent());
            Files.writeString(path, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            log.warn("append job log failed file={}", logFileName, e);
        }
    }


    /**
     * 按行读取日志内容
     *
     * @param logFileName 日志文件名称
     * @param formLine    从第几行开始读取 首次为1
     * @param maxLines    单次返回的最大的行数
     * @return
     * @since 2026/10/26
     */
    public static JobLogCatResult readLog(String logFileName,
                                          int formLine, int maxLines) {
        JobLogCatResult jobLogCatResult = new JobLogCatResult();
        jobLogCatResult.setFromLine(Math.max(formLine, 1));
        if (logFileName == null || !Files.isRegularFile(Paths.get(logFileName))) {
            // 文件尚不存在: 任务刚开始或者节点不在本机
            jobLogCatResult.setToLine(jobLogCatResult.getFromLine() - 1);
            jobLogCatResult.setContent("");
            jobLogCatResult.setEnd(false);
            jobLogCatResult.setMissing(true);
            return jobLogCatResult;
        }
        StringBuilder sb = new StringBuilder();
        // 已经扫过的行
        int current = 0;
        // 本次收集的行
        int collected = 0;

        int start = jobLogCatResult.getFromLine();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(logFileName), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                current++;
                if (current < start) {
                    continue;
                }
                sb.append(line).append("\n");
                collected++;
                if (collected >= maxLines) {
                    break;
                }
            }

        } catch (IOException e) {
//            throw new RuntimeException(e);
            log.warn("read job log failed file={}", logFileName, e);
            jobLogCatResult.setEnd(true);
            jobLogCatResult.setToLine(start - 1);
            jobLogCatResult.setContent("读取日志失败:" + e.getMessage());
            return jobLogCatResult;
        }
        jobLogCatResult.setToLine(start + collected - 1);
        if (collected == 0) {
            jobLogCatResult.setToLine(start - 1);
        }
        jobLogCatResult.setContent(sb.toString());
        // 未读满 maxLines 说明已到文件末尾；
        jobLogCatResult.setEnd(collected < maxLines);
        jobLogCatResult.setMissing(false);
        return jobLogCatResult;
    }

    /**
     * 按 logId + 开始日期定位文件（跨天任务需用 startTime 所在日）
     *
     * @param basePath  日志文件根目录
     * @param localDate 日期
     * @param logId     任务日志id
     * @return
     * @since 2026/10/26
     */

    public static String resolveFile(String basePath, LocalDate localDate, Long logId) {
        return Paths.get(basePath, localDate.format(DAY), logId + ".log")
                .toAbsolutePath().toString();
    }

}
