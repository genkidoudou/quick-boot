<template>
  <div class="app-container">
    <C7JsonTable
        ref="tableRef"
        :list-function="pageGroup"
        :table-columns="tableColumns"
        :search-columns="searchColumns"
        row-key="id"
        show-add-button
        show-edit-button
        :delete-function="removeGroup"
        :add-button-permi="['msg:group:add']"
        :edit-button-permi="['msg:group:edit']"
        :delete-button-permi="['msg:group:remove']"
        @add-click="() => formDialogRef?.open()"
        @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
            link
            type="primary"
            :icon="User"
            v-hasPermi="['msg:group:add', 'msg:group:edit']"
            @click="openMembers(row)"
        >
          维护成员
        </el-button>
        <el-button
            link
            type="primary"
            :icon="Edit"
            v-hasPermi="['msg:group:edit']"
            @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
            link
            type="primary"
            :icon="Delete"
            v-hasPermi="['msg:group:remove']"
            @click="tableRef?.removeRows(row, { confirmMessage: `确认删除群组「${row.name || row.id}」？` })"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <C7FormDialog
        ref="formDialogRef"
        v-model="open"
        :title="title"
        width="520px"
        :form="form"
        :form-ref="formRef"
        :get-payload="getPayload"
        :submit="submitGroup"
        :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="群组名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入群组名称" maxlength="255"/>
        </el-form-item>
        <el-form-item label="成员类型" prop="memberType">
          <c7-radio
              v-model="form.memberType"
              :data-list="MSG_GROUP_MEMBER_TYPE"
              :disabled="!!form.id"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="255"/>
        </el-form-item>
      </el-form>
    </C7FormDialog>

    <el-dialog
        v-model="memberOpen"
        :title="memberTitle"
        :width="isDeptMember ? '520px' : '880px'"
        append-to-body
        destroy-on-close
        @opened="onMemberOpened"
    >
      <template v-if="isUserMember">
        <div class="group-member-panel__head">
          <span>已选成员</span>
          <el-button type="primary" plain :icon="Upload" @click="openImport">导入</el-button>
        </div>
        <C7JsonTable
            ref="memberTableRef"
            :list-function="pageChosenUsers"
            :table-columns="memberColumns"
            :search-columns="[]"
            :show-selection="false"
            row-key="id"
        >
          <template #actions="{ row }">
            <el-button link type="danger" :icon="Delete" @click="removeMember(row)">删除</el-button>
          </template>
        </C7JsonTable>
      </template>
      <el-tree
          v-else-if="isDeptMember"
          ref="deptTreeRef"
          class="group-dept-tree"
          node-key="id"
          show-checkbox
          default-expand-all
          :data="deptTreeData"
          :props="deptTreeProps"
          empty-text="暂无部门"
      />
      <template v-else-if="isExternalMember">
        <C7JsonTable
            ref="externalTableRef"
            :list-function="pageChosenExternals"
            :table-columns="externalColumns"
            :search-columns="externalSearchColumns"
            :show-selection="false"
            show-add-button
            add-button-text="添加"
            :add-button-permi="['msg:group:add', 'msg:group:edit']"
            row-key="id"
            @add-click="openExternal"
        >
          <template #actions="{ row }">
            <el-button link type="danger" :icon="Delete" @click="removeExternal(row)">删除</el-button>
          </template>
        </C7JsonTable>
      </template>
      <p v-else class="group-member-panel__hint">请先修改群组并确定成员类型。</p>
      <template v-if="isDeptMember" #footer>
        <el-button @click="memberOpen = false">取消</el-button>
        <el-button type="primary" :loading="savingDept" @click="confirmDept">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
        v-model="importOpen"
        title="导入成员"
        width="860px"
        append-to-body
        destroy-on-close
    >
      <C7JsonTable
          :list-function="pageUnchosenUsers"
          :table-columns="userColumns"
          :search-columns="userSearchColumns"
          row-key="id"
          @selection-change="onImportSelection"
      />
      <template #footer>
        <el-button @click="importOpen = false">取消</el-button>
        <el-button
            type="primary"
            :disabled="!importSelection.length"
            :loading="importing"
            @click="confirmImport"
        >
          导入
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
        v-model="externalOpen"
        title="添加外部人"
        width="480px"
        append-to-body
        destroy-on-close
    >
      <el-form ref="externalFormRef" :model="externalForm" :rules="externalRules" label-width="72px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="externalForm.name" maxlength="64" placeholder="名称"/>
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="externalForm.phone" maxlength="20" placeholder="手机和邮箱至少填一项"/>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="externalForm.email" maxlength="128" placeholder="手机和邮箱至少填一项"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="externalOpen = false">取消</el-button>
        <el-button type="primary" :loading="addingExternal" @click="addExternal">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 消息群组：先保存群组并确定成员类型，再从操作栏维护成员。
 */
