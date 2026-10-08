<template>
  <C7Dialog v-model="visible" :title="form.id ? '修改菜单' : '新增菜单'" width="800px" :on-confirm="submit">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="108px">
      <el-form-item prop="parentId">
        <template #label>
          <C7FormLabel text="上级菜单" :tip="MENU_TIPS.parentId"/>
        </template>
        <C7TreeSelect
            v-model="form.parentId"
            :data-list="treeData"
            value-key="id"
            label-key="name"
            children-key="children"
            check-strictly
            filterable
            placeholder="请选择上级菜单"
            @change="onParentChange"
        />
      </el-form-item>
      <el-collapse v-if="showQuickParse" v-model="quickParseOpen" class="quick-parse">
        <el-collapse-item name="parse" title="从 Controller 快速生成">
          <el-input
              v-model="controllerSource"
              type="textarea"
              :rows="8"
              placeholder="粘贴 Controller 源码，将解析出菜单页和按钮权限"
          />
          <div class="quick-parse__actions">
            <el-button type="primary" @click="parseController">解析</el-button>
          </div>
          <el-table v-if="parsedButtons.length" :data="parsedButtons" size="small" border class="quick-parse__table">
            <el-table-column width="52" align="center">
              <template #default="{ row }">
                <el-checkbox v-model="row.checked"/>
              </template>
            </el-table-column>
            <el-table-column label="按钮名称" min-width="140">
              <template #default="{ row }">
                <el-input v-model="row.menuName"/>
              </template>
            </el-table-column>
            <el-table-column label="权限字符" min-width="180">
              <template #default="{ row }">
                <el-input v-model="row.perms"/>
              </template>
            </el-table-column>
            <el-table-column label="排序" width="100">
              <template #default="{ row }">
                <el-input-number v-model="row.orderNum" :min="1" :max="999" controls-position="right" size="small"/>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <el-form-item prop="menuType">
        <template #label>
          <C7FormLabel text="菜单类型" :tip="MENU_TIPS.menuType"/>
        </template>
        <c7-radio v-model="form.menuType" :data-list="menuTypeOptions" :disabled="lockMenuType"/>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item prop="menuName">
            <template #label>
              <C7FormLabel
                  :text="form.menuType === 'F' ? '按钮名称' : '菜单名称'"
                  :tip="form.menuType === 'F' ? MENU_TIPS.buttonName : MENU_TIPS.menuName"
              />
            </template>
            <el-input v-model="form.menuName" maxlength="255" placeholder="请输入名称"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="orderNum">
            <template #label>
              <C7FormLabel text="显示排序" :tip="MENU_TIPS.orderNum"/>
            </template>
            <el-input-number v-model="form.orderNum" :min="0" :max="9999" controls-position="right"
                             style="width: 100%"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="form.menuType !== 'F'" :gutter="16">
        <el-col :span="12">
          <el-form-item prop="icon">
            <template #label>
              <C7FormLabel text="菜单图标" :tip="MENU_TIPS.icon"/>
            </template>
            <el-popover v-model:visible="iconPopoverVisible" placement="bottom-start" :width="540" trigger="click">
              <template #reference>
                <el-input v-model="form.icon" placeholder="点击选择图标" readonly>
                  <template #prefix>
                    <svg-icon v-if="form.icon" :icon-class="form.icon" class="menu-icon-prefix"/>
                    <el-icon v-else>
                      <Search/>
                    </el-icon>
                  </template>
                </el-input>
              </template>
              <IconSelect :active-icon="form.icon" @selected="onIconSelected"/>
            </el-popover>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="path">
            <template #label>
              <C7FormLabel text="路由地址" :tip="MENU_TIPS.path"/>
            </template>
            <el-input v-model="form.path" placeholder="如 menu 或 https://example.com"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="form.menuType !== 'F'" :gutter="16">
        <el-col :span="12">
          <el-form-item prop="component">
            <template #label>
              <C7FormLabel text="组件路径" :tip="MENU_TIPS.component"/>
            </template>
            <el-input
                v-model="form.component"
                :placeholder="form.menuType === 'M' ? '目录一般为 Layout' : '如 system/menu/index'"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="routeName">
            <template #label>
              <C7FormLabel text="路由名称" :tip="MENU_TIPS.routeName"/>
            </template>
            <el-input v-model="form.routeName" placeholder="如 SysMenu"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item v-if="form.menuType === 'C'" prop="query">
        <template #label>
          <C7FormLabel text="路由参数" :tip="MENU_TIPS.query"/>
        </template>
        <el-input v-model="form.query" placeholder='如 {"id": 1}'/>
      </el-form-item>
      <el-form-item prop="perms">
        <template #label>
          <C7FormLabel text="权限字符" :tip="MENU_TIPS.perms"/>
        </template>
        <div class="perms-dynamic">
          <div v-for="(_, index) in permsList" :key="index" class="perms-dynamic__row">
            <el-input v-model="permsList[index]" placeholder="如 system:menu:list" clearable/>
            <el-button v-if="permsList.length > 1" type="danger" link @click="removePermsRow(index)">删除</el-button>
          </div>
          <el-button type="primary" link @click="addPermsRow">添加权限</el-button>
        </div>
      </el-form-item>
      <el-row v-if="form.menuType !== 'F'" :gutter="16">
        <el-col :span="12">
          <el-form-item prop="isFrame">
            <template #label>
              <C7FormLabel text="是否外链" :tip="MENU_TIPS.isFrame"/>
            </template>
            <el-radio-group v-model="form.isFrame">
              <el-radio value="0">否</el-radio>
              <el-radio value="1">是</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="isCache">
            <template #label>
              <C7FormLabel text="是否缓存" :tip="MENU_TIPS.isCache"/>
            </template>
            <el-radio-group v-model="form.isCache">
              <el-radio value="0">缓存</el-radio>
              <el-radio value="1">不缓存</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col v-if="form.menuType !== 'F'" :span="12">
          <el-form-item prop="visible">
            <template #label>
              <C7FormLabel text="显示状态" :tip="MENU_TIPS.visible"/>
            </template>
            <el-radio-group v-model="form.visible">
              <el-radio v-for="d in sys_show_hide" :key="d.value" :value="d.value">{{ d.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="status">
            <template #label>
              <C7FormLabel
                  :text="form.menuType === 'F' ? '按钮状态' : '菜单状态'"
                  :tip="form.menuType === 'F' ? MENU_TIPS.buttonStatus : MENU_TIPS.menuStatus"
              />
            </template>
            <c7-radio v-model="form.status" :data-list="COMMON_STATUS"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="remark">
        <template #label>
          <C7FormLabel text="备注" :tip="MENU_TIPS.remark"/>
        </template>
        <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="100"/>
      </el-form-item>
    </el-form>
  </C7Dialog>
</template>

<script setup>
/**
 * 菜单新增/修改：目录 / 菜单 / 按钮，对齐 SysMenuVo。
 */
import {computed, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {Search} from '@element-plus/icons-vue'
import {addMenu, batchAddMenu, getMenu, parseMenuController, treeselectMenu, updateMenu} from '@/api/system/menu'
import {useDict} from '@/utils/dict'
import IconSelect from '@/components/IconSelect/index.vue'

defineOptions({name: 'SysMenuForm'})

const ROOT = '0'

const MENU_TIPS = {
  parentId: '选择上级菜单；顶级选「主类目」。',
  menuType: '目录用于侧栏分组；菜单为可访问页面；按钮为权限点，不生成路由。',
  menuName: '侧栏、标签页或权限树中显示的名称。',
  buttonName: '权限树中显示的按钮名称。',
  orderNum: '显示顺序，数字越小越靠前。',
  icon: '侧栏菜单图标，从图标库选择 SVG 名称。',
  path: '侧栏路由 path。外链时可填 http(s) 地址。',
  component: 'views 下组件路径，如 system/user/index；顶级目录一般为 Layout。',
  routeName: 'Vue Router 的 name，建议英文驼峰；用于标签页与缓存。',
  query: '路由 query，例如 {"id":1}。',
  perms: '可添加多个权限标识，保存时用英文逗号拼接入库。',
  isFrame: '选「是」表示访问外部地址，路由地址填完整 URL。',
  isCache: '选缓存则 keep-alive 生效，需路由名称与页面组件 name 一致。',
  visible: '选隐藏则不出现在侧栏，路由仍可能被直接访问。',
  menuStatus: '选停用则菜单不可用，侧栏不展示。',
  buttonStatus: '选停用则按钮权限不可用。',
  remark: '仅管理端备注，不影响路由与权限。'
}

const {COMMON_STATUS, MENU_TYPE, sys_show_hide} = useDict('COMMON_STATUS', 'MENU_TYPE', 'sys_show_hide')

const emit = defineEmits(['success'])
const visible = ref(false)
const iconPopoverVisible = ref(false)
const formRef = ref(null)
const treeData = ref([])
const form = reactive(emptyForm())
const lockMenuType = ref(false)
const permsList = ref([''])
const controllerSource = ref('')
const quickParseOpen = ref([])
const parsedButtons = ref([])
const showQuickParse = computed(() => !form.id && !lockMenuType.value)
const menuTypeOptions = computed(() => {
  const list = MENU_TYPE.value || []
  if (!lockMenuType.value) {
    return list
  }
  return list.filter((item) => item.value === 'F')
})

const rules = {
  parentId: [{required: true, message: '请选择上级菜单', trigger: 'change'}],
  menuType: [{required: true, message: '请选择菜单类型', trigger: 'change'}],
  menuName: [{required: true, message: '名称不能为空', trigger: 'blur'}],
  orderNum: [{required: true, message: '排序不能为空', trigger: 'change'}],
  path: [{
    validator: (_rule, value, callback) => {
      if (form.menuType !== 'F' && !String(value || '').trim()) {
        callback(new Error('路由地址不能为空'))
        return
      }
      callback()
    },
    trigger: 'blur'
  }]
}

function emptyForm() {
  return {
    id: undefined,
    parentId: ROOT,
    menuName: '',
    menuType: 'C',
    path: '',
    routeName: '',
    component: '',
    perms: '',
    icon: '',
    orderNum: 0,
    query: '',
    isFrame: '0',
    isCache: '0',
    visible: '0',
    status: '0',
    remark: ''
  }
}

function resetForm() {
  Object.assign(form, emptyForm())
  lockMenuType.value = false
  permsList.value = ['']
  controllerSource.value = ''
  quickParseOpen.value = []
  parsedButtons.value = []
  formRef.value?.clearValidate?.()
}

function splitPerms(raw) {
  return String(raw || '')
      .replace(/，/g, ',')
      .split(',')
      .map((item) => item.trim())
      .filter((item) => item.length > 0)
}

function joinPerms(list) {
  const seen = new Set()
  const result = []
  for (const item of list || []) {
    const value = String(item || '').replace(/，/g, ',').trim()
    if (!value || seen.has(value)) {
      continue
    }
    seen.add(value)
    result.push(value)
  }
  return result.join(',')
}

function addPermsRow() {
  permsList.value.push('')
}

function removePermsRow(index) {
  permsList.value.splice(index, 1)
  if (!permsList.value.length) {
    permsList.value = ['']
  }
}

function findTreeNode(nodes, id) {
  for (const node of nodes || []) {
    if (String(node.id) === String(id)) {
      return node
    }
    const hit = findTreeNode(node.children, id)
    if (hit) {
      return hit
    }
  }
  return undefined
}

function applyParentMenuType(parentMenuType) {
  if (form.id) {
    return
  }
  if (parentMenuType === 'C') {
    form.menuType = 'F'
    lockMenuType.value = true
    return
  }
  lockMenuType.value = false
}

function onParentChange(parentId) {
  const node = findTreeNode(treeData.value, parentId)
  applyParentMenuType(node?.menuType)
}

function stringifyTreeIds(nodes) {
  if (!Array.isArray(nodes)) {
    return []
  }
  return nodes.map((node) => ({
    ...node,
    id: node?.id == null ? node.id : String(node.id),
    children: stringifyTreeIds(node.children)
  }))
}

function loadTree() {
  treeselectMenu().then((res) => {
    treeData.value = [{
      id: ROOT,
      name: '主类目',
      children: stringifyTreeIds(res.data || [])
    }]
  })
}

function fillForm(row) {
  Object.assign(form, emptyForm(), {
    id: row.id ?? row.menuId,
    parentId: row.parentId == null ? ROOT : String(row.parentId),
    menuName: row.menuName ?? '',
    menuType: row.menuType || 'C',
    path: row.path ?? '',
    routeName: row.routeName ?? '',
    component: row.component ?? '',
    perms: row.perms ?? '',
    icon: row.icon ?? '',
    orderNum: row.orderNum ?? 0,
    query: row.query ?? '',
    isFrame: row.isFrame ?? '0',
    isCache: row.isCache ?? '0',
    visible: row.visible ?? '0',
    status: row.status ?? '0',
    remark: row.remark ?? ''
  })
  const parsed = splitPerms(row.perms)
  permsList.value = parsed.length ? parsed : ['']
}

/**
 * @param {{ id?: string|number, menuId?: string|number, parentId?: string|number, parentMenuType?: string }} [payload]
 */
function open(payload = {}) {
  resetForm()
  visible.value = true
  loadTree()
  const parentId = payload.parentId
  if (parentId != null) {
    form.parentId = String(parentId)
  }
  const id = payload.menuId ?? payload.id
  if (id != null && id !== '') {
    getMenu(id).then((res) => {
      fillForm(res.data || {})
    })
    return
  }
  applyParentMenuType(payload.parentMenuType)
}

function onIconSelected(name) {
  form.icon = name
  iconPopoverVisible.value = false
}

async function parseController() {
  if (!String(controllerSource.value || '').trim()) {
    ElMessage.warning('请粘贴 Controller 源码')
    return
  }
  const res = await parseMenuController(controllerSource.value)
  const data = res.data || {}
  const menu = data.menu || {}
  const buttons = data.buttons || []
  form.menuType = 'C'
  form.menuName = menu.menuName
  form.path = menu.path
  form.component = menu.component
  form.routeName = menu.routeName
  form.orderNum = menu.orderNum ?? 0
  permsList.value = splitPerms(menu.perms)
  parsedButtons.value = buttons.map((item) => ({
    ...item,
    checked: true
  }))
  quickParseOpen.value = ['parse']
  ElMessage.success(`已解析 1 个菜单、${buttons.length} 个按钮`)
}

function selectedButtons() {
  return parsedButtons.value.filter((item) => item.checked && String(item.menuName || '').trim())
}

function toPayload() {
  const body = {
    id: form.id,
    parentId: form.parentId === '' || form.parentId == null ? 0 : form.parentId,
    menuName: form.menuName,
    menuType: form.menuType,
    path: form.path,
    routeName: form.routeName,
    component: form.component,
    perms: joinPerms(permsList.value),
    icon: form.icon,
    orderNum: form.orderNum,
    query: form.query,
    isFrame: form.isFrame,
    isCache: form.isCache,
    visible: form.visible,
    status: form.status,
    remark: form.remark
  }
  if (body.menuType === 'M' && !body.component) {
    body.component = 'Layout'
  }
  if (body.menuType === 'F') {
    body.path = undefined
    body.component = undefined
    body.routeName = undefined
    body.icon = undefined
    body.query = undefined
    body.isFrame = '0'
    body.isCache = '0'
  }
  if (!body.id) {
    delete body.id
  }
  return body
}

async function submit() {
  await formRef.value?.validate()
  const body = toPayload()
  if (body.id) {
    await updateMenu(body)
    ElMessage.success('修改成功')
  } else {
    const buttons = body.menuType === 'C' ? selectedButtons() : []
    if (buttons.length) {
      await batchAddMenu({
        menu: body,
        buttons: buttons.map((item) => ({
          menuName: item.menuName,
          menuType: 'F',
          perms: item.perms,
          orderNum: item.orderNum,
          isFrame: '0',
          isCache: '0',
          visible: '0',
          status: '0'
        }))
      })
      ElMessage.success('已生成菜单和按钮')
    } else {
      await addMenu(body)
      ElMessage.success('新增成功')
    }
  }
  visible.value = false
  emit('success')
}

defineExpose({open})
</script>

<style scoped>
.menu-icon-prefix {
  width: 16px;
  height: 16px;
}

.perms-dynamic {
  width: 100%;
}

.perms-dynamic__row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.quick-parse {
  margin-bottom: 12px;
}

.quick-parse__actions {
  margin: 8px 0;
}

.quick-parse__table {
  width: 100%;
}
</style>
