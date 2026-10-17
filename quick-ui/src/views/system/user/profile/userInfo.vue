<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="用户昵称" prop="nickName">
      <el-input v-model="form.nickName" maxlength="30" />
    </el-form-item>
    <el-form-item label="手机号码" prop="phonenumber">
      <el-input v-model="form.phonenumber" maxlength="11" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" maxlength="50" />
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
        <el-radio value="0">男</el-radio>
        <el-radio value="1">女</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUserProfile } from '@/api/system/user'
import { getCurrentInstance } from 'vue'
import { isEmail, isPhone, requiredRule } from '@/utils/validate'

defineOptions({ name: 'ProfileUserInfo' })

const props = defineProps({
  user: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['updated'])

const { proxy } = getCurrentInstance()
const formRef = ref(null)
const submitting = ref(false)
const form = reactive({
  nickName: '',
  phonenumber: '',
  email: '',
  sex: '0'
})

const rules = {
  nickName: [requiredRule('用户昵称不能为空')],
  email: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!isEmail(value)) {
          callback(new Error('请输入正确的邮箱地址'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  phonenumber: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!isPhone(value)) {
          callback(new Error('请输入正确的手机号码'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

watch(
  () => props.user,
  (user) => {
    form.nickName = user?.nickName || ''
    form.phonenumber = user?.phonenumber || ''
    form.email = user?.email || ''
    form.sex = user?.sex ?? '0'
  },
  { immediate: true, deep: true }
)

async function submit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    await updateUserProfile({
      nickName: form.nickName,
      phonenumber: form.phonenumber,
      email: form.email,
      sex: form.sex
    })
    ElMessage.success('修改成功')
    emit('updated')
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
