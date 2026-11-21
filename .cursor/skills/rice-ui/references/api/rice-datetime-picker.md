<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-datetime-picker API

> 组件名：`rice-datetime-picker`
> 官方文档：https://riceui.cn/components/datetime-picker.html
> 源码：`components/rice-datetime-picker/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `type` |  | `DateTimePickerType` | - |
| `min-date` / `minDate` |  | `string` | - |
| `max-date` / `maxDate` |  | `string` | - |
| `value-format` / `valueFormat` |  | `string` | - |
| `format` |  | `string` | - |
| `toolbar-position` / `toolbarPosition` | 标题栏的位置,可选 top none | `string` | bottom |
| `title` |  | `string ｜ number` | - |
| `confirm-button-text` / `confirmButtonText` | 确认按钮的文字，设置为空字符串可以隐藏 | `string` | 确定 |
| `cancel-button-text` / `cancelButtonText` | 取消按钮的文字，设置为空字符串可以隐藏 | `string｜number` | 取消 |
| `close-on-click-overlay` / `closeOnClickOverlay` | 是否在点击遮罩层后关闭 | `boolean` | true |
| `close-on-click-confirm` / `closeOnClickConfirm` | 是否在点击确认按钮后关闭 | `boolean` | true |
| `close-on-click-cancel` / `closeOnClickCancel` | 是否在点击取消按钮后关闭 | `boolean` | true |
| `loading` | 是否为加载状态 | `boolean` | false |
| `filter` |  | `DateTimePickerFilter` | - |
| `formatter` |  | `DateTimePickerFormatter` | - |
| `font-size` / `fontSize` | 选项的文字大小， | `string` | 16px |
| `option-height` / `optionHeight` | 每个选项的高度 | `string｜number` | 50px |
| `visible-option-num` / `visibleOptionNum` | 可见选项的个数 | `number` | 5 |
| `z-index` / `zIndex` | 层级 | `number` | 10099 |
| `use-popup` / `usePopup` | 是否使用 popup | `boolean` | true |
| `empty-text` / `emptyText` |  | `string` | - |
| `safe-area-inset-bottom` / `safeAreaInsetBottom` | 是否开启底部安全区域 | `boolean` | true |
| `immediate-change` / `immediateChange` | 是否在手指松开时立即触发 change 事件。若不开启则会在滚动动画结束后触发 change 事件仅微信小程序支持 | `boolean` | true |
| `model-value` | 绑定值 | `string` | 当前日期 |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@confirm` | 点击确认按钮时触发 | (value:string, extend:DateTimePickerExtend) |
| `@cancel` | 点击取消按钮时触发 | (value:string, extend:DateTimePickerExtend) |
| `@change` | 选项改变时触发 | (value:string, extend:DateTimePickerExtend) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `default` | 默认插槽（若组件支持） |
