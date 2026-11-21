<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Checkbox API

> 源码：`packages/C7Checkbox/`
> 标签：`<C7Checkbox>` / `<c7-checkbox>`

C7 多选框：在 `ElCheckboxGroup` 上统一静态/异步选项加载、与 `C7Select` 一致的 **`response.data` → `resultKey` → `dataFormatter`** 解析链、**`joinValue`** 对外编码（对齐 `C7Select` 的 **`separator`**：逗号串空值为 **`''`**）、以及可选「全选/半选」。**静态优先**：若父级传入的 **`dataList` 不为 `undefined`**（含空数组），则选项来自 **`dataList`**，**不会**因 **`autoLoad`** 发起 **`fetchData`**。**`reload()`**：仅当存在 **`fetchData`** 时重新请求；仅有静态 **`dataList`** 时为 **no-op**。**`change`**：载荷 **始终**为 **`string[]`**；**`update:modelValue`** 由 **`joinValue`** 决定 **逗号串**或 **`string[]`**。**限制**：若选项 `value` 本身可能含英文逗号，请勿使用 **`joinValue=true`**。@emits update:modelValue 由 `joinValue` 决定对外 `string` 或 `string[]`@emits change 始终 `string[]`@emits loading-change `fetchData` 并发进行中为 true

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model` | 默认 model |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `data-list` / `dataList` | 静态选项；**`!== undefined` 即视为静态模式**（含 `[]`），优先级高于异步 | `Array` | undefined |
| `fetch-data` / `fetchData` | 异步加载：`mergedParams` 为 **`{ ...fetchParams }`**（**不含 `query`**）。@param {Record<string, *>} mergedParams@returns {Promise<import('axios').AxiosResponse｜any>} | `Function` | undefined |
| `fetch-params` / `fetchParams` |  | `Object` | () => ({}) |
| `result-key` / `resultKey` | 从 `response.data` 上取列表的点路径 | `String` | '' |
| `data-formatter` / `dataFormatter` | 在 `resultKey` 解析后对数组做最终整形@param {*} list@returns {Array} | `Function` | undefined |
| `label-key` / `labelKey` | 行对象上 label 字段路径，默认 `label` | `String` | 'label' |
| `value-key` / `valueKey` | 行对象上 value 字段路径，默认 `value` | `String` | 'value' |
| `auto-load` / `autoLoad` | 无静态 `dataList` 时，挂载后自动 `fetchData` 一次 | `Boolean` | false |
| `join-value` / `joinValue` | 为 true 时对外 **`v-model` / `update:modelValue`** 使用逗号分隔 **string**；空选择固定为 **`''`**（与 **`C7Select.separator`** 一致）。为 false 时对外为 **`string[]`**。 | `Boolean` | false |
| `show-select-all` / `showSelectAll` | 是否展示「全选」行 | `Boolean` | false |
| `indeterminate` / `indeterminate` | **deprecated**：旧文档用名；语义同 **`showSelectAll`**，展示行条件为 **`showSelectAll ｜｜ indeterminate`**。 | `Boolean` | false |
| `min` / `min` | 最小选中数（不在组件内拦截提交，仅保留 props 供页面校验或后续扩展） | `Number` | undefined |
| `max` / `max` | 最大选中数；达到上限时未选项禁用新增选中；且影响「全选」是否可点 | `Number` | undefined |
| `disabled` / `disabled` | 整组禁用 | `Boolean` | false |
| `checkbox-style` / `checkboxStyle` | `default` ｜ `button` ｜ `border` | `String` | 'default' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 始终 `string[]` |
| `@loading-change` | `fetchData` 并发进行中为 true |

## 插槽

| 插槽 |
| --- |
| `default` |
