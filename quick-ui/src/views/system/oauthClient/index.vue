<template>
  <div class="app-container qb-workbench">
    <div class="qb-banner-card oauth-summary">
      <div class="qb-banner-card__head">
        <h2 class="oauth-summary__title">OAuth 客户端</h2>
        <p class="oauth-summary__desc">管理客户端凭证、授权类型与请求签名开关</p>
      </div>
      <div class="qb-banner-card__dock">
        <span class="qb-badge-soft is-info">认证</span>
        <span class="qb-badge-soft">签名校验</span>
      </div>
    </div>
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
          :icon="View"
          v-hasPermi="['system:oauthClient:query']"
          @click="openCredential(row)"
        >
          查看
        </el-button>
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

    <C7Dialog v-model="credentialOpen" title="查看凭证" width="560px" :footer="false">
      <el-form label-width="96px" v-loading="credentialLoading">
        <el-form-item label="客户端名称">
          <span>{{ credential.clientName || '-' }}</span>
        </el-form-item>
        <el-form-item label="客户端ID">
          <div class="credential-row">
            <el-input :model-value="credential.clientId" readonly/>
            <el-button type="primary" @click="copyText(credential.clientId, '客户端ID')">复制</el-button>
          </div>
        </el-form-item>
        <el-form-item label="客户端密钥">
          <div class="credential-row">
            <el-input :model-value="credential.clientSecret" readonly show-password type="password"/>
            <el-button type="primary" @click="copyText(credential.clientSecret, '客户端密钥')">复制</el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="warning" plain @click="copyBoth">一键复制 ID + 密钥</el-button>
        </el-form-item>
      </el-form>
    </C7Dialog>

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
            placeholder="授权方式 + 安全策略（统一写入 grant_types）"
            style="width: 100%"
          >
            <el-option-group label="授权方式">
              <el-option
                v-for="item in authOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-option-group>
            <el-option-group label="安全策略">
              <el-option
                v-for="item in securityOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-option-group>
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
 * OAuth 客户端管理。
 * 授权方式与安全策略统一写入 grant_types（如 password,sms,captcha,sign），后端用 contains 判断。
 */
import { computed, reactive, ref } from 'vue'
import { Delete, Edit, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDict } from '@/utils/dict'
import {
  addOauthClient,
  getOauthClientCredential,
  pageOauthClient,
  removeOauthClient,
  updateOauthClient
} from '@/api/system/oauthClient'

defineOptions({ name: 'SysOauthClient' })

const { COMMON_STATUS, GRANT_TYPES } = useDict('COMMON_STATUS', 'GRANT_TYPES')

/** 安全策略约定值（与字典 GRANT_TYPES 中 captcha/sign 一致） */
const SECURITY_VALUES = new Set(['captcha', 'sign'])
const SECURITY_FALLBACK = [
  { label: '校验验证码', value: 'captcha' },
  { label: '校验签名', value: 'sign' }
]

const authOptions = computed(() =>
  (GRANT_TYPES.value || []).filter((i) => !SECURITY_VALUES.has(i.value))
)
const securityOptions = computed(() => {
  const fromDict = (GRANT_TYPES.value || []).filter((i) => SECURITY_VALUES.has(i.value))
  return fromDict.length ? fromDict : SECURITY_FALLBACK
})

/** 字段说明 */
const FIELD_TIPS = {
  clientId: '应用唯一标识，对应登录请求中的 clientId；创建后不可改。',
  clientName: '管理端展示名称，便于区分 quick-ui / quick-h5 等端。',
  clientType: 'public=浏览器/H5 等公开端（不依赖 secret）；confidential=可保管密钥的服务端应用。',
  grantTypes: '统一写入 grant_types。含 captcha 则登录要验证码；含 sign 则请求要 HMAC。扩展只需加字典项。',
  apiPathPatterns: '允许访问的接口 Ant 路径，可添加多条，保存时用英文逗号拼接。',
  tokenTimeout: '该端登录后 Sa-Token 有效时长（秒）；留空则使用全局默认。',
  status: '停用后该客户端不可再用于登录。',
  remark: '可选备注，仅管理查看。'
}

const CLIENT_TYPE_OPTIONS = [
  { label: 'public（公开）', value: 'public' },
  { label: 'confidential（机密）', value: 'confidential' }
]

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())
const createdSecret = ref('')

const title = computed(() => (form.id ? '修改OAuth客户端' : '新增OAuth客户端'))

