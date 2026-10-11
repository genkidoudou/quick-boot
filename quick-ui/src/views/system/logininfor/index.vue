<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageLogininfor"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
    >
      <template #actions="{ row }">
        <el-button link type="primary" :icon="View" v-hasPermi="['system:logininfor:query']" @click="openDetail(row)">
          详情
        </el-button>
      </template>
    </C7JsonTable>

    <C7Dialog v-model="open" title="登录日志详情">
      <template #footer>
        <el-button @click="open = false">关闭</el-button>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ detail.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="客户端">{{ detail.clientId }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detail.ipAddr }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ detail.loginLocation }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{ detail.browser }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{ detail.os }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel }}</el-descriptions-item>
        <el-descriptions-item label="登录时间">{{ detail.loginTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
    </C7Dialog>
  </div>
</template>

<script setup>
/**
 * 登录日志：仅查询与详情（对齐后端 SysLogininfor）。
 */
import { computed, reactive, ref } from 'vue'
import { View } from '@element-plus/icons-vue'
import { useDict } from '@/utils/dict'
import { getLogininfor, pageLogininfor } from '@/api/system/logininfor'

defineOptions({ name: 'SysLogininfor' })

const { SYS_LOGIN_STATUS } = useDict('SYS_LOGIN_STATUS')

const tableRef = ref(null)
const open = ref(false)
const detail = reactive({})

const statusLabel = computed(() => {
  const hit = (SYS_LOGIN_STATUS.value || []).find((d) => String(d.value) === String(detail.status))
  return hit?.label ?? detail.status ?? ''
})

const tableColumns = computed(() => [
  { prop: 'userName', label: '用户名', minWidth: 120, showOverflowTooltip: true },
  { prop: 'ipAddr', label: 'IP', minWidth: 130 },
  { prop: 'loginLocation', label: '地点', minWidth: 120, showOverflowTooltip: true },
  { prop: 'browser', label: '浏览器', minWidth: 120, showOverflowTooltip: true },
  { prop: 'os', label: '操作系统', minWidth: 120, showOverflowTooltip: true },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: SYS_LOGIN_STATUS.value || [], width: 90 },
  { prop: 'loginTime', label: '登录时间', minWidth: 170 },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 90, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'userName', label: '用户名', type: 'input' },
  { prop: 'ipAddr', label: 'IP', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', dataList: SYS_LOGIN_STATUS.value || [] }
])

async function openDetail(row) {
  const res = await getLogininfor(row.id)
  Object.keys(detail).forEach((k) => delete detail[k])
  Object.assign(detail, res?.data || row)
  open.value = true
}
</script>
