# quickboot 对接积木报表 / JimuBI

本目录是 **对接文档 + 代码示例**，不是已落地代码。落地时请按文档改 `quickboot` / `quick-ui`，不要直接把 `examples/` 当成可编译模块。

## 范围

| 模块 | 版本 | 本阶段 |
|------|------|--------|
| `jimureport-spring-boot4-starter` | **2.5.6.1** | 对接 |
| `jimubi-spring-boot4-starter` | **2.5.6.1** | 对接 |
| `jimureport-echarts-starter` | 2.3.0 | 建议带上 |
| JimuChatBI / AI 自动建表 | — | 不做 |
| magic-api | — | 不做（与 Boot 4 不兼容） |

官方参考：

- [快速集成](https://help.jimureport.com/quick/)
- [GitHub Releases（含 2.5.6.1 安全补丁说明）](https://github.com/jeecgboot/jimureport/releases)

## 文档索引

| 文件 | 说明 |
|------|------|
| [对接指南.md](./对接指南.md) | 完整步骤、风险、验收、与本仓库现有预埋的对应关系 |
| [examples/](./examples/) | 可复制改造的 Java / YAML / SQL / POM 示例（含详细注释） |

## 仓库里已经有的预埋（落地时复用，勿重复造）

- 父 POM 已声明 Boot4 starter 与 Jeecg 仓库（当前版本属性仍是 `2.5.0`，需升到 `2.5.6.1`）
- `application.yml`：Flyway `placeholder-replacement: false`；Druid 放行积木常见 `where 1=1`
- 慢 SQL：`/jmreport/`、`/drag/`、`/jimubi/` 已标记为 JIMU
- 前端：`quick-ui/src/api/report/jimu.js`、iframe 自动带 `token`（`IframeToggle`）

## 建议落地顺序（摘要）

1. 升版本并引入依赖 → 编译通过  
2. Flyway 执行官方初始化 SQL（报表 + BI）  
3. Sa-Token 放行积木路径 + 实现 `JmReportTokenServiceI` / `IOnlDragExternalService`  
4. 补 `/report/jimu/catalog/*` 目录接口  
5. 菜单挂 `/jmreport/list`、`/drag/list`  
6. 登录后验收设计器、SQL 数据集、iframe 带 token  

细节与示例见 [对接指南.md](./对接指南.md)。
