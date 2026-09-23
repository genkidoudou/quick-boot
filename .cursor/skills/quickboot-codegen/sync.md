# 从数据库同步

MCP 服务名是 `mysql`，配置在 `.cursor/mcp.json`。用系统 `node`（需 ≥ 18）启动本地安装的 [`mysql-mcp-server`](https://www.npmjs.com/package/mysql-mcp-server)（`.cursor/mysql-mcp`）。连接与 `quickboot/quickboot-web/src/main/resources/application-dev.yml` 的 datasource 一致，库名 `quickboot-dev`。不要在对话或文件里复述密码。

工具（调用前先核对参数名）：

| 工具 | 用途 |
|---|---|
| `list_tables` | 列库表。参数 `database` 可省略（默认 `MYSQL_DATABASE`） |
| `describe_table` | 列结构（`DESCRIBE`） |
| `execute_query` | 只读 SQL：`SELECT` / `SHOW` / `DESCRIBE` / `EXPLAIN` |

仓库对 `validators.js` 打了补丁（`postinstall` → `patch-validators.js`），允许 `SHOW CREATE TABLE`，否则会被误判为写操作。

`execute_query` 结果是 JSON 数组文本。服务端默认最多返回 1000 行。需要更多行时在 SQL 里写 `LIMIT n OFFSET m`，直到某一页不足 `n` 行。

## 同步 DDL

触发：用户要同步表结构、导出 DDL，或把库里的建表语句放到 `sdd/ddl`。

1. 用户点名表则只处理这些表。未点名时调用 `list_tables`（`database` 为 `quickboot-dev`）。返回数组里表名在 `Tables_in_quickboot-dev`（或 `Tables_in_<库名>`）字段。
2. 每张表调用 `execute_query`，`query` 为 ``SHOW CREATE TABLE `{表名}` ``，`database` 为 `quickboot-dev`。
3. 从结果第一行的 `Create Table`（或 `Create Table` 键）取出建表语句，写入 `sdd/ddl/{表名}.sql`。语句末尾补一个分号和换行。已有同名文件则覆盖。
4. 全量同步时，删除 `sdd/ddl` 中库里已经不存在的 `.sql`。点名同步时只覆盖被点名的文件。

这些文件是库里的表结构快照，不替代 Flyway 迁移。生成业务代码时，若 `sdd/ddl/{表名}.sql` 已存在，以该文件为建表语句来源。

## 同步字典

触发：用户要同步字典，或更新 `sdd/dict`。

1. 用 `execute_query` 读完 `sys_dict_type` 和 `sys_dict_data` 的全部行（`SELECT *`，按上一节处理分页）。
2. 原始行写入：
   - `sdd/dict/sys_dict_type.json`
   - `sdd/dict/sys_dict_data.json`
   内容是 JSON 数组，字段名与查询列一致。
3. 两边都有 `dict_type` 时，再写 `sdd/dict/by-type.json`，供后续按类型取值：

```json
{
  "user_status": {
    "dictName": "用户状态",
    "status": "0",
    "items": []
  }
}
```

`dictName`、`status` 来自类型表的 `dict_name`、`status`。`items` 是该 `dict_type` 下的数据行；有 `dict_sort` 时按它升序。没有 `dict_type` 列时不写 `by-type.json`，并在回复里说明两份原始 JSON 的列名。

## MCP 安装

依赖安装：

```bash
cd .cursor/mysql-mcp
npm install --registry=https://registry.npmmirror.com
```

改完 `.cursor/mcp.json` 后，在 Cursor 里重新加载 MCP。
