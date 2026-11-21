<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-textarea API

> 组件名：`rice-textarea`
> 官方文档：https://riceui.cn/components/textarea.html
> 源码：`components/rice-textarea/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `placeholder` |  | `string` | - |
| `placeholder-style` / `placeholderStyle` |  | `string` | - |
| `maxlength` |  | `number` | - |
| `cursor-spacing` / `cursorSpacing` |  | `number` | - |
| `cursor-color` / `cursorColor` |  | `string` | - |
| `auto-focus` / `autoFocus` |  | `boolean` | - |
| `focus` |  | `boolean` | - |
| `confirm-type` / `confirmType` | 设置键盘右下角按钮的文字，可选值：return、done、send、search、next、go | `String` | return |
| `confirm-hold` / `confirmHold` |  | `boolean` | - |
| `auto-height` / `autoHeight` |  | `boolean` | - |
| `cursor` |  | `number` | - |
| `selection-start` / `selectionStart` |  | `number` | - |
| `selection-end` / `selectionEnd` |  | `number` | - |
| `adjust-position` / `adjustPosition` |  | `boolean` | - |
| `inputmode` |  | `string` | - |
| `hold-keyboard` / `holdKeyboard` |  | `boolean` | - |
| `fixed` |  | `boolean` | - |
| `show-confirm-bar` / `showConfirmBar` |  | `boolean` | - |
| `disable-default-padding` / `disableDefaultPadding` |  | `boolean` | - |
| `height` |  | `string ｜ number` | - |
| `min-height` / `minHeight` |  | `string ｜ number` | - |
| `max-height` / `maxHeight` |  | `string ｜ number` | - |
| `color` |  | `string` | - |
| `font-size` / `fontSize` |  | `string ｜ number` | - |
| `show-word-limit` / `showWordLimit` |  | `boolean` | - |
| `border` | 输入框的边框，可选值：surround、bottom、none | `String` | surround |
| `bg-color` / `bgColor` |  | `string` | - |
| `textarea-align` / `textareaAlign` |  | `'left' ｜ 'right' ｜ 'center'` | - |
| `textarea-style` / `textareaStyle` |  | `UTSJSONObject` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@input` | 输入时触发 | (value:string) |
| `@focus` | 输入框获得焦点时触发 | (event: UniTextareaFocusEvent) |
| `@blur` | 输入框失去焦点时触发 | (event: UniTextareaBlurEvent) |
| `@keyboardheightchange` | 键盘高度改变时触发 | (event: UniInputKeyboardHeightChangeEvent) |
| `@change` | 非聚焦状态内容改变时触发（仅组件失去焦点时且用户输入改变内容才触发 | (value: string) |
| `@linechange` | 输入框行数变化时调用 | (event: UniTextareaLineChangeEvent) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifdef` |  |
| `endif` |  |
| `4d4d4d` |  |
| `c8c9cc` |  |
| `ifndef` |  |
| `323233` |  |
| `f5f5f5` |  |
| `c0c4cc` |  |
| `8d9095` |  |
