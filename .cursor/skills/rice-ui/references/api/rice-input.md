<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-input API

> 组件名：`rice-input`
> 官方文档：https://riceui.cn/components/input.html
> 源码：`components/rice-input/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `type` |  | `string` | - |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `placeholder` |  | `string` | - |
| `placeholder-style` / `placeholderStyle` |  | `string` | - |
| `maxlength` |  | `number` | - |
| `cursor-spacing` / `cursorSpacing` |  | `number` | - |
| `cursor-color` / `cursorColor` |  | `string` | - |
| `auto-focus` / `autoFocus` |  | `boolean` | - |
| `focus` |  | `boolean` | - |
| `confirm-type` / `confirmType` | 设置键盘右下角按钮的文字，仅在 type为text 时生效，可选值：done、send、search、next、go | `String` | done |
| `confirm-hold` / `confirmHold` |  | `boolean` | - |
| `cursor` |  | `number` | - |
| `selection-start` / `selectionStart` |  | `number` | - |
| `selection-end` / `selectionEnd` |  | `number` | - |
| `adjust-position` / `adjustPosition` |  | `boolean` | - |
| `inputmode` |  | `string` | - |
| `hold-keyboard` / `holdKeyboard` |  | `boolean` | - |
| `color` |  | `string` | - |
| `font-size` / `fontSize` |  | `string ｜ number` | - |
| `border` | 输入框的边框，可选值：surround、bottom、none | `String` | surround |
| `focus-border` / `focusBorder` |  | `boolean` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `shape` | 输入框形状，可选值：square、round | `String` | square |
| `height` |  | `string ｜ number` | - |
| `clearable` |  | `boolean` | - |
| `clear-trigger` / `clearTrigger` |  | `'always' ｜ 'focus'` | - |
| `clear-icon` / `clearIcon` |  | `string` | - |
| `show-password` / `showPassword` |  | `boolean` | - |
| `show-password-trigger` / `showPasswordTrigger` |  | `'always' ｜ 'focus'` | - |
| `prefix-icon` / `prefixIcon` |  | `string` | - |
| `suffix-icon` / `suffixIcon` |  | `string` | - |
| `icon-size` / `iconSize` |  | `string ｜ number` | - |
| `icon-color` / `iconColor` |  | `string` | - |
| `input-align` / `inputAlign` |  | `'left' ｜ 'right' ｜ 'center'` | - |
| `iput-style` / `iputStyle` |  | `UTSJSONObject` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@input` | 输入时触发 | (value:string) |
| `@focus` | 输入框获得焦点时触发 | (event: UniInputFocusEvent) |
| `@blur` | 输入框失去焦点时触发 | (event: UniInputBlurEvent) |
| `@keyboardheightchange` | 键盘高度改变时触发 | (event: UniInputKeyboardHeightChangeEvent) |
| `@change` | 非聚焦状态内容改变时触发（仅组件失去焦点时且用户输入改变内容才触发 | (value: string) |
| `@confirm` | 点击完成按钮时触发 | (value: string) |
| `@nicknamereview` | 输入框失去焦点时触发 | (event: any) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `prefix` |  |
| `prefixIcon` |  |
| `clearIcon` |  |
| `suffix` |  |
| `suffixIcon` |  |
| `ifndef` |  |
| `endif` |  |
| `ifdef` |  |
| `4d4d4d` |  |
| `c8c9cc` |  |
| `323233` |  |
| `f5f5f5` |  |
| `c0c4cc` |  |
| `8d9095` |  |
