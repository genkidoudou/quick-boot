<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Radio API

> 源码：`packages/C7Radio/`
> 标签：`<C7Radio>` / `<c7-radio>`

C7 业务单选组：在 `ElRadioGroup` 上统一 **静态 `dataList` / `options`** 与 **`fetchData` + `fetchParams`**异步字典、**`response.data` → `resultKey` → `dataFormatter`** 解析链，以及 **`labelKey` / `valueKey`** 行映射。**与 `C7Select` 对齐**：`dataList` 与 `options` 为别名，**同时存在时 `dataList` 优先**；`fetchData(mergedParams)` **不注入 `query`**。**与 `C7Select` 不同**：**`autoLoad` 默认 `true`**（单选无「展开再拉」，首屏尽早出选项）。**根级透传**：除 `RESERVED_ATTR_KEYS` 外，`$attrs` 绑定至 **`ElRadioGroup`**（如 `size`、`disabled`、`fill`、`text-color`），便于与 **`el-form-item`** 常规用法配合。**值比较**：选项就绪后校验「当前 `modelValue` 是否在选项中」使用 **`Object.is`**；若业务使用 **深度相等对象** 作 value，请避免或自行保证引用一致。**空态插槽 `#empty`**：渲染在 **`.c7-radio__empty`** 内、**`ElRadioGroup` 之前**（`emptyDisplay='slot'` 时）。@emits update:modelValue@emits change 与 `v-model` 值形态一致@emits loading-change `fetchData` 并发计数 >0 为 true

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model` | 默认 model |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `data-list` / `dataList` | 静态选项；与 `options` 别名；**同时存在时优先** | `Array` | undefined |
| `options` / `options` | `dataList` 别名；**`dataList !== undefined` 时仅用 `dataList`** | `Array` | undefined |
| `fetch-data` / `fetchData` | 异步加载选项；`mergedParams` 为 **`{ ...fetchParams }` 浅拷贝**，**不含 `query`**。@param {Record<string, *>} mergedParams@returns {Promise<import('axios').AxiosResponse｜any>} | `Function` | undefined |
| `fetch-params` / `fetchParams` |  | `Object` | () => ({}) |
| `result-key` / `resultKey` |  | `String` | '' |
| `data-formatter` / `dataFormatter` | 在 `resultKey` 解析后对数组做最终整形@param {*} list@returns {Array} | `Function` | undefined |
| `label-key` / `labelKey` | 从行对象取展示文案的点路径，默认 **`label`** | `String` | 'label' |
| `value-key` / `valueKey` | 从行对象取选项值的点路径，默认 **`value`** | `String` | 'value' |
| `auto-load` / `autoLoad` | 无静态绑定且存在 `fetchData` 时，是否在挂载后自动拉取一次 | `Boolean` | true |
| `radio-style` / `radioStyle` | `default`：普通 `ElRadio`；`button`：`ElRadioButton`；`border`：`ElRadio` + border | `String` | 'default' |
| `fetch-error-behavior` / `fetchErrorBehavior` | `fetch` reject 时的选项与模型策略 | `String` | 'keep-last' |
| `invalid-model-behavior` / `invalidModelBehavior` | 当前 `modelValue` 不在选项中时的策略 | `String` | 'keep' |
| `empty-display` / `emptyDisplay` | 无选项时的占位：`none` ｜ `text` ｜ `slot` | `String` | 'none' |
| `empty-text` / `emptyText` | `emptyDisplay='text'` 时展示；空串则不渲染文本 | `String` | '' |
| `suppress-invalid-model-dev-warn` / `suppressInvalidModelDevWarn` | 为 true 时不输出「当前值不在选项中」的开发期 `console.warn` | `Boolean` | false |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 与 `v-model` 值形态一致 |
| `@loading-change` | `fetchData` 并发计数 >0 为 true |

## 插槽

| 插槽 |
| --- |
| `empty` |
