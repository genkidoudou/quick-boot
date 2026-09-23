# 新 Maven 模块

在 `quickboot-system` 内加表时不要改本节。只有落点是新模块时，按下面清单改接线。模块名使用小写英文，例如 `order`，目录 `quickboot/quickboot-order`。

## 清单

1. `quickboot/pom.xml` 的 `<modules>` 增加 `quickboot-{模块}`。
2. 新建 `quickboot/quickboot-{模块}/pom.xml`。父工程是 `io.github.genkidoudou:quickboot:1.0-SNAPSHOT`。`groupId` 为 `io.github.genkidoudou.{模块}`，`artifactId` 为 `quickboot-{模块}`。依赖 `quickboot-core` 与 `spring-modulith-api`，版本写法对齐 `quickboot-system/pom.xml`。
3. 包 `io.github.genkidoudou.{模块}` 的 `package-info.java`：

```java
@org.springframework.modulith.ApplicationModule(
    displayName = "{模块}",
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {"core", "common"}
)
package io.github.genkidoudou.{模块};

import org.springframework.modulith.ApplicationModule;
```

4. `quickboot-web/pom.xml` 增加对该模块的依赖，版本 `${project.version}`。
5. `io.github.genkidoudou.web` 的 `package-info.java` 中，`allowedDependencies` 加上该模块名。
6. `WebApplication` 的 `scanBasePackages` 加上 `io.github.genkidoudou.{模块}`。`@MapperScan` 加上 `io.github.genkidoudou.{模块}.internal.mapper`。

## 边界

- 默认生成落在 `internal`（含 `internal.vo`）。`api` 及子包可被其他模块引用，只放对外 API 与对外 Vo。
- `internal` 及子包只在本模块内使用；其他模块禁止 import `internal`。
- `quickboot-web` 不 import `{模块}.internal`。Controller 放在业务模块的 `internal.controller`，由组件扫描加载。
- 业务代码的包、类和 SQL 规则见 [backend.md](backend.md)。页面见 [frontend.md](frontend.md)。
