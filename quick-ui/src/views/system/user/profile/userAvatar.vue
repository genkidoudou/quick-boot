<template>
  <div class="user-info-head" title="点击修改头像" @click="openDialog">
    <img :src="avatar" class="img-circle img-lg" alt="avatar" />
    <div class="user-info-head__mask">修改头像</div>
  </div>

  <el-dialog v-model="open" title="修改头像" width="420px" append-to-body @closed="reset">
    <div class="avatar-editor">
      <img :src="previewUrl" class="avatar-editor__img" alt="头像预览" />
      <el-button @click="pickFile">选择图片</el-button>
      <div class="form-tip">支持 jpg、jpeg、png、gif、webp，不超过 2MB</div>
      <input
        ref="fileInputRef"
        class="avatar-editor__input"
        type="file"
        accept=".jpg,.jpeg,.png,.gif,.webp"
        @change="onFileChange"
      />
    </div>
    <template #footer>
      <el-button @click="open = false">取消</el-button>
      <el-button type="primary" :loading="uploading" @click="confirmUpload">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 个人中心头像：选择图片后在弹框内预览，确认后再上传。
 */
import {computed, onBeforeUnmount, ref} from 'vue'
import {ElMessage} from 'element-plus'
import useUserStore from '@/store/modules/user'
import {uploadAvatar} from '@/api/system/user'

defineOptions({name: 'ProfileUserAvatar'})

const MAX_BYTES = 2 * 1024 * 1024
const IMAGE_EXT = ['jpg', 'jpeg', 'png', 'gif', 'webp']

const userStore = useUserStore()
const avatar = computed(() => userStore.avatar)

const open = ref(false)
const uploading = ref(false)
const fileInputRef = ref(null)
const selectedFile = ref(null)
const objectUrl = ref('')

const previewUrl = computed(() => objectUrl.value || avatar.value)

function openDialog() {
  reset()
  open.value = true
}

function pickFile() {
  fileInputRef.value?.click()
}

function onFileChange(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) {
    return
  }
  const ext = file.name.includes('.') ? file.name.split('.').pop().toLowerCase() : ''
  if (!IMAGE_EXT.includes(ext)) {
    ElMessage.warning('请选择 jpg、jpeg、png、gif 或 webp 图片')
    return
  }
  if (file.size > MAX_BYTES) {
    ElMessage.warning('图片不能超过 2MB')
    return
  }
  revokeObjectUrl()
  selectedFile.value = file
  objectUrl.value = URL.createObjectURL(file)
}

function revokeObjectUrl() {
  if (objectUrl.value) {
    URL.revokeObjectURL(objectUrl.value)
    objectUrl.value = ''
  }
}

function reset() {
  uploading.value = false
  selectedFile.value = null
  revokeObjectUrl()
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

async function confirmUpload() {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }
  uploading.value = true
  try {
    await uploadAvatar(selectedFile.value)
    await userStore.getInfo()
    ElMessage.success('头像已更新')
    open.value = false
  } finally {
    uploading.value = false
  }
}

onBeforeUnmount(revokeObjectUrl)
</script>

<style scoped lang="scss">
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
  cursor: pointer;
}

.img-circle {
  border-radius: 50%;
}

.img-lg {
  width: 120px;
  height: 120px;
  object-fit: cover;
}

.user-info-head__mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #fff;
  font-size: 13px;
  background: rgba(0, 0, 0, 0.45);
  opacity: 0;
  transition: opacity 0.2s;
}

.user-info-head:hover .user-info-head__mask {
  opacity: 1;
}

.avatar-editor {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-editor__img {
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 50%;
  border: 1px solid var(--el-border-color);
  background: var(--el-fill-color-light);
}

.avatar-editor__input {
  display: none;
}

.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
