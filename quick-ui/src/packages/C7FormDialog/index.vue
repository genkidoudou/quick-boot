<template>
  <C7Dialog
      v-model="open"
      v-bind="forwardedAttrs"
      :on-confirm="handleConfirm"
  >
    <template v-if="$slots.title" #title>
      <slot name="title"/>
    </template>
    <slot/>
    <template v-if="$slots.extra" #extra>
      <slot name="extra"/>
    </template>
    <template v-if="$slots.footer" #footer>
      <slot name="footer"/>
    </template>
  </C7Dialog>
</template>

<script setup>
import {computed, nextTick, useAttrs} from 'vue'
import {ElMessage} from 'element-plus'
import C7Dialog from '../C7Dialog/index.vue'

defineOptions({name: 'C7FormDialog', inheritAttrs: false})

/**
 * 表单 CRUD 弹窗：在 {@link C7Dialog} 之上统一
 * 「open → getPayload 初始化 form → 校验 → submit → 提示 → onSuccess → 关窗」。
 *
 * **壳层**：标题、宽度、`mobile-mode`、footer 等透传给 `C7Dialog`。
 * **打开**：`open(row?)` → 调 `getPayload`（页面自行写 `form`）→ 清校验 → 显示。
 * **提交**：接管 `onConfirm`；不要再传 `:on-confirm`。
 *
 * @prop {object} form 响应式表单（用于判断新增/修改文案、onSuccess）
 * @prop {object|null} formRef `el-form` ref
 * @prop {function(row?): any|Promise} getPayload 初始化：页面闭包写 `form`
 * @prop {function(): Promise} submit 提交：页面闭包读 `form`
 * @prop {function(object): boolean} [isEdit] 默认看 `form.id`
 * @prop {string|function(boolean): string} [successMessage]
 * @prop {function(object, {isEdit: boolean}): any} [onSuccess]
 */

const RESERVED_ATTR_KEYS = new Set([
  'form',
  'formRef',
  'getPayload',
  'submit',
  'isEdit',
  'successMessage',
  'onSuccess',
  'onConfirm',
  'modelValue',
  'visible'
])

const props = defineProps({
  /** 响应式表单模型（判断编辑态 / onSuccess） */
  form: {type: Object, required: true},
  /** el-form ref（推荐传 `formRef`） */
  formRef: {type: Object, default: null},
  /**
   * 初始化：`(row?) => void | Promise`
   * 页面内直接 `Object.assign(form, …)` / 拉详情，不要 return 再灌表。
   */
  getPayload: {type: Function, required: true},
  /** 提交：`() => Promise`（直接用页面 `form`） */
  submit: {type: Function, required: true},
  /**
   * @param {Record<string, any>} form
   */
  isEdit: {type: Function, default: null},
  successMessage: {type: [String, Function], default: undefined},
  /**
   * @param {Record<string, any>} form
   * @param {{ isEdit: boolean }} ctx
   */
  onSuccess: {type: Function, default: null}
})

const open = defineModel({type: Boolean, default: false})

const attrs = useAttrs()

const forwardedAttrs = computed(() => {
  const out = {}
  for (const key of Object.keys(attrs)) {
    if (RESERVED_ATTR_KEYS.has(key)) continue
    if (key === 'onConfirm' || key === 'on-confirm') continue
    out[key] = attrs[key]
  }
  return out
})

/**
 * @returns {null|{ validate?: Function, clearValidate?: Function }}
 */
function resolveFormInstance() {
  const raw = props.formRef
  if (raw == null) return null
  if (Object.prototype.hasOwnProperty.call(raw, 'value')) {
    return raw.value || null
  }
  return raw
}

/**
 * @param {Record<string, any>} form
 */
function resolveIsEdit(form) {
  if (typeof props.isEdit === 'function') {
    return !!props.isEdit(form)
  }
  return form?.id != null && form.id !== ''
}

/**
 * @param {boolean} editing
 */
function resolveSuccessMessage(editing) {
  if (typeof props.successMessage === 'function') {
    return props.successMessage(editing)
  }
  if (typeof props.successMessage === 'string' && props.successMessage) {
    return props.successMessage
  }
  return editing ? '修改成功' : '新增成功'
}

/**
 * 打开弹窗：getPayload 初始化 → 清校验 → 显示。
 * 参数原样传给 `getPayload`（如部门「在某节点下新增」可传第二参）。
 * @param {...any} args
 */
async function openForm(...args) {
  await props.getPayload(...args)
  open.value = true
  await nextTick()
  resolveFormInstance()?.clearValidate?.()
}

async function handleConfirm() {
  const formInst = resolveFormInstance()
  if (formInst && typeof formInst.validate === 'function') {
    await formInst.validate()
  } else if (import.meta.env.DEV) {
    console.warn('[C7FormDialog] formRef 无效或缺少 validate()，已跳过表单校验')
  }

  const editing = resolveIsEdit(props.form)

  /** @type {any} 返回 string 作成功文案；false 跳过默认提示 */
  const result = await props.submit()

  if (result !== false) {
    ElMessage.success(typeof result === 'string' && result ? result : resolveSuccessMessage(editing))
  }

  if (typeof props.onSuccess === 'function') {
    await props.onSuccess(props.form, {isEdit: editing})
  }
}

defineExpose({
  open: openForm,
  confirm: handleConfirm
})
</script>
