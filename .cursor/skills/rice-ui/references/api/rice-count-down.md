<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-count-down API

> 组件名：`rice-count-down`
> 官方文档：https://riceui.cn/components/count-down.html
> 源码：`components/rice-count-down/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `time` |  | `number` | - |
| `format` |  | `string` | - |
| `auto-start` / `autoStart` |  | `boolean` | - |
| `millisecond` |  | `boolean` | - |
| `font-size` / `fontSize` |  | `string ｜ number` | - |
| `color` |  | `string` | - |
| `text-class` / `textClass` |  | `string` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `change` | 倒计时改变时触发 | `(current:CurrentTime)` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | 倒计时改变时触发 | (current:CurrentTime) |
| `@default` | 自定义内容 | ( current : CurrentTime) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `default` | 默认插槽（若组件支持） |
