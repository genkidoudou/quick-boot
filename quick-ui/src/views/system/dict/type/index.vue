<template>
  <div class="app-container">
    <C7JsonTable
        ref="tableRef"
        :list-function="pageDictType"
        :table-columns="tableColumns"
        :search-columns="searchColumns"
        row-key="id"
        show-add-button
        show-edit-button
        show-import-button
        :delete-function="removeDictType"
        :add-button-permi="['system:dictType:add']"
        :edit-button-permi="['system:dictType:edit']"
        :delete-button-permi="['system:dictType:remove']"
        @add-click="openForm()"
        @edit-click="openForm"
        :export-function="exportExcelDictType"
        :import-function="importExcelDictType"
        :import-template-download-fn="downloadDictTypeImportTemplate"
        import-template-file-name="dict-type-import-template.xlsx"
        :export-button-permi="['system:dictType:export']"
        :import-button-permi="['system:dictType:import']"
    >
      <template #toolbar-left>
        <el-button type="primary" plain :icon="Plus" v-hasPermi="['system:dictType:add']" @click="openQuick">便捷添加</el-button>
        <el-button type="danger" plain v-hasPermi="['system:dict:refresh']" @click="handleRefresh">刷新缓存</el-button>
      </template>
      <template #dictType="{ row }">
        <el-button link type="primary" @click="goData(row)">{{ row.dictType }}</el-button>
      </template>
      <template #actions="{ row }">
        <el-button
            link
            type="primary"
            :icon="Edit"
            v-hasPermi="['system:dictType:edit']"
            @click="openForm(row)"
        >
          修改
        </el-button>
        <el-button
            link
            type="primary"
            :icon="Delete"
            v-hasPermi="['system:dictType:remove']"
            @click="handleDelete(row)"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <C7Dialog v-model="open" :title="title" :on-confirm="submitForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" maxlength="255"/>
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" maxlength="255" :disabled="form.id"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status">

          </c7-radio>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100"/>
        </el-form-item>
      </el-form>
    </C7Dialog>

    <C7Dialog v-model="quickOpen" title="便捷添加字典" width="720px" :on-confirm="submitQuick">
      <el-alert type="info" :closable="false" show-icon class="mb-3" title="首行写名称和类型，后续每行一项。支持 = : 空格 以及逗号分隔。"/>
      <el-form label-width="96px">
        <el-form-item label="字典文本">
          <el-input
              v-model="quickSource"
              type="textarea"
              :rows="8"
              :placeholder="QUICK_PLACEHOLDER"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain @click="parseQuick">解析</el-button>
        </el-form-item>
      </el-form>
      <template v-if="quickParsed">
        <el-form :model="quickParsed" label-width="96px">
          <el-form-item label="字典名称">
            <el-input v-model="quickParsed.dictName" maxlength="255"/>
          </el-form-item>
          <el-form-item label="字典类型">
            <el-input v-model="quickParsed.dictType" maxlength="255"/>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="quickParsed.remark" type="textarea" :rows="2" maxlength="100"/>
          </el-form-item>
        </el-form>
        <el-table :data="quickParsed.items" size="small" border>
          <el-table-column label="标签" min-width="140">
            <template #default="{ row }">
              <el-input v-model="row.dictLabel"/>
            </template>
          </el-table-column>
          <el-table-column label="键值" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.dictValue"/>
            </template>
          </el-table-column>
          <el-table-column label="排序" width="90">
            <template #default="{ row }">
              <el-input v-model="row.dictSort"/>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </C7Dialog>
  </div>
</template>

<script setup>
/**
 * 字典类型管理：列表 / 新增 / 修改 / 删除（对齐 SysDictType 后端）。
 */
import {computed, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {Delete, Edit, Plus} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useDict} from '@/utils/dict'
import {
  addDictType,
  pageDictType,
  removeDictType,
  updateDictType,
  exportExcelDictType,
  importExcelDictType,
  downloadDictTypeImportTemplate,
  parseDictType,
  quickAddDictType
} from '@/api/system/dict/type'
import C7Radio from "@/packages/C7Radio/index.vue";

defineOptions({name: 'SysDictType'})

const router = useRouter()

/** 状态字典：COMMON_STATUS（与表字段注释一致） */
const {COMMON_STATUS} = useDict('COMMON_STATUS')

const tableRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改字典类型' : '新增字典类型'))

const tableColumns = computed(() => [
  {prop: 'dictName', label: '字典名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'dictType', label: '字典类型', minWidth: 140, showOverflowTooltip: true,columnType: 'slot', slotName: 'dictType' },
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100},
  {prop: 'remark', label: '备注', minWidth: 160, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'dictName', label: '字典名称', type: 'input'},
  {prop: 'dictType', label: '字典类型', type: 'input'},
  {prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || []}
])

const rules = {
  dictName: [{required: true, message: '字典名称不能为空', trigger: 'blur'}],
  dictType: [{required: true, message: '字典类型不能为空', trigger: 'blur'}]
}

function emptyForm() {
  return {
    id: undefined,
    dictName: '',
    dictType: '',
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
      dictName: row.dictName ?? '',
      dictType: row.dictType ?? '',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
  open.value = true
}

/**
 * 行内删除。
 *
 * @param {Record<string, any>} row
 */
async function handleDelete(row) {
  if (row?.id == null) {
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除字典类型「${row.dictName || row.dictType || row.id}」？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  await removeDictType([row.id])
  ElMessage.success('删除成功')
  tableRef.value?.refreshData()
}

async function submitForm() {
  await formRef.value?.validate()
  const payload = {
    id: form.id,
    dictName: form.dictName,
    dictType: form.dictType,
    status: form.status,
    remark: form.remark
  }
  if (form.id) {
    await updateDictType(payload)
    ElMessage.success('修改成功')
  } else {
    await addDictType(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  tableRef.value?.refreshData()
}

const QUICK_PLACEHOLDER = `通用状态 COMMON_STATUS
正常=0
停用=1

或：
MENU_TYPE 菜单类型
目录=M, 菜单=C, 按钮=F

或：
通用状态(COMMON_STATUS)
正常:0
停用:1`

const quickOpen = ref(false)
const quickSource = ref('')
const quickParsed = ref(null)

function openQuick() {
  quickSource.value = ''
  quickParsed.value = null
  quickOpen.value = true
}

async function parseQuick() {
  const body = await parseDictType(quickSource.value)
  const res = body?.data ?? body
  const items = Array.isArray(res?.items) ? res.items : []
  quickParsed.value = {
    dictName: res?.dictName ?? '',
    dictType: res?.dictType ?? '',
    remark: res?.remark ?? '',
    status: res?.status ?? '0',
    items
  }
  ElMessage.success(`已解析字典「${quickParsed.value.dictName}」，${items.length} 个字典项`)
}

async function submitQuick() {
  if (!quickParsed.value) {
    await parseQuick()
  }
  const payload = quickParsed.value
  if (!payload?.dictName || !payload?.dictType) {
    ElMessage.warning('请先解析出字典名称和类型')
    throw new Error('invalid')
  }
  if (!payload.items?.length) {
    ElMessage.warning('请至少保留一个字典项')
    throw new Error('invalid')
  }
  await quickAddDictType(payload)
  ElMessage.success('添加成功')
  quickOpen.value = false
  tableRef.value?.refreshData()
}

function handleRefresh() {
  ElMessage.info('刷新缓存接口尚未接入')
}

function goData(row) {
  if (!row?.dictType) {
    return
  }
  router.push({
    path: '/sys/dictData',
    query: { dictType: row.dictType }
  })
}
</script>
