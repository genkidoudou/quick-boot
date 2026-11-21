# 06 C7MessageBox API

> 源码：`packages/C7MessageBox/index.js` · 从 `@/packages` 导入

## 导出

- `c7Confirm` / `c7Alert` / `c7Prompt` / `c7DangerConfirm`
- `c7Loading`
- `setMessageBoxDefaults`（全局浅合并默认 options）
- 工具：`mergeMessageBoxOptions`、`mapMessageBoxResolve`、`mapMessageBoxReject`、`splitTitleAndOptions`

## 行为约定（与 EP 差异）

对话框类 API **始终 resolve** 为 `{ action: 'confirm'|'cancel'|'close', value? }`。  
用户取消/关闭路径 **不会** 向业务 `reject`（与 Element Plus 默认「取消即 reject」解耦）。

```js
import { c7Confirm, c7DangerConfirm } from '@/packages'

const { action } = await c7Confirm('确认提交？')
if (action !== 'confirm') return
```

## asyncConfirm

可在 options 里传 `asyncConfirm: () => Promise`，在 `beforeClose` 内 await；失败可不 `done()` 保持弹窗。详见源码 JSDoc。

## 与 JsonTable 删除

行内删除优先用 `tableRef.removeRows(...)`（内部已含确认）。仅自定义确认流程时再用 `c7Confirm` / `c7DangerConfirm`。
