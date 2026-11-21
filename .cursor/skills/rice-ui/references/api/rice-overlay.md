<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-overlay API

> 组件名：`rice-overlay`
> 官方文档：https://riceui.cn/components/overlay.html
> 源码：`components/rice-overlay/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `z-index` / `zIndex` | 层级 | `Number` | 999 |
| `close-on-click-overlay` / `closeOnClickOverlay` | 是否在点击遮罩层后关闭 | `Boolean` | true |
| `duration` | 动画时长，单位ms | `Number` | 300ms |
| `bg-color` / `bgColor` |  | `string` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@click` | 点击遮罩层时触发 | (event:UniPointerEvent) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifndef` |  |
| `endif` |  |
| `ifdef` |  |
