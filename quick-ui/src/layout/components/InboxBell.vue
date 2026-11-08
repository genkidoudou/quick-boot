<template>
  <div class="inbox-bell right-menu-item hover-effect">
    <el-popover
      v-model:visible="open"
      placement="bottom-end"
      :width="380"
      trigger="click"
      :show-arrow="false"
      :offset="8"
      popper-class="inbox-bell-popper"
      @show="loadRecent"
    >
      <template #reference>
        <button type="button" class="inbox-bell__trigger" aria-label="站内信">
          <el-badge
            :value="inboxStore.unreadCount"
            :hidden="inboxStore.unreadCount === 0"
            :max="99"
            class="inbox-bell__badge"
          >
            <span class="inbox-bell__icon-wrap" :class="{ 'has-unread': inboxStore.unreadCount > 0 }">
              <el-icon :size="18"><Bell /></el-icon>
            </span>
          </el-badge>
        </button>
      </template>

      <div class="inbox-bell__panel">
        <div class="inbox-bell__head">
          <div class="inbox-bell__head-main">
            <span class="inbox-bell__head-mark">
              <el-icon :size="16"><Bell /></el-icon>
            </span>
            <div>
              <div class="inbox-bell__head-title">消息中心</div>
              <div class="inbox-bell__head-sub">
                {{ inboxStore.unreadCount > 0 ? `有 ${inboxStore.unreadCount} 条未读` : '暂无未读消息' }}
              </div>
            </div>
          </div>
        </div>

        <div v-if="loading" class="inbox-bell__state">
          <el-icon class="is-loading" :size="22"><Loading /></el-icon>
          <span>正在获取消息</span>
        </div>
        <div v-else-if="loadFailed" class="inbox-bell__state">
          <div class="inbox-bell__state-icon is-warn">
            <el-icon :size="20"><WarningFilled /></el-icon>
          </div>
          <span>消息加载失败</span>
          <button type="button" class="inbox-bell__retry" @click="loadRecent">重试</button>
        </div>
        <div v-else-if="!rows.length" class="inbox-bell__state">
          <div class="inbox-bell__state-icon">
            <el-icon :size="20"><ChatDotRound /></el-icon>
          </div>
          <span>暂时没有未读消息</span>
          <span class="inbox-bell__state-tip">新消息到来时会显示在这里</span>
        </div>
        <div v-else class="inbox-bell__list">
          <button
            v-for="row in rows"
            :key="row.id"
            type="button"
            class="inbox-bell__item"
            @click="openMessage(row)"
          >
            <span class="inbox-bell__avatar">
              <el-icon :size="16"><ChatDotRound /></el-icon>
            </span>
            <span class="inbox-bell__body">
              <span class="inbox-bell__row">
                <span class="inbox-bell__title">{{ row.title || '无标题' }}</span>
                <span class="inbox-bell__time">{{ relativeTime(row.createTime) }}</span>
              </span>
              <span class="inbox-bell__preview">{{ row.content || '暂无正文' }}</span>
            </span>
          </button>
        </div>

        <div class="inbox-bell__footer">
          <button type="button" class="inbox-bell__all" @click="goAll">
            <span>查看全部消息</span>
            <el-icon :size="14"><ArrowRight /></el-icon>
          </button>
        </div>
      </div>
    </el-popover>

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
          <div class="inbox-msg-dialog__heading">
            <div class="inbox-msg-dialog__title">{{ detail.title || '站内信' }}</div>
            <div class="inbox-msg-dialog__meta">{{ detail.createTime }}</div>
          </div>
        </div>
      </template>
      <div class="inbox-msg-dialog__content">{{ detail.content || '-' }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 顶栏站内信：未读角标、最近未读、点开即已读。
 */
import {onBeforeUnmount, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ArrowRight, Bell, ChatDotRound, Loading, WarningFilled} from '@element-plus/icons-vue'
import {pageMineInbox} from '@/api/msg/inbox'
import useInboxStore from '@/store/modules/inbox'

const router = useRouter()
const inboxStore = useInboxStore()

const open = ref(false)
const loading = ref(false)
const loadFailed = ref(false)
const rows = ref([])
const detailOpen = ref(false)
const detail = ref({})
let timer

onMounted(async () => {
  try {
    await inboxStore.refreshCount()
    timer = setInterval(() => {
      inboxStore.refreshCount().catch(() => {})
    }, 60000)
  } catch {
    // 后端还没加载新接口时不轮询，避免反复提示
  }
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
})

function relativeTime(value) {
  if (!value) {
    return ''
  }
  const date = new Date(String(value).replace(/-/g, '/'))
  if (Number.isNaN(date.getTime())) {
    return String(value).slice(0, 16)
  }
  const diff = Date.now() - date.getTime()
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  if (diff < minute) {
    return '刚刚'
  }
  if (diff < hour) {
    return `${Math.floor(diff / minute)} 分钟前`
  }
  if (diff < day) {
    return `${Math.floor(diff / hour)} 小时前`
  }
  if (diff < 7 * day) {
    return `${Math.floor(diff / day)} 天前`
  }
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

async function loadRecent() {
  loading.value = true
  loadFailed.value = false
  try {
    const res = await pageMineInbox({current: 1, size: 5, param: {readFlag: '0'}})
    rows.value = res?.data?.records || []
    await inboxStore.refreshCount()
    if (!rows.value.length && inboxStore.unreadCount > 0) {
      const again = await pageMineInbox({current: 1, size: 5, param: {readFlag: '0'}})
      rows.value = again?.data?.records || []
    }
  } catch {
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

async function openMessage(row) {
  detail.value = row
  detailOpen.value = true
  open.value = false
  if (row.readFlag === '0') {
    await inboxStore.markRead(row.id)
    rows.value = rows.value.filter((item) => item.id !== row.id)
  }
}

function goAll() {
  open.value = false
  router.push('/user/messages')
}
</script>

<style scoped>
.inbox-bell {
  display: inline-flex;
  align-items: center;
  height: 100%;
  padding: 0;
}

.inbox-bell__trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0 10px;
  border: 0;
  background: transparent;
  color: inherit;
  cursor: pointer;
}

.inbox-bell__icon-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 10px;
  transition: background 0.2s ease, color 0.2s ease;
}

.inbox-bell__trigger:hover .inbox-bell__icon-wrap,
.inbox-bell__icon-wrap.has-unread {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.inbox-bell__badge :deep(.el-badge__content) {
  border: 2px solid #fff;
  height: 16px;
  min-width: 16px;
  padding: 0 4px;
  line-height: 12px;
  font-size: 11px;
  font-weight: 700;
  transform: translate(8px, -6px);
}

.inbox-bell__panel {
  margin: -12px;
}

.inbox-bell__head {
  padding: 16px 16px 14px;
  background:
    radial-gradient(120px 60px at 0% 0%, color-mix(in srgb, var(--el-color-primary) 18%, transparent), transparent),
    linear-gradient(180deg, #f8fafc 0%, #fff 100%);
  border-bottom: 1px solid #eef1f5;
}

.inbox-bell__head-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.inbox-bell__head-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  color: #fff;
  background: linear-gradient(135deg, var(--el-color-primary), color-mix(in srgb, var(--el-color-primary) 70%, #1d4ed8));
  box-shadow: 0 8px 16px color-mix(in srgb, var(--el-color-primary) 28%, transparent);
}

.inbox-bell__head-title {
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.3;
}

.inbox-bell__head-sub {
  margin-top: 2px;
  font-size: 12px;
  color: #8b93a1;
}

.inbox-bell__list {
  max-height: 360px;
  overflow: auto;
  padding: 10px;
  background: #f7f8fb;
}

.inbox-bell__item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  text-align: left;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.inbox-bell__item:last-child {
  margin-bottom: 0;
}

.inbox-bell__item:hover {
  border-color: color-mix(in srgb, var(--el-color-primary) 28%, #fff);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  transform: translateY(-1px);
}

.inbox-bell__avatar {
  position: relative;
  display: inline-flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.inbox-bell__avatar::after {
  content: '';
  position: absolute;
  top: -2px;
  right: -2px;
  width: 8px;
  height: 8px;
  border: 2px solid #fff;
  border-radius: 50%;
  background: #f56c6c;
}

.inbox-bell__body {
  min-width: 0;
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}

.inbox-bell__row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.inbox-bell__title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 650;
  color: #1f2937;
  line-height: 1.4;
}

.inbox-bell__time {
  flex-shrink: 0;
  font-size: 12px;
  color: #9aa3af;
}

.inbox-bell__preview {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.5;
}

.inbox-bell__state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 180px;
  padding: 28px 16px;
  background: #f7f8fb;
  color: #6b7280;
  font-size: 13px;
}

.inbox-bell__state-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin-bottom: 4px;
  border-radius: 16px;
  color: var(--el-color-primary);
  background: #fff;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.06);
}

.inbox-bell__state-icon.is-warn {
  color: #e6a23c;
}

.inbox-bell__state-tip {
  font-size: 12px;
  color: #9aa3af;
}

.inbox-bell__retry {
  margin-top: 4px;
  border: 0;
  border-radius: 999px;
  padding: 6px 14px;
  background: #fff;
  color: var(--el-color-primary);
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
  cursor: pointer;
}

.inbox-bell__footer {
  padding: 10px 12px 12px;
  background: #fff;
  border-top: 1px solid #eef1f5;
}

.inbox-bell__all {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 100%;
  height: 38px;
  border: 0;
  border-radius: 10px;
  background: linear-gradient(180deg, #fff 0%, #f8fafc 100%);
  box-shadow: inset 0 0 0 1px #e8ecf2;
  color: #374151;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.inbox-bell__all:hover {
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--el-color-primary) 25%, #fff);
}

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
  margin-top: 2px;
  font-size: 12px;
  color: #9aa3af;
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
.inbox-bell-popper.el-popover {
  padding: 0 !important;
  border: 1px solid #e8ecf2 !important;
  border-radius: 16px !important;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14) !important;
  overflow: hidden;
}

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
