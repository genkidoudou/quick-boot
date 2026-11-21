<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-signature API

> 组件名：`rice-signature`
> 官方文档：https://riceui.cn/components/signature.html
> 源码：`components/rice-signature/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `type` | 导出图片的格式,可选jpeg | `String` | png |
| `width` | canvas宽度 | `String｜Number` | 100% |
| `height` | canvas高度 | `String｜Number` | 200px |
| `disabled` |  | `boolean` | - |
| `tips` |  | `string` | - |
| `tips-size` / `tipsSize` | 提示语大小 | `String｜Number` | 24px |
| `tips-color` / `tipsColor` |  | `string` | - |
| `line-color` / `lineColor` |  | `string` | - |
| `line-width` / `lineWidth` |  | `number` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `content-bg-color` / `contentBgColor` |  | `string` | - |
| `clear-button-text` / `clearButtonText` | 清除按钮文案，为空时不显示此按钮 | `String` | 清除 |
| `confirm-button-text` / `confirmButtonText` | 确认按钮文案，为空时不显示此按钮 | `String` | 确认 |
| `revoke-button-text` / `revokeButtonText` |  | `string` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@submit` | 点击确定按钮时触发 | (image:string) |
| `@done` | 等同于 submit 事件，返回 base64 格式的图片地址 | (image:string) |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `tips` |  |
| `000` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
