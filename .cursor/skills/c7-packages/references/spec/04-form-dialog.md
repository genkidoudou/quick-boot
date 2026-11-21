# 04 C7FormDialog / C7Dialog

## 分工

| 组件 | 用途 |
|---|---|
| **C7FormDialog** | CRUD 表单弹窗（推荐） |
| **C7Dialog** | 通用壳；非表单提交、或 FormDialog 不够用时 |

## C7FormDialog 约定

统一流水线：`open → getPayload 写 form → 校验 → submit → 提示 → onSuccess → 关窗`。

**必传：**

- `form` — 响应式对象
- `form-ref` — `el-form` 的 ref
- `get-payload` — `(row?) => void|Promise`，**在闭包里写 form**，不要 return 再灌表
- `submit` — `() => Promise`

**打开：** `formDialogRef.open(row?)`（不要只靠改 `v-model` 而跳过 getPayload）。

**不要**再传 `:on-confirm`（提交由 FormDialog 接管）。

```vue
<C7FormDialog
  ref="formDialogRef"
  v-model="open"
  :title="title"
  width="640px"
  mobile-mode="drawer"
  :form="form"
  :form-ref="formRef"
  :get-payload="getPayload"
  :submit="submitXxx"
  :on-success="() => tableRef?.refreshData()"
>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
    <!-- 字段 -->
  </el-form>
</C7FormDialog>
```

标题、宽度、`mobile-mode`、footer 等透传给底层 `C7Dialog`。

## getPayload / submit 模式

```js
async function getPayload(row) {
  Object.assign(form, emptyForm())
  if (row?.id != null) {
    const res = await getXxx(row.id)
    Object.assign(form, { /* 映射字段 */ })
  }
}

async function submitXxx() {
  if (form.id) await updateXxx(form)
  else await addXxx(form)
}
```

## C7Dialog

需要自定义确认逻辑、纯展示、或非 CRUD 时用。确认走 `:on-confirm` 或默认 footer。细节见 `api/C7Dialog.md`。
