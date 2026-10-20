<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageJobLog"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
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
 * 定时任务日志：列表 / 详情 / 删除（对齐后端 SysJobLog，无新增和修改）。
 */
import {computed, ref} from 'vue'
import {Delete, View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {getJobLog, pageJobLog, removeJobLog} from '@/api/job/jobLog'

defineOptions({name: 'SysJobLog'})

const {JOB_STATUS} = useDict('JOB_STATUS')

const tableRef = ref(null)
const detailOpen = ref(false)
const detail = ref(null)

const tableColumns = computed(() => [
  {prop: 'jobId', label: '任务 ID', minWidth: 180, showOverflowTooltip: true},
  {prop: 'jobParam', label: '任务参数', minWidth: 160, showOverflowTooltip: true},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: JOB_STATUS.value || [], width: 100},
  {prop: 'startTime', label: '开始时间', width: 170},
  {prop: 'endTime', label: '结束时间', width: 170},
  {prop: 'costMs', label: '耗时', width: 110, formatter: (row) => formatCost(row.costMs)},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 140, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'jobId', label: '任务 ID', type: 'input'},
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
