<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-code-input API

> 组件名：`rice-code-input`
> 官方文档：https://riceui.cn/components/code-input.html
> 源码：`components/rice-code-input/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `maxlength` | 最大可输入字符的个数 | `Number` | 4 |
| `dot` |  | `boolean` | - |
| `mode` | 模式选择，可选值：box、line | `String` | box |
| `bg-color` / `bgColor` |  | `string` | - |
| `border-color` / `borderColor` |  | `string` | - |
| `show-cursor` / `showCursor` | 是否显示输入框中间的光标 | `Boolean` | true |
| `cursor-color` / `cursorColor` |  | `string` | - |
| `space` | 输入框之间的间距 | `String｜Number` | 10px |
| `justify-content` / `justifyContent` |  | `string` | - |
| `size` | 输入框大小，宽高一致 | `String｜Number` | 42px |
| `font-size` / `fontSize` |  | `string ｜ number` | - |
| `color` |  | `string` | - |
| `bold` | 文字是否加粗 | `Boolean` | false |
| `focus` | 是否获得焦点，如果页面一打开就要获得焦点该值可设置为 true | `Boolean` | false |
| `disabled-keyboard` / `disabledKeyboard` |  | `boolean` | - |
| `adjust-position` / `adjustPosition` | 键盘弹起时，是否自动上推页面 | `Boolean` | true |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@finish` | 输入字符个数达到 maxlength 值时触发 | value:string |
| `@change` | 输入内容改变时触发 | value:string |
| `@focus` | 获得焦点时触发 | event:UniInputFocusEvent |
| `@blur` | 失去焦点时触发 | event:UniInputBlurEvent |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifndef` |  |
| `endif` |  |
| `ifdef` |  |
