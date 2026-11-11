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
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="112px">
        <el-form-item label="客户端ID" prop="clientId">
          <el-input v-model="form.clientId" placeholder="请输入客户端ID" maxlength="255" :disabled="!!form.id"/>
        </el-form-item>
        <el-form-item label="客户端密钥" prop="clientSecret">
          <el-input
            v-model="form.clientSecret"
            type="password"
            show-password
            :placeholder="form.id ? '留空表示不修改' : '请输入客户端密钥'"
            maxlength="512"
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item label="客户端名称" prop="clientName">
          <el-input v-model="form.clientName" placeholder="请输入客户端名称" maxlength="255"/>
        </el-form-item>
        <el-form-item label="客户端类型" prop="clientType">
          <el-select v-model="form.clientType" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in CLIENT_TYPE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="授权类型" prop="grantTypes">
          <el-input v-model="form.grantTypes" placeholder="逗号分隔，如 password,sms" maxlength="255"/>
        </el-form-item>
        <el-form-item label="接口路径" prop="apiPathPatterns">
          <el-input v-model="form.apiPathPatterns" placeholder="如 /**" maxlength="255"/>
        </el-form-item>
        <el-form-item label="Token时长" prop="tokenTimeout">
          <el-input-number v-model="form.tokenTimeout" :min="0" :controls="true" placeholder="秒"/>
        </el-form-item>
        <el-form-item label="校验验证码" prop="checkCaptcha">
          <c7-radio :data-list="COMMON_IS" v-model="form.checkCaptcha"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100"/>
        </el-form-item>
      </el-form>
    </C7FormDialog>
  </div>
</template>

<script setup>
/**
 * OAuth 客户端管理：列表 / 新增 / 修改 / 删除（对齐后端 SysOauthClient）。
 */
import { computed, reactive, ref } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { useDict } from '@/utils/dict'
import {
  addOauthClient,
  pageOauthClient,
  removeOauthClient,
  updateOauthClient
} from '@/api/system/oauthClient'

defineOptions({ name: 'SysOauthClient' })

const { COMMON_STATUS, COMMON_IS } = useDict('COMMON_STATUS', 'COMMON_IS')

/** 客户端类型：表注释为 public|confidential，非系统字典。 */
const CLIENT_TYPE_OPTIONS = [
  { label: 'public', value: 'public' },
  { label: 'confidential', value: 'confidential' }
]

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改OAuth客户端' : '新增OAuth客户端'))

const tableColumns = computed(() => [
  { prop: 'clientId', label: '客户端ID', minWidth: 140, showOverflowTooltip: true },
  { prop: 'clientName', label: '客户端名称', minWidth: 140, showOverflowTooltip: true },
  { prop: 'clientType', label: '类型', width: 120, showOverflowTooltip: true },
  { prop: 'grantTypes', label: '授权类型', minWidth: 140, showOverflowTooltip: true },
  { prop: 'apiPathPatterns', label: '接口路径', minWidth: 120, showOverflowTooltip: true },
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
  clientSecret: form.id
    ? []
    : [{ required: true, message: '客户端密钥不能为空', trigger: 'blur' }],
  clientName: [{ required: true, message: '客户端名称不能为空', trigger: 'blur' }],
  clientType: [{ required: true, message: '客户端类型不能为空', trigger: 'change' }],
  grantTypes: [{ required: true, message: '授权类型不能为空', trigger: 'blur' }],
  apiPathPatterns: [{ required: true, message: '接口路径不能为空', trigger: 'blur' }]
}))

function emptyForm() {
  return {
    id: undefined,
    clientId: '',
    clientSecret: '',
    clientName: '',
    clientType: 'public',
    grantTypes: 'password',
    apiPathPatterns: '/**',
    tokenTimeout: undefined,
    checkCaptcha: '1',
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
      clientId: row.clientId ?? '',
      clientSecret: '',
      clientName: row.clientName ?? '',
      clientType: row.clientType ?? 'public',
      grantTypes: row.grantTypes ?? 'password',
      apiPathPatterns: row.apiPathPatterns ?? '/**',
      tokenTimeout: row.tokenTimeout ?? undefined,
      checkCaptcha: row.checkCaptcha ?? '1',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
}

async function submitOauthClient() {
  const payload = {
    id: form.id,
    clientId: form.clientId,
    clientSecret: form.clientSecret,
    clientName: form.clientName,
    clientType: form.clientType,
    grantTypes: form.grantTypes,
    apiPathPatterns: form.apiPathPatterns,
    tokenTimeout: form.tokenTimeout,
    checkCaptcha: form.checkCaptcha,
    status: form.status,
    remark: form.remark
  }
  if (form.id) {
    await updateOauthClient(payload)
  } else {
    await addOauthClient(payload)
  }
}
</script>
