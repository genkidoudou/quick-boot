<template>
  <div class="app-container">
    <C7JsonTable
        ref="tableRef"
        :list-function="pageDictData"
        :table-columns="tableColumns"
        :search-columns="searchColumns"
        :default-search-param="defaultSearchParam"
        row-key="id"
        show-add-button
        show-edit-button
        :delete-function="removeDictData"
        :add-button-permi="['system:dictData:add']"
        :edit-button-permi="['system:dictData:edit']"
        :delete-button-permi="['system:dictData:remove']"
        @add-click="openForm()"
        @edit-click="openForm"
    >
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
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" maxlength="255"/>
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入字典标签" maxlength="255"/>
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入字典值" maxlength="255"/>
        </el-form-item>
        <el-form-item label="排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" :min="0" :max="9999" controls-position="right"/>
        </el-form-item>
        <el-form-item label="回显样式" prop="listClass">
          <el-select v-model="form.listClass" clearable placeholder="请选择回显样式" style="width: 100%">
            <el-option
                v-for="item in LIST_CLASS_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="样式类名" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="css class" maxlength="255"/>
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="form.isDefault">
            <el-radio v-for="item in COMMON_IS" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in COMMON_STATUS" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100"/>
        </el-form-item>
      </el-form>
    </C7Dialog>
  </div>
</template>

<script setup>
/**
 * 字典数据管理：列表 / 新增 / 修改 / 删除（对齐 SysDictType 跳转）。
 */
import {computed, reactive, ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {LIST_CLASS_OPTIONS, useDict} from '@/utils/dict'
import {
  addDictData,
  pageDictData,
  removeDictData,
  updateDictData
} from '@/api/system/dict/data'
import {Delete, Edit} from "@element-plus/icons-vue";
import {removeDictType} from "@/api/system/dict/type.js";

const route = useRoute()
const {COMMON_STATUS, COMMON_IS} = useDict('COMMON_STATUS', 'COMMON_IS')

const tableRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const dictType = computed(() => String(route.params.dictType || route.query.dictType || ''))
const defaultSearchParam = {
  dictType: dictType.value
}
const form = reactive(emptyForm())
watch(dictType, (value) => {
  form.dictType = value
})

const title = computed(() => (form.id ? '修改字典数据' : '新增字典数据'))

const tableColumns = computed(() => [
  {prop: 'dictType', label: '字典类型', minWidth: 120, showOverflowTooltip: true},
  {prop: 'dictLabel', label: '字典标签', minWidth: 120, showOverflowTooltip: true},
  {prop: 'dictValue', label: '字典值', minWidth: 100, showOverflowTooltip: true},
  {prop: 'dictSort', label: '排序', width: 80},
  {prop: 'listClass', label: '回显样式', width: 100},
  {prop: 'isDefault', label: '是否默认', columnType: 'tag', dictList: COMMON_IS.value || [], width: 100},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 90},
  {prop: 'remark', label: '备注', minWidth: 140, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}

])

const searchColumns = computed(() => [
  {prop: 'dictType', label: '字典类型', type: 'input'},
  {prop: 'dictLabel', label: '字典标签', type: 'input'},
  {prop: 'dictValue', label: '字典值', type: 'input'},
  {prop: 'isDefault', label: '是否默认', type: 'select', dataList: COMMON_IS.value || []},
  {prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || []}
])

const rules = {
  dictType: [{required: true, message: '字典类型不能为空', trigger: 'blur'}],
  dictLabel: [{required: true, message: '字典标签不能为空', trigger: 'blur'}],
  dictValue: [{required: true, message: '字典值不能为空', trigger: 'blur'}]
}

function emptyForm() {
  return {
    id: undefined,
    dictType: defaultSearchParam.dictType || '',
    dictLabel: '',
    dictValue: '',
    dictSort: 0,
    cssClass: '',
    listClass: 'default',
    isDefault: '1',
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
      dictType: row.dictType ?? '',
      dictLabel: row.dictLabel ?? '',
      dictValue: row.dictValue ?? '',
      dictSort: row.dictSort ?? 0,
      cssClass: row.cssClass ?? '',
      listClass: row.listClass ?? 'default',
      isDefault: row.isDefault ?? '1',
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
    dictType: form.dictType,
    dictLabel: form.dictLabel,
    dictValue: form.dictValue,
    dictSort: form.dictSort,
    cssClass: form.cssClass,
    listClass: form.listClass,
    isDefault: form.isDefault,
    status: form.status,
    remark: form.remark
  }
  if (form.id) {
    await updateDictData(payload)
    ElMessage.success('修改成功')
  } else {
    await addDictData(payload)
    ElMessage.success('新增成功')
  }
  tableRef.value?.refreshData()
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
  await removeDictData([row.id])
  ElMessage.success('删除成功')
  tableRef.value?.refreshData()
}
</script>
