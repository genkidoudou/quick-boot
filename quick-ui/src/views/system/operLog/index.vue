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

    <C7Dialog v-model="open" title="操作日志详情" width="720px">
      <template #footer>
        <el-button @click="open = false">关闭</el-button>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人">{{ detail.operName }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ detail.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="方法">{{ detail.method }}</el-descriptions-item>
        <el-descriptions-item label="URL">{{ detail.operUrl }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detail.ipAddr }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ detail.loginLocation }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ detail.costTime }}</el-descriptions-item>
        <el-descriptions-item label="链路ID">{{ detail.traceId }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detail.operTime }}</el-descriptions-item>
        <el-descriptions-item label="参数">{{ detail.params }}</el-descriptions-item>
        <el-descriptions-item label="返回">{{ detail.jsonResult }}</el-descriptions-item>
        <el-descriptions-item label="错误信息">{{ detail.errorMsg }}</el-descriptions-item>
      </el-descriptions>
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

const statusLabel = computed(() => {
  const hit = (OPER_STATUS.value || []).find((d) => String(d.value) === String(detail.status))
  return hit?.label ?? detail.status ?? ''
})

const tableColumns = computed(() => [
  { prop: 'operName', label: '操作人', minWidth: 100, showOverflowTooltip: true },
  { prop: 'requestMethod', label: '请求方式', width: 100 },
  { prop: 'operUrl', label: 'URL', minWidth: 180, showOverflowTooltip: true },
  { prop: 'ipAddr', label: 'IP', minWidth: 130 },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: OPER_STATUS.value || [], width: 90 },
  { prop: 'costTime', label: '耗时', width: 90 },
  { prop: 'operTime', label: '操作时间', minWidth: 170 },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 90, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'operName', label: '操作人', type: 'input' },
  { prop: 'operUrl', label: 'URL', type: 'input' },
  { prop: 'requestMethod', label: '请求方式', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', dataList: OPER_STATUS.value || [] }
])

async function openDetail(row) {
  const res = await getOperLog(row.id)
  Object.keys(detail).forEach((k) => delete detail[k])
  Object.assign(detail, res?.data || row)
  open.value = true
}
</script>
