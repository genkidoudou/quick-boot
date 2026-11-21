<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-radio-group API

> 组件名：`rice-radio-group`
> 官方文档：https://riceui.cn/components/radio.html
> 源码：`components/rice-radio-group/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `direction` | 排列方向，可选值：col、row | `String` | col |
| `checked-color` / `checkedColor` |  | `string` | - |
| `icon-size` / `iconSize` |  | `string ｜ number` | - |
| `label-size` / `labelSize` |  | `string ｜ number` | - |
| `label-color` / `labelColor` |  | `string` | - |
| `shape` |  | `RadioShape` | - |
| `icon-position` / `iconPosition` |  | `RadioIconPosition` | - |
| `space-between` / `spaceBetween` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `custom-class` / `customClass` |  | `string` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@shape` | String | 形状，如设置此值，将会覆盖radio-group的shape值，可选值：square、round |
| `@change` | 绑定值改变时触发 | (value:string｜number｜boolean) |
| `@default` | 自定义文本 | { checked : boolean, disabled : boolean } |
| `@icon` | 自定义图标 | { checked : boolean, disabled : boolean } |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `default` | 默认插槽（若组件支持） |
