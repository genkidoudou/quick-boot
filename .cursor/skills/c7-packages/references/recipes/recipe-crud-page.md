# 配方：标准 CRUD 页

**标杆**：`quick-ui/src/views/system/user/index.vue`  
**前置**：`installPackages` 已执行；API 模块按 `spec/02` 手写。

## 1. 模板骨架

```vue
<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageXxx"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeXxx"
      :add-button-permi="['module:res:add']"
      :edit-button-permi="['module:res:edit']"
      :delete-button-permi="['module:res:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="Edit" v-hasPermi="['module:res:edit']"
          @click="formDialogRef?.open(row)">修改</el-button>
        <el-button link type="primary" :icon="Delete" v-hasPermi="['module:res:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除「${row.name || row.id}」？` })">
          删除
        </el-button>
      </template>
    </C7JsonTable>

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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px" label-position="right">
        <!-- 字段：状态用 c7-radio；下拉用 c7-select / C7TreeSelect -->
      </el-form>
    </C7FormDialog>
  </div>
</template>
```

## 2. 脚本要点

```js
/**
 * Xxx：列表 / 新增 / 修改 / 删除（对齐后端 Xxx）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {pageXxx, getXxx, addXxx, updateXxx, removeXxx} from '@/api/...'

defineOptions({name: 'SysXxx'})

const {some_dict} = useDict('some_dict')
const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())
const title = computed(() => (form.id ? '修改' : '新增'))

const tableColumns = computed(() => [/* ... */])
const searchColumns = computed(() => [/* ... */])
const rules = computed(() => ({/* ... */}))

function emptyForm() {
  return {id: undefined /* ... */}
}

async function getPayload(row) {
  Object.assign(form, emptyForm())
  if (row?.id != null) {
    const res = await getXxx(row.id)
    Object.assign(form, res?.data || row)
  }
}

async function submitXxx() {
  if (form.id) await updateXxx(form)
  else await addXxx(form)
}
```

## 3. 检查清单

- [ ] columns / rules 均为 `computed`
- [ ] 无审计字段列
- [ ] 打开用 `formDialogRef.open`
- [ ] 成功回调 `tableRef.refreshData()`
- [ ] 权限标识与后端菜单一致
