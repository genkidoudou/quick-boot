<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-color-picker API

> 组件名：`rice-color-picker`
> 官方文档：https://riceui.cn/components/color-picker.html
> 源码：`components/rice-color-picker/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `preset-colors` / `presetColors` | 显示在底部的预设色 | `string[]` | [] |
| `show-alpha` / `showAlpha` | 是否显示透明色的滑块 | `boolean` | true |
| `disabled-input` / `disabledInput` | 是否禁用输入框 | `boolean` | false |
| `disabled-switch` / `disabledSwitch` | 是否禁用格式切换 | `boolean` | false |
| `panel-class` / `panelClass` |  | `string` | - |
| `preview-class` / `previewClass` |  | `string` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@change` | 一次交互结束后触发，例如手指松开滑块，输入框失焦，点击底部预置色或结束面板拖动 | value:string |
| `@changing` | 拖动颜色面板、色相滑块、透明度滑块的过程中持续触发 | value:string |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifdef` |  |
| `endif` |  |
| `arrow-left` |  |
| `arrow-right` |  |
| `1989fa` |  |
| `ifndef` |  |
| `ff0` |  |
| `0f0` |  |
| `0ff` |  |
| `00f` |  |
| `f0f` |  |
| `969799` |  |
| `fff` |  |
| `707070` |  |
