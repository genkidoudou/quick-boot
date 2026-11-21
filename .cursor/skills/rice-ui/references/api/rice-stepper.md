<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-stepper API

> 组件名：`rice-stepper`
> 官方文档：https://riceui.cn/components/stepper.html
> 源码：`components/rice-stepper/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `mode` |  | `'square' ｜ 'round' ｜ 'outline'` | - |
| `step` |  | `number` | - |
| `step-strictly` / `stepStrictly` |  | `boolean` | - |
| `min` |  | `number` | - |
| `max` |  | `number` | - |
| `auto-fixed` / `autoFixed` |  | `boolean` | - |
| `integer` |  | `boolean` | - |
| `decimal-length` / `decimalLength` |  | `number` | - |
| `input-width` / `inputWidth` |  | `number ｜ string` | - |
| `button-size` / `buttonSize` |  | `number ｜ string` | - |
| `font-size` / `fontSize` |  | `number ｜ string` | - |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `disable-plus` / `disablePlus` |  | `boolean` | - |
| `disable-minus` / `disableMinus` |  | `boolean` | - |
| `disable-input` / `disableInput` |  | `boolean` | - |
| `show-plus` / `showPlus` |  | `boolean` | - |
| `show-minus` / `showMinus` |  | `boolean` | - |
| `show-input` / `showInput` |  | `boolean` | - |
| `before-change` / `beforeChange` |  | `BeforeChangeInterceptor` | - |
| `plus-btn-style` / `plusBtnStyle` |  | `UTSJSONObject` | - |
| `minus-btn-style` / `minusBtnStyle` |  | `UTSJSONObject` | - |
| `input-style` / `inputStyle` |  | `UTSJSONObject` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `change` | 绑定值被改变时触发，input框输入过程中不会触发 | `{value:number,oldValue?:number}` | - |
| `input` | 组件 Input 输入时触发 | `value:string ，当前输入的值` | - |
| `focus` | 组件 Input 获得焦点时触发 | `event:UniInputFocusEvent` | - |
| `blur` | 组件 Input 失去焦点时触发 | `event:UniInputBlurEvent` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | 绑定值被改变时触发，input框输入过程中不会触发 | {value:number,oldValue?:number} |
| `@input` | 组件 Input 输入时触发 | value:string ，当前输入的值 |
| `@focus` | 组件 Input 获得焦点时触发 | event:UniInputFocusEvent |
| `@blur` | 组件 Input 失去焦点时触发 | event:UniInputBlurEvent |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `minus` |  |
| `plus` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `323233` |  |
| `f5f5f5` |  |
