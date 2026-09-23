# quick-ui 管理端

目录相对于 `quick-ui/src`。

| 落点 | API | 页面 |
|---|---|---|
| 系统模块 | `api/system/{资源}.js` | `views/system/{资源}/index.vue` |
| 新模块 | `api/{模块}/{资源}.js` | `views/{模块}/{资源}/index.vue` |

路由由后端菜单下发。页面文件路径必须与菜单 `component` 一致（去掉 `views/` 前缀和 `.vue`）。不要在 `router/index.js` 里写死业务路由。

## API

使用 `@/api/_factory/createCrudApi`。`basePath` 与 Controller 路径一致，并带前导 `/`。

```javascript
import { createCrudApi } from '@/api/_factory/createCrudApi'

const crud = createCrudApi('/sys/notice')

export const pageNotice = crud.page
export const getNotice = crud.get
export const addNotice = crud.add
export const updateNotice = crud.update
export const removeNotice = crud.remove
```

用户点名 Excel 时改为 `createCrudApi('/sys/notice', { export: true })`，并导出 `crud.export`、`crud.downloadImportTemplate`、`crud.importExcel`。

## 列表页

用 `C7JsonTable`。`listFunction` 传 `pageXxx`。删除传 `removeXxx`，入参已是 id 数组。

```vue
<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageNotice"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeNotice"
      :add-button-permi="['system:notice:add']"
      :edit-button-permi="['system:notice:edit']"
      :delete-button-permi="['system:notice:remove']"
      @add-click="openForm()"
      @edit-click="openForm"
    />
    <C7Dialog v-model="open" :title="title" @confirm="submitForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <!-- 只放字段表中标记为表单的列 -->
      </el-form>
    </C7Dialog>
  </div>
</template>
```

`tableColumns` 来自字段表里的列表列：

```javascript
const tableColumns = [
  { prop: 'noticeTitle', label: '公告标题', minWidth: 160, showOverflowTooltip: true },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: statusOptions, width: 100 }
]
```

`columnType` 使用 `text`（默认）、`tag`、`image`、`link`、`slot`。字典列用 `columnType: 'tag'` 加 `dictList`。

`searchColumns` 只放标记为查询的列：

```javascript
const searchColumns = [
  { prop: 'noticeTitle', label: '公告标题', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', dataList: statusOptions }
]
```

`type` 使用 `input`、`select`、`date`、`daterange`、`slot`。

## 表单

`C7Dialog` 用 `v-model` 控制显隐，确定按钮走 `@confirm`。新增调用 `addXxx`，修改调用 `updateXxx`。成功后关闭弹窗，并调用表格暴露的 `refreshData`。

表单不展示审计字段，也不展示未标记为表单的列。密码、密钥类字段默认不进列表。

Excel 按钮仅在用户点名时打开：`show-export-button`、`show-import-button`，并传入 `exportFunction`、`importFunction`、`importTemplateDownloadFn` 以及对应的 `exportButtonPermi`、`importButtonPermi`。
