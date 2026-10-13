<template>
  <div class="app-container">
    <C7JsonTable
        ref="tableRef"
        :list-function="pageUser"
        :table-columns="tableColumns"
        :search-columns="searchColumns"
        row-key="id"
        show-add-button
        show-edit-button
        :delete-function="removeUser"
        :add-button-permi="['system:user:add']"
        :edit-button-permi="['system:user:edit']"
        :delete-button-permi="['system:user:remove']"
        @add-click="() => formDialogRef?.open()"
        @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #roleNames="{row}">
        <el-tag type="primary" v-for="roleName  in row.roleNames">{{ roleName }}</el-tag>
      </template>
      <template #actions="{ row }">
        <el-button link type="primary" :icon="Edit" v-hasPermi="['system:user:edit']" @click="formDialogRef?.open(row)">
          修改
        </el-button>
        <el-button
            link
            type="primary"
            :icon="Delete"
            v-hasPermi="['system:user:remove']"
            @click="tableRef?.removeRows(row, { confirmMessage: `确认删除用户「${row.userName || row.nickName || row.id}」？` })"
        >
          删除
        </el-button>
        <el-button
            link
            type="primary"
            :icon="Edit"
            v-hasPermi="['system:user:resetPassword']"
            @click="resetUserPassword(row)"
        >
          重置密码
        </el-button>
      </template>
    </C7JsonTable>

    <C7FormDialog
        ref="formDialogRef"
        v-model="open"
        :title="title"
        width="640px"
        mobile-mode="drawer"
        :form="form"
        :form-ref="formRef"
        :get-payload="getPayload"
        :submit="submitUser"
        :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px" label-position="right">
        <el-row :gutter="16">
          <el-col :span="24" :sm="12">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="form.userName" placeholder="字母或数字，大于6位" maxlength="255" :disabled="!!form.id"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入昵称" maxlength="255"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="角色" prop="roleIds">
              <c7-select
                  v-model="form.roleIds"
                  :fetch-data="assignableRoles"
                  label-key="roleName"
                  value-key="id"
                  auto-load
                  multiple
                  clearable
                  filterable
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="密码" prop="password">
              <el-input
                  v-model="form.password"
                  type="password"
                  show-password
                  :placeholder="form.id ? '不修改请留空' : '请输入密码'"
                  maxlength="255"
                  autocomplete="new-password"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="部门" prop="deptId">
              <C7TreeSelect
                  v-model="form.deptId"
                  :fetch-data="treeselectDept"
                  value-key="id"
                  label-key="name"
                  children-key="children"
                  auto-load
                  check-strictly
                  filterable
                  clearable
                  placeholder="请选择部门"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="手机号码" maxlength="11"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="邮箱" maxlength="255"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="用户类型" prop="userType">
              <c7-radio :data-list="user_type" v-model="form.userType"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" :sm="12">
            <el-form-item label="性别" prop="sex">
              <c7-radio :data-list="sex" v-model="form.sex"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="user_status" v-model="form.status"/>
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
 * 系统用户：列表 / 新增 / 修改 / 删除（对齐后端 SysUser）。
 */
import {computed, reactive, ref} from 'vue'
import {Delete, Edit} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useDict} from '@/utils/dict'
import {
  emailRules,
  nickNameRules,
  passwordRules,
  phoneRules,
  requiredRule,
  userNameRules
} from '@/utils/validate'
import {treeselectDept} from '@/api/system/dept'
import {addUser, getUser, openSafe, pageUser, removeUser, resetPassword, updateUser} from '@/api/system/user'
import {assignableRoles} from '@/api/system/role'
import C7Select from '@/packages/C7Select/index.vue'

defineOptions({name: 'SysUser'})

const {user_type, sex, user_status} = useDict('user_type', 'sex', 'user_status')

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())
const title = computed(() => (form.id ? '修改用户' : '新增用户'))

