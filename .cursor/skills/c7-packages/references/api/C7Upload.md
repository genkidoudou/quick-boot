<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Upload API

> 源码：`packages/C7Upload/`
> 标签：`<C7Upload>` / `<c7-upload>`

C7 文件上传：基于 `ElUpload` 拖拽上传，按 `classify` 拉取分类规则（数量/大小/扩展名/压缩），可选图片压缩后调用 `/file/upload`；`v-model:results` 绑定已成功上传的结果列表。@prop {string} classify 上传分类（必填，对应 sys_file_classify.classify）@prop {boolean} [autoUpload=true] 选文件后是否立即上传@emits update:results 上传成功或删除后更新结果列表（v-model:results）@emits success 单文件上传成功@emits error 上传或加载分类规则失败@emits change 文件列表或结果变更

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model:results` | type: Array, default: () => [] |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `classify` / `classify` | 上传分类（必填，对应 sys_file_classify.classify） | `String` | （必填） |
| `auto-upload` / `autoUpload` | 选中文件后是否立即上传 | `Boolean` | true |
| `disabled` / `disabled` | 禁用上传 | `Boolean` | false |
| `upload-fn` / `uploadFn` | 自定义上传函数 (file, classify) => Promise，默认走 /file/upload | `Function` | undefined |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@success` | 单文件上传成功 |
| `@error` | 上传或加载分类规则失败 |
| `@change` | 文件列表或结果变更 |

## 插槽

| 插槽 |
| --- |
| `tip` |

## 暴露方法

`submit`、`clearFiles`、`getFiles`、`reloadRule`
