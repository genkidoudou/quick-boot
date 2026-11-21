<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7TimePicker API

> 源码：`packages/C7TimePicker/`
> 标签：`<C7TimePicker>` / `<c7-time-picker>`

C7 时间选择器：基于 **Element Plus 2.10+** 的 **`ElTimePicker`**。**默认格式**：当 **`format`、** **`valueFormat`、** **`value-format` 均为 `undefined`** 时，同时注入 **`format` 与 `valueFormat` 为 `HH:mm:ss`**；**任一已传**则不注入对应项，与裸 **`ElTimePicker`** 一致。**范围模式**（**`is-range` / `isRange` 为真**）：- **`rangeMerge`**（默认 **`false`**）：为 **`true`** 时 **`v-model`** 对外为 **单字符串**（两段用 **`mergeDelimiter`** 拼接）；为 **`false`** 时对外为 **EP 原生范围值**（一般为 **二元数组**）。**与 `C7DatePicker` 的差异**：**`rangeMerge=false`** 时 **不**将外向 **`string`** 按 **`mergeDelimiter`** 解析为回显数组（误传合并串将得到 **`null` + warn**，见 **`outerToInner`**）。- **`mergeDelimiter`**（默认 **`','`**）：仅用于 **存储合并串**；**与 EP `range-separator`（面板展示）语义分离**。**`null` / `undefined` / 空字符串** 时有效分隔符回退为 **`','`**。- **`rangeMerge=true`** 下父级传入合并串时拆成 **二元数组** 供 EP 回显；非法串 → 内向 **`null`** 并 **`console.warn`**（去重防刷屏）。**非范围**：**`rangeMerge` / `mergeDelimiter`** 无效果，**`outerToInner` / `innerToOuter`** 直透传。**`change`**：载荷经 **`innerToOuter`**，与 **`update:modelValue`** 外向形态一致（含清空为 **`null`**）。@prop {unknown} modelValue 对外绑定；范围且 **`rangeMerge`** 时为 **`string | null`**；范围且非合并时为 **EP 范围形态**；非范围时同 EP。@prop {boolean} [rangeMerge=false] 范围模式下是否将值合并为单字符串对外输出。@prop {string} [mergeDelimiter=','] 存储用分隔符；空串按 **`','`** 处理。@emits update:modelValue 归一化后的值@emits change 归一化后的值（与 **`update:modelValue`** 形态一致）@emits blur@emits focus

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `model-value` / `modelValue` |  | `[String, Number, Date, Array, Object]` | undefined |
| `range-merge` / `rangeMerge` |  | `Boolean` | false |
| `merge-delimiter` / `mergeDelimiter` |  | `String` | ' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 归一化后的值（与 |
| `@blur` |  |
| `@focus` |  |

## 插槽

| 插槽 |
| --- |
| `default` |
