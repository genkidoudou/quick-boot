<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-subsection API

> 组件名：`rice-subsection`
> 官方文档：https://riceui.cn/components/subsection.html
> 源码：`components/rice-subsection/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `list` |  | `string[]` | - |
| `shape` | 形状 round/square | `String` | square |
| `init-animate` / `initAnimate` | 初始化时是否使用动画 | `Boolean` | false |
| `duration` | 动画时长，单位ms | `Number` | 300 |
| `before-change` / `beforeChange` |  | `(index : number) => Promise<boolean> ｜ boolean` | - |
| `bar-class` / `barClass` |  | `string` | - |
| `item-class` / `itemClass` |  | `string` | - |
| `text-class` / `textClass` |  | `string` | - |
| `active-text-class` / `activeTextClass` |  | `string` | - |
| `inactive-text-class` / `inactiveTextClass` |  | `string` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | 切换时触发 | index |
| `@clickSub` | 点击时触发 | index |
| `@default` | 自定义item | {index:当前的索引,subname：当前的值} |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `item` |  |
| `ifndef` |  |
| `endif` |  |
