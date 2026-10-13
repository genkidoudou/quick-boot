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

    <C7Dialog v-model="open" title="登录日志详情" width="720px" mobile-mode="drawer">
      <template #footer>
        <el-button type="primary" @click="open = false">关闭</el-button>
      </template>
      <C7Descriptions
        :data="detail"
        :items="detailItems"
        :column="2"
        border
        label-width="96px"
      />
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

const tableColumns = computed(() => [
  { prop: 'userName', label: '用户名', minWidth: 120, showOverflowTooltip: true },
  { prop: 'ipAddr', label: 'IP', minWidth: 130 },
  { prop: 'browser', label: '浏览器', minWidth: 110, showOverflowTooltip: true },
  { prop: 'os', label: '操作系统', minWidth: 110, showOverflowTooltip: true },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: SYS_LOGIN_STATUS.value || [], width: 90 },
  { prop: 'traceId', label: '链路ID', minWidth: 160, showOverflowTooltip: true },
  { prop: 'loginTime', label: '登录时间', minWidth: 170 },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 90, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'userName', label: '用户名', type: 'input' },
  { prop: 'ipAddr', label: 'IP', type: 'input' },
  { prop: 'traceId', label: '链路ID', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', dataList: SYS_LOGIN_STATUS.value || [] }
])

const detailItems = computed(() => [
  { prop: 'userName', label: '用户名' },
  { prop: 'userId', label: '用户ID', columnType: 'copy', emptyText: '-' },
  { prop: 'status', label: '状态', columnType: 'tag', options: SYS_LOGIN_STATUS.value || [] },
  { prop: 'loginTime', label: '登录时间' },
  { prop: 'ipAddr', label: 'IP', columnType: 'copy' },
  { prop: 'loginLocation', label: '地点', emptyText: '-' },
  { prop: 'browser', label: '浏览器', emptyText: '-' },
  { prop: 'os', label: '操作系统', emptyText: '-' },
  { prop: 'clientId', label: '客户端', emptyText: '-' },
  { prop: 'traceId', label: '链路ID', span: 2, columnType: 'copy', emptyText: '-' }
])

async function openDetail(row) {
  const res = await getLogininfor(row.id)
  Object.keys(detail).forEach((k) => delete detail[k])
  Object.assign(detail, res?.data || row)
  open.value = true
}
</script>
