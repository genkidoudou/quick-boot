package io.github.genkidoudou.job.internal.quartz;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 定时任务配置
 *
 * @author luyanan
 * @since 2026/10/26
 */
@Data
@ConfigurationProperties(prefix = "qc.job")
public class JobLogProperties {


    /**
     * 过程日志的根目录
     *
     * @since 2026/10/26
     */

    private String logPath = "logs/job";

    /**
     * 日志保留天数
     *
     * @since 2026/10/26
     */

    private int logRetentionDays = 30;
}
