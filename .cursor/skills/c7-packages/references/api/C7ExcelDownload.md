<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7ExcelDownload API

> 源码：`packages/C7ExcelDownload/`
> 标签：`<C7ExcelDownload>` / `<c7-excel-download>`

C7 导出下载按钮：执行 **`downloadFn`** 取 **Blob**（或 **`{ data, headers }`**），按优先级解析文件名后通过 **`objectURL` + `<a download>`** 触发浏览器下载；管理 **`v-model:downloading`**；**JSON 错误 Blob** 与 **`download()`** 对齐提示。**与 `request` 配合**：需要 **`Content-Disposition`** 时，请使用**`downloadRequest(url, params, { returnBlobWithHeaders: true })`**，在 **`downloadFn`** 中 **`return`** 该 Promise 结果（或自行组装 **`{ data, headers }`**）。@emits success(fileName) 下载链成功触发后@emits error(err) 失败（含 **`downloadFn` reject**、JSON Blob、无可用文件名等）

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model:downloading` | type: Boolean, default: false |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `download-fn` / `downloadFn` | 未传入时点击无效并在开发环境 `console.warn`（见 **`handleClick`**） | `Function` | undefined |
| `file-name` / `fileName` | 非空时优先作为保存文件名，**不再**解析 **`Content-Disposition`** | `String` | '' |
| `default-file-name` / `defaultFileName` | 当 **`fileName`** 与响应头均未得到有效名时的兜底文件名 | `String` | '' |
| `notify` / `notify` | 自定义通知；传入后错误路径 **仅**走此回调（成功路径仍 **`emit('success')`**，不强制 toast）。@param {'success'｜'error'｜'warning'｜'info'} type@param {string} message | `Function` | undefined |
| `default-label` / `defaultLabel` | 默认插槽为空时的按钮文案 | `String` | '导出' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@success` |  |
| `@error` |  |

## 插槽

| 插槽 |
| --- |
| `default` |
