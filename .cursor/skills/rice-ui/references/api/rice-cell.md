<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-cell API

> 组件名：`rice-cell`
> 官方文档：https://riceui.cn/components/cell.html
> 源码：`components/rice-cell/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `title` |  | `string ｜ number` | - |
| `value` |  | `string ｜ number` | - |
| `label` |  | `string ｜ number` | - |
| `size` |  | `'large' ｜ 'normal'` | - |
| `icon` |  | `string` | - |
| `url` |  | `string` | - |
| `border` |  | `boolean` | - |
| `clickable` |  | `boolean` | - |
| `arrow` |  | `boolean` | - |
| `arrow-direction` / `arrowDirection` |  | `'up' ｜ 'down' ｜ 'left' ｜ 'right'` | - |
| `center` |  | `boolean` | - |
| `title-style` / `titleStyle` |  | `UTSJSONObject` | - |
| `title-text-style` / `titleTextStyle` |  | `UTSJSONObject` | - |
| `value-style` / `valueStyle` |  | `UTSJSONObject` | - |
| `value-text-style` / `valueTextStyle` |  | `UTSJSONObject` | - |
| `label-style` / `labelStyle` |  | `UTSJSONObject` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `click` | 单元格点击 | `unknown` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@click` | 单元格点击 | - |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `leftIcon` |  |
| `icon` |  |
| `title` |  |
| `label` |  |
| `value` |  |
| `rightIcon` |  |
| `ifndef` |  |
| `endif` |  |
| `ifdef` |  |
