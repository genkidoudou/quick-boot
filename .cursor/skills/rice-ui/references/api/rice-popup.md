<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-popup API

> 组件名：`rice-popup`
> 官方文档：https://riceui.cn/components/popup.html
> 源码：`components/rice-popup/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `duration` | 动画时长，单位ms | `Number` | 300 |
| `position` | 位置，可选值：top、bottom、left、right、center | `String` | center |
| `z-index` / `zIndex` | z-index值 | `Number` | 999 |
| `opacity` |  | `boolean` | - |
| `zoom` | 是否使用缩放，对center 有效 | `Boolean` | true |
| `overlay` | 是否显示遮罩层 | `Boolean` | true |
| `overlay-bg-color` / `overlayBgColor` |  | `string` | - |
| `closeable` | 是否显示关闭图标 | `Boolean` | true |
| `close-icon` / `closeIcon` |  | `string` | - |
| `close-icon-position` / `closeIconPosition` |  | `"top-left" ｜ "top-right" ｜ "bottom-left" ｜ "bottom-right"` | - |
| `close-on-click-overlay` / `closeOnClickOverlay` |  | `boolean` | - |
| `radius` |  | `number ｜ string` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `safe-area-inset-top` / `safeAreaInsetTop` |  | `boolean` | - |
| `safe-area-inset-bottom` / `safeAreaInsetBottom` |  | `boolean` | - |
| `close-on-slide-down` / `closeOnSlideDown` |  | `boolean` | - |
| `slide-down-threshold` / `slideDownThreshold` |  | `number` | - |
| `show-drag-bar` / `showDragBar` |  | `boolean` | - |
| `drag-wrap-class` / `dragWrapClass` |  | `string` | - |
| `drag-bar-class` / `dragBarClass` |  | `string` | - |
| `lock-scroll` / `lockScroll` |  | `boolean` | - |
| `scroll-id` / `scrollId` |  | `string` | - |
| `before-close` / `beforeClose` |  | `BeforeChangeInterceptor` | - |
| `margin-top` / `marginTop` |  | `string ｜ number` | - |
| `popup-class` / `popupClass` |  | `string` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `show` | 是否显示 | `Boolean` | false |
| `close-on-slide-down 1.1.6` | 下滑一段距离后关闭position为bottom时有效 | `Boolean` | false |
| `slide-down-threshold 1.1.6` | 下滑一段距离关闭的阈值单位px | `Number` | 40 |
| `show-drag-bar 1.1.6` | 是否显示顶部bar | `Boolean` | false |
| `lock-scroll 1.1.6` | web端是否开启滚动穿透 | `Boolean` | true |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| _以官方文档 Events 为准_ | | |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `closeIcon` |  |
| `drag` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `c8c9cc` |  |
