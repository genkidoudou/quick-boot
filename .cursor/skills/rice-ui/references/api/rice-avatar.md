<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-avatar API

> 组件名：`rice-avatar`
> 官方文档：https://riceui.cn/components/avatar.html
> 源码：`components/rice-avatar/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `src` |  | `string` | - |
| `round` |  | `boolean` | - |
| `radius` |  | `string ｜ number` | - |
| `size` |  | `string ｜ number` | - |
| `mode` |  | `string` | - |
| `text` |  | `string ｜ number` | - |
| `icon` |  | `string` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `color` |  | `string` | - |
| `font-size` / `fontSize` |  | `string ｜ number` | - |
| `text-class` / `textClass` |  | `string` | - |
| `default-url` / `defaultUrl` |  | `string` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `error` | 头像加载失败时触发 | `event: UniImageErrorEvent｜null` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@error` | 头像加载失败时触发 | event: UniImageErrorEvent｜null |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `icon` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `fff` |  |
