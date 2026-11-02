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
        <el-form-item v-if="form.channelType === '2'" prop="webhookUrl">
          <template #label>
            <span class="channel-field-label">
              Webhook
              <el-popover placement="top" :width="260" trigger="hover">
                <template #reference>
                  <el-icon class="channel-field-label__eye"><View /></el-icon>
                </template>
                <p class="channel-hint">发送时固定用 POST，把标题和正文发到这个地址。</p>
              </el-popover>
            </span>
          </template>
          <el-input v-model="form.webhookUrl" placeholder="Webhook 地址" maxlength="512" />
        </el-form-item>
        <el-form-item v-if="form.channelType === '3'" label="Bean 名称" prop="beanName">
          <el-input v-model="form.beanName" placeholder="Spring Bean 名称" maxlength="255" />
        </el-form-item>
        <el-form-item prop="templateContent">
          <template #label>
            <span class="channel-field-label">
              模板内容
              <el-popover placement="top" :width="280" trigger="hover">
                <template #reference>
                  <el-icon class="channel-field-label__eye"><View /></el-icon>
                </template>
                <p class="channel-hint">留空则直接发送正文。填写后按每个对象替换：</p>
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
  </div>
</template>

<script setup>
/**
 * 消息渠道：列表 / 新增 / 修改 / 删除（对齐后端 MsgChannel）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit, View} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {addChannel, pageChannel, removeChannel, updateChannel} from '@/api/msg/channel'

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
  {token: '{{userId}}', label: '用户'},
  {token: '{{deptId}}', label: '部门'},
  {token: '{{groupId}}', label: '群组'},
  {token: '{{name}}', label: '名称'},
  {token: '{{phone}}', label: '手机'},
  {token: '{{email}}', label: '邮箱'}
]

const tableColumns = computed(() => [
  {prop: 'name', label: '渠道名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'channelType', label: '渠道类型', columnType: 'tag', dictList: MSG_CHANNEL_TYPE.value || [], width: 120},
  {prop: 'webhookUrl', label: 'Webhook', minWidth: 180, showOverflowTooltip: true},
  {prop: 'beanName', label: 'Bean 名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'name', label: '渠道名称', type: 'input'},
  {prop: 'channelType', label: '渠道类型', type: 'select', dataList: MSG_CHANNEL_TYPE.value || []},
  {prop: 'beanName', label: 'Bean 名称', type: 'input'},
  {prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || []}
])

const rules = computed(() => ({
  name: [{required: true, message: '渠道名称不能为空', trigger: 'blur'}],
  channelType: [{required: true, message: '渠道类型不能为空', trigger: 'change'}]
}))

function emptyForm() {
  return {
    id: undefined,
    name: '',
    channelType: '1',
    webhookUrl: '',
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
      beanName: row.beanName ?? '',
      templateContent: row.templateContent ?? '',
      status: row.status ?? '0',
      remark: row.remark ?? ''
    })
  }
}

async function submitChannel() {
  const payload = {
    id: form.id,
    name: form.name,
    channelType: form.channelType,
    webhookUrl: form.channelType === '2' ? form.webhookUrl : '',
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
