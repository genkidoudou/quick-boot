<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-search API

> 组件名：`rice-search`
> 官方文档：https://riceui.cn/components/search.html
> 源码：`components/rice-search/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `label` |  | `string` | - |
| `shape` | 搜索框形状，可选值：square、round | `String` | square |
| `bg-color` / `bgColor` |  | `string` | - |
| `input-color` / `inputColor` |  | `string` | - |
| `font-size` / `fontSize` |  | `string ｜ number` | - |
| `color` |  | `string` | - |
| `height` |  | `string ｜ number` | - |
| `maxlength` |  | `number` | - |
| `hold-keyboard` / `holdKeyboard` |  | `boolean` | - |
| `placeholder` |  | `string` | - |
| `placeholder-style` / `placeholderStyle` |  | `string` | - |
| `focus` |  | `boolean` | - |
| `clearable` |  | `boolean` | - |
| `clear-icon` / `clearIcon` |  | `string` | - |
| `clear-trigger` / `clearTrigger` | 显示清除图标的时机，可选值：always、focus | `String` | focus |
| `show-action` / `showAction` |  | `boolean` | - |
| `action-text` / `actionText` |  | `string` | - |
| `action-size` / `actionSize` |  | `string ｜ number` | - |
| `action-color` / `actionColor` |  | `string` | - |
| `disabled` |  | `boolean` | - |
| `input-align` / `inputAlign` | 输入框对齐方式，可选值：left、center、right | `String` | left |
| `left-icon` / `leftIcon` |  | `string` | - |
| `right-icon` / `rightIcon` |  | `string` | - |
| `left-icon-size` / `leftIconSize` |  | `string ｜ number` | - |
| `left-icon-color` / `leftIconColor` |  | `string` | - |
| `right-icon-size` / `rightIconSize` |  | `string ｜ number` | - |
| `right-icon-color` / `rightIconColor` |  | `string` | - |
| `wrapper-class` / `wrapperClass` |  | `string` | - |
| `input-class` / `inputClass` |  | `string` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@input` | 输入时触发 | (value:string) |
| `@search` | 点击键盘什么的搜索按钮或回车键时触发 | (value:string) |
| `@focus` | 输入框获得焦点时触发 | (event: UniInputFocusEvent) |
| `@blur` | 输入框失去焦点时触发 | (event: UniInputBlurEvent) |
| `@keyboardheightchange` | 键盘高度改变时触发 | (event: UniInputKeyboardHeightChangeEvent) |
| `@clickInput` | 点击输入框时触发 | (e : UniPointerEvent) |
| `@clickLeftIcon` | 点击左侧图标时触发 | (value: string) |
| `@clickRightIcon` | 点击右侧图标时触发 | (value: string) |
| `@clickAction` | 点击右侧内容触发 | (value: string) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `left` |  |
| `label` |  |
| `leftIcon` |  |
| `rightIcon` |  |
| `clearIcon` |  |
| `action` |  |
| `ifdef` |  |
| `endif` |  |
| `4d4d4d` |  |
| `c8c9cc` |  |
| `ifndef` |  |
| `323233` |  |
| `f5f5f5` |  |
