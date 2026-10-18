<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageFileClassify"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeFileClassify"
      :add-button-permi="['system:fileClassify:add']"
      :edit-button-permi="['system:fileClassify:edit']"
      :delete-button-permi="['system:fileClassify:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['system:fileClassify:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['system:fileClassify:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除文件分类「${row.classifyName || row.classifyCode || row.id}」？` })"
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
      :submit="submitFileClassify"
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="分类编码" prop="classifyCode">
          <el-input
            v-model="form.classifyCode"
            placeholder="如 avatar、contract"
            maxlength="64"
            :disabled="!!form.id"
            @blur="normalizeClassifyCode"
          />
          <div class="form-tip">上传路径参数，建议小写字母、数字、下划线；保存后不可改</div>
        </el-form-item>
        <el-form-item label="分类名称" prop="classifyName">
          <el-input v-model="form.classifyName" placeholder="如 用户头像、合同附件" maxlength="255" />
        </el-form-item>
        <el-form-item label="允许类型" prop="allowTypeList">
          <el-checkbox-group v-model="form.allowTypeList" class="file-type-group">
            <el-checkbox
              v-for="item in fileTypes"
              :key="item.key"
              :label="item.key"
              border
              class="file-type-item"
            >
              <span class="file-type-item__label">{{ item.label }}</span>
              <span class="file-type-item__exts">{{ item.exts.join(',') }}</span>
            </el-checkbox>
          </el-checkbox-group>
          <div v-if="!fileTypes.length" class="form-tip">请先在字典管理中维护 FILE_ALLOW_TYPE</div>
          <div class="form-tip">
            勾选后按字典值自动转成后缀；将保存为：{{ allowExtPreview || '（请选择类型）' }}
          </div>
        </el-form-item>
        <el-form-item label="大小上限" prop="maxSizeValue">
          <div class="size-input">
            <el-input-number
              v-model="form.maxSizeValue"
              :min="1"
              :max="sizeInputMax"
              :precision="form.maxSizeUnit === 'MB' ? 2 : 0"
              :step="form.maxSizeUnit === 'MB' ? 1 : 100"
              controls-position="right"
              class="size-input__number"
            />
            <el-select v-model="form.maxSizeUnit" class="size-input__unit" @change="onSizeUnitChange">
              <el-option label="KB" value="KB" />
              <el-option label="MB" value="MB" />
            </el-select>
          </div>
          <div class="form-tip">入库仍为字节；当前约 {{ formatBytes(sizeToBytes(form.maxSizeValue, form.maxSizeUnit)) }}</div>
        </el-form-item>
        <el-form-item label="每次个数" prop="maxCount">
          <el-input-number
            v-model="form.maxCount"
            :min="1"
            :max="100"
            :step="1"
            controls-position="right"
          />
          <div class="form-tip">单次请求最多上传几个文件</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="是否匿名" prop="anonymous">
          <c7-radio :data-list="COMMON_IS" v-model="form.anonymous" />
          <div class="form-tip">选「是」表示该分类上传可不登录（需后端配合校验）</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100" />
        </el-form-item>
      </el-form>
    </C7FormDialog>
  </div>
</template>

<script setup>
/**
 * 文件分类：列表 / 新增 / 修改 / 删除（对齐后端 SysFileClassify）。
 */
import {computed, reactive, ref, watch} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {useDict} from '@/utils/dict'
import {addFileClassify, pageFileClassify, removeFileClassify, updateFileClassify} from '@/api/system/fileClassify'

defineOptions({name: 'SysFileClassify'})

/** 文件允许类型字典：标签为类型名，字典值为逗号分隔后缀。 */
const FILE_ALLOW_TYPE_CODE = 'FILE_ALLOW_TYPE'

const {COMMON_STATUS, COMMON_IS, FILE_ALLOW_TYPE} = useDict('COMMON_STATUS', 'COMMON_IS', FILE_ALLOW_TYPE_CODE)

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())
/** 编辑时的原始后缀，字典异步到达后用来反推勾选。 */
const sourceAllowExt = ref('')
const typeInferred = ref(true)
/** 切换单位时用于换算，避免数值语义突变 */
const lastSizeUnit = ref('MB')

const title = computed(() => (form.id ? '修改文件分类' : '新增文件分类'))

const sizeInputMax = computed(() => (form.maxSizeUnit === 'MB' ? 1024 : 1024 * 1024))

/** 字典项：label=类型名，value=后缀列表。 */
const fileTypes = computed(() => (FILE_ALLOW_TYPE.value || [])
  .map((item) => ({
    key: item.value,
    label: item.label,
    exts: normalizeExtList(String(item.value ?? '').split(','))
  }))
  .filter((item) => item.label && item.exts.length))

/** 勾选类型后展开的后缀预览 */
const allowExtPreview = computed(() => typesToExtList(form.allowTypeList).join(','))

