<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Copy API

> 源码：`packages/C7Copy/`
> 标签：`<C7Copy>` / `<c7-copy>`

C7 复制：统一纯文本写入剪贴板（Clipboard API 优先，`execCommand('copy')` 降级）、提示与事件。**安全上下文**：仅在 `window.isSecureContext` 且存在 `navigator.clipboard.writeText` 时走 Clipboard；否则直接降级。`writeText` reject 时继续尝试降级。**重入**：一次复制流程（含 `getCopyText` 的 Promise）未完成前再次触发会被忽略。**disabled**：不调用 `beforeCopy`、不写剪贴板、不 emit、不提示（与 design 一致）。@emits copy(resolvedText) 已解析待写入串、尚未写入剪贴板@emits success(resolvedText) 写入成功@emits error(err: Error) 写入失败

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `text` / `text` | 待复制内容；`null`/`undefined` 视为 `''`；`Number` 会转为字符串 | `[String, Number, null]` | undefined |
| `get-copy-text` / `getCopyText` | 基于规范化后的 `text` 字符串生成最终写入内容；可返回 Promise。返回值非字符串时会 `String(...)`；`null`/`undefined` 结果视为 `''`。 | `Function` | undefined |
| `mode` / `mode` | 展示形态：`button` ｜ `icon` ｜ `text` ｜ `clickable`；`none` 为 `clickable` 的兼容别名。 | `String` | 'button' |
| `disabled` / `disabled` | 为 true 时不触发复制与任何提示 | `Boolean` | false |
| `show-message` / `showMessage` | 未传入 `notify` 时是否使用默认 `ElMessage` | `Boolean` | true |
| `notify` / `notify` | 自定义通知；传入后成功/失败 **仅**走此回调，不再调用 `ElMessage`。@param {'success'｜'error'｜'info'｜'warning'} type@param {string} message | `Function` | undefined |
| `before-copy` / `beforeCopy` | 返回严格 `false` 时阻止复制（不调用 `getCopyText`、不写剪贴板、不 emit） | `Function` | undefined |
| `after-copy` / `afterCopy` | 剪贴板写入成功后、`emit('success')` 之前调用 | `Function` | undefined |
| `button-text` / `buttonText` | `mode=button` 时默认插槽为空则显示的按钮文案 | `String` | '复制' |
| `button-type` / `buttonType` | `mode=button` 时传给 `ElButton` 的 `type` | `String` | 'default' |
| `icon-button-type` / `iconButtonType` | `mode=icon` 时传给 `ElButton` 的 `type`（仅影响颜色语义，按钮为 link） | `String` | 'primary' |
| `icon-label` / `iconLabel` | `mode=icon` 时图标右侧可选文案 | `String` | '' |
| `success-message` / `successMessage` | 默认复制成功提示（`notify` 未传入且 `showMessage` 为 true 时使用） | `String` | '复制成功' |
| `error-message-prefix` / `errorMessagePrefix` | 默认复制失败提示文案前缀（会拼接 `err.message`） | `String` | '复制失败：' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@copy` |  |
| `@success` |  |
| `@error` |  |

## 插槽

| 插槽 |
| --- |
| `default` |
