<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageFile"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      :delete-function="removeFile"
      :delete-button-permi="['system:file:remove']"
    >
      <template #toolbar-left>
        <el-button
          type="primary"
          plain
          :icon="Upload"
          v-hasPermi="['system:file:add']"
          @click="openUpload"
        >
          上传
        </el-button>
      </template>
      <template #fileUrl="{ row }">
        <C7Copy v-if="row.path" mode="text" :text="row.path" />
        <span v-else>—</span>
      </template>
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['system:file:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除文件「${row.originalName || row.id}」？` })"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <el-dialog
      v-model="uploadOpen"
      title="上传文件"
      width="560px"
      append-to-body
      destroy-on-close
      @closed="resetUpload"
    >
      <el-form label-width="96px">
        <el-form-item label="上传分类" required>
          <el-select v-model="uploadClassify" placeholder="请选择分类" style="width: 100%" @change="onClassifyChange">
            <el-option
              v-for="item in classifies"
              :key="item.classifyCode"
              :label="`${item.classifyName}（${item.classifyCode}）`"
              :value="item.classifyCode"
            />
          </el-select>
          <div v-if="currentClassify" class="form-tip">
            后缀 {{ currentClassify.allowExt || '—' }}，
            单文件不超过 {{ formatBytes(currentClassify.maxSizeBytes) }}，
            每次最多 {{ currentClassify.maxCount || 1 }} 个
          </div>
        </el-form-item>
        <el-form-item label="文件">
          <el-upload
            ref="uploadRef"
            v-model:file-list="fileList"
            drag
            :auto-upload="false"
            :disabled="!uploadClassify || uploading"
            :accept="acceptAttr"
            :limit="currentClassify?.maxCount || 1"
            :on-exceed="onExceed"
          >
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div>将文件拖到此处，或点击选择</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadOpen = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="confirmUpload">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 文件管理：列表预览 / 复制地址 / 按分类上传 / 删除（对齐后端 SysFile）。
 */
import {computed, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {Delete, Upload, UploadFilled} from '@element-plus/icons-vue'
import C7Copy from '@/packages/C7Copy/index.vue'
import {listUploadClassifies, pageFile, removeFile, uploadFile} from '@/api/system/file'

defineOptions({name: 'SysFile'})

const tableRef = ref(null)
const uploadRef = ref(null)
const uploadOpen = ref(false)
const uploading = ref(false)
const uploadClassify = ref('')
const classifies = ref([])
const fileList = ref([])

const currentClassify = computed(() =>
  classifies.value.find((item) => item.classifyCode === uploadClassify.value) || null
)

const acceptAttr = computed(() => {
  const ext = currentClassify.value?.allowExt
  if (!ext) {
    return ''
  }
  return String(ext)
    .split(',')
    .map((s) => s.trim().toLowerCase())
    .filter(Boolean)
    .map((item) => (item.startsWith('.') ? item : `.${item}`))
    .join(',')
})

const tableColumns = computed(() => [
  {prop: 'originalName', label: '原始文件名', minWidth: 160, showOverflowTooltip: true},
  {prop: 'classifyCode', label: '分类编码', minWidth: 120, showOverflowTooltip: true},
  {prop: 'path', label: '预览', columnType: 'image', width: 88, height: 48},
  {prop: 'path', label: '访问地址', columnType: 'slot', slotName: 'fileUrl', minWidth: 240},
  {prop: 'ext', label: '后缀', width: 90},
  {prop: 'sizeBytes', label: '文件大小', width: 120, formatter: (row) => formatBytes(row.sizeBytes)},
  {prop: 'createName', label: '上传人', width: 120, showOverflowTooltip: true},
  {prop: 'createTime', label: '上传时间', width: 170},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 100, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'classifyCode', label: '分类编码', type: 'input'},
  {prop: 'originalName', label: '原始文件名', type: 'input'},
  {prop: 'ext', label: '后缀', type: 'input'}
])

/**
 * @param {number|null|undefined} bytes
 * @returns {string}
 */
function formatBytes(bytes) {
  if (bytes == null || bytes < 0) {
    return '—'
  }
  if (bytes === 0) {
    return '0 B'
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

function openUpload() {
  resetUpload()
  uploadOpen.value = true
  loadClassifies()
}

async function loadClassifies() {
  const res = await listUploadClassifies()
  classifies.value = Array.isArray(res?.data) ? res.data : []
  if (!uploadClassify.value && classifies.value.length === 1) {
    uploadClassify.value = classifies.value[0].classifyCode
  }
}

function onClassifyChange() {
  fileList.value = []
  uploadRef.value?.clearFiles()
}

function onExceed() {
  const max = currentClassify.value?.maxCount || 1
  ElMessage.warning(`最多选择 ${max} 个文件`)
}

function resetUpload() {
  uploadClassify.value = ''
  uploading.value = false
  fileList.value = []
  uploadRef.value?.clearFiles()
}

async function confirmUpload() {
  if (!uploadClassify.value) {
    ElMessage.warning('请先选择上传分类')
    return
  }
  const files = fileList.value.map((item) => item.raw).filter(Boolean)
  if (!files.length) {
    ElMessage.warning('请先选择文件')
    return
  }
  uploading.value = true
  try {
    for (const file of files) {
      await uploadFile(file, uploadClassify.value)
    }
    ElMessage.success('上传成功')
    tableRef.value?.refreshData()
    uploadOpen.value = false
  } finally {
    uploading.value = false
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

.upload-icon {
  font-size: 28px;
  color: var(--el-text-color-secondary);
}
</style>
