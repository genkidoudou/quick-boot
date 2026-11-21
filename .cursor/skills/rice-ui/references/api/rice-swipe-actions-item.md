<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-swipe-actions-item API

> 组件名：`rice-swipe-actions-item`
> 官方文档：https://riceui.cn/components/swipe-actions.html
> 源码：`components/rice-swipe-actions-item/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `name` |  | `string ｜ number` | - |
| `left-menu` / `leftMenu` |  | `SwipeActionsMenu[]` | - |
| `right-menu` / `rightMenu` |  | `SwipeActionsMenu[]` | - |
| `disabled` | 是否禁用 | `Boolean` | false |
| `duration` |  | `number` | - |
| `auto-close` / `autoClose` | 点击按钮是否自动关闭单元格 | `Boolean` | true |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `accordion` | 是否手风琴模式 | `Boolean` | false |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@disabled` | Boolean | 是否禁止滑动 |
| `@duration` | String | 动画过渡时间，单位ms |
| `@auto-close` | Boolean | 是否自动关闭 |
| `@open` | 打开时触发 | (event:SwipeActionsItemOpen) |
| `@close` | 关闭指定的单元格（通过name值进行匹配） | (event:SwipeActionsItemClose) |
| `@click` | 点击单元格时触发 | (event:SwipeActionsItemClick) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `left` |  |
| `right` |  |
| `comment` |  |
