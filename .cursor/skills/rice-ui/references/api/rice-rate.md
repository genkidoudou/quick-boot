<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-rate API

> 组件名：`rice-rate`
> 官方文档：https://riceui.cn/components/rate.html
> 源码：`components/rice-rate/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `count` | 图标总数 | `Number` | 5 |
| `icon` | 选中时的图标名称或图片链接，等同于 Icon 组件的 name 属性 | `String` | star-fill |
| `void-icon` / `voidIcon` | 未选中时的图标名称或图片链接，等同于 Icon 组件的 name 属性 | `String` | star |
| `size` |  | `string ｜ number` | - |
| `gutter` |  | `string ｜ number` | - |
| `color` |  | `string` | - |
| `void-color` / `voidColor` |  | `string` | - |
| `allow-half` / `allowHalf` | 是否允许半选 | `Boolean` | false |
| `clearable` | 值为true是，再次点击相同的值时，可以将值重置为 0 | `Boolean` | false |
| `readonly` |  | `boolean` | - |
| `disabled` |  | `boolean` | - |
| `touchable` | 是否可以通过滑动手势选择评分 | `Boolean` | true |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | 当前分值变化时触发的事件 | (value:number) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `icon` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
