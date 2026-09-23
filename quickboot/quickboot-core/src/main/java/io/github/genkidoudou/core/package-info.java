/**
 * 核心基础设施 Modulith 应用模块：基类实体、MyBatis-Plus 配置、登录上下文等，无业务域 API。
 * <p>OPEN：基础设施模块，子包可被其他模块引用。
 */
@org.springframework.modulith.ApplicationModule(
  displayName = "core",
  type = org.springframework.modulith.ApplicationModule.Type.OPEN,
  allowedDependencies = {"common"}
)
package io.github.genkidoudou.core;