const tableColumns = computed(() => [
  {prop: 'classifyCode', label: '分类编码', minWidth: 140, showOverflowTooltip: true},
  {prop: 'classifyName', label: '分类名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'allowExt', label: '允许后缀', minWidth: 180, showOverflowTooltip: true},
  {
    prop: 'maxSizeBytes',
    label: '大小上限',
    width: 120,
    formatter: (row) => formatBytes(row.maxSizeBytes)
  },
  {prop: 'maxCount', label: '每次个数', width: 100},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100},
  {prop: 'anonymous', label: '是否匿名', columnType: 'tag', dictList: COMMON_IS.value || [], width: 110},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'classifyCode', label: '分类编码', type: 'input'},
  {prop: 'classifyName', label: '分类名称', type: 'input'},
  {prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || []},
  {prop: 'anonymous', label: '是否匿名', type: 'select', dataList: COMMON_IS.value || []}
])

const rules = computed(() => ({
  classifyCode: [
    {required: true, message: '分类编码不能为空', trigger: 'blur'},
    {
      pattern: /^[a-z][a-z0-9_]{0,63}$/,
      message: '以小写字母开头，仅小写字母、数字、下划线',
      trigger: 'blur'
    }
  ],
  classifyName: [{required: true, message: '分类名称不能为空', trigger: 'blur'}],
  allowTypeList: [
    {
      type: 'array',
      required: true,
      min: 1,
      message: '请至少选择一种文件类型',
      trigger: 'change'
    }
  ],
  maxSizeValue: [
    {required: true, message: '大小上限不能为空', trigger: 'change'},
    {
      validator: (_rule, value, callback) => {
        if (value == null || value <= 0) {
          callback(new Error('大小上限必须大于 0'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  maxCount: [{required: true, message: '每次上传个数不能为空', trigger: 'change'}],
  status: [{required: true, message: '状态不能为空', trigger: 'change'}],
  anonymous: [{required: true, message: '是否可以匿名不能为空', trigger: 'change'}]
}))

function emptyForm() {
  return {
    id: undefined,
    classifyCode: '',
    classifyName: '',
    allowTypeList: [],
    maxSizeValue: 10,
    maxSizeUnit: 'MB',
    maxCount: 1,
    status: '0',
    anonymous: '0',
    remark: ''
  }
}

/**
 * 类型键列表 → 去重后缀列表。
 * @param {string[]} typeKeys
 * @returns {string[]}
 */
function typesToExtList(typeKeys) {
  const keys = Array.isArray(typeKeys) ? typeKeys : []
  const seen = new Set()
  const out = []
  for (const key of keys) {
    const item = fileTypes.value.find((t) => t.key === key)
    if (!item) {
      continue
    }
    for (const ext of item.exts) {
      if (seen.has(ext)) {
        continue
      }
      seen.add(ext)
      out.push(ext)
    }
  }
  return out
}

/**
 * 规范化后缀：去点、小写、去重。
 * @param {unknown} list
 * @returns {string[]}
 */
function normalizeExtList(list) {
  const raw = Array.isArray(list) ? list : []
  const seen = new Set()
  const out = []
  for (const item of raw) {
    let ext = String(item ?? '').trim().toLowerCase()
    if (ext.startsWith('.')) {
      ext = ext.slice(1)
    }
    if (!ext || seen.has(ext)) {
      continue
    }
    if (!/^[a-z0-9]{1,16}$/.test(ext)) {
      continue
    }
    seen.add(ext)
    out.push(ext)
  }
  return out
}

/**
 * 已存后缀反推勾选类型：优先完整覆盖，再按包含关系回退。
 * @param {string[]} extList
 * @returns {string[]}
 */
function inferTypesFromExts(extList) {
  const exts = normalizeExtList(extList)
  if (!exts.length) {
    return []
  }
  const remaining = new Set(exts)
  const matched = []

  const types = fileTypes.value
  const ordered = [...types].sort((a, b) => b.exts.length - a.exts.length)
  for (const type of ordered) {
    if (type.exts.length && type.exts.every((e) => remaining.has(e))) {
      matched.push(type.key)
      type.exts.forEach((e) => remaining.delete(e))
    }
  }

  if (remaining.size) {
    for (const type of types) {
      if (matched.includes(type.key)) {
        continue
      }
      const overlap = [...remaining].filter((e) => type.exts.includes(e))
      if (!overlap.length) {
        continue
      }
      matched.push(type.key)
      overlap.forEach((e) => remaining.delete(e))
    }
  }

  return matched
}

function normalizeClassifyCode() {
  form.classifyCode = String(form.classifyCode ?? '')
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9_]/g, '')
}

/**
 * @param {number|null|undefined} value
 * @param {'KB'|'MB'} unit
 * @returns {number|undefined}
 */
function sizeToBytes(value, unit) {
  if (value == null || Number.isNaN(Number(value))) {
    return undefined
  }
  const n = Number(value)
  if (n <= 0) {
    return undefined
  }
  if (unit === 'KB') {
    return Math.round(n * 1024)
  }
  return Math.round(n * 1024 * 1024)
}

/**
 * @param {number|null|undefined} bytes
 * @returns {{ value: number|undefined, unit: 'KB'|'MB' }}
 */
function bytesToSize(bytes) {
  if (bytes == null || bytes <= 0) {
    return {value: 10, unit: 'MB'}
  }
  if (bytes % (1024 * 1024) === 0) {
    return {value: bytes / (1024 * 1024), unit: 'MB'}
  }
  if (bytes % 1024 === 0 && bytes < 1024 * 1024) {
    return {value: bytes / 1024, unit: 'KB'}
  }
  if (bytes >= 1024 * 1024) {
    return {value: Math.round((bytes / (1024 * 1024)) * 100) / 100, unit: 'MB'}
  }
  return {value: Math.max(1, Math.round(bytes / 1024)), unit: 'KB'}
}

/**
 * @param {number|null|undefined} bytes
 * @returns {string}
 */
function formatBytes(bytes) {
  if (bytes == null || bytes <= 0) {
    return '—'
  }
  if (bytes >= 1024 * 1024) {
    const mb = bytes / (1024 * 1024)
    return `${Number.isInteger(mb) ? mb : mb.toFixed(2)} MB`
  }
  if (bytes >= 1024) {
    const kb = bytes / 1024
    return `${Number.isInteger(kb) ? kb : kb.toFixed(1)} KB`
  }
  return `${bytes} B`
}

function onSizeUnitChange(unit) {
  const bytes = sizeToBytes(form.maxSizeValue, lastSizeUnit.value)
  const next = bytesToSize(bytes)
  form.maxSizeUnit = unit
  if (unit === next.unit) {
    form.maxSizeValue = next.value
  } else if (unit === 'MB' && bytes != null) {
    form.maxSizeValue = Math.max(0.01, Math.round((bytes / (1024 * 1024)) * 100) / 100)
  } else if (unit === 'KB' && bytes != null) {
    form.maxSizeValue = Math.max(1, Math.round(bytes / 1024))
  }
  lastSizeUnit.value = unit
}

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
  lastSizeUnit.value = 'MB'
  sourceAllowExt.value = ''
  typeInferred.value = true
  if (row && row.id != null) {
    const size = bytesToSize(row.maxSizeBytes)
    sourceAllowExt.value = String(row.allowExt ?? '')
    const inferred = fileTypes.value.length
      ? inferTypesFromExts(sourceAllowExt.value.split(','))
      : []
    typeInferred.value = !sourceAllowExt.value || inferred.length > 0 || fileTypes.value.length > 0
    Object.assign(form, {
      id: row.id,
      classifyCode: row.classifyCode ?? '',
      classifyName: row.classifyName ?? '',
      allowTypeList: inferred,
      maxSizeValue: size.value,
      maxSizeUnit: size.unit,
      maxCount: row.maxCount ?? 1,
      status: row.status ?? '0',
      anonymous: row.anonymous ?? '0',
      remark: row.remark ?? ''
    })
    lastSizeUnit.value = size.unit
  }
}

watch(fileTypes, (types) => {
  if (typeInferred.value || !sourceAllowExt.value || !types.length) {
    return
  }
  form.allowTypeList = inferTypesFromExts(sourceAllowExt.value.split(','))
  typeInferred.value = true
})

async function submitFileClassify() {
  normalizeClassifyCode()
  const payload = {
    id: form.id,
    classifyCode: form.classifyCode,
    classifyName: form.classifyName,
    allowExt: typesToExtList(form.allowTypeList).join(','),
    maxSizeBytes: sizeToBytes(form.maxSizeValue, form.maxSizeUnit),
    maxCount: form.maxCount,
    status: form.status,
    anonymous: form.anonymous,
    remark: form.remark
  }
  if (payload.id) {
    await updateFileClassify(payload)
  } else {
    await addFileClassify(payload)
  }
}
</script>

<style scoped>
.form-tip {
  margin-top: 4px;
  font-size: 12px;
  line-height: 1.4;
  color: var(--el-text-color-secondary);
}

.file-type-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}

.file-type-item {
  margin: 0 !important;
  height: auto !important;
  padding: 8px 12px !important;
}

.file-type-item__label {
  display: block;
  font-weight: 600;
  line-height: 1.2;
}

.file-type-item__exts {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  line-height: 1.3;
  color: var(--el-text-color-secondary);
  font-weight: 400;
  white-space: normal;
  max-width: 160px;
}

.size-input {
  display: flex;
  gap: 8px;
  width: 100%;
}

.size-input__number {
  flex: 1;
}

.size-input__unit {
  width: 88px;
}
</style>
