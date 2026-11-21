<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-float-fab API

> 组件名：`rice-float-fab`
> 官方文档：https://riceui.cn/components/float-fab.html
> 源码：`components/rice-float-fab/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `axis` | 拖动的方向，可选值：x、y、xy | `String` | xy |
| `adsorption` |  | `'x' ｜ 'y'` | - |
| `gap` |  | `number ｜ string` | - |
| `gap-top` / `gapTop` |  | `number ｜ string` | - |
| `gap-bottom` / `gapBottom` |  | `number ｜ string` | - |
| `gap-left` / `gapLeft` |  | `number ｜ string` | - |
| `gap-right` / `gapRight` |  | `number ｜ string` | - |
| `over-gap` / `overGap` |  | `boolean` | - |
| `default-position` / `defaultPosition` |  | `'center' ｜ 'top-left' ｜ 'top-right' ｜ 'bottom-right' ｜ 'bottom-left'` | - |
| `duration` |  | `number` | - |
| `icon` |  | `string` | - |
| `icon-color` / `iconColor` |  | `string` | - |
| `icon-size` / `iconSize` |  | `string ｜ Number` | - |
| `height` | 悬浮球的高度 | `String｜Number` | 60px |
| `width` | 悬浮球的宽度 | `String｜Number` | 60px |
| `radius` |  | `string ｜ number` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `disabled` |  | `boolean` | - |
| `z-index` / `zIndex` |  | `number` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `offset` | Object(FloatFabOffset) | `悬浮球的位置， {x:number,y:number}` | {-1,-1} |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@offsetChange` | 小球位置改变时触发 | (position:FloatFabOffset) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifndef` |  |
| `icon` |  |
| `endif` |  |
| `ifdef` |  |
| `fff` |  |
