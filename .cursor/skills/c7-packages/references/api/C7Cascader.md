<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Cascader API

> 源码：`packages/C7Cascader/`
> 标签：`<C7Cascader>` / `<c7-cascader>`

C7 级联选择：在 `ElCascader` 上统一静态/整树异步/懒加载、`resultKey`/`dataFormatter`、字段映射、多选 `separator` 与 `valueType`。**静态**：`dataList` 与 `options` 为别名；**`dataList !== undefined` 时仅用 `dataList`**（与 `C7TreeSelect` 一致）。**整树异步**：无静态、**非懒加载**、**`autoLoad=true`** 时挂载后 `fetchData({ ...fetchParams })`（无 `parentId`）。**懒加载**：**`lazy=true`** 且提供 **`fetchData`** 时，通过 EP **`props.lazy` + `props.lazyLoad`** 拉取子层；根层 **`parentId === rootParentId`**，子层 **`parentId`** 为展开父节点映射后的 **`value`**；接口返回 **当前层扁平列表**。**`resultKey`**：从 **`response.data`** 取列表路径（与 `C7Select`/`C7TreeSelect` 一致），**不**表示 `children` 字段名（子节点字段为 **`childrenKey`**）。**多选 + `separator`**：仅当 **`emit-path` 为 false** 且选中为 **一维标量** 时对外逗号串；**`emitPath` 默认 true** 时 **`separator` 无效**，开发环境 **`console.warn`**。**`valueType`**：与 `C7TreeSelect` 对齐；**`emitPath=true`** 时对路径 **各层标量** 做类型转换。@emits update:modelValue@emits change 载荷与对外 `modelValue` 一致@emits load-error 异步失败，参数为 `err`@emits visible-change@emits loading-change 内部 `fetchData` 进行中为 true

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
| `lazy` / `lazy` | 为 true 且无静态数据、且提供 `fetchData` 时，使用 EP `props.lazy` + `props.lazyLoad` 按层请求 | `Boolean` | false |
| `root-parent-id` / `rootParentId` | 懒加载根层 `parentId`；子层使用父节点 `value` | `unknown` | null |
| `multiple` / `multiple` |  | `Boolean` | false |
| `separator` / `separator` |  | `Boolean` | false |
| `label-key` / `labelKey` |  | `String` | 'label' |
| `value-key` / `valueKey` |  | `String` | 'value' |
| `children-key` / `childrenKey` |  | `String` | 'children' |
| `value-type` / `valueType` |  | `String` | 'auto' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 载荷与对外 `modelValue` 一致 |
| `@load-error` | 异步失败，参数为 `err` |
| `@visible-change` |  |
| `@loading-change` | 内部 `fetchData` 进行中为 true |

## 插槽

| 插槽 |
| --- |
| `default` |

## 暴露方法

`loading`、`reload`、`cascaderRef`