import {computed, nextTick, reactive, ref} from 'vue'
import {Delete, Edit, Upload, User} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  addGroup,
  addGroupExternal,
  importGroupUser,
  listGroupDeptTree,
  pageChooseUser,
  pageGroup,
  pageGroupExternal,
  pageUnChooseUser,
  removeGroup,
  removeGroupExternal,
  removeGroupUser,
  saveGroupDept,
  updateGroup
} from '@/api/msg/group'
import C7Radio from '@/packages/C7Radio/index.vue'
import {useDict} from '@/utils/dict.js'

defineOptions({name: 'MsgGroup'})

const tableRef = ref(null)
const memberTableRef = ref(null)
const externalTableRef = ref(null)
const deptTreeRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const externalFormRef = ref(null)
const open = ref(false)
const memberOpen = ref(false)
const externalOpen = ref(false)
const importOpen = ref(false)
const importing = ref(false)
const savingDept = ref(false)
const addingExternal = ref(false)
const importSelection = ref([])
const deptTreeData = ref([])
const deptTreeProps = {label: 'deptName', children: 'children'}
const form = reactive(emptyForm())
const externalForm = reactive(emptyExternal())
const memberGroup = reactive({id: undefined, name: '', memberType: ''})

const {MSG_GROUP_MEMBER_TYPE, user_status} = useDict('MSG_GROUP_MEMBER_TYPE', 'user_status')
const title = computed(() => (form.id ? '修改群组' : '新增群组'))
const isUserMember = computed(() => String(memberGroup.memberType) === '1')
const isDeptMember = computed(() => String(memberGroup.memberType) === '2')
const isExternalMember = computed(() => String(memberGroup.memberType) === '3')
const memberTitle = computed(() => `维护成员${memberGroup.name ? ` - ${memberGroup.name}` : ''}`)

const tableColumns = computed(() => [
  {prop: 'name', label: '群组名称', minWidth: 180, showOverflowTooltip: true},
  {
    prop: 'memberType',
    label: '成员类型',
    columnType: 'tag',
    dictList: MSG_GROUP_MEMBER_TYPE.value || [],
    width: 120
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: 180,
  },
  {prop: 'remark', label: '备注', minWidth: 200, showOverflowTooltip: true},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 240, fixed: 'right', align: 'center'}
])

const searchColumns = computed(() => [
  {prop: 'name', label: '群组名称', type: 'input'},
  {
    prop: 'memberType',
    label: '成员类型',
    columnType: 'select',
    dictList: MSG_GROUP_MEMBER_TYPE.value || [],
    width: 120
  },
])

const memberColumns = computed(() => [
  {prop: 'userName', label: '用户名', minWidth: 160, showOverflowTooltip: true},
  {prop: 'userName', label: '昵称', minWidth: 160, showOverflowTooltip: true},
  {prop: 'userStatus', label: '用户状态', columnType: 'tag', dictList: user_status.value || [], width: 100},
  {prop: 'createTime', label: '导入时间', width: 180,},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 100, align: 'center'}
])

const userColumns = computed(() => [
  {prop: 'userName', label: '用户名', minWidth: 140, showOverflowTooltip: true},
  {prop: 'nickName', label: '昵称', minWidth: 120, showOverflowTooltip: true},
  {prop: 'deptName', label: '部门', minWidth: 120, showOverflowTooltip: true},
  {prop: 'phonenumber', label: '手机号码', minWidth: 130},
  {prop: 'status', label: '状态', columnType: 'tag', dictList: user_status.value || [], width: 90}
])

const userSearchColumns = computed(() => [
  {prop: 'userName', label: '用户名', type: 'input'},
  {prop: 'nickName', label: '昵称', type: 'input'}
])

const externalColumns = computed(() => [
  {prop: 'name', label: '名称', minWidth: 140, showOverflowTooltip: true},
  {prop: 'phone', label: '手机', minWidth: 140},
  {prop: 'email', label: '邮箱', minWidth: 180, showOverflowTooltip: true},
  {prop: 'createTime', label: '添加时间', width: 180},
  {prop: 'actions', label: '操作', columnType: 'slot', width: 100, align: 'center'}
])

const externalSearchColumns = computed(() => [
  {prop: 'name', label: '名称', type: 'input'}
])

const rules = computed(() => ({
  name: [{required: true, message: '群组名称不能为空', trigger: 'blur'}],
  memberType: [{required: true, message: '成员类型不能为空', trigger: 'change'}]
}))

const externalRules = {
  name: [{required: true, message: '名称不能为空', trigger: 'blur'}],
  phone: [{validator: validateExternalContact, trigger: 'blur'}],
  email: [{validator: validateExternalContact, trigger: 'blur'}]
}

function emptyForm() {
  return {
    id: undefined,
    name: '',
    remark: '',
    memberType: '1'
  }
}

function emptyExternal() {
  return {name: '', phone: '', email: ''}
}

function validateExternalContact(_rule, _value, callback) {
  if (!externalForm.phone.trim() && !externalForm.email.trim()) {
    callback(new Error('手机和邮箱至少填一项'))
    return
  }
  callback()
}

