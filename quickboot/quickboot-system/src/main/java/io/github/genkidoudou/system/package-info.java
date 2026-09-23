/**
 * 系统域 Modulith 应用模块。
 * <p>
 * 约定（由 {@code ModulithArchitectureTest} 强制，而非 NamedInterface 逐包标注）：
 * <ul>
 *   <li>{@code api} 及全部子包：对外可见</li>
 *   <li>{@code internal} 及全部子包：禁止被其他模块引用</li>
 * </ul>
 * 模块类型为 OPEN，避免 CLOSED + NamedInterface 不递归子包的繁琐配置。
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "system",
        type = ApplicationModule.Type.OPEN,
        allowedDependencies = {"core", "common"}
)
package io.github.genkidoudou.system;

import org.springframework.modulith.ApplicationModule;
