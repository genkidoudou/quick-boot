<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageSendRecordItem"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="View" @click="openDetail(row)">查看详情</el-button>
      </template>
    </C7JsonTable>

    <el-dialog v-model="detailOpen" title="发送明细" width="640px" append-to-body destroy-on-close>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="发送记录">{{ detail.recordId }}</el-descriptions-item>
        <el-descriptions-item label="渠道">{{ detail.channelName || detail.channelId }}</el-descriptions-item>
        <el-descriptions-item label="对象类型">{{ dictLabel(MSG_SEND_TARGET_TYPE, detail.targetType) }}</el-descriptions-item>
        <el-descriptions-item label="口径">{{ detail.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结果">{{ dictLabel(MSG_SEND_ITEM_STATUS, detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="发送内容">
          <div class="send-record-item__content">{{ detail.content || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="失败原因">{{ detail.detail || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 发送明细：只读列表。渠道显示名称，用户对象的口径显示昵称 / 用户名。
 */
import {computed, reactive, ref} from 'vue'
import {View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {pageSendRecordItem} from '@/api/msg/sendRecordItem'

defineOptions({name: 'MsgSendRecordItem'})

const {MSG_SEND_TARGET_TYPE, MSG_SEND_ITEM_STATUS} = useDict('MSG_SEND_TARGET_TYPE', 'MSG_SEND_ITEM_STATUS')

const detailOpen = ref(false)
const detail = reactive(emptyDetail())

const tableColumns = computed(() => [
  {prop: 'recordId', label: '发送记录', width: 180, showOverflowTooltip: true},
  {prop: 'channelName', label: '渠道', minWidth: 140, showOverflowTooltip: true},
  {prop: 'targetType', label: '对象类型', columnType: 'tag', dictList: MSG_SEND_TARGET_TYPE.value || [], width: 110},
  {prop: 'name', label: '口径', minWidth: 160, showOverflowTooltip: true},
  {prop: 'content', label: '发送内容', minWidth: 200, showOverflowTooltip: true},
  {prop: 'status', label: '结果', columnType: 'tag', dictList: MSG_SEND_ITEM_STATUS.value || [], width: 90},
  {prop: 'detail', label: '失败原因', minWidth: 160, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 120, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'recordId', label: '发送记录', type: 'input'},
  {prop: 'targetType', label: '对象类型', type: 'select', dataList: MSG_SEND_TARGET_TYPE.value || []},
  {prop: 'name', label: '口径', type: 'input'},
  {prop: 'status', label: '结果', type: 'select', dataList: MSG_SEND_ITEM_STATUS.value || []}
])

function emptyDetail() {
  return {
    recordId: '',
    channelId: '',
    channelName: '',
    targetType: '',
    name: '',
    status: '',
    content: '',
    detail: ''
  }
}

function dictLabel(dictList, value) {
  const found = (dictList || []).find((item) => String(item.value) === String(value ?? ''))
  return found ? found.label : (value || '-')
}

function openDetail(row) {
  Object.assign(detail, emptyDetail(), row)
  detailOpen.value = true
}
</script>

<style scoped>
.send-record-item__content {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
