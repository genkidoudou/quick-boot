/**
 * 消息中心 Modulith 应用模块。
 * <p>
 * {@code internal} 及全部子包只在本模块内使用。
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "msg",
        type = ApplicationModule.Type.OPEN,
        allowedDependencies = {"core", "common"}
)
package io.github.genkidoudou.msg;

import org.springframework.modulith.ApplicationModule;
