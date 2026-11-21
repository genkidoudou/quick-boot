# 03 · 列表 / 弹层 / 反馈

## C7JsonTable

- `listFunction(q: { current, size, param }) => Promise<{ records, total }>`
- 插槽 `#item="{ row, index }"`
- 暴露 `refreshData`

标杆：`pages/system/config/index.uvue`。

## C7CardGrid

同 `listFunction`，插槽 `#card`；分页用内置 `C7Pagination`。

## C7Dialog / C7FormDialog

```uvue
<c7-dialog v-model="show" title="标题" :on-confirm="save">
  <!-- 内容 -->
</c7-dialog>

<c7-form-dialog
  ref="dlg"
  v-model="show"
  :form="form"
  :get-payload="getPayload"
  :submit="submit"
/>
```

`open(row?)` → `getPayload` → 提交走 `submit`；成功 toast 后可 `onSuccess`。

## MessageBox

```uts
import { c7Confirm, c7DangerConfirm, c7Loading } from '@/components/c7-message-box/index.uts'

const r = await c7Confirm('确认？', '提示')
if (r.action == 'confirm') { /* ... */ }
// 取消/关闭不 reject
```

## 权限

列表「新增/删除」等与 packages 一样可配 permi 数组，内部 `hasPermi`。
