<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-dialog API

> 组件名：`rice-dialog`
> 官方文档：https://riceui.cn/components/dialog.html
> 源码：`components/rice-dialog/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `title` |  | `string` | - |
| `width` |  | `string ｜ number` | - |
| `message` |  | `string` | - |
| `message-align` / `messageAlign` | 文本内容对齐方式，可选值：center、left、right | `String` | center |
| `button-theme` / `buttonTheme` | 按钮的风格，可选值：default、round、square | `String` | default |
| `button-layout` / `buttonLayout` | 多按钮排列方式，buttonTheme不为 default 时生效，可选值：row、col | `String` | row |
| `show-confirm-button` / `showConfirmButton` |  | `boolean` | - |
| `confirm-button-text` / `confirmButtonText` |  | `string` | - |
| `confirm-button-color` / `confirmButtonColor` |  | `string` | - |
| `confirm-button-disabled` / `confirmButtonDisabled` |  | `boolean` | - |
| `show-cancel-button` / `showCancelButton` |  | `boolean` | - |
| `cancel-button-text` / `cancelButtonText` |  | `string` | - |
| `cancel-button-color` / `cancelButtonColor` |  | `string` | - |
| `cancel-button-disabled` / `cancelButtonDisabled` |  | `boolean` | - |
| `duration` |  | `number` | - |
| `overlay` |  | `boolean` | - |
| `overlay-bg-color` / `overlayBgColor` |  | `string` | - |
| `close-on-click-overlay` / `closeOnClickOverlay` |  | `boolean` | - |
| `before-close` / `beforeClose` |  | `BeforeChangeInterceptor` | - |
| `z-index` / `zIndex` |  | `number` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `margin-top` / `marginTop` |  | `string ｜ number` | - |
| `use-dialog-page` / `useDialogPage` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `confirm` |  | `() => void` | - |
| `cancel` |  | `() => void` | - |
| `click-overlay` / `clickOverlay` |  | `() => void` | - |
| `open` |  | `() => void` | - |
| `close` |  | `() => void` | - |
| `opened` |  | `() => void` | - |
| `closed` |  | `() => void` | - |
| `ready` |  | `(pageIns : UniPage) => void` | - |
| `fail` |  | `(errMsg : string) => void` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| _以官方文档 Events 为准_ | | |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `title` |  |
| `footer` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `f2f3f5` |  |
| `3a3a3c` |  |
