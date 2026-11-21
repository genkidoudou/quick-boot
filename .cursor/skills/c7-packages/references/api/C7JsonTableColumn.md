<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7JsonTableColumn API

> 源码：`packages/C7JsonTableColumn/`
> 标签：`<C7JsonTableColumn>` / `<c7-json-table-column>`

C7 表格列渲染器：根据 `columns` 配置生成 `el-table-column`，内置 text/tag/image/link/slot 等列类型，与 {@link C7JsonTable} 配合使用；列级 `emptyText` 可覆盖表级默认空占位。@prop {Record<string, unknown>[]} columns 列描述数组（必填）@prop {string} [emptyText] 表级默认空文案，text 列无 formatter 时兜底

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `columns` / `columns` | 列描述数组（必填）。非数组时本组件不渲染列，开发环境 `console.warn`。@type {Record<string, unknown>[]} | `Array` | （必填） |
| `empty-text` / `emptyText` | 表级默认空文案：用于 **`columnType==='text'`** 且无 **`formatter`** 时，当列上未配置 **`emptyText`** 且单元格值为 **`null` / `undefined` / ''`** 的展示兜底（链式：`列 emptyText` → 本 prop → **`'-'`**）。 | `String` | undefined |

## 事件

| 事件 | 说明 |
| --- | --- |
| _无或未解析_ | |

## 插槽

| 插槽 |
| --- |
| `header` |
| `default` |
