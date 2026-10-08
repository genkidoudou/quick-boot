# 后端生成

包根：`io.github.genkidoudou.{模块}`。系统模块名是 `system`，代码在 `quickboot/quickboot-system`。

**权威参考：** 仓库中 `SysUser` 全链路（骨架）。Controller 权限 / 防重、Service 事务、菜单与字典 SQL 以本文件与 [templates.md](templates.md) 为准（SysUser 样板可能尚未全部挂上这些注解）。  
**落文件模板：** [templates.md](templates.md)。先判定**业务表 / 关联表**，再选模板章节。

## 关联表（默认精简生成）

判定与范围见 [SKILL.md 表类型](SKILL.md)。

默认只生成：

| 类型 | 说明 | 模板 |
|---|---|---|
| Entity | 不继承 `BaseEntity`（无完整审计时） | [templates §8.1](templates.md) |
| Mapper | `BaseBaseMapper` | [templates §8.2](templates.md) |
| 绑定 Service | 按主 id 查询/覆盖关联 id 列表；**无 Vo** | [templates §8.3](templates.md) |

默认**不生成**：Vo、`BaseVoServiceImpl` CRUD、独立 Controller、菜单、前端、`api`、字典 SQL。

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

## Controller 注解（业务表必须）

| 接口 | 权限 | 防重复提交 |
|---|---|---|
| `page` | `@SaCheckPermission("{前缀}:{资源}:list")` | — |
| `get` | `@SaCheckPermission("{前缀}:{资源}:query")` | — |
| `add` | `@SaCheckPermission("{前缀}:{资源}:add")` | `@Idempotent(ttlSeconds = 10, key = "#userId")` |
| `update` | `@SaCheckPermission("{前缀}:{资源}:edit")` | `@Idempotent(ttlSeconds = 10, key = "#userId")` |
| `remove` | `@SaCheckPermission("{前缀}:{资源}:remove")` | — |

- 权限：`cn.dev33.satoken.annotation.SaCheckPermission`
- 防重：`io.github.genkidoudou.common.idempotency.Idempotent`（`includeUri` 默认 true）
- `{前缀}`：系统模块 `system`，新模块为模块名

## Service 事务（业务表必须）

`saveVo`、`updateVoById` **必须**覆盖并标注：

```java
@Transactional(rollbackFor = Exception.class)
```

即使方法体仅 `return super.saveVo(vo)` / `super.updateVoById(vo)` 也要写。有密码加密等逻辑时放在同一方法内、`super` 之前。

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

## 字典列识别

列注释形如 **`中文说明(dict_type)`**（括号内为字典类型编码）时，判定为字典字段。

| 示例注释 | 字典名称 | `dict_type` |
|---|---|---|
| `用户类型(user_type)` | 用户类型 | `user_type` |
| `性别(sex)` | 性别 | `sex` |
| `状态(COMMON_STATUS)` | 状态 | `COMMON_STATUS` |

匹配：注释能解析出尾部一对括号，且括号内为标识符（字母/数字/下划线，可含大小写）。括号前文本作 `dict_name`。

字段表「字典类型」填该 `dict_type`。前端列表/搜索用 `tag` + `dictList` / `select`。

**生成字典 SQL 前必须问用户该 `dict_type` 的枚举值**（每项至少：`dict_label`、`dict_value`，可选 `dict_sort`）。未答完枚举前不要写 `sys_dict_data` INSERT，也不要结束生成。

若 `sdd/dict` 或库中已有同 `dict_type`，告知用户并询问：跳过 / 仍按用户枚举覆盖写入迁移（默认跳过已存在类型）。

## 分层要点（细节以模板为准）

- Service 基类：`BaseVoServiceImpl`（禁止 `CrudServiceImpl`）。
- 方法名：`pageVo`、`getVoById`、`saveVo`、`updateVoById`、`removeByIds` → `deleteByIds`。
- 先接口后实现；`getVoById` 不存在则抛 `WarningException.literal`。
- Controller 只校验转发：`@Validated(AddGroup/UpdateGroup)`；权限与防重见上表。
- Vo：`AddGroup`/`UpdateGroup`；手机号/用户名/密码用 `PatternConstants`；敏感字段 `WRITE_ONLY`。（业务表）
- **关联表：** 无 Vo；不继承完整 `BaseEntity` 时只声明实有列；用绑定 Service，不用 `BaseVoServiceImpl` CRUD。

## SQL

迁移目录：`quickboot/quickboot-web/src/main/resources/db/migration/`。  
文件名 `V{n}__{表名}.sql`。`n` = 已有最大版本 + 1。若目录不存在则创建。

