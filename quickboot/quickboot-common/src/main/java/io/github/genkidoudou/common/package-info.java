/**
 * 通用能力 Modulith 应用模块：横切 SPI、工具与监控采集（注解/切面/事件），不含业务域实体。
 * <p>OPEN：作为共享基建，子包可被其他模块引用。
 */
@org.springframework.modulith.ApplicationModule(
  displayName = "common",
  type = org.springframework.modulith.ApplicationModule.Type.OPEN,
  allowedDependencies = {}
)
package io.github.genkidoudou.common;