/** @param {Record<string, any>} [row] */
function getPayload(row) {
  Object.assign(form, emptyForm())
  if (row && row.id != null) {
    Object.assign(form, {
      id: row.id,
      name: row.name ?? '',
      memberType: row.memberType ?? '',
      remark: row.remark ?? ''
    })
  }
}

async function submitGroup() {
  const payload = {
    id: form.id,
    name: form.name,
    memberType: form.memberType,
    remark: form.remark
  }
  if (payload.id) {
    await updateGroup(payload)
  } else {
    await addGroup(payload)
  }
}

function openMembers(row) {
  memberGroup.id = row.id
  memberGroup.name = row.name ?? ''
  memberGroup.memberType = row.memberType ?? ''
  deptTreeData.value = []
  Object.assign(externalForm, emptyExternal())
  externalOpen.value = false
  memberOpen.value = true
}

function openExternal() {
  Object.assign(externalForm, emptyExternal())
  externalOpen.value = true
}

function onMemberOpened() {
  if (isDeptMember.value) {
    loadDeptTree()
  }
}

function pageChosenUsers(data) {
  return pageChooseUser(memberGroup.id, data)
}

function pageUnchosenUsers(data) {
  return pageUnChooseUser(memberGroup.id, data)
}

function pageChosenExternals(data) {
  return pageGroupExternal(memberGroup.id, data)
}

async function addExternal() {
  const formEl = externalFormRef.value
  if (formEl) {
    const valid = await formEl.validate().catch(() => false)
    if (!valid) {
      return
    }
  }
  const name = externalForm.name.trim()
  const phone = externalForm.phone.trim()
  const email = externalForm.email.trim()
  addingExternal.value = true
  try {
    await addGroupExternal(memberGroup.id, {name, phone, email})
    ElMessage.success('已添加')
    externalOpen.value = false
    externalTableRef.value?.refreshData()
  } catch {
    // 拦截器已弹出接口文案
  } finally {
    addingExternal.value = false
  }
}

async function removeExternal(row) {
  try {
    await ElMessageBox.confirm(`确认移除外部人「${row.name || row.id}」？`, '提示', {type: 'warning'})
  } catch {
    return
  }
  try {
    await removeGroupExternal(memberGroup.id, [row.id])
    ElMessage.success('已删除')
    externalTableRef.value?.refreshData()
  } catch {
    // 拦截器已弹出接口文案
  }
}

function buildDeptTree(list) {
  const nodes = (list || []).map((item) => ({
    id: item.id,
    parentId: item.parentId,
    deptName: item.deptName,
    children: []
  }))
  const byId = new Map(nodes.map((node) => [String(node.id), node]))
  const roots = []
  for (const node of nodes) {
    const parent = byId.get(String(node.parentId))
    if (parent && String(node.parentId) !== '0') {
      parent.children.push(node)
    } else {
      roots.push(node)
    }
  }
  return roots
}

async function loadDeptTree() {
  try {
    const res = await listGroupDeptTree(memberGroup.id)
    const list = res?.data || []
    deptTreeData.value = buildDeptTree(list)
    const checked = list.filter((item) => item.chosen).map((item) => item.id)
    await nextTick()
    deptTreeRef.value?.setCheckedKeys(checked)
  } catch {
    deptTreeData.value = []
  }
}

async function confirmDept() {
  if (!memberGroup.id) {
    return
  }
  const deptIds = deptTreeRef.value?.getCheckedKeys(false) || []
  savingDept.value = true
  try {
    await saveGroupDept(memberGroup.id, deptIds)
    ElMessage.success('已保存')
    memberOpen.value = false
  } catch {
    // 拦截器已弹出接口文案
  } finally {
    savingDept.value = false
  }
}

function openImport() {
  importSelection.value = []
  importOpen.value = true
}

function onImportSelection(rows) {
  importSelection.value = rows || []
}

async function confirmImport() {
  const userIds = importSelection.value.map((row) => row.id).filter((id) => id != null)
  if (!userIds.length || !memberGroup.id) {
    return
  }
  importing.value = true
  try {
    await importGroupUser(memberGroup.id, userIds)
    ElMessage.success('导入成功')
    importOpen.value = false
    memberTableRef.value?.refreshData()
  } catch {
    // 拦截器已弹出接口文案
  } finally {
    importing.value = false
  }
}

async function removeMember(row) {
  try {
    await ElMessageBox.confirm(`确认移除用户「${row.userName || row.userId || row.id}」？`, '提示', {type: 'warning'})
  } catch {
    return
  }
  try {
    await removeGroupUser(memberGroup.id, [row.id])
    ElMessage.success('已删除')
    memberTableRef.value?.refreshData()
  } catch {
    // 拦截器已弹出接口文案
  }
}
</script>

<style scoped>
.group-member-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-weight: 600;
}

.group-member-panel__hint {
  margin: 0;
  color: var(--el-text-color-secondary);
}

.group-dept-tree {
  max-height: 420px;
  overflow: auto;
}
</style>
