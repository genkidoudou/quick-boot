<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7DatePicker API

> 源码：`packages/C7DatePicker/`
> 标签：`<C7DatePicker>` / `<c7-date-picker>`

C7 日期选择器：基于 **Element Plus 2.10+** 的 **`ElDatePicker`**。**默认格式**：当父级 **未** 传入 **`format` / `valueFormat`**（含 **`value-format`**）时，按 **`type`** 注入内置映射（见脚本内 **`FORMAT_DEFAULTS`**）；**未命中映射的 `type`** 不注入，行为与裸 **`ElDatePicker`** 一致。**范围类 `type`**（**`daterange` / `datetimerange` / `monthrange` / `yearrange`**）：- **`rangeMerge`**（默认 **`false`**）：为 **`true`** 时 **`v-model`** 对外为 **单字符串**（两段用 **`mergeDelimiter`** 拼接）；为 **`false`** 时对外为 **EP 原生范围值**（一般为 **二元数组**）。- **`mergeDelimiter`**（默认 **`','`**）：仅用于 **存储合并串** 的拼接/拆分；**与 EP `rangeSeparator`（面板「起—止」展示）语义分离**。若传入 **空字符串**，**回退为 `','`**。- 父级传入 **合并串**（含分隔符）时拆成 **二元数组** 供 EP 回显；**非法串**（段数不为 2、空段等）→ 内部 **`null`** 并 **`console.warn`**。**非范围 `type`**：**`rangeMerge` / `mergeDelimiter`** 无效果。**`change`**：载荷经 **`innerToOuter`** 归一化，与 **`update:modelValue`** 对外形态一致（含清空为 **`null`**）。@prop {unknown} modelValue 对外绑定值；范围且 **`rangeMerge`** 时为 **`string | null`**；范围且非合并时为 **EP 范围形态**；非范围时同 EP。@prop {boolean} [rangeMerge=false] 范围类型下是否将值合并为单字符串对外输出。@prop {string} [mergeDelimiter=','] 存储用分隔符；空串按 **`','`** 处理。@emits update:modelValue 归一化后的值@emits change 归一化后的值（与 **`update:modelValue`** 形态一致）@emits blur@emits focus

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
