<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageInbox"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeInbox"
      :add-button-permi="['msg:inbox:add']"
      :edit-button-permi="['msg:inbox:edit']"
      :delete-button-permi="['msg:inbox:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['msg:inbox:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['msg:inbox:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除站内信「${row.title || row.id}」？` })"
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
      :submit="submitInbox"
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="收件用户" prop="userId">
          <el-input v-model="form.userId" placeholder="用户 ID" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="255" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="已读" prop="readFlag">
          <c7-radio :data-list="READ_FLAG" v-model="form.readFlag" />
        </el-form-item>
        <el-form-item label="渠道 ID" prop="channelId">
          <el-input v-model="form.channelId" placeholder="来源渠道，可空" />
        </el-form-item>
        <el-form-item label="记录 ID" prop="recordId">
          <el-input v-model="form.recordId" placeholder="发送记录，可空" />
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
 * 站内信：列表 / 新增 / 修改 / 删除（对齐后端 MsgInbox）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {addInbox, pageInbox, removeInbox, updateInbox} from '@/api/msg/inbox'

defineOptions({name: 'MsgInbox'})

const READ_FLAG = ref([
  {label: '未读', value: '0'},
  {label: '已读', value: '1'}
])

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改站内信' : '新增站内信'))

const tableColumns = computed(() => [
  {prop: 'userId', label: '收件用户', width: 140, showOverflowTooltip: true},
  {prop: 'title', label: '标题', minWidth: 160, showOverflowTooltip: true},
  {prop: 'readFlag', label: '已读', columnType: 'tag', dictList: READ_FLAG.value, width: 90},
  {prop: 'channelId', label: '渠道', width: 140, showOverflowTooltip: true},
  {prop: 'recordId', label: '发送记录', width: 140, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'title', label: '标题', type: 'input'},
  {prop: 'readFlag', label: '已读', type: 'select', dataList: READ_FLAG.value},
  {prop: 'channelId', label: '渠道', type: 'input'},
  {prop: 'recordId', label: '发送记录', type: 'input'}
])

const rules = computed(() => ({
  userId: [{required: true, message: '收件用户不能为空', trigger: 'blur'}],
  title: [{required: true, message: '标题不能为空', trigger: 'blur'}],
  content: [{required: true, message: '内容不能为空', trigger: 'blur'}]
}))

function emptyForm() {
  return {
    id: undefined,
    userId: '',
    title: '',
    content: '',
    readFlag: '0',
    channelId: '',
    recordId: '',
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
      userId: row.userId ?? '',
      title: row.title ?? '',
      content: row.content ?? '',
      readFlag: row.readFlag ?? '0',
      channelId: row.channelId ?? '',
      recordId: row.recordId ?? '',
      remark: row.remark ?? ''
    })
  }
}

async function submitInbox() {
  const payload = {
    id: form.id,
    userId: blankToNull(form.userId),
    title: form.title,
    content: form.content,
    readFlag: form.readFlag,
    channelId: blankToNull(form.channelId),
    recordId: blankToNull(form.recordId),
    remark: form.remark
  }
  if (payload.id) {
    await updateInbox(payload)
  } else {
    await addInbox(payload)
  }
}
</script>
