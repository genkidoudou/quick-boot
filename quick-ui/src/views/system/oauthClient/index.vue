<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageOauthClient"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeOauthClient"
      :add-button-permi="['system:oauthClient:add']"
      :edit-button-permi="['system:oauthClient:edit']"
      :delete-button-permi="['system:oauthClient:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['system:oauthClient:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['system:oauthClient:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除客户端「${row.clientName || row.clientId || row.id}」？` })"
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
      :submit="submitOauthClient"
      :on-success="onFormSuccess"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item prop="clientId">
          <template #label>
            <C7FormLabel text="客户端ID" :tip="FIELD_TIPS.clientId"/>
          </template>
          <el-input v-model="form.clientId" placeholder="请输入客户端ID" maxlength="255" :disabled="!!form.id"/>
        </el-form-item>
        <el-form-item prop="clientName">
          <template #label>
            <C7FormLabel text="客户端名称" :tip="FIELD_TIPS.clientName"/>
          </template>
          <el-input v-model="form.clientName" placeholder="请输入客户端名称" maxlength="255"/>
        </el-form-item>
        <el-form-item prop="clientType">
          <template #label>
            <C7FormLabel text="客户端类型" :tip="FIELD_TIPS.clientType"/>
          </template>
          <el-select v-model="form.clientType" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in CLIENT_TYPE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="grantTypeList">
          <template #label>
            <C7FormLabel text="授权类型" :tip="FIELD_TIPS.grantTypes"/>
          </template>
          <el-select
            v-model="form.grantTypeList"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择授权类型"
            style="width: 100%"
          >
            <el-option
              v-for="item in GRANT_TYPES"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="apiPathList">
          <template #label>
            <C7FormLabel text="接口路径" :tip="FIELD_TIPS.apiPathPatterns"/>
          </template>
          <div class="path-list">
            <div v-for="(path, index) in form.apiPathList" :key="index" class="path-list__row">
              <el-input v-model="form.apiPathList[index]" placeholder="如 /api/** 或 /**" maxlength="255"/>
              <el-button
                link
                type="danger"
                :disabled="form.apiPathList.length <= 1"
                @click="removeApiPath(index)"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" link @click="addApiPath">添加路径</el-button>
          </div>
        </el-form-item>
        <el-form-item prop="tokenTimeout">
          <template #label>
            <C7FormLabel text="Token时长" :tip="FIELD_TIPS.tokenTimeout"/>
          </template>
          <el-input-number v-model="form.tokenTimeout" :min="0" :controls="true" placeholder="秒"/>
        </el-form-item>
        <el-form-item prop="checkCaptcha">
          <template #label>
            <C7FormLabel text="校验验证码" :tip="FIELD_TIPS.checkCaptcha"/>
          </template>
          <c7-radio :data-list="COMMON_IS" v-model="form.checkCaptcha"/>
        </el-form-item>
        <el-form-item prop="status">
          <template #label>
            <C7FormLabel text="状态" :tip="FIELD_TIPS.status"/>
          </template>
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status"/>
        </el-form-item>
        <el-form-item prop="remark">
          <template #label>
            <C7FormLabel text="备注" :tip="FIELD_TIPS.remark"/>
          </template>
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100"/>
        </el-form-item>
      </el-form>
    </C7FormDialog>
  </div>
</template>

<script setup>
/**
 * OAuth 客户端管理：列表 / 新增 / 修改 / 删除（对齐后端 SysOauthClient）。
 * 密钥仅新增时由后端生成并弹窗展示一次；授权类型、接口路径在前端多选/多条后拼成逗号字段。
 */
import { computed, reactive, ref } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { useDict } from '@/utils/dict'
import {
  addOauthClient,
  pageOauthClient,
  removeOauthClient,
  updateOauthClient
} from '@/api/system/oauthClient'

defineOptions({ name: 'SysOauthClient' })

const { COMMON_STATUS, COMMON_IS, GRANT_TYPES } = useDict('COMMON_STATUS', 'COMMON_IS', 'GRANT_TYPES')

/** 字段说明（C7FormLabel tip） */
const FIELD_TIPS = {
  clientId: '应用唯一标识，对应登录请求中的 clientId；创建后不可改。',
  clientName: '管理端展示名称，便于区分 quick-ui / quick-h5 等端。',
  clientType: 'public=浏览器/H5 等公开端（不依赖 secret）；confidential=可保管密钥的服务端应用。',
  grantTypes: '该客户端允许的登录/换票方式（字典 GRANT_TYPES），多选后以逗号存库。',
  apiPathPatterns: '允许访问的接口 Ant 路径，可添加多条，保存时用英文逗号拼接。',
  tokenTimeout: '该端登录后 Sa-Token 有效时长（秒）；留空则使用全局默认。',
  checkCaptcha: '为「是」时，password/sms 等登录须先通过行为验证码。',
  status: '停用后该客户端不可再用于登录。',
  remark: '可选备注，仅管理查看。'
}

