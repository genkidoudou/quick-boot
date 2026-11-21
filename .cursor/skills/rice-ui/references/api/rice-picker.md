<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-picker API

> 组件名：`rice-picker`
> 官方文档：https://riceui.cn/components/picker.html
> 源码：`components/rice-picker/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `columns` |  | `PickerOption[][]` | - |
| `toolbar-position` / `toolbarPosition` | 顶部栏位置，可选值：top、bottom、none | `String` | bottom |
| `title` |  | `string` | - |
| `confirm-button-text` / `confirmButtonText` | 确定按钮文字，设置为空字符串可以隐藏 | `String` | 确定 |
| `cancel-button-text` / `cancelButtonText` | 取消按钮文字，设置为空字符串可以隐藏 | `String` | 取消 |
| `font-size` / `fontSize` | 选项的文字大小 | `String｜Number` | 16px |
| `option-height` / `optionHeight` | 每个选项的高度 | `String｜Number` | 50px |
| `visible-option-num` / `visibleOptionNum` | 可见的选项个数 | `Number` | 5 |
| `close-on-click-overlay` / `closeOnClickOverlay` | 是否在点击遮罩层后关闭 | `Boolean` | true |
| `close-on-click-confirm` / `closeOnClickConfirm` | 是否在点击确认按钮后关闭 | `Boolean` | true |
| `close-on-click-cancel` / `closeOnClickCancel` | 是否在点击取消按钮后关闭 | `Boolean` | true |
| `loading` |  | `boolean` | - |
| `safe-area-inset-bottom` / `safeAreaInsetBottom` |  | `boolean` | - |
| `use-popup` / `usePopup` |  | `boolean` | - |
| `immediate-change` / `immediateChange` |  | `boolean` | - |
| `z-index` / `zIndex` | z-index 层级 | `Boolean` | 10099 |
| `empty-text` / `emptyText` |  | `string` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@confirm` | 点击确认按钮时触发 | (event: PickerEvent) |
| `@cancel` | 点击取消按钮时触发 | (event: PickerEvent) |
| `@change` | 选项改变时触发 | (event: PickerEvent) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `top` |  |
| `ifdef` |  |
| `endif` |  |
| `empty` |  |
| `ifndef` |  |
| `bottom` |  |
| `2b2b2b` |  |
| `f2f2f2` |  |
| `323232` |  |
| `f5f5f5` |  |
| `3a3a3c` |  |
| `e5e5e5` |  |
| `1989fa` |  |
