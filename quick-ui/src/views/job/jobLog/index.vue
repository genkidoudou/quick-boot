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
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 定时任务日志：列表 / 详情 / 删除。
 * 支持从任务页带 ?jobId= 跳转，默认按该任务过滤关联日志。
 */
import {computed, nextTick, ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {Delete, View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {getJobLog, pageJobLog, removeJobLog} from '@/api/job/jobLog'

defineOptions({name: 'SysJobLog'})

const route = useRoute()
const {JOB_STATUS} = useDict('JOB_STATUS')

const tableRef = ref(null)
const detailOpen = ref(false)
const detail = ref(null)

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
  {prop: 'actions', label: '操作', columnType: 'slot', width: 140, fixed: 'right', align: 'center'}
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
</script>

<style scoped>
.log-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
}
</style>
