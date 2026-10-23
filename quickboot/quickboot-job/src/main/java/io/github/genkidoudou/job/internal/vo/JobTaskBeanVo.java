package io.github.genkidoudou.job.internal.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现 {@code ITask} 的 Spring Bean 列表项，供管理端下拉选择。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobTaskBeanVo {

    /** Spring Bean 名称（写入 invokeTarget）。 */
    private String beanName;

    /** 实现类全限定名，便于区分同名含义。 */
    private String className;
}
