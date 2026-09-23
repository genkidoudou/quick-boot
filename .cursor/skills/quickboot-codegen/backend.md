# 后端生成

包根：`io.github.genkidoudou.{模块}`。系统模块名是 `system`，代码在 `quickboot/quickboot-system`。

**权威参考：** 仓库中 `SysUser` 全链路。  
**落文件模板：** [templates.md](templates.md)。先判定**业务表 / 关联表**，再选模板章节。

## 关联表（默认精简生成）

判定与范围见 [SKILL.md 表类型](SKILL.md)。

默认只生成：

| 类型 | 说明 | 模板 |
|---|---|---|
| Entity | 不继承 `BaseEntity`（无完整审计时） | [templates §8.1](templates.md) |
| Mapper | `BaseBaseMapper` | [templates §8.2](templates.md) |
| 绑定 Service | 按主 id 查询/覆盖关联 id 列表；**无 Vo** | [templates §8.3](templates.md) |

默认**不生成**：Vo、`BaseVoServiceImpl` CRUD、独立 Controller、菜单、前端、`api`。

绑定能力挂在主资源 Controller（如用户分配角色），或由薄 `I{Name}Service` 供主 Service 调用。用户明确要求关联表独立 CRUD 时，才按业务表 §1–6 生成（Vo 仍只在 `internal.vo`）。

## 包边界（默认全部进 internal）

| 包 | 用途 |
|---|---|
| `{模块}.internal.**` | 本模块私有：Entity、Mapper、Service、Controller、Vo、ApiImpl |
| `{模块}.api.**` | 对外契约：仅跨模块时（`api.api`、对外 Vo） |

**业务表默认生成：**

| 类型 | 包 | 模板章节 |
|---|---|---|
| Entity | `internal.entity` | [templates §1](templates.md) |
| Vo | `internal.vo`（默认） | [templates §2](templates.md) |
| Mapper | `internal.mapper` | [templates §3](templates.md) |
| `I{Name}Service` | `internal.service` | [templates §4](templates.md) |
| `{Name}ServiceImpl` | `internal.service.impl` | [templates §5](templates.md) |
| Controller | `internal.controller` | [templates §6](templates.md) |
| Api / ApiImpl | 仅跨模块 | [templates §7](templates.md) |

不要默认创建 `api.vo` / `api.api`。Controller 注入 `I{Name}Service`，不注入 Impl。

**何时写 api：** 其他模块要 Java 依赖调用本模块时；Vo 迁到或新建在 `api.vo`；ApiImpl 只委托 Service，返回 Vo。关联表默认不写 api。

## JavaDoc（必须）

| 目标 | 要求 |
|---|---|
| 类 / 接口 | 类上方 `/** ... */`；Entity 写明表名 |
| 公开方法 | 说明、`@param`、`@return` |
| 字段 | 单行 `/** ... */` |
| Mapper | 即使无方法也要有类注释 |

## HTTP（禁止 PUT / DELETE）

只用 `@GetMapping` / `@PostMapping`。标准：`POST page`、`GET {id}`、`POST add`、`POST update`、`POST remove`。

## 类型映射

| SQL | Java |
|---|---|
| `bigint` | `Long` |
| `int` | `Integer` |
| `decimal` / `numeric` | `BigDecimal` |
| `datetime` / `timestamp` | `LocalDateTime` |
| `date` | `LocalDate` |
| `varchar` / `char` / `text` | `String` |
| 状态、字典类 | `String` |

列名异常（如 `role_Id`）用 `@TableField("role_Id")`。

## 分层要点（细节以模板为准）

- Service 基类：`BaseVoServiceImpl`（禁止 `CrudServiceImpl`）。
- 方法名：`pageVo`、`getVoById`、`saveVo`、`updateVoById`、`removeByIds` → `deleteByIds`。
- 先接口后实现；`getVoById` 不存在则抛 `WarningException.literal`。
- Controller 只校验转发：`@Validated(AddGroup/UpdateGroup)`。
- Vo：`AddGroup`/`UpdateGroup`；手机号/用户名/密码用 `PatternConstants`；敏感字段 `WRITE_ONLY`。（业务表）
- **关联表：** 无 Vo；不继承完整 `BaseEntity` 时只声明实有列；用绑定 Service，不用 `BaseVoServiceImpl` CRUD。

## SQL

迁移目录：`quickboot/quickboot-web/src/main/resources/db/migration/`。  
文件名 `V{n}__{表名}.sql`。`n` = 已有最大版本 + 1。

建表含主键与 `BaseEntity` 列：

```sql
id          bigint       not null comment '主键',
del_flag    char(1)      default '0' comment '删除标志（0存在 1删除）',
remark      varchar(500) null comment '备注',
create_by   varchar(64)  null comment '创建者',
create_time datetime     null comment '创建时间',
update_by   varchar(64)  null comment '更新者',
update_time datetime     null comment '更新时间'
```

用户已给 `CREATE TABLE` 时以其为准，缺审计列则补进同一迁移。

## 权限与菜单

- 系统：`system:{资源}:list|add|edit|remove`；URL `sys/{资源}`
- 新模块：`{模块}:{资源}:...`；URL `{模块}/{资源}`
- 菜单组件：`system/{资源}/index` 或 `{模块}/{资源}/index`
- 找到菜单表列定义后再写 INSERT；找不到则迁移末尾用注释列出，不编造列
