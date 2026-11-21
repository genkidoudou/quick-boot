<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Switch API

> 源码：`packages/C7Switch/`
> 标签：`<C7Switch>` / `<c7-switch>`

C7 业务开关：基于 **Element Plus 2.10+** 的 **`ElSwitch`**，用其 **`before-change`**（实现内对应 **`beforeChange` prop** 勿混淆）串联**`beforeChange` → 确认（`confirmFn` 优先）→ 可选 `asyncChange` → 提交值`**；对外 **`v-model`** 与 **`activeValue` / `inactiveValue`** 值体系一致。**字典文案**：**`dictList`** 与 **`activeValue` / `inactiveValue`** 使用 **`===`** 匹配 **`item.value`**；命中则用 **`label`**，否则回退 **`activeText` / `inactiveText`**。**确认弹窗**：仅当 **未**配置 **`confirmFn`** 且 **`confirmMessage`** 为 **非空字符串（`trim()` 后）** 时，使用 **`ElMessageBox.confirm(confirmMessage, ...)`**。**颜色**：若传入 **`activeColor` / `inactiveColor`**，通过根节点上的 **`--el-switch-on-color` / `--el-switch-off-color`** 注入（与 EP 主题变量一致，可被外层覆盖）。**并发**：自 **`beforeChange` 流水线开始**至 **`update:modelValue` 提交完成**期间 **`busy`** 为真，新的切换请求 **直接拒绝**（返回 **`false`**，不 **`emit('cancel')`**）。@emits update:modelValue 成功提交新值时（先于 **change**）@emits change 载荷 **`(newVal, oldVal)`**，仅在成功提交后@emits cancel 用户中止确认（含 **`confirmFn` 假值 / reject** 与 **`confirmMessage` 弹窗取消**）；**`beforeChange` 返回 `false`** 时 **不**触发

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `model-value` / `modelValue` |  | `[Boolean, String, Number]` | （必填） |
| `active-value` / `activeValue` |  | `[Boolean, String, Number]` | true |
| `inactive-value` / `inactiveValue` |  | `[Boolean, String, Number]` | false |
| `active-text` / `activeText` |  | `String` | '' |
| `inactive-text` / `inactiveText` |  | `String` | '' |
| `dict-list` / `dictList` |  | `Array` | null |
| `confirm-fn` / `confirmFn` |  | `Function` | null |
| `confirm-message` / `confirmMessage` | 非空（trim 后）且未配置 `confirmFn` 时弹出 `ElMessageBox.confirm` | `String` | '' |
| `async-change` / `asyncChange` | 异步提交；**reject/抛错** 时不更新 `modelValue`、**不** `emit('cancel')`。@param {*} newVal@returns {void｜Promise<void>} | `Function` | null |
| `after-change` / `afterChange` | 值已成功提交后调用（含无 `asyncChange` 的同步路径）。@param {*} newVal | `Function` | null |
| `before-change` / `beforeChange` | 切换前钩子；返回严格 **`false`** 时 **完全静默**中止（不 `emit('cancel')`）。@param {*} newVal 目标值@returns {boolean｜Promise<boolean｜void>｜void} | `Function` | null |
| `disabled` / `disabled` |  | `Boolean` | false |
| `active-color` / `activeColor` | 映射到 `--el-switch-on-color` | `String` | '' |
| `inactive-color` / `inactiveColor` | 映射到 `--el-switch-off-color` | `String` | '' |
| `loading` / `loading` | 与内部 `asyncChange` loading 合并传入 `ElSwitch` | `Boolean` | false |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` | 载荷 |
| `@cancel` | 用户中止确认（含 |

## 插槽

| 插槽 |
| --- |
| `default` |
