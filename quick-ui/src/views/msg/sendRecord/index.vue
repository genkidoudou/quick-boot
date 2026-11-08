<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageSendRecord"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="View" @click="openItems(row)">查看明细</el-button>
      </template>
    </C7JsonTable>

    <el-dialog v-model="itemOpen" :title="itemTitle" width="860px" append-to-body destroy-on-close>
      <el-table v-loading="itemLoading" :data="itemRows" border>
        <el-table-column prop="channelName" label="渠道" min-width="140" show-overflow-tooltip />
        <el-table-column prop="name" label="口径" min-width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="结果" width="90">
          <template #default="{ row }">{{ dictLabel(MSG_SEND_ITEM_STATUS, row.status) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="发送内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="detail" label="失败原因" min-width="160" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 发送记录：只读列表。用户列是收件人，渠道来自本次明细。
 */
import {computed, ref} from 'vue'
import {View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {pageSendRecord} from '@/api/msg/sendRecord'
import {pageSendRecordItem} from '@/api/msg/sendRecordItem'

defineOptions({name: 'MsgSendRecord'})

const {MSG_SEND_SOURCE, MSG_SEND_RECORD_STATUS, MSG_SEND_ITEM_STATUS} = useDict(
  'MSG_SEND_SOURCE',
  'MSG_SEND_RECORD_STATUS',
  'MSG_SEND_ITEM_STATUS'
)

const tableRef = ref(null)
const itemOpen = ref(false)
const itemLoading = ref(false)
const itemTitle = ref('发送明细')
const itemRows = ref([])

const tableColumns = computed(() => [
  {prop: 'title', label: '标题', minWidth: 160, showOverflowTooltip: true},
  {prop: 'channelName', label: '渠道', minWidth: 140, showOverflowTooltip: true},
  {prop: 'source', label: '来源', columnType: 'tag', dictList: MSG_SEND_SOURCE.value || [], width: 110},
  {prop: 'status', label: '总状态', columnType: 'tag', dictList: MSG_SEND_RECORD_STATUS.value || [], width: 110},
  {prop: 'receiverNames', label: '用户', minWidth: 180, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 120, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'title', label: '标题', type: 'input'},
  {prop: 'source', label: '来源', type: 'select', dataList: MSG_SEND_SOURCE.value || []},
  {prop: 'status', label: '总状态', type: 'select', dataList: MSG_SEND_RECORD_STATUS.value || []}
])

function dictLabel(dictList, value) {
  const found = (dictList || []).find((item) => String(item.value) === String(value ?? ''))
  return found ? found.label : (value ?? '')
}

async function openItems(row) {
  itemTitle.value = `发送明细：${row.title || row.id}`
  itemOpen.value = true
  itemLoading.value = true
  itemRows.value = []
  try {
    const res = await pageSendRecordItem({current: 1, size: 100, param: {recordId: row.id}})
    itemRows.value = res?.data?.records || []
  } finally {
    itemLoading.value = false
  }
}
</script>
