<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-switch API

> 组件名：`rice-switch`
> 官方文档：https://riceui.cn/components/switch.html
> 源码：`components/rice-switch/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `active-value` / `activeValue` |  | `string ｜ number ｜ boolean` | - |
| `inactive-value` / `inactiveValue` |  | `string ｜ number ｜ boolean` | - |
| `loading` |  | `boolean` | - |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `width` |  | `string ｜ number` | - |
| `icon-size` / `iconSize` |  | `string ｜ number` | - |
| `text-size` / `textSize` |  | `string ｜ number` | - |
| `active-color` / `activeColor` |  | `string` | - |
| `inactive-color` / `inactiveColor` |  | `string` | - |
| `active-text` / `activeText` |  | `string ｜ number` | - |
| `inactive-text` / `inactiveText` |  | `string ｜ number` | - |
| `active-icon` / `activeIcon` |  | `string ｜ number` | - |
| `inactive-icon` / `inactiveIcon` |  | `string ｜ number` | - |
| `prompt-position` / `promptPosition` | 图标或文本显示的位置，可选值：inball、outball | `String` | outball |
| `before-change` / `beforeChange` |  | `BeforeChangeInterceptor` | - |
| `vibrate` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | Switch 状态发生变化时触发 | value:string｜number｜boolean |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `fff` |  |