const grantLabelMap = computed(() => {
  const map = {}
  for (const item of [...authOptions.value, ...securityOptions.value]) {
    map[item.value] = item.label
  }
  return map
})

const tableColumns = computed(() => [
  { prop: 'clientId', label: '客户端ID', minWidth: 140, showOverflowTooltip: true },
  { prop: 'clientName', label: '客户端名称', minWidth: 140, showOverflowTooltip: true },
  { prop: 'clientType', label: '类型', width: 120, showOverflowTooltip: true },
  {
    prop: 'grantTypes',
    label: '授权类型',
    minWidth: 200,
    showOverflowTooltip: true,
    formatter: (row) => formatGrantTypes(row.grantTypes)
  },
  { prop: 'apiPathPatterns', label: '接口路径', minWidth: 140, showOverflowTooltip: true },
  { prop: 'tokenTimeout', label: 'Token时长', width: 110 },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100 },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 220, fixed: 'right', align: 'center' }
])

const credentialOpen = ref(false)
const credentialLoading = ref(false)
const credential = reactive({
  clientName: '',
  clientId: '',
  clientSecret: ''
})

const searchColumns = computed(() => [
  { prop: 'clientId', label: '客户端ID', type: 'input' },
  { prop: 'clientName', label: '客户端名称', type: 'input' },
  {
    prop: 'clientType',
    label: '客户端类型',
    type: 'select',
    dataList: CLIENT_TYPE_OPTIONS
  },
  { prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || [] }
])

const rules = computed(() => ({
  clientId: [{ required: true, message: '客户端ID不能为空', trigger: 'blur' }],
  clientName: [{ required: true, message: '客户端名称不能为空', trigger: 'blur' }],
  clientType: [{ required: true, message: '客户端类型不能为空', trigger: 'change' }],
  grantTypeList: [
    {
      validator: (_rule, value, callback) => {
        const list = (value || []).map(String)
        const grants = list.filter((v) => !SECURITY_VALUES.has(v))
        if (!grants.length) {
          callback(new Error('请至少选择一种授权方式（密码/短信等）'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
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
    status: '0',
    remark: ''
  }
}

function splitCsv(raw, fallback = []) {
  if (raw == null || String(raw).trim() === '') {
    return [...fallback]
  }
  return String(raw)
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
}

function formatGrantTypes(raw) {
  return splitCsv(raw)
    .map((v) => grantLabelMap.value[v] || v)
    .join('、') || '-'
}

/** 打开凭证查看 */
async function openCredential(row) {
  if (!row?.id) {
    return
  }
  credential.clientName = row.clientName || ''
  credential.clientId = row.clientId || ''
  credential.clientSecret = ''
  credentialOpen.value = true
  credentialLoading.value = true
  try {
    const res = await getOauthClientCredential(row.id)
    const data = res?.data ?? res
    credential.clientName = data?.clientName ?? row.clientName ?? ''
    credential.clientId = data?.clientId ?? row.clientId ?? ''
    credential.clientSecret = data?.clientSecret ?? ''
  } catch (e) {
    credentialOpen.value = false
    throw e
  } finally {
    credentialLoading.value = false
  }
}

async function copyText(text, label = '内容') {
  const value = String(text || '')
  if (!value) {
    ElMessage.warning(`${label}为空`)
    return
  }
  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(value)
    } else {
      const ta = document.createElement('textarea')
      ta.value = value
      document.body.appendChild(ta)
      ta.select()
      document.execCommand('copy')
      document.body.removeChild(ta)
    }
    ElMessage.success(`已复制${label}`)
  } catch {
    ElMessage.error('复制失败，请手动选择复制')
  }
}

function copyBoth() {
  const id = credential.clientId || ''
  const secret = credential.clientSecret || ''
  if (!id && !secret) {
    ElMessage.warning('凭证为空')
    return
  }
  return copyText(`clientId=${id}\nclientSecret=${secret}`, '凭证')
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
    // 关闭/取消
  }
}
</script>

<style scoped>
.qb-workbench {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: var(--qb-bottom-clearance);
}

.oauth-summary__title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: var(--qb-fg);
}

.oauth-summary__desc {
  margin: 0;
  font-size: 13px;
  color: var(--qb-muted-fg);
}

.oauth-summary__dock,
.qb-banner-card__dock {
  display: flex;
  align-items: center;
  gap: 8px;
}

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

.credential-row {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
}

.credential-row .el-input {
  flex: 1;
}
</style>
