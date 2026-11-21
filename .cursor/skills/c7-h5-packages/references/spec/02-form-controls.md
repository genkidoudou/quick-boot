# 02 · 表单控件

## 选项类（Select / Radio / Checkbox / Cascader / TreeSelect）

统一：

- `dataList` 优先于 `options`
- `labelKey` / `valueKey`（Cascader/Tree 另有 `childrenKey`）
- `v-model` + `@change`

示例：

```uvue
<c7-select v-model="status" :options="statusOpts" placeholder="请选择" />
<c7-radio v-model="status" :data-list="statusOpts" />
<c7-checkbox v-model="flags" :options="statusOpts" :join-value="true" />
```

## 开关 / 日期时间

```uvue
<c7-switch v-model="enabled" active-value="1" inactive-value="0" />
<c7-date-picker v-model="date" type="date" />
<c7-time-picker v-model="time" />
```

## 标签

```uvue
<c7-form-label text="状态" tip="点击查看说明" />
```

## 上传

```uvue
<c7-upload classify="avatar" v-model:results="files" :upload-fn="doUpload" />
```

`uploadFn(filePath, classify)` 由页面提供；未配置会 toast 提示。
