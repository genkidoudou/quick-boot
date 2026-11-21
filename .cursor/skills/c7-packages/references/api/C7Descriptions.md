<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Descriptions API

> 源码：`packages/C7Descriptions/`
> 标签：`<C7Descriptions>` / `<c7-descriptions>`

C7 描述列表：在 **`ElDescriptions`** 上以 **`data` + `items`** 配置驱动详情单元格，内置 **`tag`（`C7DictTag`）**、**`image`**、**`link`**、**`copy`/`copyable`** 与文本列。**根透传**：除 **`data` / `items` / `defaultEmptyText`** 外，其余属性经 **`$attrs`** 传给 **`ElDescriptions`**（与 EP 文档一致）。**`row` 与 `data`**：具名插槽 **`item.slotName`** 的作用域 **`row`** 与父传入的 **`data` 为同一引用**。若 **`data` 为 `null`/`undefined`**，**`row` 亦为 `null`/`undefined`**；点路径取值结果为 **`undefined`**，**不抛错**。**展示空**（仅 **非 `tag`** 列）：**`value` 为 `null`、`undefined` 或 `''`** 时展示 **`item.emptyText ?? defaultEmptyText`**。**`[]`** 首期不视为空。**`tag` 列**不适用空文案，始终 **`C7DictTag`**。**`link` 展示空**：不渲染 **`<a>`**，仅展示上述空文案（避免空 **`href`**）。**`items` 与 `el-descriptions-item`**：仅将 **白名单字段**（**`label`、`span`、`width`…**）绑定到 **`el-descriptions-item`**；**`prop`、`columnType`、`formatter`** 等列逻辑字段不会透传到 EP 节点。

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `data` / `data` | 详情数据对象；与插槽 **`row`** 同一引用。为 **`null`/`undefined`** 时 **`row`** 同值，**`lodash/get`** 得到 **`undefined`**。 | `Object` | null |
| `items` / `items` | 列配置数组 | `Array` | () => [] |
| `default-empty-text` / `defaultEmptyText` | 非 **`tag`** 列在「展示空」时的默认文案 | `String` | '暂无' |

## 事件

| 事件 | 说明 |
| --- | --- |
| _无或未解析_ | |

## 插槽

| 插槽 |
| --- |
| `title` |
| `extra` |
