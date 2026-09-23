---
name: quickboot-codegen
description: >-
  按本仓库约定生成 quickboot 后端 CRUD 与 quick-ui 管理端页面，并通过 mysql MCP 把库表 DDL 同步到 sdd/ddl、把 sys_dict_type 与 sys_dict_data 同步到 sdd/dict。用户要加管理功能、根据表或建表语句生成代码、在 quickboot-system 加业务表、新建 Maven 业务模块、同步 DDL、同步字典时使用。
---

# Quickboot 代码生成

为本仓库生成管理端业务代码。先收成字段表，再按同一条链写出后端、SQL、权限和管理端页面。`quick-h5` 不在本技能范围内。

生成前先读本仓库里最近的同类实现，再写代码。**后端以 `SysUser` 为权威参考，落文件必须按 [templates.md](templates.md) 模板替换占位符生成。** 类注释、注解和分层以现有代码为准；契约见下文，细则与模板见 [backend.md](backend.md)。

## 先读哪份参考

| 场景 | 读取 |
|---|---|
| 任何一次生成 | 本文件的字段表与契约 |
| 写 Java / SQL | [backend.md](backend.md) + **完整模板 [templates.md](templates.md)** + 仓库 `SysUser*` |
| 写 quick-ui | [frontend.md](frontend.md) |
| 新建 Maven 模块 | [module.md](module.md) |
| 从库同步 DDL 或字典 | [sync.md](sync.md) |

## 入口

两条代码入口都先产出同一张字段表，再进入生成链。同步库表不走字段表。

1. **口头描述。** 业务名、字段、放在 `quickboot-system` 还是新模块。缺的列、查询项、表单项先问用户，问完再写文件。
2. **按表生成。** 用户给出 `CREATE TABLE`、字段清单，或表名加列说明。已给出的建表语句是表结构来源；只在缺少审计列时补列，不另写一张表。`sdd/ddl/{表名}.sql` 已存在时，以该文件为建表语句来源。
3. **同步库表。** 同步 DDL 或字典时只走 [sync.md](sync.md)，不生成 Java 和页面。

字段表：

| 项 | 内容 |
|---|---|
| 表名 | 蛇形，如 `sys_notice` |
| 类名 | 帕斯卡，如 `SysNotice` |
| 落点 | `system` 或新模块名 |
| 列 | 列名、SQL 类型、Java 类型、注释、是否列表、是否查询、是否表单、字典类型 |

这些列属于 `io.github.genkidoudou.core.entity.BaseEntity`，不写进 Entity / Vo：`del_flag`、`remark`、`create_by`、`create_time`、`update_by`、`update_time`。主键写在实体上：`Long id`，`@TableId(value = "id", type = IdType.ASSIGN_ID)`。

字段表另加一列（可选）：**表类型** = `业务表` | `关联表`。生成前先判定表类型，再选生成链。

落点：

- 表名以 `sys_` 开头，放进 `quickboot-system`。
- 其他表名，新建与 `system` 平级的 Maven 模块。
- 用户指定落点时，以用户指定为准。

## 表类型：业务表 vs 关联表

**关联表判定**（满足即可视为关联表）：

- 表名含两端实体（如 `sys_user_role`、`sys_role_menu`），或用户标明「关联 / 中间表」；
- 业务含义是多对多（或一对多挂载）关系；
- 字段以两端外键为主（如 `user_id` + `role_id`），可有 `id` / `digest` / 部分创建审计，**无独立业务表单语义**。

| 产物 | 业务表（默认） | 关联表（默认） |
|---|---|---|
| Entity + Mapper | 要 | 要 |
| Vo | `internal.vo` | **不要** |
| 标准 CRUD Controller / 菜单 / 前端 | 要 | **不要** |
| `BaseVoServiceImpl` 全套 `pageVo/saveVo/...` | 要 | **不要** |
| 绑定 Service（按主 id 查/覆写关联 id 列表） | — | 要（可挂主资源 Service，或薄 `I{Name}Service`） |
| `api.vo` / `api.api` | 仅跨模块 | **不要**（除非用户明确跨模块） |

关联关系通过**主资源**接口维护（如给用户分配角色：`List<Long> roleIds`），不单独做关联表管理页。

仅当用户明确要求「关联表也要独立 CRUD / 管理端列表」时，再按业务表生成 `internal.vo` + Controller（仍不进 `api`）。

细则与模板：[backend.md](backend.md)、[templates.md §8](templates.md)。

## 生成顺序

1. 判定表类型（业务表 / 关联表）；读 [templates.md](templates.md) 与仓库 `SysUser*`。
2. **业务表：** 按 templates §1–6 拷贝替换；Entity → Mapper → 先 `I{Name}Service` 再 `BaseVoServiceImpl` → Controller → Vo（`internal.vo`）。不要默认写 `api`。
3. **关联表：** 只按 templates §8 生成 Entity + Mapper + 绑定 Service；**不生成 Vo / Controller / 前端 / api**。
4. 仅跨模块业务能力时再写 `api.api` + `api.vo` + ApiImpl（模板 §7）。
5. Flyway SQL（业务表含菜单时再写菜单 INSERT；关联表默认不写菜单）。
6. 业务表再写 quick-ui；关联表跳过。
7. 对照契约与检查清单自检。

## 契约

- 分页入参 `PageRequest`：`{ current, size, param }`。出参 `R<PageInfo<Vo>>`，前端取 `data.records` 与 `data.total`。（**仅业务表 CRUD**）
- 接口：`POST {base}/page`、`GET {base}/{id}`、`POST {base}/add`、`POST {base}/update`、`POST {base}/remove`。`remove` 的请求体是 `List<Long>`。（**仅业务表**）
- **关联表默认不生成 Vo、不生成独立 CRUD HTTP**；绑定逻辑挂主资源或薄 Service，见「表类型」。
- **HTTP 方法：只允许 `GET`、`POST`。禁止 `@PutMapping` / `@DeleteMapping`。**
- **业务表 Service：先 `I{Name}Service`，再 `{Name}ServiceImpl extends BaseVoServiceImpl`；Controller 注入接口。** 方法名：`pageVo`、`getVoById`、`saveVo`、`updateVoById`、`removeByIds`。
- **Controller 只做校验与转发**：`AddGroup` / `UpdateGroup`；业务放 Service。
- **Vo 校验**（业务表）：`PatternConstants`；敏感字段 `WRITE_ONLY`。
- **JavaDoc**：生成的类/方法/字段均需注释。
- 系统模块 URL `sys/{资源}`；权限字 `system:{资源}:list|add|edit|remove`。
- Excel / 菜单 / 前端仅业务表且用户需要时生成。
- Controller 只返回 Vo；`api` 仅跨模块。

## 自检

- 先判定业务表 / 关联表，生成范围与表类型一致。
- **关联表：** 无 Vo、无独立 Controller/前端/api；有 Entity+Mapper；绑定方法用外键 id 列表。
- **业务表：** Vo 默认在 `internal.vo`；ServiceImpl 继承 `BaseVoServiceImpl`；Controller 注入接口；无 PUT/DELETE。
- 未要求跨模块时无无用 `api`。
- 缺审计列的关联表 Entity 不继承 `BaseEntity`。
- 新模块接线完整；业务表菜单路径正确。
