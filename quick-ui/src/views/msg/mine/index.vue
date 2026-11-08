<template>
  <div class="app-container mine-message">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageMineInbox"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="View" @click="openDetail(row)">查看</el-button>
      </template>
    </C7JsonTable>

    <el-dialog
      v-model="detailOpen"
      width="560px"
      append-to-body
      align-center
      class="inbox-msg-dialog"
      destroy-on-close
    >
      <template #header>
        <div class="inbox-msg-dialog__header">
          <div class="inbox-msg-dialog__badge">
            <el-icon :size="18"><ChatDotRound /></el-icon>
          </div>
          <div>
            <div class="inbox-msg-dialog__title">{{ detail.title || '站内信' }}</div>
            <div class="inbox-msg-dialog__meta">
              <span class="mine-message__flag" :class="detail.readFlag === '1' ? 'is-read' : 'is-unread'">
                {{ readText(detail.readFlag) }}
              </span>
              <span>{{ detail.createTime }}</span>
            </div>
          </div>
        </div>
      </template>
      <div class="inbox-msg-dialog__content">{{ detail.content || '-' }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 我的消息：当前登录人的站内信。查看未读时标为已读。
 */
import {computed, ref} from 'vue'
import {ChatDotRound, View} from '@element-plus/icons-vue'
import {pageMineInbox} from '@/api/msg/inbox'
import useInboxStore from '@/store/modules/inbox'

defineOptions({name: 'MyMessages'})

const READ_FLAG = [
  {label: '未读', value: '0'},
  {label: '已读', value: '1'}
]

const inboxStore = useInboxStore()
const tableRef = ref(null)
const detailOpen = ref(false)
const detail = ref({})

const tableColumns = computed(() => [
  {prop: 'title', label: '标题', minWidth: 180, showOverflowTooltip: true},
  {prop: 'readFlag', label: '状态', columnType: 'tag', dictList: READ_FLAG, width: 90},
  {prop: 'createTime', label: '时间', width: 170},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 100, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'title', label: '标题', type: 'input'},
  {prop: 'readFlag', label: '状态', type: 'select', dataList: READ_FLAG}
])

function readText(flag) {
  return flag === '1' ? '已读' : '未读'
}

async function openDetail(row) {
  detail.value = row
  detailOpen.value = true
  if (row.readFlag !== '0') {
    return
  }
  await inboxStore.markRead(row.id)
  row.readFlag = '1'
  tableRef.value?.refreshData()
}
</script>

<style scoped>
.inbox-msg-dialog__header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-right: 24px;
}

.inbox-msg-dialog__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  color: #fff;
  background: linear-gradient(135deg, var(--el-color-primary), color-mix(in srgb, var(--el-color-primary) 70%, #1d4ed8));
}

.inbox-msg-dialog__title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.35;
}

.inbox-msg-dialog__meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  font-size: 12px;
  color: #9aa3af;
}

.mine-message__flag {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 20px;
}

.mine-message__flag.is-unread {
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.mine-message__flag.is-read {
  color: #909399;
  background: #f2f3f5;
}

.inbox-msg-dialog__content {
  min-height: 120px;
  padding: 16px 18px;
  border-radius: 12px;
  background: #f7f8fb;
  color: #374151;
  font-size: 14px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>

<style>
.inbox-msg-dialog.el-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.inbox-msg-dialog .el-dialog__header {
  margin-right: 0;
  padding: 18px 20px 8px;
}

.inbox-msg-dialog .el-dialog__body {
  padding: 8px 20px 20px;
}
</style>
