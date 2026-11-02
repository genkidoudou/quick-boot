<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageSendRecordItem"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeSendRecordItem"
      :add-button-permi="['msg:sendRecordItem:add']"
      :edit-button-permi="['msg:sendRecordItem:edit']"
      :delete-button-permi="['msg:sendRecordItem:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['msg:sendRecordItem:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['msg:sendRecordItem:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除发送明细「${row.id}」？` })"
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
      :submit="submitSendRecordItem"
      :on-success="() => tableRef?.refreshData()"
      width="720px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="发送记录" prop="recordId">
          <el-input v-model="form.recordId" placeholder="发送记录 ID" />
        </el-form-item>
        <el-form-item label="渠道" prop="channelId">
          <el-input v-model="form.channelId" placeholder="渠道 ID" />
        </el-form-item>
        <el-form-item label="对象类型" prop="targetType">
          <c7-radio :data-list="MSG_SEND_TARGET_TYPE" v-model="form.targetType" />
        </el-form-item>
        <el-form-item v-if="form.targetType === '1'" label="用户" prop="userId">
          <el-input v-model="form.userId" placeholder="用户 ID" />
        </el-form-item>
        <el-form-item v-if="form.targetType === '2'" label="部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="部门 ID" />
        </el-form-item>
        <el-form-item v-if="form.targetType === '4'" label="群组" prop="groupId">
          <el-input v-model="form.groupId" placeholder="群组 ID" />
        </el-form-item>
        <template v-if="form.targetType === '3'">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="外部人名称" maxlength="64" />
          </el-form-item>
          <el-form-item label="手机" prop="phone">
            <el-input v-model="form.phone" placeholder="手机或邮箱至少一项" maxlength="20" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="手机或邮箱至少一项" maxlength="128" />
          </el-form-item>
        </template>
        <el-form-item label="发送内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="该对象在该渠道实际发出的内容" />
        </el-form-item>
        <el-form-item label="结果" prop="status">
          <c7-radio :data-list="MSG_SEND_ITEM_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="失败原因" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="2" maxlength="512" placeholder="失败时填写" />
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
 * 发送明细：列表 / 新增 / 修改 / 删除（对齐后端 MsgSendRecordItem）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {addSendRecordItem, pageSendRecordItem, removeSendRecordItem, updateSendRecordItem} from '@/api/msg/sendRecordItem'

defineOptions({name: 'MsgSendRecordItem'})

const {MSG_SEND_TARGET_TYPE, MSG_SEND_ITEM_STATUS} = useDict('MSG_SEND_TARGET_TYPE', 'MSG_SEND_ITEM_STATUS')

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改发送明细' : '新增发送明细'))

const tableColumns = computed(() => [
  {prop: 'recordId', label: '发送记录', width: 150, showOverflowTooltip: true},
  {prop: 'channelId', label: '渠道', width: 140, showOverflowTooltip: true},
  {prop: 'targetType', label: '对象类型', columnType: 'tag', dictList: MSG_SEND_TARGET_TYPE.value || [], width: 110},
  {prop: 'name', label: '名称', minWidth: 120, showOverflowTooltip: true},
  {prop: 'status', label: '结果', columnType: 'tag', dictList: MSG_SEND_ITEM_STATUS.value || [], width: 90},
  {prop: 'detail', label: '失败原因', minWidth: 160, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'recordId', label: '发送记录', type: 'input'},
  {prop: 'channelId', label: '渠道', type: 'input'},
  {prop: 'targetType', label: '对象类型', type: 'select', dataList: MSG_SEND_TARGET_TYPE.value || []},
  {prop: 'name', label: '名称', type: 'input'},
  {prop: 'status', label: '结果', type: 'select', dataList: MSG_SEND_ITEM_STATUS.value || []}
])

const rules = computed(() => ({
  recordId: [{required: true, message: '发送记录不能为空', trigger: 'blur'}],
  channelId: [{required: true, message: '渠道不能为空', trigger: 'blur'}],
  targetType: [{required: true, message: '对象类型不能为空', trigger: 'change'}],
  content: [{required: true, message: '发送内容不能为空', trigger: 'blur'}],
  status: [{required: true, message: '结果不能为空', trigger: 'change'}]
}))

function emptyForm() {
  return {
    id: undefined,
    recordId: '',
    channelId: '',
    targetType: '1',
    groupId: '',
    userId: '',
    deptId: '',
    name: '',
    phone: '',
    email: '',
    content: '',
    status: '1',
    detail: '',
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
      recordId: row.recordId ?? '',
      channelId: row.channelId ?? '',
      targetType: row.targetType ?? '1',
      groupId: row.groupId ?? '',
      userId: row.userId ?? '',
      deptId: row.deptId ?? '',
      name: row.name ?? '',
      phone: row.phone ?? '',
      email: row.email ?? '',
      content: row.content ?? '',
      status: row.status ?? '1',
      detail: row.detail ?? '',
      remark: row.remark ?? ''
    })
  }
}

async function submitSendRecordItem() {
  const payload = {
    id: form.id,
    recordId: blankToNull(form.recordId),
    channelId: blankToNull(form.channelId),
    targetType: form.targetType,
    groupId: form.targetType === '4' ? blankToNull(form.groupId) : null,
    userId: form.targetType === '1' ? blankToNull(form.userId) : null,
    deptId: form.targetType === '2' ? blankToNull(form.deptId) : null,
    name: form.targetType === '3' ? form.name : '',
    phone: form.targetType === '3' ? form.phone : '',
    email: form.targetType === '3' ? form.email : '',
    content: form.content,
    status: form.status,
    detail: form.detail,
    remark: form.remark
  }
  if (payload.id) {
    await updateSendRecordItem(payload)
  } else {
    await addSendRecordItem(payload)
  }
}
</script>
