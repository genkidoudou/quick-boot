<template>
  <el-dialog
      :model-value="visible"
      :title="dialogTitle"
      width="460px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      append-to-body
      destroy-on-close
  >
    <el-alert
        type="warning"
        :closable="false"
        show-icon
        class="mb-3"
        :title="dialogAlert"
    />
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input
            v-model="form.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码"
            autocomplete="current-password"
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
            v-model="form.newPassword"
            type="password"
            show-password
            placeholder="至少8位，含大小写、数字与特殊字符"
            autocomplete="new-password"
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
            autocomplete="new-password"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleLogout">退出登录</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 强制改密弹窗：默认密码或密码过期时弹出，不可关闭；改密成功后清除标记。
 */
import {computed, reactive, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {passwordRules, requiredRule} from '@/utils/validate'
import {updatePassword} from '@/api/system/user'
import useUserStore from '@/store/modules/user'

defineOptions({name: 'ForceChangePasswordDialog'})

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)

const visible = computed(() => !!userStore.defaultPassword || !!userStore.passwordExpired)

const dialogTitle = computed(() => {
  if (userStore.defaultPassword && userStore.passwordExpired) {
    return '请修改密码'
  }
  if (userStore.defaultPassword) {
    return '当前为默认密码，请修改密码'
  }
  return '密码已过期，请修改密码'
})

const dialogAlert = computed(() => {
  if (userStore.defaultPassword && userStore.passwordExpired) {
    return '当前为默认密码且已过期，修改后才能继续使用系统'
  }
  if (userStore.defaultPassword) {
    return '首次登录或重置后请立即修改默认密码'
  }
  return '密码已失效，修改后才能继续使用系统'
})

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  oldPassword: [requiredRule('原密码不能为空')],
  newPassword: [
    ...passwordRules({required: true}),
    {
      validator: (_rule, value, callback) => {
        if (value && value === form.oldPassword) {
          callback(new Error('新密码不能与原密码相同'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    requiredRule('请再次输入新密码'),
    {
      validator: (_rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

watch(visible, (open) => {
  if (open) {
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    formRef.value?.clearValidate?.()
  }
})

async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    await updatePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    userStore.defaultPassword = false
    userStore.passwordExpired = false
    ElMessage.success('密码修改成功')
  } catch {
    // 业务错误已由 request 拦截器提示
  } finally {
    submitting.value = false
  }
}

async function handleLogout() {
  await userStore.logOut()
  router.push('/login')
}
</script>
