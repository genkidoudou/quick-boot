<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7ExcelUpload API

> 源码：`packages/C7ExcelUpload/`
> 标签：`<C7ExcelUpload>` / `<c7-excel-upload>`

C7 Excel 同步导入：拖拽/点选 xls/xlsx，调用 `uploadFn(file, strategy)` 完成导入；无导入导出中心依赖；可选模板下载与失败明细下载。@prop {function(File, 'overwrite'|'ignore'): Promise} uploadFn 导入函数@prop {function(): Promise<Blob>} [templateDownloadFn] 模板下载@emits success 导入完成，载荷为后端返回的结果对象@emits error 导入失败@emits cancel 用户点击取消

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model:uploading` | type: Boolean, default: false |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `accept` / `accept` |  | `String` | '.xls |
| `max-size-mb` / `maxSizeMb` |  | `Number` | 10 |
| `upload-fn` / `uploadFn` |  | `Function` | undefined |
| `notify` / `notify` |  | `Function` | undefined |
| `template-download-fn` / `templateDownloadFn` |  | `Function` | undefined |
| `template-file-name` / `templateFileName` |  | `String` | 'import-template.xlsx' |
| `error-file-name` / `errorFileName` |  | `String` | 'import-error.xlsx' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@success` | 导入完成，载荷为后端返回的结果对象 |
| `@error` | 导入失败 |
| `@cancel` | 用户点击取消 |

## 插槽

| 插槽 |
| --- |
| `templateFileName` |
| `409eff` |
| `606266` |
| `fafcff` |
| `eef5ff` |
| `c0c4cc` |
| `303133` |

## 暴露方法

`uploading`、`reset`
