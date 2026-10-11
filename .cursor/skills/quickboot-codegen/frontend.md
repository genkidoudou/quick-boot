# quick-ui 管理端

目录相对于 `quick-ui/src`。

**权威参考（生成前必读并对齐）：** `views/system/dict/type/index.vue`  
（配套 API：`api/system/dict/type.js`；更简 CRUD 可再对照 `views/system/dict/data/index.vue`。）

字典页里的「便捷添加 / 跳转数据 / 刷新缓存」是字典业务专属，**标准 CRUD 生成不要抄这些**；只对齐下列通用骨架。

| 落点 | API | 页面 |
|---|---|---|
| 系统模块 | `api/system/{资源}.js` | `views/system/{资源}/index.vue` |
| 新模块 | `api/{模块}/{资源}.js` | `views/{模块}/{资源}/index.vue` |

路由由后端菜单下发。页面文件路径必须与菜单 `component` 一致（去掉 `views/` 前缀和 `.vue`）。不要在 `router/index.js` 里写死业务路由。

## 必须对齐的约定

| 项 | 约定 |
|---|---|
| 列表 | `C7JsonTable`：`list-function` / `delete-function` / 权限 props / `@add-click` `@edit-click` |
| 弹窗 | `C7Dialog` 用 **`:on-confirm="submitForm"`**（不要用 `@confirm`；`onConfirm` resolve 后自动关弹窗） |
| 列配置 | `tableColumns`、`searchColumns` 必须是 **`computed`**（字典异步加载后才能刷新） |
| 字典 | `useDict('DICT_TYPE')`；列表 `columnType: 'tag'` + `dictList: XXX.value \|\| []`；查询 `type: 'select'` + `dataList` |
| 状态类表单项 | 优先 `<c7-radio :data-list="XXX" v-model="form.status" />`（或 `el-radio-group` 遍历字典） |
| 操作列 | `columnType: 'slot'` + `#actions`：行内修改 / 删除；删除用 `ElMessageBox.confirm` 后调 `removeXxx([id])` |
| 组件名 | `defineOptions({ name: 'SysXxx' })`，与后端实体名一致 |
| 表单宽度 | `label-width="96px"` |
| 刷新 | 成功后 `tableRef.value?.refreshData()` |
| 审计列 | 列表/表单均不展示 `createBy` / `createTime` / `updateBy` / `updateTime` / `delFlag` |

## API

标准 CRUD **手写** `request`，对齐 `api/system/dict/type.js`。`BASE` 与 Controller 路径一致，并带前导 `/`。**禁止**使用 `createCrudApi` 或其它 API 工厂。

```javascript
/**
 * {中文名} API，对齐后端 `{basePath}`。
 */
import request from '@/utils/request'

const BASE = '/sys/notice'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageNotice(data) {
  return request({
    url: `${BASE}/page`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param: data?.param ?? {}
    }
  })
}

/**
 * 详情。
 * @param {string|number} id
 */
export function getNotice(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addNotice(data) {
  return request({
    url: `${BASE}/add`,
    method: 'post',
    data
  })
}

/**
 * 修改。
 * @param {Record<string, any>} data
 */
export function updateNotice(data) {
  return request({
    url: `${BASE}/update`,
    method: 'post',
    data
  })
}

/**
 * 批量删除。
 * @param {Array<string|number>} ids
 */
export function removeNotice(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}
```

用户点名 Excel 时，按 `dict/type.js` 同样手写 `exportExcel` / `importExcelTemplate` / `importExcel`（可用 `downloadRequest`）。有自定义接口（如字典 `parse` / `quickAdd`）时继续手写 `request`。

## 列表页完整模板

占位：`{Name}`=`Notice`，权限前缀 `system:notice`，路径 `/sys/notice`。按字段表替换列与表单项。

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
    >
      <!-- 仅当用户点名 Excel 时增加：
      show-import-button
      :export-function="exportExcelNotice"
      :import-function="importExcelNotice"
      :import-template-download-fn="downloadNoticeImportTemplate"
      import-template-file-name="notice-import-template.xlsx"
      :export-button-permi="['system:notice:export']"
      :import-button-permi="['system:notice:import']"
      -->
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['system:notice:edit']"
          @click="openForm(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['system:notice:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除「${row.noticeTitle || row.id}」？` })"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <C7Dialog v-model="open" :title="title" :on-confirm="submitForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="500" />
        </el-form-item>
      </el-form>
    </C7Dialog>
  </div>
