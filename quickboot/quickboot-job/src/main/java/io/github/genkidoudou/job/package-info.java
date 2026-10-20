/**
 * 定时任务域 Modulith 应用模块。
 * <p>
 * {@code internal} 及全部子包只在本模块内使用。
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "job",
        type = ApplicationModule.Type.OPEN,
        allowedDependencies = {"core", "common"}
)
package io.github.genkidoudou.job;

import org.springframework.modulith.ApplicationModule;