同一迁移文件建议顺序：建表 → 字典 INSERT → 菜单 INSERT。

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

用户已给 `CREATE TABLE` 时以其为准，缺审计列则补进同一迁移。列定义对齐 `sdd/ddl/{表名}.sql`（若已有）。

### 字典 INSERT（业务表，问完枚举后）

对齐 `sdd/ddl/sys_dict_type.sql`、`sys_dict_data.sql` 列。ID 用明确 bigint（可雪花风格），并在 SQL 注释标明可改。

```sql
-- 字典类型 {dict_type}
INSERT INTO sys_dict_type (id, del_flag, dict_name, dict_type, status)
VALUES ({typeId}, '0', '{字典名称}', '{dict_type}', '0');

-- 字典数据 {dict_type}
INSERT INTO sys_dict_data (id, del_flag, dict_type, dict_label, dict_value, dict_sort, is_default, status)
VALUES
  ({dataId1}, '0', '{dict_type}', '{标签1}', '{值1}', 0, '0', '0'),
  ({dataId2}, '0', '{dict_type}', '{标签2}', '{值2}', 1, '0', '0');
```

同一迁移里多个字典类型时，每个类型各写一组。已存在则按上文「跳过 / 覆盖」策略。

### 菜单 INSERT（业务表必须）

对齐 `sdd/ddl/sys_menu.sql`。`MENU_TYPE`：`M` 目录、`C` 菜单、`F` 按钮。

默认生成（不问用户也要写；`parent_id` 用户指定则用指定值，否则 `0` 并在 SQL 顶注释「请按实际父菜单修改 parent_id」）：

1. **一条 C（菜单页）**
2. **四条 F（按钮）**：查询列表、查询详情、新增、修改、删除——权限字与 Controller 一致时，按钮可为：`list`、`query`、`add`、`edit`、`remove`（共 5 个 F），或合并 list/query 为「查询」一个按钮 + add/edit/remove（共 4 个 F）。**默认 5 个 F**，与权限字一一对应。

| 项 | C 菜单 | F 按钮 |
|---|---|---|
| `menu_type` | `C` | `F` |
| `menu_name` | `{中文名}` | 列表 / 详情 / 新增 / 修改 / 删除 |
| `path` | `{资源}`（系统）或按模块约定 | NULL |
| `component` | `system/{资源}/index` 或 `{模块}/{资源}/index` | NULL |
| `perms` | `{前缀}:{资源}:list` | `{前缀}:{资源}:list\|query\|add\|edit\|remove` |
| `parent_id` | 用户指定或 `0` | C 菜单的 `id` |
| `order_num` | 合理默认 | 1..n |
| `is_frame` / `is_cache` / `visible` / `status` | `'0'` | `'0'` |
| `del_flag` | `'0'` | `'0'` |

示例骨架（ID 请换成实值）：

```sql
-- parent_id 请按实际父菜单修改（当前为 0）
INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, path, component, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  ({menuId}, '0', 0, '{中文名}', 'C', '{资源}', 'system/{资源}/index', 'system:{资源}:list', 1, '0', '0', '0', '0');

INSERT INTO sys_menu (id, del_flag, parent_id, menu_name, menu_type, perms, order_num, is_frame, is_cache, visible, status)
VALUES
  ({btnList}, '0', {menuId}, '{中文名}列表', 'F', 'system:{资源}:list', 1, '0', '0', '0', '0'),
  ({btnQuery}, '0', {menuId}, '{中文名}详情', 'F', 'system:{资源}:query', 2, '0', '0', '0', '0'),
  ({btnAdd}, '0', {menuId}, '{中文名}新增', 'F', 'system:{资源}:add', 3, '0', '0', '0', '0'),
  ({btnEdit}, '0', {menuId}, '{中文名}修改', 'F', 'system:{资源}:edit', 4, '0', '0', '0', '0'),
  ({btnRemove}, '0', {menuId}, '{中文名}删除', 'F', 'system:{资源}:remove', 5, '0', '0', '0', '0');
```

关联表默认不写菜单。

## 权限与菜单（摘要）

- 系统：`system:{资源}:list|query|add|edit|remove`；URL `sys/{资源}`
- 新模块：`{模块}:{资源}:...`；URL `{模块}/{资源}`
- 菜单组件：`system/{资源}/index` 或 `{模块}/{资源}/index`
- 列定义以 `sdd/ddl/sys_menu.sql` 为准，不编造不存在的列