</template>

<script setup>
/**
 * 公告管理：列表 / 新增 / 修改 / 删除（对齐后端 SysNotice）。
 */
import { computed, reactive, ref } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDict } from '@/utils/dict'
import {
  addNotice,
  pageNotice,
  removeNotice,
  updateNotice
} from '@/api/system/notice'

defineOptions({ name: 'SysNotice' })

/** 状态字典：与表字段注释中的字典类型一致 */
const { COMMON_STATUS } = useDict('COMMON_STATUS')

const tableRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改公告' : '新增公告'))

const tableColumns = computed(() => [
  { prop: 'noticeTitle', label: '公告标题', minWidth: 160, showOverflowTooltip: true },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100 },
  { prop: 'remark', label: '备注', minWidth: 160, showOverflowTooltip: true },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'noticeTitle', label: '公告标题', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || [] }
])

const rules = {
  noticeTitle: [{ required: true, message: '公告标题不能为空', trigger: 'blur' }]
}

function emptyForm() {
  return {
    id: undefined,
    noticeTitle: '',
    status: '0',
    remark: ''
  }
}

/**
 * @param {Record<string, any>} [row]
 */
function openForm(row) {
  Object.assign(form, emptyForm())
  if (row && row.id != null) {
    Object.assign(form, {
      id: row.id,
      noticeTitle: row.noticeTitle ?? '',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
  open.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  const payload = {
    id: form.id,
    noticeTitle: form.noticeTitle,
    status: form.status,
    remark: form.remark
  }
  if (form.id) {
    await updateNotice(payload)
    ElMessage.success('修改成功')
  } else {
    await addNotice(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  tableRef.value?.refreshData()
}
</script>
```

### 列与查询字段

`tableColumns`（字段表「列表」列）：

- 普通文本：`{ prop, label, minWidth, showOverflowTooltip: true }`
- 字典：`{ prop, label, columnType: 'tag', dictList: DICT.value || [], width: 100 }`
- 操作：固定加一行 `{ prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center' }`
- `columnType`：`text`（默认）、`tag`、`image`、`link`、`slot`

`searchColumns`（字段表「查询」列）：

- `{ prop, label, type: 'input' }`
- 字典：`{ prop, label, type: 'select', dataList: DICT.value || [] }`
- `type`：`input`、`select`、`date`、`daterange`、`slot`

## 表单

- 只放字段表标记为表单的列；密码/密钥默认不进列表。
- `remark` 若业务需要展示，可用 textarea，不强制。
- `submitForm`：`validate` → `add`/`update` → `ElMessage.success` → `open = false` → `refreshData`。
- 使用 `:on-confirm` 时，校验失败应 **throw**（或让 `validate` reject），这样弹窗不会误关。

## Excel（仅用户点名时）

在 `C7JsonTable` 上对照 dict/type 传入：

- `show-import-button`
- `:export-function`、`:import-function`、`:import-template-download-fn`
- `import-template-file-name`
- `:export-button-permi`、`:import-button-permi`

权限字与后端一致：`system:{资源}:export` / `import`。

## 自检（前端）

- [ ] 对照过 `views/system/dict/type/index.vue` 骨架（非字典专属逻辑）
- [ ] `C7Dialog` 使用 `:on-confirm`，不是 `@confirm`
- [ ] `tableColumns` / `searchColumns` 为 `computed`
- [ ] 有 `#actions` 槽；行内删除用 `tableRef?.removeRows(row, { confirmMessage })`
- [ ] 字典列用 `useDict` + `tag` / `select` / `c7-radio`
- [ ] `defineOptions({ name })` 已写
- [ ] 菜单 `component` 与页面路径一致
- [ ] 未要求 Excel 时无多余导入导出 props
