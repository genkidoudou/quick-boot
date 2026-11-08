<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageChannel"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeChannel"
      :add-button-permi="['msg:channel:add']"
      :edit-button-permi="['msg:channel:edit']"
      :delete-button-permi="['msg:channel:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['msg:channel:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Promotion"
          v-hasPermi="['msg:channel:edit']"
          @click="openTest(row)"
        >
          测试
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['msg:channel:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除渠道「${row.name || row.id}」？` })"
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
      :submit="submitChannel"
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="108px">
        <el-form-item label="渠道名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入渠道名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="渠道类型" prop="channelType">
          <c7-radio :data-list="MSG_CHANNEL_TYPE" v-model="form.channelType" />
        </el-form-item>
        <el-form-item v-if="form.channelType === '2' || form.channelType === '3'" prop="webhookUrl">
          <template #label>
            <span class="channel-field-label">
              Webhook
              <el-popover placement="top" :width="280" trigger="hover">
                <template #reference>
                  <el-icon class="channel-field-label__eye"><View /></el-icon>
                </template>
                <p class="channel-hint">通用 Webhook 按下面的请求方式把参数发到这个地址。Bean 渠道里，钉钉机器人也要填这个地址。</p>
              </el-popover>
            </span>
          </template>
          <el-input v-model="form.webhookUrl" placeholder="Webhook 地址" maxlength="512" />
        </el-form-item>
        <el-form-item v-if="form.channelType === '2'" label="请求方式" prop="webhookMethod">
          <el-select v-model="form.webhookMethod" placeholder="请选择请求方式" style="width: 100%">
            <el-option label="GET-FORM" value="GET-FORM" />
            <el-option label="POST-FORM" value="POST-FORM" />
            <el-option label="POST-JSON" value="POST-JSON" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.channelType === '2'" prop="webhookSuccess">
          <template #label>
            <span class="channel-field-label">
              成功规则
              <el-popover placement="top" :width="280" trigger="hover">
                <template #reference>
                  <el-icon class="channel-field-label__eye"><View /></el-icon>
                </template>
                <p class="channel-hint">留空则 HTTP 状态码 2xx 即成功。填写后还要看返回 JSON，例如 errcode=0、data.success=true。</p>
              </el-popover>
            </span>
          </template>
          <el-input v-model="form.webhookSuccess" placeholder="例如 errcode=0，留空只看 HTTP 状态码" maxlength="255" />
        </el-form-item>
        <el-form-item v-if="form.channelType === '3'" label="Bean 名称" prop="beanName">
          <el-input v-model="form.beanName" placeholder="例如 dingDingRobotSendMsgServiceImpl" maxlength="255" />
        </el-form-item>
        <el-form-item prop="templateContent">
          <template #label>
            <span class="channel-field-label">
              模板内容
              <el-popover placement="top" :width="280" trigger="hover">
                <template #reference>
                  <el-icon class="channel-field-label__eye"><View /></el-icon>
                </template>
                <p class="channel-hint">通用 Webhook 把这里当成 JSON 对象，占位符写在字符串值里。留空则发送标题和正文。</p>
                <ul class="channel-hint__list">
                  <li v-for="item in templateHints" :key="item.token">
                    <code>{{ item.token }}</code> {{ item.label }}
                  </li>
                </ul>
              </el-popover>
            </span>
          </template>
          <el-input v-model="form.templateContent" type="textarea" :rows="4" placeholder="留空则发送时直接用正文" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="255" />
        </el-form-item>
      </el-form>
    </C7FormDialog>

    <el-dialog v-model="testOpen" title="测试发送" width="520px" append-to-body destroy-on-close>
      <el-form ref="testFormRef" :model="testForm" :rules="testRules" label-width="88px">
        <el-form-item label="渠道">
          <el-input :model-value="testChannelName" disabled />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="testForm.title" placeholder="请输入标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="正文" prop="body">
          <el-input v-model="testForm.body" type="textarea" :rows="4" placeholder="请输入正文" />
        </el-form-item>
        <el-form-item v-if="testForm.channelType === '1'" label="用户" prop="userIds">
          <c7-select
            v-model="testForm.userIds"
            :fetch-data="fetchTestUsers"
            result-key="records"
            :data-formatter="formatTestUsers"
            label-key="label"
            value-key="id"
            multiple
            remote
            filterable
            clearable
            placeholder="请选择用户，可多选"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="消息类型" prop="msgType">
          <el-select v-model="testForm.msgType" style="width: 100%">
            <el-option v-for="item in msgTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testOpen = false">取消</el-button>
        <el-button type="primary" :loading="testLoading" @click="submitTest">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 消息渠道：列表 / 新增 / 修改 / 删除（对齐后端 MsgChannel）。
 */
import {computed, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {Delete, Edit, Promotion, View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {addChannel, pageChannel, removeChannel, testChannel, updateChannel} from '@/api/msg/channel'
import {pageUser} from '@/api/system/user'

defineOptions({name: 'MsgChannel'})

const {MSG_CHANNEL_TYPE, COMMON_STATUS} = useDict('MSG_CHANNEL_TYPE', 'COMMON_STATUS')

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改渠道' : '新增渠道'))

const templateHints = [
  {token: '{{title}}', label: '标题'},
  {token: '{{content}}', label: '正文'},
  {token: '{{address}}', label: '地址，多个用逗号'},
  {token: '{{msgType}}', label: '消息类型'}
]

const tableColumns = computed(() => [
  {prop: 'name', label: '渠道名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'channelType', label: '渠道类型', columnType: 'tag', dictList: MSG_CHANNEL_TYPE.value || [], width: 120},
  {prop: 'webhookUrl', label: 'Webhook', minWidth: 180, showOverflowTooltip: true},
  {prop: 'beanName', label: 'Bean 名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 230, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'name', label: '渠道名称', type: 'input'},
  {prop: 'channelType', label: '渠道类型', type: 'select', dataList: MSG_CHANNEL_TYPE.value || []},
  {prop: 'beanName', label: 'Bean 名称', type: 'input'},
  {prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || []}
])

const rules = computed(() => ({
  name: [{required: true, message: '渠道名称不能为空', trigger: 'blur'}],
  channelType: [{required: true, message: '渠道类型不能为空', trigger: 'change'}],
  webhookUrl: form.channelType === '2'
    ? [{required: true, message: 'Webhook 地址不能为空', trigger: 'blur'}]
    : [],
  webhookMethod: form.channelType === '2'
    ? [{required: true, message: '请求方式不能为空', trigger: 'change'}]
    : [],
  beanName: form.channelType === '3'
    ? [{required: true, message: 'Bean 名称不能为空', trigger: 'blur'}]
    : []
}))

function emptyForm() {
  return {
    id: undefined,
    name: '',
    channelType: '1',
    webhookUrl: '',
    webhookMethod: 'POST-JSON',
    webhookSuccess: '',
    beanName: '',
    templateContent: '',
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
      name: row.name ?? '',
      channelType: row.channelType ?? '1',
      webhookUrl: row.webhookUrl ?? '',
      webhookMethod: row.webhookMethod || 'POST-JSON',
      webhookSuccess: row.webhookSuccess ?? '',
      beanName: row.beanName ?? '',
      templateContent: row.templateContent ?? '',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
}

const msgTypes = [
  {value: 'TEXT', label: 'text'},
  {value: 'LINK', label: 'link'},
  {value: 'MARKDOWN', label: 'markdown'},
  {value: 'ACTION_CARD', label: 'actionCard'},
  {value: 'FEED_CARD', label: 'feedCard'}
]

const testOpen = ref(false)
const testLoading = ref(false)
const testFormRef = ref(null)
const testChannelName = ref('')
const testForm = reactive(emptyTestForm())

const testRules = computed(() => ({
  title: [{required: true, message: '标题不能为空', trigger: 'blur'}],
  body: [{required: true, message: '正文不能为空', trigger: 'blur'}],
  userIds: testForm.channelType === '1'
    ? [{
      validator: (_rule, value, callback) => {
        if (!value || !value.length) {
          callback(new Error('请选择用户'))
          return
        }
        callback()
      },
      trigger: 'change'
    }]
    : [],
  msgType: [{required: true, message: '消息类型不能为空', trigger: 'change'}]
}))

function emptyTestForm() {
  return {
    channelId: undefined,
    channelType: '1',
    title: '',
    body: '',
    userIds: [],
    msgType: 'TEXT'
  }
}

/** @param {Record<string, any>} row */
function openTest(row) {
  Object.assign(testForm, emptyTestForm(), {
    channelId: row.id,
    channelType: row.channelType ?? '1'
  })
  testChannelName.value = row.name || String(row.id)
  testOpen.value = true
}

/**
 * 用户下拉。无关键字时拉一页，输入后按用户名模糊查。
 * @param {Record<string, any>} params
 */
function fetchTestUsers(params) {
  const query = params && Object.prototype.hasOwnProperty.call(params, 'query')
    ? String(params.query || '').trim()
    : ''
  return pageUser({
    current: 1,
    size: 20,
    param: query ? {userName: query} : {}
  })
}

/** @param {Array<Record<string, any>>} list */
function formatTestUsers(list) {
  return (list || []).map((user) => ({
    ...user,
    label: [user.nickName, user.userName].filter(Boolean).join(' / ') || String(user.id)
  }))
}

async function submitTest() {
  const valid = await testFormRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  testLoading.value = true
  try {
    const res = await testChannel({
      channelId: testForm.channelId,
      title: testForm.title,
      body: testForm.body,
      address: testForm.channelType === '1' ? testForm.userIds.map((id) => String(id)) : [],
      msgType: testForm.msgType
    })
    if (res?.data === false) {
      ElMessage.error('发送失败')
      return
    }
    ElMessage.success('发送成功')
    testOpen.value = false
  } catch {
    // 请求拦截器已经弹出错误提示
  } finally {
    testLoading.value = false
  }
}

async function submitChannel() {
  const payload = {
    id: form.id,
    name: form.name,
    channelType: form.channelType,
    webhookUrl: form.channelType === '2' || form.channelType === '3' ? form.webhookUrl : '',
    webhookMethod: form.channelType === '2' ? form.webhookMethod : '',
    webhookSuccess: form.channelType === '2' ? form.webhookSuccess : '',
    beanName: form.channelType === '3' ? form.beanName : '',
    templateContent: form.templateContent,
    status: form.status,
    remark: form.remark
  }
  if (payload.id) {
    await updateChannel(payload)
  } else {
    await addChannel(payload)
  }
}
</script>

<style scoped>
.channel-field-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.channel-field-label__eye {
  color: var(--el-text-color-secondary);
  cursor: help;
  font-size: 14px;
}

.channel-hint {
  margin: 0;
  line-height: 1.5;
}

.channel-hint__list {
  margin: 8px 0 0;
  padding-left: 18px;
  line-height: 1.7;
}

.channel-hint__list code {
  font-family: inherit;
}
</style>
