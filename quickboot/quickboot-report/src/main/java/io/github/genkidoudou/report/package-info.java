/**
 * 报表 Modulith 应用模块（积木报表 / JimuBI 对接）。
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "report",
        type = ApplicationModule.Type.OPEN,
        allowedDependencies = {"core", "common", "system"}
)
package io.github.genkidoudou.report;

import org.springframework.modulith.ApplicationModule;
