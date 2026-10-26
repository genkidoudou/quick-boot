<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageJobLog"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      :default-search-param="defaultSearchParam"
      row-key="id"
      :delete-function="removeJobLog"
      :delete-button-permi="['job:jobLog:remove']"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="View"
          v-hasPermi="['job:jobLog:query']"
          @click="openDetail(row)"
        >
          详情
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Document"
          v-hasPermi="['job:jobLog:cat']"
          @click="openCat(row)"
        >
          执行日志
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['job:jobLog:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除任务日志「${row.id}」？` })"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <el-dialog v-model="detailOpen" title="任务日志详情" width="640px" append-to-body>
      <el-descriptions v-if="detail" :column="1" border>
        <el-descriptions-item label="任务 ID">{{ detail.jobId }}</el-descriptions-item>
        <el-descriptions-item label="触发来源">{{ detail.triggerType || '—' }}</el-descriptions-item>
        <el-descriptions-item label="实例 ID">{{ detail.instanceId || '—' }}</el-descriptions-item>
        <el-descriptions-item label="续约时间">{{ formatDateTime(detail.heartbeatTime) }}</el-descriptions-item>
        <el-descriptions-item label="任务参数">{{ detail.jobParam || '—' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detail.startTime || '—' }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detail.endTime || '—' }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ formatCost(detail.costMs) }}</el-descriptions-item>
        <el-descriptions-item label="返回结果">
          <pre class="log-text">{{ detail.result || '—' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误异常">
          <pre class="log-text">{{ detail.errorMsg || '—' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button
          type="primary"
          v-hasPermi="['job:jobLog:cat']"
          @click="openCatFromDetail"
        >
          查看执行日志
        </el-button>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="catOpen"
      :title="catTitle"
      width="860px"
      append-to-body
      destroy-on-close
      class="job-log-cat-dialog"
      @closed="stopCatPoll"
    >
      <div class="cat-meta">
        <span>任务 ID：{{ catMeta.jobId || '—' }}</span>
        <span>状态：{{ statusLabel(catMeta.status) }}</span>
        <span v-if="catPolling" class="cat-live">拉取中…</span>
        <span v-else-if="catFinished" class="cat-done">已结束</span>
      </div>
      <el-alert
        v-if="catMissing"
        type="warning"
        :closable="false"
        show-icon
        title="日志文件暂不可用（可能跨节点、尚未写出，或已被清理）"
        class="cat-alert"
      />
      <div
        ref="catBoxRef"
        class="cat-box"
        @scroll="onCatScroll"
      >
        <pre class="cat-pre">{{ catText || '暂无过程日志' }}</pre>
      </div>
      <template #footer>
        <el-button @click="reloadCat" :disabled="!catLogId">重新加载</el-button>
        <el-button type="primary" @click="catOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 定时任务日志：列表 / 详情 / 执行过程日志（增量 cat）/ 删除。
 * 支持从任务页带 ?jobId= 跳转，默认按该任务过滤关联日志。
 */
import {computed, nextTick, onBeforeUnmount, ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {Delete, Document, View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {catJobLog, getJobLog, pageJobLog, removeJobLog} from '@/api/job/jobLog'

defineOptions({name: 'SysJobLog'})

const POLL_MS = 1000

const route = useRoute()
const {JOB_STATUS} = useDict('JOB_STATUS')

const tableRef = ref(null)
const detailOpen = ref(false)
const detail = ref(null)

const catOpen = ref(false)
const catLogId = ref(null)
const catMeta = ref({})
const catText = ref('')
const catFromLine = ref(1)
const catMissing = ref(false)
const catPolling = ref(false)
const catFinished = ref(false)
const catBoxRef = ref(null)
/** 用户上滚后暂停自动贴底；有新内容且接近底部时再恢复 */
const catStickBottom = ref(true)
let catTimer = null
let catInflight = false

const catTitle = computed(() =>
  catLogId.value ? `执行日志 #${catLogId.value}` : '执行日志'
)

/** 任务页「日志」跳转时携带的 jobId */
const jobIdFromQuery = computed(() => {
  const raw = route.query.jobId
  if (raw == null || raw === '') {
    return undefined
  }
  return String(Array.isArray(raw) ? raw[0] : raw)
})

const defaultSearchParam = computed(() => {
  const param = {}
  if (jobIdFromQuery.value) {
    param.jobId = jobIdFromQuery.value
  }
  return param
})

// 已打开日志页时再次从任务跳转（含 keep-alive）：同步搜索条件并刷新
watch(jobIdFromQuery, async (jobId) => {
  await nextTick()
  const search = tableRef.value?.searchParam
  if (!search) {
    return
  }
  if (jobId) {
    search.jobId = jobId
  } else {
    delete search.jobId
  }
  tableRef.value?.refreshData?.()
})

const tableColumns = computed(() => [
  {prop: 'jobId', label: '任务 ID', minWidth: 160, showOverflowTooltip: true},
  {prop: 'triggerType', label: '触发来源', width: 100},
  {prop: 'instanceId', label: '实例 ID', minWidth: 180, showOverflowTooltip: true},
  {
    prop: 'heartbeatTime',
    label: '续约时间',
    width: 170,
    formatter: (row) => formatDateTime(row.heartbeatTime)
  },
  {prop: 'jobParam', label: '任务参数', minWidth: 140, showOverflowTooltip: true},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: JOB_STATUS.value || [], width: 100},
  {prop: 'startTime', label: '开始时间', width: 170},
  {prop: 'endTime', label: '结束时间', width: 170},
  {prop: 'costMs', label: '耗时', width: 110, formatter: (row) => formatCost(row.costMs)},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 220, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'jobId', label: '任务 ID', type: 'input'},
  {prop: 'instanceId', label: '实例 ID', type: 'input'},
  {prop: 'status', label: '状态', type: 'select', dataList: JOB_STATUS.value || []},
  {prop: 'startDate', label: '开始时间', type: 'date'}
])

/**
 * @param {number|null|undefined} costMs
 * @returns {string}
 */
function formatCost(costMs) {
  if (costMs == null) {
    return '—'
  }
  return `${costMs} ms`
}

/** @param {string|null|undefined} value */
function formatDateTime(value) {
  if (!value) {
    return '—'
  }
  return String(value).replace('T', ' ')
}

/**
 * @param {string|null|undefined} status
 * @returns {string}
 */
function statusLabel(status) {
  const item = (JOB_STATUS.value || []).find((row) => row.value === status)
  return item?.label || status || '—'
}

/** @param {Record<string, any>} row */
async function openDetail(row) {
  const res = await getJobLog(row.id)
  detail.value = res?.data ?? null
  detailOpen.value = true
}

function openCatFromDetail() {
  if (!detail.value?.id) {
    return
  }
  detailOpen.value = false
  openCat(detail.value)
}

/**
 * 打开过程日志并开始轮询。
 * @param {Record<string, any>} row
 */
async function openCat(row) {
  stopCatPoll()
  catLogId.value = row.id
  catMeta.value = {
    jobId: row.jobId,
    status: row.status
  }
  catText.value = ''
  catFromLine.value = 1
  catMissing.value = false
  catFinished.value = false
  catStickBottom.value = true
  catOpen.value = true
  await nextTick()
  await pullCatOnce()
  startCatPoll()
}

function startCatPoll() {
  stopCatPoll()
  catPolling.value = true
  catTimer = setInterval(() => {
    pullCatOnce()
  }, POLL_MS)
}

function stopCatPoll() {
  if (catTimer != null) {
    clearInterval(catTimer)
    catTimer = null
  }
  catPolling.value = false
  catInflight = false
}

async function reloadCat() {
  if (!catLogId.value) {
    return
  }
  catText.value = ''
  catFromLine.value = 1
  catMissing.value = false
  catFinished.value = false
  catStickBottom.value = true
  stopCatPoll()
  await pullCatOnce()
  startCatPoll()
}

async function pullCatOnce() {
  if (!catLogId.value || catInflight) {
    return
  }
  catInflight = true
  try {
    const res = await catJobLog(catLogId.value, catFromLine.value)
    const data = res?.data
    if (!data) {
      return
    }
    catMissing.value = !!data.missing
    if (data.content) {
      catText.value += data.content
      // toLine 为本次最后一行；无新内容时后端约定 toLine = fromLine - 1
      const next = Number(data.toLine) + 1
      if (Number.isFinite(next) && next > catFromLine.value) {
        catFromLine.value = next
      }
      await nextTick()
      scrollCatIfNeeded()
    }
    if (data.end) {
      catFinished.value = true
      stopCatPoll()
      // 结束后再拉一次详情，刷新状态标签
      try {
        const detailRes = await getJobLog(catLogId.value)
        if (detailRes?.data) {
          catMeta.value = {
            jobId: detailRes.data.jobId,
            status: detailRes.data.status
          }
        }
      } catch {
        // 忽略
      }
    }
  } catch {
    // 单次失败不打断轮询；关闭弹窗时会 stop
  } finally {
    catInflight = false
  }
}

function onCatScroll() {
  const el = catBoxRef.value
  if (!el) {
    return
  }
  const gap = el.scrollHeight - el.scrollTop - el.clientHeight
  catStickBottom.value = gap < 48
}

function scrollCatIfNeeded() {
  if (!catStickBottom.value) {
    return
  }
  const el = catBoxRef.value
  if (!el) {
    return
  }
  el.scrollTop = el.scrollHeight
}

onBeforeUnmount(() => {
  stopCatPoll()
})
</script>

<style scoped>
.log-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
}

.cat-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.cat-live {
  color: var(--el-color-primary);
}

.cat-done {
  color: var(--el-color-success);
}

.cat-alert {
  margin-bottom: 10px;
}

.cat-box {
  height: 420px;
  overflow: auto;
  border-radius: 4px;
  background: #1e1e1e;
  border: 1px solid var(--el-border-color);
}

.cat-pre {
  margin: 0;
  padding: 12px 14px;
  min-height: 100%;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.55;
  color: #d4d4d4;
}
</style>
