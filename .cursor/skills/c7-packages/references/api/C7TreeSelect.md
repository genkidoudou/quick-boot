<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7TreeSelect API

> 源码：`packages/C7TreeSelect/`
> 标签：`<C7TreeSelect>` / `<c7-tree-select>`

C7 树选择：在 `ElTreeSelect` 上统一静态/异步整树加载、字段映射、多选 `separator` 与 `valueType`。**静态**：`dataList` 与 `options` 为别名；**`dataList !== undefined` 时仅用 `dataList`**（与 `C7Select` 一致）。**异步**：`autoLoad` 为 true、且无静态 props 绑定时，挂载后 `fetchData({ ...fetchParams })`（**无 `query`**）。**树字段**：内部将节点规范为 `{ label, value, children }` 供 EP 使用（`mapTree`）。**多选 + `separator`**：对外 `v-model`/`change` 为逗号字符串，空为 `''`；对内为数组（与 `C7Select` 一致）。**`valueType`**：`auto` 时以 **规范树根节点第一条** 的 `value` 的 `typeof` 为准（仅为 `number` 时用 number，否则按 string）；大整数/精度问题与 `Number()` 固有限制相同，业务应避免超大 ID。**`loading`**：展示态为 **内部请求中** 或 **父级经 attrs 传入的 `loading`**（后者与 `:loading="mergedLoading"` 合并为 OR）。@emits update:modelValue@emits change 载荷与对外 `modelValue` 一致@emits load-error 异步失败@emits visible-change 下拉可见性@emits loading-change 请求进行中为 true

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model` | 默认 model |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `data-list` / `dataList` |  | `Array` | undefined |
| `options` / `options` |  | `Array` | undefined |
| `fetch-data` / `fetchData` |  | `Function` | undefined |
| `fetch-params` / `fetchParams` |  | `Object` | () => ({}) |
| `result-key` / `resultKey` |  | `String` | '' |
| `data-formatter` / `dataFormatter` |  | `Function` | undefined |
| `auto-load` / `autoLoad` |  | `Boolean` | false |
| `multiple` / `multiple` |  | `Boolean` | false |
| `separator` / `separator` |  | `Boolean` | false |
| `label-key` / `labelKey` |  | `String` | 'label' |
| `value-key` / `valueKey` |  | `String` | 'value' |
| `children-key` / `childrenKey` |  | `String` | 'children' |
| `value-type` / `valueType` | `auto`：由规范树根首节点的 `value` 类型推断对外单选标量/多选元素类型 | `String` | 'auto' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 载荷与对外 `modelValue` 一致 |
| `@load-error` | 异步失败 |
| `@visible-change` | 下拉可见性 |
| `@loading-change` | 请求进行中为 true |

## 插槽

| 插槽 |
| --- |
| `default` |

## 暴露方法

`loading`、`reload`、`treeSelectRef`
