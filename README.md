# quick-boot

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3-42b883)](https://vuejs.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

开箱即用的企业级快速开发脚手架：**Spring Boot 4 模块化后端** + **Vue 3 管理端**，可选 **uni-app 多端客户端**。

面向希望快速搭建后台管理系统、权限体系与定时任务能力的个人与团队。欢迎 Star、Issue 与 PR。

---

## 特性

- **模块化单体**：Spring Modulith 划分 `common` / `core` / `system` / `job` / `web`，边界清晰、可演进
- **权限与安全**：Sa-Token、RBAC 菜单按钮权限、数据权限、验证码等常见能力
- **系统管理**：用户、角色、部门、菜单、字典、参数、文件、操作/登录日志等
- **定时任务**：Quartz JDBC 集群；支持 Bean / Shell / Python；执行租约与文件过程日志
- **Schema 即代码**：Flyway 版本化迁移；`sdd/` 保留 DDL 与字典规范
- **前后端分离**：`quick-ui`（Vite + Element Plus）；可选 `quick-h5`（H5 / 小程序）

## 在线演示

> 若已部署公网演示，请在此补充地址与账号。当前仓库未附带在线环境。

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Java 17、Spring Boot 4、Spring Modulith、MyBatis-Plus、Sa-Token、Flyway、Redis、Druid、Quartz |
| 管理端 | Vue 3、Vite、Element Plus、Pinia、pnpm |
| 移动端 | uni-app（Vue 3 + Vite），详见 [quick-h5/README.md](./quick-h5/README.md) |

## 仓库结构

```text
quick-boot/
├── quickboot/              # 后端（Maven 多模块）
│   ├── quickboot-common/   # 通用组件与 API 约定
│   ├── quickboot-core/     # 核心基础设施
│   ├── quickboot-system/   # 系统管理业务
│   ├── quickboot-job/      # 定时任务
│   └── quickboot-web/      # 启动入口与 Flyway
├── quick-ui/               # 管理端前端
├── quick-h5/               # 多端客户端（可选）
├── sdd/                    # DDL / 字典规范产物
└── docs/                   # 设计与补充文档
```

默认后端端口：**9993**。

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+（推荐 pnpm 9）
- MySQL 8.x（或兼容库）
- Redis

## 快速开始

### 1. 克隆仓库

```bash
git clone https://github.com/<your-org>/quick-boot.git
cd quick-boot
```

请将上方地址替换为你的实际仓库 URL。

### 2. 配置数据库与 Redis

编辑（或复制后本地覆盖）：

```text
quickboot/quickboot-web/src/main/resources/application-dev.yml
```

至少配置：

- `spring.datasource.url` / `username` / `password`
- `spring.data.redis.host`（及库号等）

创建空库，字符集建议 `utf8mb4`。首次启动时 **Flyway** 会自动执行：

```text
quickboot/quickboot-web/src/main/resources/db/migration/
```

> 请勿将真实生产密钥提交到公开仓库。建议使用环境变量、本地 profile 或私密配置覆盖。

### 3. 启动后端

```bash
cd quickboot
mvn -pl quickboot-web -am spring-boot:run
```

也可在 IDE 中运行：`io.github.genkidoudou.web.WebApplication`（默认 `dev` profile）。

可选：跳过 Modulith 架构校验（仅应急）

```bash
mvn verify -DskipModulithArchitecture=true
```

### 4. 启动管理端

```bash
cd quick-ui
pnpm install
pnpm dev
```

生产构建：

```bash
pnpm build:prod
```

### 5. 多端客户端（可选）

见 [quick-h5/README.md](./quick-h5/README.md)。

## 功能模块一览

| 模块 | 说明 |
|------|------|
| 系统管理 | 用户、角色、部门、菜单、字典、参数配置、文件 |
| 审计 | 操作日志、登录日志 |
| 定时任务 | 任务管理、执行日志、过程日志增量查看 |
| 认证授权 | 登录、会话、按钮级权限（前后端双校验） |

## 定时任务说明

- **调度引擎**：Quartz JDBC JobStore，可多节点集群
- **执行器**：Spring Bean（实现 `ITask`）、Shell、Python
- **状态与租约**：`sys_job_log` 记录 RUNNING/SUCCESS/FAIL；执行中心跳，节点失联后自动收尸
- **过程日志**：本地文件 `{log-path}/yyyy-MM-dd/{logId}.log`（默认根目录 `logs/job`），管理端按行拉取
- **执行时长**：默认不限制超时，适合长批处理任务

## 配置项（常用）

| 配置 | 说明 |
|------|------|
| `spring.profiles.active` | 默认 `dev` |
| `server.port` | 默认 `9993` |
| `spring.flyway.*` | 数据库迁移 |
| `qc.job.log-path` | 任务过程日志目录 |

完整配置见 `quickboot-web` 下 `application.yml` / `application-*.yml`。

## 开发约定

1. **数据库变更**必须通过 Flyway；`sdd/ddl`、`sdd/dict` 作为规范参考同步更新。
2. 后端遵循 Modulith 模块边界，避免跨模块违规依赖。
3. 新增接口权限：后端 `@SaCheckPermission` 与前端 `v-hasPermi`、菜单 SQL 保持一致。
4. 提交前尽量完成本地编译与关键路径自测；勿提交本机密钥与大数据文件。

## 参与贡献

欢迎贡献代码、文档与 Issue：

1. Fork 本仓库并创建特性分支
2. 保持改动聚焦、提交说明清晰
3. 如涉及表结构或菜单权限，请附带 Flyway 脚本
4. 发起 Pull Request，描述动机、改动点与验证方式

Bug 报告请尽量提供：版本 / 复现步骤 / 日志或截图。

## 路线图（建议）

- [ ] 补充贡献指南 `CONTRIBUTING.md` 与 Issue / PR 模板
- [ ] 提供 Docker Compose 一键启动（MySQL + Redis + 应用）
- [ ] 公网演示环境与默认体验账号文档化
- [ ] 完善英文 README（`README.en.md`）

## 致谢

感谢 Spring、Vue、Element Plus、MyBatis-Plus、Sa-Token、Quartz 等开源项目。

## License

[MIT License](./LICENSE) © 2026 quick-boot contributors
