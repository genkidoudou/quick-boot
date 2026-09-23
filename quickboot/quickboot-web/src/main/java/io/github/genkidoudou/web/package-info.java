/**
 * Web 启动与接入层 Modulith 应用模块。
 * <p>可依赖 common / core / system；禁止引用 {@code system.internal.**}（见 ModulithArchitectureTest）。
 */
@org.springframework.modulith.ApplicationModule(
  displayName = "web",
  allowedDependencies = {
    "common",
    "core",
    "system"
  }
)
package io.github.genkidoudou.web;
