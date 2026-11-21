<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-checkbox-group API

> 组件名：`rice-checkbox-group`
> 官方文档：https://riceui.cn/components/checkbox.html
> 源码：`components/rice-checkbox-group/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `max` |  | `number` | - |
| `disabled` | 是否禁用整个 CheckboxGroup | `boolean` | false |
| `readonly` | 是否只读 | `boolean` | false |
| `direction` | 排列方向，可选 row | `string` | col |
| `checked-color` / `checkedColor` |  | `string` | - |
| `icon-size` / `iconSize` | icon的大小，单位支持 px 和 rpx | `string｜number` | 20px |
| `label-size` / `labelSize` | 文字的大小，单位支持 px 和 rpx | `string｜number` | 15px |
| `label-color` / `labelColor` |  | `string` | - |
| `shape` | 形状，可选 圆形： round | `string` | square |
| `icon-position` / `iconPosition` | 选择框的对齐方式，可选 right | `string` | left |
| `space-between` / `spaceBetween` | 是否水平两端对齐， direction 为 row 时有效 | `boolean` | false |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@checked-value` | 选中时的值，单独使用 Checkbox 时该属性才有意义 | string｜number｜boolean |
| `@inchecked-value` | 未选中时的值，单独使用 Checkbox 时该属性才有意义 | string｜number｜boolean |
| `@indeterminate` | 是否为不确定状态，仅负责样式控制 | boolean |
| `@disabled` | 是否禁用 | boolean |
| `@readonly` | 是否只读 | boolean |
| `@icon-size` | icon的大小，单位支持 px 和 rpx ，如设置此值，将会覆盖 CheckboxGroup 的 iconSize 值 | string｜number |
| `@label-size` | 文字的大小，单位支持 px 和 rpx ，如设置此值，将会覆盖 CheckboxGroup 的 labelSize 值 | string｜number |
| `@shape` | 形状，可选 圆形： round ，如设置此值，将会覆盖 CheckboxGroup 的 shape 值 | string |
| `@icon-position` | 选择框的对齐方式，可选 right ，如设置此值，将会覆盖 CheckboxGroup 的 iconPosition 值 | string |
| `@space-between` | 是否水平两端对齐， direction 为 vertical 时有效，如设置此值，将会覆盖 CheckboxGroup 的 spaceBetween 值 | boolean |
| `@change` | 绑定值改变时触发 | value:Array |
| `@default` | 自定义文本 | { checked : boolean, disabled : boolean } |
| `@icon` | 自定义图标 | { checked : boolean, disabled : boolean } |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `default` | 默认插槽（若组件支持） |
