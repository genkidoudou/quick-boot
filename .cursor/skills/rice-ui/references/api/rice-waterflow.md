<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-waterflow API

> 组件名：`rice-waterflow`
> 官方文档：https://riceui.cn/components/waterflow.html
> 源码：`components/rice-waterflow/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `cross-axis-count` / `crossAxisCount` | 交叉轴元素数量 | `Numnber` | 2 |
| `main-axis-gap` / `mainAxisGap` | 主轴方向间隔 ,即每个item上下的间距 | `Number` | 0 |
| `cross-axis-gap` / `crossAxisGap` | 交叉轴方向间隔,即每列之间的间距 | `Number` | 0 |
| `associative-container` / `associativeContainer` |  | `string` | - |
| `bounces` | 控制是否回弹效果 | `Boolean` | true |
| `upper-threshold` / `upperThreshold` | 距顶部多远时（单位px），触发 scrolltoupper 事件 | `Number` | 50 |
| `lower-threshold` / `lowerThreshold` | 距底部（单位px），触发 scrolltolower 事件 | `Number` | 50 |
| `scroll-top` / `scrollTop` | 设置竖向滚动条位置 | `Number` | 0 |
| `show-scrollbar` / `showScrollbar` | 控制是否出现滚动条 | `Boolean` | true |
| `scroll-into-view` / `scrollIntoView` |  | `string` | - |
| `scroll-with-animation` / `scrollWithAnimation` | 是否在设置滚动条位置时使用滚动动画 | `Boolean` | true |
| `refresher-enabled` / `refresherEnabled` | 是否开启下拉刷新， | `Boolean` | false |
| `refresher-threshold` / `refresherThreshold` |  | `number` | - |
| `refresher-default-style` / `refresherDefaultStyle` |  | `'black' ｜ 'white' ｜ 'none'` | - |
| `refresher-background` / `refresherBackground` |  | `string` | - |
| `refresher-triggered` / `refresherTriggered` | 设置当前下拉刷新状态，true 表示下拉刷新已经被触发，false 表示下拉刷新未被触发 | `Boolean` | false |
| `enable-back-to-top` / `enableBackToTop` | iOS点击顶部状态栏滚动条返回顶部 | `Boolean` | false |
| `custom-nested-scroll` / `customNestedScroll` |  | `boolean` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| _以官方文档 Events 为准_ | | |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifdef` |  |
| `refresher` |  |
| `load` |  |
| `endif` |  |
