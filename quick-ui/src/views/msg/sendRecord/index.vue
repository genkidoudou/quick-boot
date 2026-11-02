<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageSendRecord"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeSendRecord"
      :add-button-permi="['msg:sendRecord:add']"
      :edit-button-permi="['msg:sendRecord:edit']"
      :delete-button-permi="['msg:sendRecord:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['msg:sendRecord:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['msg:sendRecord:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除发送记录「${row.title || row.id}」？` })"
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
      :submit="submitSendRecord"
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="255" />
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="提交时的原文" />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <c7-radio :data-list="MSG_SEND_SOURCE" v-model="form.source" />
        </el-form-item>
        <el-form-item label="总状态" prop="status">
          <c7-radio :data-list="MSG_SEND_RECORD_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="API Key" prop="apiKeyId">
          <el-input v-model="form.apiKeyId" placeholder="可空" />
        </el-form-item>
        <el-form-item label="用户" prop="userId">
          <el-input v-model="form.userId" placeholder="操作或归属用户，可空" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="2" maxlength="255" />
        </el-form-item>
      </el-form>
    </C7FormDialog>
  </div>
</template>

<script setup>
/**
 * 发送记录：列表 / 新增 / 修改 / 删除（对齐后端 MsgSendRecord）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {addSendRecord, pageSendRecord, removeSendRecord, updateSendRecord} from '@/api/msg/sendRecord'

defineOptions({name: 'MsgSendRecord'})

const {MSG_SEND_SOURCE, MSG_SEND_RECORD_STATUS} = useDict('MSG_SEND_SOURCE', 'MSG_SEND_RECORD_STATUS')

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改发送记录' : '新增发送记录'))

const tableColumns = computed(() => [
  {prop: 'title', label: '标题', minWidth: 160, showOverflowTooltip: true},
  {prop: 'source', label: '来源', columnType: 'tag', dictList: MSG_SEND_SOURCE.value || [], width: 110},
  {prop: 'status', label: '总状态', columnType: 'tag', dictList: MSG_SEND_RECORD_STATUS.value || [], width: 110},
  {prop: 'userId', label: '用户', width: 140, showOverflowTooltip: true},
  {prop: 'apiKeyId', label: 'API Key', width: 140, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'title', label: '标题', type: 'input'},
  {prop: 'source', label: '来源', type: 'select', dataList: MSG_SEND_SOURCE.value || []},
  {prop: 'status', label: '总状态', type: 'select', dataList: MSG_SEND_RECORD_STATUS.value || []},
  {prop: 'userId', label: '用户', type: 'input'},
  {prop: 'apiKeyId', label: 'API Key', type: 'input'}
])

const rules = computed(() => ({
  title: [{required: true, message: '标题不能为空', trigger: 'blur'}],
  content: [{required: true, message: '正文不能为空', trigger: 'blur'}],
  status: [{required: true, message: '总状态不能为空', trigger: 'change'}]
}))

function emptyForm() {
  return {
    id: undefined,
    title: '',
    content: '',
    source: '1',
    status: '1',
    apiKeyId: '',
    userId: '',
    remark: ''
  }
}

function blankToNull(value) {
  return value === '' || value == null ? null : value
}

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
  if (row && row.id != null) {
    Object.assign(form, {
      id: row.id,
      title: row.title ?? '',
      content: row.content ?? '',
      source: row.source ?? '1',
      status: row.status ?? '1',
      apiKeyId: row.apiKeyId ?? '',
      userId: row.userId ?? '',
      remark: row.remark ?? ''
    })
  }
}

async function submitSendRecord() {
  const payload = {
    id: form.id,
    title: form.title,
    content: form.content,
    source: form.source,
    status: form.status,
    apiKeyId: blankToNull(form.apiKeyId),
    userId: blankToNull(form.userId),
    remark: form.remark
  }
  if (payload.id) {
    await updateSendRecord(payload)
  } else {
    await addSendRecord(payload)
  }
}
</script>
