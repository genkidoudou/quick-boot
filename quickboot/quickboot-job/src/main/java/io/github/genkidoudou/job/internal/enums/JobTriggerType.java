package io.github.genkidoudou.job.internal.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * 触发来源：决定这次运行为什么跑，写入日志用。
 * 注意：不是任务类型（{@link JobType}）。
 */
@AllArgsConstructor
@Getter
public enum JobTriggerType {
    CRON("CRON", "定时触发"),
    MANUAL("MANUAL", "手动触发");

    private final String value;
    private final String label;

    /**
     * 按值解析触发来源。
     *
     * @param value CRON / MANUAL
     * @return 枚举；无法识别时返回 null
     */
    public static JobTriggerType from(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(item -> item.value.equals(normalized))
                .findFirst()
                .orElse(null);
    }
}
