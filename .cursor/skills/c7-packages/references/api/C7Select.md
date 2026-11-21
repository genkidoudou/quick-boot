<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Select API

> 源码：`packages/C7Select/`
> 标签：`<C7Select>` / `<c7-select>`

C7 业务下拉：在 `ElSelect` 上统一静态/异步/远程选项加载与多选 `separator` 对外格式。**静态来源**：`dataList` 与 `options` 为别名语义；**同时存在时 `dataList` 优先**（见 props 说明）。**字段映射**：`labelKey` / `valueKey`（点路径，默认 `label` / `value`），对齐 `C7Radio`；未命中时 label 降级 `text` → `String(value)`。**远程**：首次展开下拉触发 **不含 `query` 键** 的 `fetchData`；输入关键字经防抖后触发 `{ ...fetchParams, query }`。**多选 + `separator`**：对外 `v-model` / `change` 为逗号分隔字符串，空为 `''`；对内始终数组。外部逗号字符串会解析为数组；若部分 value 不在当前选项中，**不静默删除**（保留策略）。**限制**：若 option 的 `value` 本身可能含英文逗号，请勿使用 `separator` 模式。@emits update:modelValue 对外形态由 `multiple` + `separator` 决定@emits change 载荷与 `update:modelValue` 一致@emits visible-change 透传 `ElSelect` 可见性@emits loading-change `fetchData` 并发计数 >0 为 true

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model` | 默认 model |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `data-list` / `dataList` | 静态选项；与 `options` 二选一语义；**同时存在时优先于 `options`** | `Array` | undefined |
| `options` / `options` | `dataList` 别名；**若 `dataList` 已传入（含 `undefined` 占位由父组件决定）以 `dataList` 为准**——此处简化为：`dataList !== undefined` 时只用 `dataList` | `Array` | undefined |
| `fetch-data` / `fetchData` | 异步加载：`mergedParams` 至少包含 `fetchParams` 浅拷贝；远程搜索时包含 `query`。@param {Record<string, *>} mergedParams@returns {Promise<import('axios').AxiosResponse｜any>} | `Function` | undefined |
| `fetch-params` / `fetchParams` | 合并进 `fetchData` 的自定义参数 | `Object` | () => ({}) |
| `result-key` / `resultKey` | 从 `response.data` 上取列表的点路径，如 `rows` 或 `data.list` | `String` | '' |
| `data-formatter` / `dataFormatter` | 在 `resultKey` 解析后对数组做最终整形@param {*} list@returns {Array} | `Function` | undefined |
| `label-key` / `labelKey` | 从行对象取展示文案的点路径，默认 `label` | `String` | 'label' |
| `value-key` / `valueKey` | 从行对象取选项值的点路径，默认 `value`；常用 `id` | `String` | 'value' |
| `auto-load` / `autoLoad` | `remote` 为 false 时，挂载后自动 `fetchData` 一次（无 `query`）；若已配置静态 `dataList`/`options` 则跳过以避免无意义请求 | `Boolean` | false |
| `remote` / `remote` | 是否远程搜索模式（映射到 `ElSelect` 的 `remote`） | `Boolean` | false |
| `reload-on-clear` / `reloadOnClear` | 远程模式下，清空已选后是否重新拉取「全量」（无 `query`） | `Boolean` | false |
| `separator` / `separator` | 多选时是否对外使用逗号分隔字符串；空选择对外固定为 `''`。非多选时该 prop **无效**。 | `Boolean` | false |
| `multiple` / `multiple` | 多选；与 `ElSelect` 一致，须由本组件声明以便 v-model 适配 | `Boolean` | false |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 载荷与 `update:modelValue` 一致 |
| `@visible-change` | 透传 `ElSelect` 可见性 |
| `@loading-change` | `fetchData` 并发计数 >0 为 true |

## 插槽

| 插槽 |
| --- |
| `prefix` |
| `label` |
| `option` |
| `empty` |
