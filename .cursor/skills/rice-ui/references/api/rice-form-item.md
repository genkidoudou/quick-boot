<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-form-item API

> 组件名：`rice-form-item`
> 官方文档：https://riceui.cn/components/form.html
> 源码：`components/rice-form-item/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `name` |  | `string` | - |
| `label` |  | `string` | - |
| `label-width` / `labelWidth` | 标签的宽度 | `String｜Number` | auto |
| `label-position` / `labelPosition` |  | `FormLabelPosition` | - |
| `label-style` / `labelStyle` |  | `UTSJSONObject` | - |
| `rules` |  | `FormRules[]` | - |
| `required` |  | `boolean` | - |
| `show-required` / `showRequired` |  | `boolean` | - |
| `required-position` / `requiredPosition` |  | `FormRequiredPosition` | - |
| `show-error` / `showError` | 校验失败时是否显示错误信息 | `Boolean` | true |
| `suffix-icon` / `suffixIcon` |  | `string` | - |
| `suffix-icon-style` / `suffixIconStyle` |  | `UTSJSONObject` | - |
| `border-bottom` / `borderBottom` | 是否显示表单域的下划线边框 | `Boolean` | true |
| `error-bottom` / `errorBottom` | 校验未通过时下边框是否标红, border-bottom 为 true 时生效 | `Boolean` | false |
| `content-style` / `contentStyle` |  | `UTSJSONObject` | - |
| `error-style` / `errorStyle` |  | `UTSJSONObject` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `scroll-to-error` | 是否自动滚动到第一个校验不通过的字段，APP端需在 scroll-view 或 list-view 组件中使用才有效, 当前仅APP端支持 | `Boolean` | false |
| `disabled` | 是否禁用此表单中的所有组件， 支持的组件见上 | `Boolean` | false |
| `readonly` | 是否只读此表单中的所有组件， 支持的组件见上 | `Boolean` | false |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@label-width` | String｜Number | 标签的宽度，会覆盖 Form 中的 label-width |
| `@required` | 是否为必选字段，当值为空值时（空字符串、空数组、空对象、undefined、null ），校验不通过 | 是否必填，为 true 时会对表单项进行空值的校验 |
| `@show-error` | Boolean | 校验失败时是否显示错误信息 |
| `@border-bottom` | Boolean | 是否显示表单域的下划线边框 |
| `@error-bottom` | Boolean | 校验未通过时下边框是否标红, border-bottom 为 true 时生效 |
| `@validateFields` | 验证指定的表单项 | 要校验表单项的name值合集， 如 validateFields(['name','age']) |
| `@error` | 错误信息区域 | (message : string) |
| `@message` | 校验未通过时的错误信息 | String |
| `@max` | 最大值/长度，比较方式同 min | Number |
| `@pattern` | 通过正则表达式进行校验，正则无法匹配表示校验不通过 | RegExp |
| `@validator` | 通过函数进行校验,返回 true 表示校验通过，返回 false 或字符串表示校验不通过;可以返回一个 Promise 来进行异步校验 | (value : any ｜ null) => (boolean ｜ string) ｜ Promise |
| `@rule` | 当前未通过的rule | FormRules |
| `@label` | 标签文字 | String｜Null |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `label` |  |
| `suffix` |  |
| `suffixIcon` |  |
| `error` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
