<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageJob"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      :delete-function="removeJob"
      :before-delete="beforeDeleteJobs"
      :add-button-permi="['job:job:add']"
      :delete-button-permi="['job:job:remove']"
      @add-click="() => formDialogRef?.open()"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="View"
          v-hasPermi="['job:job:query']"
          @click="openView(row)"
        >
          查看
        </el-button>
        <el-button
          link
          type="primary"
          :icon="VideoPlay"
          v-hasPermi="['job:job:edit']"
          @click="onRun(row)"
        >
          执行一次
        </el-button>
        <el-dropdown trigger="click" @command="(cmd) => onMoreCommand(cmd, row)">
          <el-button link type="primary">
            更多
            <el-icon class="el-icon--right"><ArrowDown/></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="log" v-hasPermi="['job:jobLog:list']">
                <el-icon><Document/></el-icon>日志
              </el-dropdown-item>
              <el-dropdown-item command="toggle" v-hasPermi="['job:job:edit']">
                {{ row.status === '0' ? '停用' : '启用' }}
              </el-dropdown-item>
              <el-dropdown-item
                command="edit"
                :disabled="row.status === '0'"
                v-hasPermi="['job:job:edit']"
              >
                <el-icon><Edit/></el-icon>修改
              </el-dropdown-item>
              <el-dropdown-item
                command="remove"
                divided
                :disabled="row.status === '0'"
                v-hasPermi="['job:job:remove']"
              >
                <el-icon><Delete/></el-icon>删除
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </C7JsonTable>

    <C7FormDialog
      ref="formDialogRef"
      v-model="open"
      :title="title"
      :form="form"
      :form-ref="formRef"
      :get-payload="getPayload"
      :submit="submitJob"
      :on-success="() => tableRef?.refreshData()"
      :width="formDialogWidth"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="108px">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" placeholder="请输入任务名称" maxlength="255"/>
        </el-form-item>
        <el-form-item prop="jobType">
          <template #label>
            <C7FormLabel text="任务类型" :tip="JOB_TIPS.jobType"/>
          </template>
          <el-select v-model="form.jobType" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in JOB_TYPE" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.jobType === 'BEAN'" prop="invokeTarget">
          <template #label>
            <C7FormLabel text="Bean 名称" :tip="JOB_TIPS.invokeTarget"/>
          </template>
          <el-select
            v-model="form.invokeTarget"
            filterable
            clearable
            allow-create
            default-first-option
            placeholder="请选择实现 ITask 的 Bean"
            style="width: 100%"
            :loading="taskBeansLoading"
          >
            <el-option
              v-for="item in taskBeans"
              :key="item.beanName"
              :label="`${item.beanName}（${item.className}）`"
              :value="item.beanName"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isScriptType" prop="scriptContent">
          <template #label>
            <C7FormLabel text="任务脚本" :tip="scriptTip"/>
          </template>
          <div class="script-editor">
            <div class="script-editor__toolbar">
              <el-button size="small" @click="insertScriptSample">插入示例</el-button>
              <el-button size="small" type="primary" plain @click="scriptFullscreen = true">全屏编辑</el-button>
              <span class="script-editor__hint">{{ scriptToolbarHint }}</span>
            </div>
            <el-input
              v-model="form.scriptContent"
              type="textarea"
              :rows="12"
              class="script-editor__textarea"
              :placeholder="scriptPlaceholder"
              resize="vertical"
            />
          </div>
        </el-form-item>
        <el-form-item v-if="form.jobType === 'PYTHON'" prop="pythonBin">
          <template #label>
            <C7FormLabel text="解释器" :tip="JOB_TIPS.pythonBin"/>
          </template>
          <el-select v-model="form.pythonBin" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in pythonBins" :key="item" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item prop="jobParam">
          <template #label>
            <C7FormLabel text="任务参数" :tip="jobParamTip"/>
          </template>
          <el-input
            v-model="form.jobParam"
            type="textarea"
            :rows="2"
            :placeholder="jobParamPlaceholder"
            maxlength="512"
          />
        </el-form-item>
        <el-form-item prop="cronExpression">
          <template #label>
            <C7FormLabel text="Cron" :tip="JOB_TIPS.cronExpression"/>
          </template>
          <el-input v-model="form.cronExpression" placeholder="Quartz 六段，如 0 0/1 * * * ?" maxlength="255">
            <template #append>
              <el-button @click="cronOpen = true">生成</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="concurrent">
          <template #label>
            <C7FormLabel text="允许重叠" :tip="JOB_TIPS.concurrent"/>
          </template>
          <c7-radio :data-list="COMMON_IS" v-model="form.concurrent"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="2" maxlength="100"/>
        </el-form-item>
      </el-form>
    </C7FormDialog>

    <!-- 查看详情：基础信息 + 接下来几次执行时间 -->
    <el-dialog v-model="viewOpen" title="定时任务详情" width="640px" append-to-body destroy-on-close>
      <el-descriptions v-if="viewDetail" :column="1" border>
        <el-descriptions-item label="任务名称">{{ viewDetail.jobName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">{{ jobTypeLabel(viewDetail.jobType) }}</el-descriptions-item>
        <el-descriptions-item v-if="viewDetail.jobType === 'BEAN'" label="Bean 名称">
          {{ viewDetail.invokeTarget || '—' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="viewDetail.jobType === 'PYTHON'" label="解释器">
          {{ viewDetail.pythonBin || '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="任务参数">{{ viewDetail.jobParam || '—' }}</el-descriptions-item>
        <el-descriptions-item label="Cron">{{ viewDetail.cronExpression || '—' }}</el-descriptions-item>
        <el-descriptions-item label="允许重叠">{{ concurrentLabel(viewDetail.concurrent) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel(viewDetail.status) }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ viewDetail.remark || '—' }}</el-descriptions-item>
        <el-descriptions-item label="接下来执行时间">
          <div v-if="(viewDetail.nextFireTimes || []).length" class="next-fire-list">
            <div v-for="(t, idx) in viewDetail.nextFireTimes" :key="idx" class="next-fire-list__item">
              {{ idx + 1 }}. {{ formatDateTime(t) }}
            </div>
          </div>
          <span v-else>—</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="isScriptTypeOf(viewDetail)" label="任务脚本">
          <pre class="script-preview">{{ viewDetail.scriptContent || '—' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewOpen = false">关闭</el-button>
        <el-button type="primary" v-hasPermi="['job:jobLog:list']" @click="goJobLog(viewDetail)">查看日志</el-button>
      </template>
    </el-dialog>

    <JobCronPicker
      v-model="cronOpen"
      :expression="form.cronExpression"
      @confirm="(v) => (form.cronExpression = v)"
    />

    <el-dialog
      v-model="scriptFullscreen"
      :title="form.jobType === 'PYTHON' ? '编辑 Python 脚本' : '编辑 Shell 脚本'"
      width="900px"
      append-to-body
      destroy-on-close
      class="script-fullscreen-dialog"
    >
      <div class="script-editor script-editor--fullscreen">
        <div class="script-editor__toolbar">
          <el-button size="small" @click="insertScriptSample">插入示例</el-button>
          <span class="script-editor__hint">{{ scriptToolbarHint }}</span>
        </div>
        <el-input
          v-model="form.scriptContent"
          type="textarea"
          :rows="22"
          class="script-editor__textarea"
          :placeholder="scriptPlaceholder"
          resize="none"
        />
      </div>
      <template #footer>
        <el-button type="primary" @click="scriptFullscreen = false">完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 定时任务：列表 / 查看 / 新增 / 修改 / 删除。
 * 规则：状态为正常(0)时不可修改、删除，须先停用；查看展示接下来几次执行时间；可跳转关联日志。
 */
import {computed, reactive, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowDown, Delete, Document, Edit, VideoPlay, View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {
  addJob,
  changeJobStatus,
  getJob,
  listJobTaskBeans,
  pageJob,
  removeJob,
  runJob,
  updateJob
} from '@/api/job/job'
import JobCronPicker from '@/components/monitor/JobCronPicker.vue'

defineOptions({name: 'SysJob'})

const router = useRouter()
const {JOB_TYPE, COMMON_IS, COMMON_STATUS} = useDict('JOB_TYPE', 'COMMON_IS', 'COMMON_STATUS')
const pythonBins = ['python2', 'python3']

const JOB_TIPS = {
  jobType: 'BEAN 调用 Spring 中实现 ITask 的 Bean；SHELL / PYTHON 将脚本写入临时文件后由解释器执行。',
  invokeTarget: '选择容器中实现 ITask 的 Bean 名称；执行时调用 ITask.execute(jobParam)。',
  pythonBin: '仅允许 python2 或 python3，需在运行机器 PATH 中可用。',
  cronExpression: 'Quartz 六段式：秒 分 时 日 月 周。可点「生成」用可视化编辑器。',
  concurrent: '否：同一 Job 未结束前不会并行（Quartz）；是：允许重叠，请保证业务幂等。'
}

const SHELL_SAMPLE = `#!/bin/bash
echo "param=\\$1"
date
`
const PYTHON_SAMPLE = `import sys
print("argv=", sys.argv)
if len(sys.argv) > 1:
    print("param=", sys.argv[1])
`

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const cronOpen = ref(false)
const scriptFullscreen = ref(false)
const form = reactive(emptyForm())
const taskBeans = ref([])
const taskBeansLoading = ref(false)

const viewOpen = ref(false)
const viewDetail = ref(null)

const title = computed(() => (form.id ? '修改定时任务' : '新增定时任务'))
const isScriptType = computed(() => form.jobType === 'SHELL' || form.jobType === 'PYTHON')
const formDialogWidth = computed(() => (isScriptType.value ? '760px' : '640px'))

const jobParamPlaceholder = computed(() => {
  if (form.jobType === 'BEAN') {
    return '可选，传给 ITask.execute(String param)'
  }
  if (form.jobType === 'SHELL') {
    return '可选，作为脚本第 1 个命令行参数（$1）'
  }
  if (form.jobType === 'PYTHON') {
    return '可选，作为 sys.argv[1]'
  }
  return '可选'
})

const jobParamTip = computed(() => {
  if (form.jobType === 'BEAN') {
    return '整段字符串原样传入 ITask.execute(param)，可在实现类中自行解析 JSON 或键值。'
  }
  if (form.jobType === 'SHELL') {
    return '追加为 bash 脚本的第 1 个参数，脚本内用 $1 读取；整段是一个 argv，勿依赖空格拆多个参数。'
  }
  if (form.jobType === 'PYTHON') {
    return '追加为解释器后的第 1 个参数，脚本内用 sys.argv[1] 读取；整段作为单个参数。'
  }
  return '可选任务参数。'
})

const scriptTip = computed(() => {
  if (form.jobType === 'SHELL') {
    return '脚本正文会写入临时 .sh，由 bash 执行；任务参数作为 $1。注意命令安全与超时（默认 60 秒）。'
  }
  if (form.jobType === 'PYTHON') {
    return '脚本正文会写入临时 .py，由所选解释器执行；任务参数作为 sys.argv[1]。注意超时（默认 60 秒）。'
  }
  return ''
})

const scriptPlaceholder = computed(() => {
  if (form.jobType === 'SHELL') {
    return '写入 bash 脚本正文，例如：\necho \"hello $1\"'
  }
  return '写入 Python 脚本正文，例如：\nimport sys\nprint(sys.argv)'
})

const scriptToolbarHint = computed(() => {
  return form.jobType === 'PYTHON'
    ? '等宽编辑 · 参数 → sys.argv[1]'
    : '等宽编辑 · 参数 → $1'
})

const tableColumns = computed(() => [
  {prop: 'jobName', label: '任务名称', minWidth: 160, showOverflowTooltip: true},
  {prop: 'jobType', label: '任务类型', columnType: 'tag', dictList: JOB_TYPE.value || [], width: 110},
  {prop: 'invokeTarget', label: 'Bean 名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'cronExpression', label: 'Cron', minWidth: 160, showOverflowTooltip: true},
  {prop: 'concurrent', label: '允许重叠', columnType: 'tag', dictList: COMMON_IS.value || [], width: 110},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 90},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 220, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'jobName', label: '任务名称', type: 'input'},
  {prop: 'jobType', label: '任务类型', type: 'select', dataList: JOB_TYPE.value || []},
  {prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || []}
])

const rules = computed(() => ({
  jobName: [{required: true, message: '任务名称不能为空', trigger: 'blur'}],
  jobType: [{required: true, message: '任务类型不能为空', trigger: 'change'}],
  cronExpression: [{required: true, message: 'Cron 不能为空', trigger: 'blur'}],
  invokeTarget: form.jobType === 'BEAN'
    ? [{required: true, message: 'Bean 名称不能为空', trigger: 'blur'}]
    : [],
  scriptContent: isScriptType.value
    ? [{required: true, message: '任务脚本不能为空', trigger: 'blur'}]
    : [],
  pythonBin: form.jobType === 'PYTHON'
    ? [{required: true, message: '请选择解释器', trigger: 'change'}]
    : []
}))

function emptyForm() {
  return {
    id: undefined,
    jobName: '',
    jobType: 'BEAN',
    invokeTarget: '',
    jobParam: '',
    scriptContent: '',
    pythonBin: 'python3',
    cronExpression: '',
    concurrent: '1',
    status: '0',
    remark: ''
  }
}

/** 正常（启用）状态不可改删 */
function isEnabled(row) {
  return row?.status === '0'
}

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
  scriptFullscreen.value = false
  if (row && row.id != null) {
    Object.assign(form, {
      id: row.id,
      jobName: row.jobName ?? '',
      jobType: row.jobType ?? 'BEAN',
      invokeTarget: row.invokeTarget ?? '',
      jobParam: row.jobParam ?? '',
      scriptContent: row.scriptContent ?? '',
      pythonBin: row.pythonBin || 'python3',
      cronExpression: row.cronExpression ?? '',
      concurrent: row.concurrent ?? '1',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
  if (form.jobType === 'BEAN') {
    loadTaskBeans()
  }
}

async function loadTaskBeans() {
  taskBeansLoading.value = true
  try {
    const res = await listJobTaskBeans()
    taskBeans.value = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : []
  } catch {
    taskBeans.value = []
  } finally {
    taskBeansLoading.value = false
  }
}

function insertScriptSample() {
  const sample = form.jobType === 'PYTHON' ? PYTHON_SAMPLE : SHELL_SAMPLE
  if (String(form.scriptContent || '').trim()) {
    ElMessageBox.confirm('当前脚本不为空，确认用示例覆盖？', '提示', {type: 'warning'})
      .then(() => {
        form.scriptContent = sample
      })
      .catch(() => {})
    return
  }
  form.scriptContent = sample
}

watch(
  () => form.jobType,
  (type) => {
    if (type === 'BEAN') {
      loadTaskBeans()
      scriptFullscreen.value = false
    }
  }
)

async function submitJob() {
  const payload = {
    id: form.id,
    jobName: form.jobName,
    jobType: form.jobType,
    jobParam: form.jobParam,
    cronExpression: form.cronExpression,
    concurrent: form.concurrent,
    status: form.status,
    remark: form.remark,
    invokeTarget: form.jobType === 'BEAN' ? form.invokeTarget : null,
    scriptContent: form.jobType === 'BEAN' ? null : form.scriptContent,
    pythonBin: form.jobType === 'PYTHON' ? form.pythonBin : null
  }
  if (form.id) {
    await updateJob(payload)
  } else {
    await addJob(payload)
  }
}

/** @param {Record<string, any>} row */
async function openView(row) {
  const res = await getJob(row.id)
  viewDetail.value = res?.data ?? null
  viewOpen.value = true
}

/** @param {Record<string, any>} row */
function onEdit(row) {
  if (isEnabled(row)) {
    ElMessage.warning('任务处于正常状态，请先停用后再修改')
    return
  }
  formDialogRef.value?.open(row)
}

/** @param {Record<string, any>} row */
function onRemove(row) {
  if (isEnabled(row)) {
    ElMessage.warning('任务处于正常状态，请先停用后再删除')
    return
  }
  tableRef.value?.removeRows(row, {confirmMessage: `确认删除定时任务「${row.jobName || row.id}」？`})
}

/**
 * 删除前校验：任一正常（启用）任务都拒绝（与后端 assertDisabled 对齐）。
 * @param {Array<string|number>} _ids
 * @param {Array<Record<string, any>>} rows
 * @returns {boolean}
 */
function beforeDeleteJobs(_ids, rows) {
  const enabled = (rows || []).filter((r) => isEnabled(r))
  if (enabled.length) {
    ElMessage.warning(`有 ${enabled.length} 个任务处于正常状态，请先停用后再删除`)
    return false
  }
  return true
}

/**
 * 更多菜单命令分发。
 * @param {string} cmd
 * @param {Record<string, any>} row
 */
function onMoreCommand(cmd, row) {
  if (cmd === 'log') {
    goJobLog(row)
    return
  }
  if (cmd === 'toggle') {
    onToggleStatus(row)
    return
  }
  if (cmd === 'edit') {
    onEdit(row)
    return
  }
  if (cmd === 'remove') {
    onRemove(row)
  }
}

/**
 * 跳转任务日志。按已注册路由匹配，避免写死 /job/jobLog 在嵌套菜单下 404。
 * @param {Record<string, any>|null} row
 */
function goJobLog(row) {
  if (!row?.id) {
    return
  }
  viewOpen.value = false
  const hit = router.getRoutes().find((r) => {
    const p = r.path || ''
    return p === '/job/jobLog' || p.endsWith('/jobLog')
  })
  router.push({
    path: hit?.path || '/job/jobLog',
    query: {jobId: String(row.id)}
  })
}

/** @param {Record<string, any>} row */
async function onRun(row) {
  await ElMessageBox.confirm(`确认执行一次任务「${row.jobName || row.id}」？`, '提示', {type: 'warning'})
  await runJob(row.id)
  ElMessage.success('已触发执行')
  tableRef.value?.refreshData()
}

/** @param {Record<string, any>} row */
async function onToggleStatus(row) {
  const next = row.status === '0' ? '1' : '0'
  const tip = next === '1' ? '停用' : '启用'
  await ElMessageBox.confirm(`确认${tip}任务「${row.jobName || row.id}」？`, '提示', {type: 'warning'})
  await changeJobStatus({id: row.id, status: next})
  ElMessage.success(`已${tip}`)
  tableRef.value?.refreshData()
}

function jobTypeLabel(value) {
  return (JOB_TYPE.value || []).find((d) => d.value === value)?.label || value || '—'
}

function concurrentLabel(value) {
  return (COMMON_IS.value || []).find((d) => d.value === value)?.label || value || '—'
}

function statusLabel(value) {
  return (COMMON_STATUS.value || []).find((d) => d.value === value)?.label || value || '—'
}

function isScriptTypeOf(detail) {
  return detail?.jobType === 'SHELL' || detail?.jobType === 'PYTHON'
}

/** @param {string|null|undefined} value */
function formatDateTime(value) {
  if (!value) {
    return '—'
  }
  // 后端 LocalDateTime 多为 "2026-10-25T23:00:00" 或带空格
  return String(value).replace('T', ' ')
}
</script>

<style scoped>
.script-editor {
  width: 100%;
}

.script-editor__toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.script-editor__hint {
  margin-left: auto;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.script-editor__textarea :deep(textarea) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.55;
  tab-size: 4;
}

.script-editor--fullscreen .script-editor__textarea :deep(textarea) {
  min-height: 480px;
}

.next-fire-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.next-fire-list__item {
  font-variant-numeric: tabular-nums;
}

.script-preview {
  margin: 0;
  max-height: 240px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
}
</style>
