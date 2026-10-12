<template>
  <div class="app-container">
    <C7JsonTable
      ref="tableRef"
      :list-function="pageRole"
      :table-columns="tableColumns"
      :search-columns="searchColumns"
      row-key="id"
      show-add-button
      show-edit-button
      :delete-function="removeRole"
      :add-button-permi="['system:role:add']"
      :edit-button-permi="['system:role:edit']"
      :delete-button-permi="['system:role:remove']"
      @add-click="() => formDialogRef?.open()"
      @edit-click="(row) => formDialogRef?.open(row)"
    >
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          :icon="Edit"
          v-hasPermi="['system:role:edit']"
          @click="formDialogRef?.open(row)"
        >
          修改
        </el-button>
        <el-button
          link
          type="primary"
          :icon="Delete"
          v-hasPermi="['system:role:remove']"
          @click="tableRef?.removeRows(row, { confirmMessage: `确认删除角色「${row.roleName || row.roleCode || row.id}」？` })"
        >
          删除
        </el-button>
      </template>
    </C7JsonTable>

    <C7FormDialog
      ref="formDialogRef"
      v-model="open"
      :title="title"
      width="680px"
      :form="form"
      :form-ref="formRef"
      :get-payload="getPayload"
      :submit="submitRole"
      :on-success="() => tableRef?.refreshData()"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" maxlength="255" />
        </el-form-item>
        <el-form-item label="角色排序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="数据权限" prop="dataScope">
          <el-select v-model="form.dataScope" placeholder="请选择数据权限" clearable style="width: 100%">
            <el-option
              v-for="item in USER_DATA_SCOPE"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <div class="role-menu-panel">
            <div class="role-menu-panel__toolbar">
              <el-checkbox v-model="menuExpand" @change="handleMenuExpand">展开/折叠</el-checkbox>
              <el-checkbox v-model="menuNodeAll" @change="handleMenuNodeAll">全选/全不选</el-checkbox>
              <el-checkbox v-model="menuCheckStrictly">父子联动</el-checkbox>
            </div>
            <el-tree
              ref="menuTreeRef"
              class="role-menu-panel__tree"
              node-key="id"
              show-checkbox
              :data="menuOptions"
              :props="menuTreeProps"
              :check-strictly="!menuCheckStrictly"
              empty-text="加载中，请稍候"
            />
          </div>
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
 * 角色管理：列表 / 新增 / 修改 / 删除；表单内勾选菜单权限（提交 menuIds）。
 * 回显依赖详情接口返回 menuIds（后端就绪后生效；目前未返回则树为空选）。
 */
import { computed, nextTick, reactive, ref, watch } from 'vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useDict } from '@/utils/dict'
import { listMenu } from '@/api/system/menu'
import { addRole, getRole, pageRole, removeRole, updateRole } from '@/api/system/role'
import {
  echoTreeCheckedKeysWithoutCascade,
  stringifyTreeIds,
  toApiLongIds
} from '@/utils/ruoyi'

defineOptions({ name: 'SysRole' })

/** 状态 / 数据权限字典 */
const { COMMON_STATUS, USER_DATA_SCOPE } = useDict('COMMON_STATUS', 'USER_DATA_SCOPE')

const tableRef = ref(null)
const formDialogRef = ref(null)
const formRef = ref(null)
const menuTreeRef = ref(null)
const open = ref(false)
const form = reactive(emptyForm())

const menuOptions = ref([])
const menuExpand = ref(false)
const menuNodeAll = ref(false)
/** true：勾选父子联动；false：父子不关联（与 el-tree check-strictly 相反） */
const menuCheckStrictly = ref(true)
const menuTreeProps = { label: 'menuName', children: 'children' }

const title = computed(() => (form.id ? '修改角色' : '新增角色'))

const tableColumns = computed(() => [
  { prop: 'roleName', label: '角色名称', minWidth: 140, showOverflowTooltip: true },
  { prop: 'roleCode', label: '角色编码', minWidth: 140, showOverflowTooltip: true },
  { prop: 'roleSort', label: '排序', width: 80 },
  {
    prop: 'dataScope',
    label: '数据权限',
    columnType: 'tag',
    dictList: USER_DATA_SCOPE.value || [],
    minWidth: 140
  },
  { prop: 'status', label: '状态', columnType: 'tag', dictList: COMMON_STATUS.value || [], width: 100 },
  { prop: 'remark', label: '备注', minWidth: 160, showOverflowTooltip: true },
  { prop: 'actions', label: '操作', columnType: 'slot', width: 160, fixed: 'right', align: 'center' }
])

