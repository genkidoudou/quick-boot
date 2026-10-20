/**
 * Web 启动与接入层 Modulith 应用模块。
 * <p>可依赖 common / core / system / job；禁止引用各业务模块 {@code internal.**}（见 ModulithArchitectureTest）。
 */
@org.springframework.modulith.ApplicationModule(
  displayName = "web",
  allowedDependencies = {
    "common",
    "core",
    "system",
    "job"
  }
)
package io.github.genkidoudou.web;
