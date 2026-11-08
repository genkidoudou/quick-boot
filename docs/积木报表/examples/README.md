# 示例代码说明

本目录全部是**文档示例**，不会参与 Maven 编译。

| 文件 | 用途 |
|------|------|
| `pom-snippets.xml` | 父 POM 升版本 + web 模块引入依赖 |
| `application-jimureport.yml` | 配置片段；并注明仓库里已有的 Flyway/Druid 项 |
| `SaTokenConfigure.example.java` | 放行 `/jmreport/**` `/drag/**` `/jimubi/**` |
| `JmReportTokenServiceImpl.example.java` | 报表 Token / 角色 / 权限 / 字典桥接 |
| `JimuDragExternalServiceImpl.example.java` | BI 字典与日志扩展 |
| `JimuCatalogController.example.java` | 对齐前端 `jimu.js` 的目录 API |
| `V26__jimureport_menu.example.sql` | 菜单种子示意（schema 请用官方 SQL） |

使用时请改包名、对照 **2.5.6.1** jar 接口编译，并核对真实表结构。
