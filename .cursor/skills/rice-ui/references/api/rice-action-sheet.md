<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-action-sheet API

> 组件名：`rice-action-sheet`
> 官方文档：https://riceui.cn/components/action-sheet.html
> 源码：`components/rice-action-sheet/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `actions` |  | `ActionSheetAction[]` | - |
| `title` |  | `string` | - |
| `show-cancel` / `showCancel` |  | `boolean` | - |
| `cancel-text` / `cancelText` |  | `string` | - |
| `duration` |  | `number` | - |
| `z-index` / `zIndex` |  | `number` | - |
| `opacity` |  | `boolean` | - |
| `overlay` |  | `boolean` | - |
| `overlay-bg-color` / `overlayBgColor` |  | `string` | - |
| `close-on-click-action` / `closeOnClickAction` |  | `boolean` | - |
| `close-on-click-overlay` / `closeOnClickOverlay` |  | `boolean` | - |
| `radius` |  | `number ｜ string` | - |
| `safe-area-inset-bottom` / `safeAreaInsetBottom` |  | `boolean` | - |
| `use-dialog-page` / `useDialogPage` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `target` |  | `UniElement` | - |
| `select` | 选择了选项时触发 | `(action:ActionSheetAction,index:number)` | - |
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
| `@select` | 选择了选项时触发 | (action:ActionSheetAction,index:number) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
