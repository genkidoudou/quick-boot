<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-cascader API

> 组件名：`rice-cascader`
> 官方文档：https://riceui.cn/components/cascader.html
> 源码：`components/rice-cascader/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `options` | CascaderOption[] | `可选项的数据` | [] |
| `title` |  | `string` | - |
| `title-align` / `titleAlign` | 标题对齐方式，可选值：center、left | `String` | center |
| `placeholder` | 未选中时的提示文字 | `String` | 请选择 |
| `color` |  | `string` | - |
| `active-color` / `activeColor` |  | `string` | - |
| `font-size` / `fontSize` | 选项文字大小 | `String｜Number` | 16px |
| `closeable` | 是否显示关闭按钮 | `Boolean` | true |
| `close-icon` / `closeIcon` | 关闭按钮的图标 | `String` | cross |
| `border` | 选项是否显示下边框 | `Boolean` | false |
| `use-popup` / `usePopup` | 是否使用popup | `Boolean` | true |
| `close-on-finish` / `closeOnFinish` | 全部选项选择完成后是否关闭popup，是否全部选择完成判断的条件是 children 为 null | `Boolean` | true |
| `close-on-click-close` / `closeOnClickClose` | 点击关闭按钮是否关闭popup | `Boolean` | true |
| `close-on-click-overlay` / `closeOnClickOverlay` | 点击遮罩层是否关闭popup | `Boolean` | true |
| `safe-area-inset-bottom` / `safeAreaInsetBottom` |  | `boolean` | - |
| `z-index` / `zIndex` | zIndex 层级 | `Number` | 999 |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `show` | 是否显示, usePopup 为 true 时生效 | `boolean` | false |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@text` | 选项文字（必填） | String |
| `@value` | 选项对应的值（必填，且不能重复） | String｜Number |
| `@disabled` | 是否禁用 | Boolean |
| `@children` | 子选择列表,为null或不传时表示没有下一级 | CascaderOption[] |
| `@extend` | 自定义扩展字段，可以在 change 和 finish 事件回调中获取 | any |
| `@change` | 选中项变化时触发 | (value: CascaderEvent ) |
| `@finish` | 全部选项选择完成后触发，判断依据是 children 为 null 或不传 | (value: CascaderEvent ) |
| `@clickDisabled` | 点击禁用的选项时触发 | (value: CascaderClickDisabledEvent ) |
| `@getSelectedOptions` | 获取当前选中项的集合 | CascaderOption[] |
| `@optionsTop` | 选项上方区域 | tabIndex : number |
| `@optionsBottom` | 选项下方区域 | tabIndex : number |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `ifndef` |  |
| `endif` |  |
| `title` |  |
| `closeIcon` |  |
| `ifdef` |  |
| `optionsTop` |  |
| `checked` |  |
| `optionsBottom` |  |
