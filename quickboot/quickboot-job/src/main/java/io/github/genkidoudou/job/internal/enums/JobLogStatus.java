package io.github.genkidoudou.job.internal.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 单次执行状态：对应 sys_job_log.status（字典 JOB_STATUS）。
 */
@AllArgsConstructor
@Getter
public enum JobLogStatus {
    RUNNING("0", "执行中"),
    SUCCESS("1", "成功"),
    FAIL("2", "失败"),
    SKIP("3", "跳过");
    private final String value;
    private final String label;
}