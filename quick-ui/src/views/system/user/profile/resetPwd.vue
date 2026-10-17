<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input
        v-model="form.oldPassword"
        placeholder="请输入旧密码"
        type="password"
        show-password
        autocomplete="current-password"
      />
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input
        v-model="form.newPassword"
        placeholder="至少8位，含大小写、数字与特殊字符"
        type="password"
        show-password
        autocomplete="new-password"
      />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input
        v-model="form.confirmPassword"
        placeholder="请确认新密码"
        type="password"
        show-password
        autocomplete="new-password"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { updatePassword } from '@/api/system/user'
import { passwordRules, requiredRule } from '@/utils/validate'
import useUserStore from '@/store/modules/user'

defineOptions({ name: 'ProfileResetPwd' })

const { proxy } = getCurrentInstance()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  oldPassword: [requiredRule('旧密码不能为空')],
  newPassword: [
    ...passwordRules({ required: true }),
    {
      validator: (_rule, value, callback) => {
        if (value && value === form.oldPassword) {
          callback(new Error('新密码不能与旧密码相同'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    requiredRule('确认密码不能为空'),
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

async function submit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    await updatePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    userStore.defaultPassword = false
    userStore.passwordExpired = false
    ElMessage.success('修改成功')
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    formRef.value?.clearValidate?.()
  } catch {
    // 业务错误已由 request 拦截器提示
  } finally {
    submitting.value = false
  }
}

function close() {
  proxy?.$tab?.closePage()
}
</script>