const searchColumns = computed(() => [
  { prop: 'roleName', label: '角色名称', type: 'input' },
  { prop: 'roleCode', label: '角色编码', type: 'input' },
  { prop: 'dataScope', label: '数据权限', type: 'select', dataList: USER_DATA_SCOPE.value || [] },
  { prop: 'status', label: '状态', type: 'select', dataList: COMMON_STATUS.value || [] }
])

const rules = {
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  roleCode: [{ required: true, message: '角色编码不能为空', trigger: 'blur' }],
  dataScope: [{ required: true, message: '数据权限不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
}

function emptyForm() {
  return {
    id: undefined,
    roleName: '',
    roleCode: '',
    roleSort: 0,
    dataScope: '1',
    status: '0',
    remark: '',
    menuIds: []
  }
}

/**
 * 兼容 menuId 字段；Long 已由后端序列化为 string。
 * @param {Array} nodes
 */
function toMenuTree(nodes) {
  if (!Array.isArray(nodes)) {
    return []
  }
  return nodes.map((n) => ({
    ...n,
    id: n?.id ?? n?.menuId,
    children: toMenuTree(n?.children || [])
  }))
}

async function loadMenuTree() {
  const res = await listMenu({})
  menuOptions.value = toMenuTree(res?.data || [])
}

/**
 * @param {boolean} expand
 */
function setTreeExpand(expand) {
  const tree = menuTreeRef.value
  if (!tree?.store?.nodesMap) {
    return
  }
  Object.values(tree.store.nodesMap).forEach((node) => {
    node.expanded = expand
  })
}

function handleMenuExpand(val) {
  setTreeExpand(!!val)
}

function handleMenuNodeAll(val) {
  const tree = menuTreeRef.value
  if (!tree) {
    return
  }
  if (val) {
    tree.setCheckedNodes(menuOptions.value)
  } else {
    tree.setCheckedKeys([])
  }
}

/** 读取勾选 + 半选菜单 id（半选父节点也需提交，否则后端树不完整） */
function collectMenuIds() {
  const tree = menuTreeRef.value
  if (!tree) {
    return []
  }
  const checked = tree.getCheckedKeys(false) || []
  const half = tree.getHalfCheckedKeys() || []
  return toApiLongIds([...checked, ...half])
}

/**
 * @param {Array<string|number>} menuIds
 */
async function echoMenuChecked(menuIds) {
  await nextTick()
  const tree = menuTreeRef.value
  if (!tree) {
    return
  }
  tree.setCheckedKeys([])
  echoTreeCheckedKeysWithoutCascade(tree, stringifyTreeIds(menuIds))
}

/**
 * @param {Record<string, any>} [row]
 */
async function getPayload(row) {
  Object.assign(form, emptyForm())
  menuExpand.value = false
  menuNodeAll.value = false
  menuCheckStrictly.value = true

  try {
    await loadMenuTree()
  } catch {
    menuOptions.value = []
    ElMessage.error('菜单权限树加载失败')
  }

  if (row && row.id != null) {
    try {
      const res = await getRole(row.id)
      const data = res?.data || row
      Object.assign(form, {
        id: data.id,
        roleName: data.roleName ?? '',
        roleCode: data.roleCode ?? '',
        roleSort: data.roleSort ?? 0,
        dataScope: data.dataScope ?? '1',
        status: data.status ?? '0',
        remark: data.remark ?? '',
        menuIds: data.menuIds || []
      })
    } catch {
      Object.assign(form, {
        id: row.id,
        roleName: row.roleName ?? '',
        roleCode: row.roleCode ?? '',
        roleSort: row.roleSort ?? 0,
        dataScope: row.dataScope ?? '1',
        status: row.status ?? '0',
        remark: row.remark ?? '',
        menuIds: []
      })
    }
  }
}

/** 弹窗打开且树挂载后再回显勾选 */
watch(open, async (val) => {
  if (!val) return
  await nextTick()
  await echoMenuChecked(form.menuIds || [])
})

async function submitRole() {
  const menuIds = collectMenuIds()
  const payload = {
    id: form.id,
    roleName: form.roleName,
    roleCode: form.roleCode,
    roleSort: form.roleSort,
    dataScope: form.dataScope,
    status: form.status,
    remark: form.remark,
    menuIds
  }
  if (form.id) {
    await updateRole(payload)
  } else {
    await addRole(payload)
  }
}
</script>

<style scoped>
.role-menu-panel {
  width: 100%;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  padding: 10px 12px;
  background: var(--el-fill-color-blank);
}

.role-menu-panel__toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 8px;
}

.role-menu-panel__tree {
  max-height: 280px;
  overflow: auto;
}
</style>
