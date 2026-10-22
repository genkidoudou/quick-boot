package io.github.genkidoudou.job.internal.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * 任务类型：决定怎么执行，对应 {@code sys_job.job_type}。
 */
@AllArgsConstructor
@Getter
public enum JobType {

    BEAN("BEAN", "Bean"),
    SHELL("SHELL", "Shell"),
    PYTHON("PYTHON", "Python");

    private final String value;
    private final String label;

    /**
     * 按值解析任务类型。
     *
     * @param value BEAN / SHELL / PYTHON
     * @return 枚举；无法识别时返回 null
     */
    public static JobType get(String value) {
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
