<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageJob"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeJob"
      :add-button-permi="['job:job:add']"
      :edit-button-permi="['job:job:edit']"
      :delete-button-permi="['job:job:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['job:job:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['job:job:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除定时任务「${row.jobName || row.id}」？` })"
        >
          删除
        </el-button>
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
      width="640px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" placeholder="请输入任务名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="任务类型" prop="jobType">
          <el-select v-model="form.jobType" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in JOB_TYPE" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.jobType === 'BEAN'" label="Bean 名称" prop="invokeTarget">
          <el-input v-model="form.invokeTarget" placeholder="Spring Bean 名称，如 demoTask" maxlength="255" />
        </el-form-item>
        <el-form-item v-if="form.jobType === 'SHELL' || form.jobType === 'PYTHON'" label="任务脚本" prop="scriptContent">
          <el-input v-model="form.scriptContent" type="textarea" :rows="8" placeholder="脚本正文" />
        </el-form-item>
        <el-form-item v-if="form.jobType === 'PYTHON'" label="解释器" prop="pythonBin">
          <el-select v-model="form.pythonBin" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in pythonBins" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务参数" prop="jobParam">
          <el-input v-model="form.jobParam" type="textarea" :rows="2" placeholder="可选" maxlength="512" />
        </el-form-item>
        <el-form-item label="Cron" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="Quartz 六段，如 0 0/1 * * * ?" maxlength="255" />
        </el-form-item>
        <el-form-item label="允许重叠" prop="concurrent">
          <c7-radio :data-list="COMMON_IS" v-model="form.concurrent" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="2" maxlength="100" />
        </el-form-item>
      </el-form>
    </C7FormDialog>
  </div>
</template>

<script setup>
/**
 * 定时任务：列表 / 新增 / 修改 / 删除（对齐后端 SysJob）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {addJob, pageJob, removeJob, updateJob} from '@/api/job/job'

defineOptions({name: 'SysJob'})

const {JOB_TYPE, COMMON_IS, COMMON_STATUS} = useDict('JOB_TYPE', 'COMMON_IS', 'COMMON_STATUS')
const pythonBins = ['python2', 'python3']

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改定时任务' : '新增定时任务'))

const tableColumns = computed(() => [
  {prop: 'jobName', label: '任务名称', minWidth: 160, showOverflowTooltip: true},
  {prop: 'jobType', label: '任务类型', columnType: 'tag', dictList: JOB_TYPE.value || [], width: 110},
  {prop: 'invokeTarget', label: 'Bean 名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'cronExpression', label: 'Cron', minWidth: 160, showOverflowTooltip: true},
  {prop: 'concurrent', label: '允许重叠', columnType: 'tag', dictList: COMMON_IS.value || [], width: 110},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 90},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 140, fixed: 'right', align: 'center'}
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
  scriptContent: form.jobType === 'SHELL' || form.jobType === 'PYTHON'
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

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
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
}

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
</script>