/** 客户端类型：表注释为 public|confidential，非系统字典。 */
const CLIENT_TYPE_OPTIONS = [
  { label: 'public（公开）', value: 'public' },
  { label: 'confidential（机密）', value: 'confidential' }
]

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())
/** 新增成功后一次性展示的明文密钥 */
const createdSecret = ref('')

const title = computed(() => (form.id ? '修改OAuth客户端' : '新增OAuth客户端'))

const tableColumns = computed(() => [
  { prop: 'clientId', label: '客户端ID', minWidth: 140, showOverflowTooltip: true },
  { prop: 'clientName', label: '客户端名称', minWidth: 140, showOverflowTooltip: true },
  { prop: 'clientType', label: '类型', width: 120, showOverflowTooltip: true },
  { prop: 'grantTypes', label: '授权类型', minWidth: 160, showOverflowTooltip: true },
  { prop: 'apiPathPatterns', label: '接口路径', minWidth: 140, showOverflowTooltip: true },
  { prop: 'tokenTimeout', label: 'Token时长', width: 110 },
  { prop: 'checkCaptcha', label: '校验验证码', columnType: 'tag', dictList: COMMON_IS.value || [], width: 110 },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100 },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'clientId', label: '客户端ID', type: 'input' },
  { prop: 'clientName', label: '客户端名称', type: 'input' },
  {
    prop: 'clientType',
    label: '客户端类型',
    type: 'select',
    dataList: CLIENT_TYPE_OPTIONS
  },
  { prop: 'checkCaptcha', label: '校验验证码', type: 'select', dataList: COMMON_IS.value || [] },
  { prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || [] }
])

const rules = computed(() => ({
  clientId: [{ required: true, message: '客户端ID不能为空', trigger: 'blur' }],
  clientName: [{ required: true, message: '客户端名称不能为空', trigger: 'blur' }],
  clientType: [{ required: true, message: '客户端类型不能为空', trigger: 'change' }],
  grantTypeList: [{ type: 'array', required: true, min: 1, message: '请至少选择一种授权类型', trigger: 'change' }],
  apiPathList: [
    {
      validator: (_rule, value, callback) => {
        const list = (value || []).map((s) => String(s || '').trim()).filter(Boolean)
        if (!list.length) {
          callback(new Error('请至少填写一条接口路径'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}))

function emptyForm() {
  return {
    id: undefined,
    clientId: '',
    clientName: '',
    clientType: 'public',
    grantTypeList: ['password'],
    apiPathList: ['/**'],
    tokenTimeout: undefined,
    checkCaptcha: '1',
    status: '0',
    remark: ''
  }
}

/** 逗号串 → 数组 */
function splitCsv(raw, fallback = []) {
  if (raw == null || String(raw).trim() === '') {
    return [...fallback]
  }
  return String(raw)
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
}

function addApiPath() {
  form.apiPathList.push('')
}

function removeApiPath(index) {
  if (form.apiPathList.length <= 1) {
    return
  }
  form.apiPathList.splice(index, 1)
}

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
  createdSecret.value = ''
  if (row && row.id != null) {
    Object.assign(form, {
      id: row.id,
      clientId: row.clientId ?? '',
      clientName: row.clientName ?? '',
      clientType: row.clientType ?? 'public',
      grantTypeList: splitCsv(row.grantTypes, ['password']),
      apiPathList: splitCsv(row.apiPathPatterns, ['/**']),
      tokenTimeout: row.tokenTimeout ?? undefined,
      checkCaptcha: row.checkCaptcha ?? '1',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
}

async function submitOauthClient() {
  const grantTypes = (form.grantTypeList || []).map(String).filter(Boolean).join(',')
  const apiPathPatterns = (form.apiPathList || []).map((s) => String(s || '').trim()).filter(Boolean).join(',')
  const payload = {
    id: form.id,
    clientId: form.clientId,
    clientName: form.clientName,
    clientType: form.clientType,
    grantTypes,
    apiPathPatterns,
    tokenTimeout: form.tokenTimeout,
    checkCaptcha: form.checkCaptcha,
    status: form.status,
    remark: form.remark
  }
  if (form.id) {
    createdSecret.value = ''
    await updateOauthClient(payload)
    return
  }
  const res = await addOauthClient(payload)
  createdSecret.value = res?.data?.clientSecret || ''
}

/**
 * @param {Record<string, any>} _form
 * @param {{ isEdit: boolean }} ctx
 */
async function onFormSuccess(_form, ctx) {
  tableRef.value?.refreshData()
  if (ctx?.isEdit || !createdSecret.value) {
    return
  }
  const secret = createdSecret.value
  createdSecret.value = ''
  try {
    await ElMessageBox.confirm(
      `客户端密钥仅展示一次，请立即复制保存：\n\n${secret}`,
      '密钥已生成',
      {
        confirmButtonText: '复制并关闭',
        cancelButtonText: '关闭',
        type: 'warning',
        distinguishCancelAndClose: true
      }
    )
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(secret)
    }
  } catch {
    // 用户点关闭/取消：不再复制
  }
}
</script>

<style scoped>
.path-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.path-list__row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.path-list__row .el-input {
  flex: 1;
}
</style>
