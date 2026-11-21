<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7DictTag API

> 源码：`packages/C7DictTag/`
> 标签：`<C7DictTag>` / `<c7-dict-tag>`

C7 字典标签：只读将 **`modelValue`**（单值 / 多值 / 逗号串）按 **`options`** 匹配为 **`ElTag`** 列表。**解析**：**`null`/`undefined`** 与解析后无非空原子 → 展示 **`-`**；**`number`** 为单原子；**`array`** 按序不去重；**`string`** 若 **包含 `separator`** 则按分隔符拆分（各段 **trim**、去空），否则整段为单原子。**匹配**：对每个原子 **`val`**，取 **`options`** 中 **首个** 满足 **`String(opt.value) === String(val)`** 的项的 **`label`**。**`mk` 与 `max`**：仅对 **已匹配** 原子自 **1** 递增编号 **`mk`**。当 **`max > 0`** 时：**`mk <= max`** 渲染字典色 tag；**`mk === max + 1`** 在该位渲染 **`+N`**（**`N = 已匹配总数 − max`**）；**`mk > max + 1`** 不占位。**`max`** 为 **`0`/负数/`undefined`** 时不折叠。**未匹配**：**`showValue=true`** → **`type=info`** 展示 **`String(val)`**；**`showValue=false`** → 该位 **`-`**。**`dictType`**：当字典项未配置 **`listClass`/`elTagType`** 时的 **`ElTag.type`** 兜底；已配置时以字典项为准。**`+N`**：**`collapse=true`** 时 **`ElTooltip`** 展示溢出 **`label`** 列表；**`collapse=false`** 时 **`ElPopover` + `click`** 展示同序列表。**注意**：本组件 **不** **`emit('update:modelValue')`**，仅展示。

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `model-value` / `modelValue` | 当前值：单值、数组或含分隔符的字符串；只读展示 | `[String, Number, Array]` | undefined |
| `options` / `options` | 字典行：`{ label, value }`；匹配首项 | `Array` | () => [] |
| `separator` / `separator` | 逗号串拆分时使用的分隔符，默认 **`,`**；仅当 **string** 形态 **包含** 该子串时拆分 | `String` | ' |
| `show-value` / `showValue` | 未匹配时是否以 **info** tag 展示原始原子值；为 **false** 时该位为 **`-`** | `Boolean` | false |
| `max` / `max` | 大于 **0** 时启用「仅前 **max** 个已匹配 tag + **`+N`**」 | `Number` | undefined |
| `collapse` / `collapse` | 为 **true** 时 **`+N`** 使用 **`ElTooltip`**（hover 展示溢出 **label**）；为 **false** 时 **`+N`** 使用 **`ElPopover`（click）** 展示溢出列表。 | `Boolean` | false |
| `dict-type` / `dictType` | 业务字典类型名，用于映射 **`ElTag.type`**；未知时 **`primary`** | `String` | '' |
| `size` / `size` | 透传 **`ElTag.size`** | `String` | undefined |
| `effect` / `effect` | 透传 **`ElTag.effect`** | `String` | undefined |
| `round` / `round` | 透传 **`ElTag.round`** | `Boolean` | undefined |

## 事件

| 事件 | 说明 |
| --- | --- |
| _无或未解析_ | |

## 插槽

| 插槽 |
| --- |
| `content` |
| `reference` |
