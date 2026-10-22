package io.github.genkidoudou.job.internal.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * 任务触发来源
 *
 * @author luyanan
 * @since 2026/10/22
 */
@AllArgsConstructor
@Getter
public enum JobTriggerType {

    BEAN("BEAN", "BEAN"),
    SHELL("SHELL", "SHELL"),
    PYTHON("PYTHON", "PYTHON");

    private String value;

    private String label;


    public static JobTriggerType get(String triggerType) {
        if (StrUtil.isBlank(triggerType)) {
            return null;
        }
        return Arrays
                .stream(JobTriggerType.values())
                .filter(a -> a.getValue().toUpperCase(Locale.ROOT)
                        .equals(triggerType.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElse(null);
    }
}
