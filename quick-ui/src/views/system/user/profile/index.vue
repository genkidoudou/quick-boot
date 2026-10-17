<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="text-center">
            <user-avatar />
          </div>
          <ul class="list-group list-group-striped">
            <li class="list-group-item">
              用户名称
              <div class="pull-right">{{ user.userName || '-' }}</div>
            </li>
            <li class="list-group-item">
              手机号码
              <div class="pull-right">{{ user.phonenumber || '-' }}</div>
            </li>
            <li class="list-group-item">
              用户邮箱
              <div class="pull-right">{{ user.email || '-' }}</div>
            </li>
            <li class="list-group-item">
              所属部门
              <div class="pull-right">{{ user.deptName || '-' }}</div>
            </li>
            <li class="list-group-item">
              所属角色
              <div class="pull-right">{{ roleGroup || '-' }}</div>
            </li>
            <li class="list-group-item">
              创建日期
              <div class="pull-right">{{ user.createTime || '-' }}</div>
            </li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <template #header>
            <span>基本资料</span>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <user-info :user="user" @updated="loadProfile" />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <reset-pwd />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * 个人中心：展示资料、编辑基本信息、修改密码。
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { getUserProfile } from '@/api/system/user'
import UserAvatar from './userAvatar.vue'
import UserInfo from './userInfo.vue'
import ResetPwd from './resetPwd.vue'

defineOptions({ name: 'Profile' })

const activeTab = ref('userinfo')
const user = reactive({})

const roleGroup = computed(() => {
  const names = user.roleNames
  return Array.isArray(names) && names.length ? names.join('、') : ''
})

async function loadProfile() {
  const res = await getUserProfile()
  const data = res?.data || {}
  Object.keys(user).forEach((key) => delete user[key])
  Object.assign(user, data)
}

onMounted(loadProfile)
</script>

<style scoped lang="scss">
.text-center {
  text-align: center;
  margin-bottom: 16px;
}

.list-group {
  padding-left: 0;
  list-style: none;
  margin: 0;
}

.list-group-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  padding: 12px 0;
  font-size: 13px;
  color: var(--el-text-color-regular);

  &:last-child {
    border-bottom: none;
  }
}

.pull-right {
  color: var(--el-text-color-primary);
  text-align: right;
  word-break: break-all;
}
</style>
