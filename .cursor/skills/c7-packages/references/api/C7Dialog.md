<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Dialog API

> 源码：`packages/C7Dialog/`
> 标签：`<C7Dialog>` / `<c7-dialog>`

C7 弹窗 / 抽屉：在 **`ElDialog` / `ElDrawer`** 上统一 **footer、异步确定、双 v-model** 与 **`modalProps` 透传**。**显隐**：以 **`modelValue`** 为主；未传 **`modelValue`** 时回退 **`visible`**。关闭时 **同时** **`emit('update:modelValue', false)`** 与 **`emit('update:visible', false)`**。若二者 **同时显式传入** 且 **布尔不一致**，开发环境 **至多每个打开周期** **`console.warn` 一次**（以 **`modelValue`** 为准）。**确定**：传入 **`onConfirm`** 时点击确定会 **await** 其结果；**resolve** 后自动关闭，**reject/throw** 不关闭、**不**内置 **`ElMessage`**。确定钮 **`loading`**：**`confirmLoading !== undefined`** 时 **以外部为准**（可覆盖内部 **`onConfirm` pending**）；否则使用内部 loading。未传 **`onConfirm`** 时点击确定 **依次** **`emit('confirm')`、`emit('submit')`**，**不**自动关闭（由父级处理）。**自定义标题**：具名插槽 **`#title`** 映射至 EP 的 **`#header`**（Dialog / Drawer 一致）。**透传**：**`v-bind` 到根（除 `class`/`style` 外）** 与 **`modalProps`** 合并进壳层；与内部默认 **同名键** 时 **以后合并者为准**（顺序：**内部默认 → `modalProps` → 根上非监听属性**）。**不建议** 在根上覆盖 **`model-value` / `v-model`**，否则可能与双 **`emit`** 行为冲突。@emits update:modelValue@emits update:visible@emits open@emits opened@emits close@emits closed@emits cancel@emits confirm@emits submit

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `model-value` / `modelValue` | 显隐主绑定；未传时回退 `visible` | `Boolean` | undefined |
| `visible` / `visible` | 兼容 `v-model:visible` | `Boolean` | undefined |
| `mode` / `mode` | `dialog`：ElDialog；`drawer`：ElDrawer | `String` | 'dialog' |
| `title` / `title` |  | `String` | '' |
| `footer` / `footer` | 为 `true` 且无 `#footer` 时渲染默认「取消 / 确定」；有 `#footer` 时始终展示插槽内容 | `Boolean` | true |
| `cancel-text` / `cancelText` |  | `String` | '取消' |
| `confirm-text` / `confirmText` |  | `String` | '确定' |
| `confirm-loading` / `confirmLoading` | 若不为 `undefined`，确定钮 **`:loading`** 仅认该值（覆盖内部 **`onConfirm` pending**）。 | `Boolean` | undefined |
| `on-confirm` / `onConfirm` | 异步确定；成功 resolve 后自动关闭 | `Function` | null |
| `width` / `width` | Dialog 宽度（Drawer 请用 `size` 或 `modalProps`） | `[String, Number]` | '520px' |
| `size` / `size` | Drawer 尺寸，对齐 `ElDrawer.size` | `[String, Number]` | undefined |
| `mobile-mode` / `mobileMode` | 窄屏（≤767px）且 `mode=dialog` 时的呈现：- `drawer`：底部抽屉（默认，更适合 H5 表单）- `fullscreen`：全屏 Dialog- `off`：保持桌面 Dialog，仅做宽度/表单样式收敛 | `String` | 'drawer' |
| `modal-props` / `modalProps` | 合并进壳层；键冲突时优先于内部默认、弱于根组件上的非监听透传 | `Object` | () => ({}) |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@update:visible` |  |
| `@open` |  |
| `@opened` |  |
| `@close` |  |
| `@closed` |  |
| `@cancel` |  |
| `@confirm` |  |
| `@submit` |  |

## 插槽

| 插槽 |
| --- |
| `header` |
| `title` |
| `footer` |
| `extra` |
| `ebeef5` |
