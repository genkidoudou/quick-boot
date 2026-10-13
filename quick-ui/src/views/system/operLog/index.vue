<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageOperLog"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="View" v-hasPermi="['system:operLog:query']" @click="openDetail(row)">
          详情
        </el-button>
      </template>
    </C7JsonTable>

    <C7Dialog v-model="open" title="操作日志详情" width="800px" mobile-mode="drawer">
      <template #footer>
        <el-button type="primary" @click="open = false">关闭</el-button>
      </template>
      <C7Descriptions
        :data="detail"
        :items="detailItems"
        :column="2"
        border
        label-width="96px"
      >
        <template #payload="{ value }">
          <pre class="log-detail__pre">{{ formatJson(value) }}</pre>
        </template>
        <template #error="{ value }">
          <pre class="log-detail__pre log-detail__pre--error">{{ value || '-' }}</pre>
        </template>
      </C7Descriptions>
    </C7Dialog>
  </div>
</template>

<script setup>
/**
 * 操作日志：仅查询与详情（对齐后端 SysOperLog）。
 */
import { computed, reactive, ref } from 'vue'
import { View } from '@element-plus/icons-vue'
import { useDict } from '@/utils/dict'
import { getOperLog, pageOperLog } from '@/api/system/operLog'

defineOptions({ name: 'SysOperLog' })

const { OPER_STATUS } = useDict('OPER_STATUS')

const tableRef = ref(null)
const open = ref(false)
const detail = reactive({})

const tableColumns = computed(() => [
  { prop: 'operName', label: '操作人', minWidth: 100, showOverflowTooltip: true },
  { prop: 'requestMethod', label: '请求方式', width: 100 },
  { prop: 'operUrl', label: 'URL', minWidth: 180, showOverflowTooltip: true },
  { prop: 'ipAddr', label: 'IP', minWidth: 130 },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: OPER_STATUS.value || [], width: 90 },
  { prop: 'costTime', label: '耗时(ms)', width: 100 },
  { prop: 'traceId', label: '链路ID', minWidth: 160, showOverflowTooltip: true },
  { prop: 'operTime', label: '操作时间', minWidth: 170 },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 90, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'operName', label: '操作人', type: 'input' },
  { prop: 'operUrl', label: 'URL', type: 'input' },
  { prop: 'traceId', label: '链路ID', type: 'input' },
  { prop: 'requestMethod', label: '请求方式', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', dataList: OPER_STATUS.value || [] }
])

const detailItems = computed(() => [
  { prop: 'operName', label: '操作人', emptyText: '-' },
  { prop: 'status', label: '状态', columnType: 'tag', options: OPER_STATUS.value || [] },
  { prop: 'requestMethod', label: '请求方式', emptyText: '-' },
  { prop: 'costTime', label: '耗时(ms)', emptyText: '-' },
  { prop: 'operUrl', label: 'URL', span: 2, columnType: 'copy', emptyText: '-' },
  { prop: 'method', label: '方法', span: 2, columnType: 'copy', emptyText: '-' },
  { prop: 'ipAddr', label: 'IP', columnType: 'copy', emptyText: '-' },
  { prop: 'loginLocation', label: '地点', emptyText: '-' },
  { prop: 'browser', label: '浏览器', emptyText: '-' },
  { prop: 'os', label: '操作系统', emptyText: '-' },
  { prop: 'clientId', label: '客户端', emptyText: '-' },
  { prop: 'operTime', label: '操作时间', emptyText: '-' },
  { prop: 'traceId', label: '链路ID', span: 2, columnType: 'copy', emptyText: '-' },
  { prop: 'params', label: '请求参数', span: 2, columnType: 'slot', slotName: 'payload' },
  { prop: 'jsonResult', label: '返回结果', span: 2, columnType: 'slot', slotName: 'payload' },
  { prop: 'errorMsg', label: '错误信息', span: 2, columnType: 'slot', slotName: 'error' }
])

function formatJson(raw) {
  if (raw == null || raw === '') {
    return '-'
  }
  if (typeof raw !== 'string') {
    try {
      return JSON.stringify(raw, null, 2)
    } catch {
      return String(raw)
    }
  }
  try {
    return JSON.stringify(JSON.parse(raw), null, 2)
  } catch {
    return raw
  }
}

async function openDetail(row) {
  const res = await getOperLog(row.id)
  Object.keys(detail).forEach((k) => delete detail[k])
  Object.assign(detail, res?.data || row)
  open.value = true
}
</script>

<style scoped>
.log-detail__pre {
  margin: 0;
  max-height: 240px;
  overflow: auto;
  padding: 8px 10px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}

.log-detail__pre--error {
  color: var(--el-color-danger);
  background: var(--el-color-danger-light-9);
}
</style>
