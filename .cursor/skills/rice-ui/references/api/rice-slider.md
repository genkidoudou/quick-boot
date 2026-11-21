<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-slider API

> 组件名：`rice-slider`
> 官方文档：https://riceui.cn/components/slider.html
> 源码：`components/rice-slider/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `min` |  | `number` | - |
| `max` |  | `number` | - |
| `step` |  | `number` | - |
| `range` |  | `boolean` | - |
| `show-value` / `showValue` |  | `boolean` | - |
| `value-position` / `valuePosition` |  | `'top' ｜ 'right' ｜ 'none'` | - |
| `value-color` / `valueColor` |  | `string` | - |
| `format-valuetip` / `formatValuetip` |  | `(value : number) => number ｜ string` | - |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `mode` | 滑块风格,可选值：capsule | `String` | default |
| `track-height` / `trackHeight` |  | `string ｜ number` | - |
| `button-size` / `buttonSize` |  | `string ｜ number` | - |
| `button-color` / `buttonColor` |  | `string` | - |
| `button-border-color` / `buttonBorderColor` |  | `string` | - |
| `button-class` / `buttonClass` |  | `string` | - |
| `active-color` / `activeColor` |  | `string` | - |
| `inactive-color` / `inactiveColor` |  | `string` | - |
| `click-change` / `clickChange` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `click-change 1.1.6` | 点击slider时小球是否直接移动到该位置 | `Boolean` | true |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | 拖动结束后触发 | value:number[]\number ，双滑块时为数组格式 |
| `@changing` | 拖动过程中触发 | value:number[]\number ，双滑块时为数组格式 |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifndef` |  |
| `endif` |  |
| `ifdef` |  |
| `fff` |  |
