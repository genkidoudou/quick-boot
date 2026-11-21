<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-row API

> 组件名：`rice-row`
> 官方文档：https://riceui.cn/components/layout.html
> 源码：`components/rice-row/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `gutter` | 列元素之间的间距 | `number` | 0 |
| `justify` | flex 布局下的水平排列方式，可选值见下 Justify Options | `string` | start |
| `align` |  | `'top' ｜ 'center' ｜ 'bottom'` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@span` | 栅格占据的列数,24等分 | number |
| `@offset` | 栅格左侧的间隔格数 ,计算方式与 span 相同 | number |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `default` | 默认插槽（若组件支持） |
