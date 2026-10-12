<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageConfig"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeConfig"
      :add-button-permi="['system:config:add']"
      :edit-button-permi="['system:config:edit']"
      :delete-button-permi="['system:config:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="Edit" v-hasPermi="['system:config:edit']" @click="formDialogRef?.open(row)">
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['system:config:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除参数「${row.configName || row.configKey || row.id}」？` })"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <C7FormDialog
      ref="formDialogRef"
      v-model="open"
      :title="title"
      :form="form"
      :form-ref="formRef"
      :get-payload="getPayload"
      :submit="submitConfig"
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" maxlength="255" />
        </el-form-item>
        <el-form-item label="参数值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" placeholder="请输入参数值" :rows="3" maxlength="512" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <c7-radio :data-list="COMMON_IS" v-model="form.configType" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100" />
        </el-form-item>
      </el-form>
    </C7FormDialog>
  </div>
</template>

<script setup>
/**
 * 参数配置：列表 / 新增 / 修改 / 删除（对齐后端 SysConfig）。
 */
import { computed, reactive, ref } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { useDict } from '@/utils/dict'
import { addConfig, pageConfig, removeConfig, updateConfig } from '@/api/system/config'

defineOptions({ name: 'SysConfig' })

const { COMMON_IS } = useDict('COMMON_IS')

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改参数' : '新增参数'))

const tableColumns = computed(() => [
  { prop: 'configName', label: '参数名称', minWidth: 140, showOverflowTooltip: true },
  { prop: 'configKey', label: '参数键名', minWidth: 160, showOverflowTooltip: true },
  { prop: 'configValue', label: '参数值', minWidth: 180, showOverflowTooltip: true },
  { prop: 'configType', label: '系统内置', columnType: 'tag', dictList: COMMON_IS.value || [], width: 110 },
  { prop: 'remark', label: '备注', minWidth: 140, showOverflowTooltip: true },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'configName', label: '参数名称', type: 'input' },
  { prop: 'configKey', label: '参数键名', type: 'input' },
  { prop: 'configType', label: '系统内置', type: 'select', dataList: COMMON_IS.value || [] }
])

const rules = {
  configName: [{ required: true, message: '参数名称不能为空', trigger: 'blur' }],
  configKey: [{ required: true, message: '参数键名不能为空', trigger: 'blur' }],
  configValue: [{ required: true, message: '参数值不能为空', trigger: 'blur' }],
  configType: [{ required: true, message: '是否系统内置不能为空', trigger: 'change' }]
}

function emptyForm() {
  return {
    id: undefined,
    configName: '',
    configKey: '',
    configValue: '',
    configType: '1',
    remark: ''
  }
}

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
  if (row && row.id != null) {
    Object.assign(form, {
      id: row.id,
      configName: row.configName ?? '',
      configKey: row.configKey ?? '',
      configValue: row.configValue ?? '',
      configType: row.configType ?? '1',
      remark: row.remark ?? ''
    })
  }
}

async function submitConfig() {
  const payload = { ...form }
  if (form.id) {
    await updateConfig(payload)
  } else {
    await addConfig(payload)
  }
}
</script>