const tableColumns = computed(() => [
  {prop: 'userName', label: '用户名', minWidth: 120, showOverflowTooltip: true},
  {prop: 'nickName', label: '昵称', minWidth: 100, showOverflowTooltip: true},
  {prop: 'deptName', label: '部门', minWidth: 80, showOverflowTooltip: true},
  {prop: 'roleNames', label: '角色', minWidth: 140, showOverflowTooltip: true, columnType: "slot"},
  {prop: 'phonenumber', label: '手机号码', minWidth: 80},
  {prop: 'email', label: '邮箱', minWidth: 120, showOverflowTooltip: true},
  {prop: 'userType', label: '用户类型', columnType: 'tag', dictList: user_type.value || [], width: 110},
  {prop: 'sex', label: '性别', columnType: 'tag', dictList: sex.value || [], width: 80},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: user_status.value || [], width: 90},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 240, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'userName', label: '用户名', type: 'input'},
  {prop: 'nickName', label: '昵称', type: 'input'},
  {prop: 'phonenumber', label: '手机号码', type: 'input'},
  {prop: 'status', label: '状态', type: 'select', dataList: user_status.value || []},
  {prop: 'userType', label: '用户类型', type: 'select', dataList: user_type.value || []}
])

const rules = computed(() => ({
  userName: userNameRules,
  nickName: nickNameRules,
  password: passwordRules({required: !form.id}),
  email: emailRules,
  phonenumber: phoneRules,
  status: [requiredRule('状态不能为空', 'change')]
}))

function emptyForm() {
  return {
    id: undefined,
    deptId: undefined,
    userName: '',
    nickName: '',
    userType: '0',
    email: '',
    phonenumber: '',
    sex: '1',
    password: '',
    status: '0',
    remark: '',
    roleIds: []
  }
}

/**
 * 初始化：直接写页面 form。
 * @param {Record<string, any>} [row]
 */
async function getPayload(row) {
  Object.assign(form, emptyForm())
  if (row && row.id != null) {
    const res = await getUser(row.id)
    const data = res?.data || row
    Object.assign(form, {
      id: data.id,
      deptId: data.deptId == null || data.deptId === '' ? undefined : data.deptId,
      userName: data.userName ?? '',
      nickName: data.nickName ?? '',
      userType: data.userType ?? '0',
      email: data.email ?? '',
      phonenumber: data.phonenumber ?? '',
      sex: data.sex ?? '1',
      password: '',
      status: data.status ?? '0',
      remark: data.remark ?? '',
      roleIds: data.roleIds || []
    })
  }
}


/** 提交：组装业务差异 + 新增/修改分支（直接读页面 form） */
async function submitUser() {
  const payload = {
    id: form.id,
    deptId: form.deptId === '' || form.deptId == null ? null : form.deptId,
    userName: form.userName,
    nickName: form.nickName,
    userType: form.userType,
    email: form.email || undefined,
    phonenumber: form.phonenumber || undefined,
    sex: form.sex,
    status: form.status,
    remark: form.remark,
    roleIds: form.roleIds || []
  }
  if (form.password) {
    payload.password = form.password
  } else if (!form.id) {
    payload.password = form.password
  }
  if (payload.id) {
    await updateUser(payload)
  } else {
    await addUser(payload)
  }
}


/**
 * 重置用户密码：确认 → 二次认证密码 → openSafe → resetPassword。
 * @param {Record<string, any>} row
 */
async function resetUserPassword(row) {
  if (row?.id == null) {
    return
  }
  try {
    await ElMessageBox.confirm(
        `确认重置用户「${row.userName || row.nickName || row.id}」的密码？`,
        '提示',
        {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }
    )
  } catch {
    return
  }
  let password
  try {
    const {value} = await ElMessageBox.prompt('请输入当前登录密码以完成二次认证', '二次认证', {
      inputType: 'password',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '当前登录密码',
      inputValidator: (v) => (String(v || '').trim() ? true : '密码不能为空')
    })
    password = value
  } catch {
    return
  }
  try {
    await openSafe(password)
    await resetPassword(row.id)
    ElMessage.success('重置成功')
  } catch {
    // 业务错误已由 request 拦截器提示，避免冒泡到 AppMain 整页显示 error
  }
}
</script>
