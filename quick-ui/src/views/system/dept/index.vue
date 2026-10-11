<template>
  <div class="app-container">
    <el-form v-show="showSearch" :inline="true" :model="query" class="mb-2" @submit.prevent>
      <el-form-item label="部门名称">
        <el-input
          v-model="query.deptName"
          placeholder="请输入部门名称"
          clearable
          style="width: 200px"
          @keyup.enter="loadData"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="部门状态" clearable style="width: 200px">
          <el-option v-for="d in COMMON_STATUS || []" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" v-hasPermi="['system:dept:list']" @click="loadData">搜索</el-button>
        <el-button :icon="Refresh" v-hasPermi="['system:dept:list']" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="dept-toolbar mb8" align="middle">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" v-hasPermi="['system:dept:add']" @click="openForm()">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <el-col :span="1.5" class="dept-toolbar__search-toggle">
        <el-tooltip :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top">
          <el-button circle :icon="Search" @click="showSearch = !showSearch" />
        </el-tooltip>
      </el-col>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="deptList"
      row-key="id"
      border
      :default-expand-all="expandAll"
      :tree-props="{ children: 'children' }"
    >
      <el-table-column prop="deptName" label="部门名称" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column prop="orderNum" label="排序" width="90" align="center" />
      <el-table-column prop="leader" label="负责人" min-width="100" :show-overflow-tooltip="true" />
      <el-table-column prop="phone" label="联系电话" min-width="120" />
      <el-table-column prop="status" label="状态" width="90" align="center">
        <template #default="{ row }">
          <C7DictTag :model-value="row.status" :options="COMMON_STATUS" />
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="210" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Edit" v-hasPermi="['system:dept:edit']" @click="openForm(row)">
            修改
          </el-button>
          <el-button link type="primary" :icon="Plus" v-hasPermi="['system:dept:add']" @click="openForm(null, row)">
            新增
          </el-button>
          <el-button link type="primary" :icon="Delete" v-hasPermi="['system:dept:remove']" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <C7Dialog v-model="open" :title="title" :on-confirm="submitForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="上级部门" prop="parentId">
          <C7TreeSelect
            v-model="form.parentId"
            :data-list="parentTreeData"
            value-key="id"
            label-key="name"
            children-key="children"
            check-strictly
            filterable
            placeholder="请选择上级部门"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="负责人" maxlength="255" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="联系电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱" maxlength="255" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <c7-radio :data-list="COMMON_STATUS" v-model="form.status" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" :rows="3" maxlength="100" />
        </el-form-item>
      </el-form>
    </C7Dialog>
  </div>
</template>

<script setup>
/**
 * 部门管理：树表 + 增删改（对齐菜单管理）。
 * 根上级部门 id=0，名称为「总公司」。
 */
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDict } from '@/utils/dict'
import { addDept, delDept, getDept, listDept, treeselectDept, updateDept } from '@/api/system/dept'

defineOptions({ name: 'SysDept' })

const ROOT = '0'
const ROOT_NAME = '总公司'

const { COMMON_STATUS } = useDict('COMMON_STATUS')

const loading = ref(false)
const deptList = ref([])
const expandAll = ref(false)
const refreshTable = ref(true)
const showSearch = ref(true)
const open = ref(false)
const formRef = ref(null)
const parentTreeData = ref([])
const query = ref({ deptName: '', status: '' })
const form = reactive(emptyForm())

const title = computed(() => (form.id ? '修改部门' : '新增部门'))

const rules = {
  parentId: [{ required: true, message: '上级部门不能为空', trigger: 'change' }],
  deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
}

function emptyForm() {
  return {
    id: undefined,
    parentId: ROOT,
    deptName: '',
    orderNum: 0,
    leader: '',
    phone: '',
    email: '',
    status: '0',
    remark: ''
  }
}

/** 编辑时排除自身及子孙，避免环。比较时统一 String，避免混用。 */
function excludeSelfAndDescendants(nodes, selfId) {
  if (!Array.isArray(nodes) || selfId == null || selfId === '') {
    return nodes
  }
  const sid = String(selfId)
  return nodes
    .filter((n) => String(n.id) !== sid)
    .map((n) => ({
      ...n,
      children: excludeSelfAndDescendants(n.children, selfId)
    }))
}

function loadParentTree(excludeId) {
  return treeselectDept().then((res) => {
    // Long 已由后端序列化为 string
    const children = excludeSelfAndDescendants(res.data || [], excludeId)
    parentTreeData.value = [
      {
        id: ROOT,
        name: ROOT_NAME,
        children
      }
    ]
  })
}

function loadData() {
  const searching = !!(query.value.deptName || query.value.status)
  loading.value = true
  listDept(query.value)
    .then((res) => {
      deptList.value = res.data || []
      if (searching) {
        expandAll.value = true
        refreshTable.value = false
        nextTick(() => {
          refreshTable.value = true
        })
      }
    })
    .finally(() => {
      loading.value = false
    })
}

function resetQuery() {
  query.value = { deptName: '', status: '' }
  loadData()
}

function toggleExpandAll() {
  refreshTable.value = false
  expandAll.value = !expandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

/**
 * @param {Record<string, any>|null} [row] 编辑行；为空则新增
 * @param {Record<string, any>} [parentRow] 在某节点下新增时的父行
 */
async function openForm(row, parentRow) {
  Object.assign(form, emptyForm())
  const excludeId = row?.id ?? row?.deptId
  await loadParentTree(excludeId)

  if (row && (row.id != null || row.deptId != null)) {
    const id = row.id ?? row.deptId
    const res = await getDept(id)
    const data = res?.data || row
    Object.assign(form, {
      id: data.id,
      parentId: data.parentId == null ? ROOT : data.parentId,
      deptName: data.deptName ?? '',
      orderNum: data.orderNum ?? 0,
      leader: data.leader ?? '',
      phone: data.phone ?? '',
      email: data.email ?? '',
      status: data.status ?? '0',
      remark: data.remark ?? ''
    })
  } else if (parentRow) {
    form.parentId = parentRow.id ?? parentRow.deptId ?? ROOT
  }

  open.value = true
}

async function handleDelete(row) {
  const id = row?.id ?? row?.deptId
  if (id == null) return
  try {
    await ElMessageBox.confirm(`是否确认删除名称为「${row.deptName}」的数据项？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  await delDept(id)
  ElMessage.success('删除成功')
  loadData()
}

async function submitForm() {
  await formRef.value?.validate()
  const payload = {
    ...form,
    parentId: form.parentId === '' || form.parentId == null ? 0 : form.parentId
  }
  if (form.id) {
    await updateDept(payload)
    ElMessage.success('修改成功')
  } else {
    await addDept(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dept-toolbar {
  margin-bottom: 12px;
  flex-wrap: nowrap;
}
.dept-toolbar__search-toggle {
  margin-left: auto;
}
.mb8 {
  margin-bottom: 8px;
}
</style>
