/**
 * 表单/URL 校验工具。
 * - 布尔判断：`isExternal` / `isPhone` 等
 * - Element Plus 规则：`userNameRules` / `passwordRules` 等，供 `el-form` `:rules` 复用
 *
 * 其余 validator 也可通过 main.js 挂载为 $validate，供模板或 Options API 按需调用。
 */

/** 常用正则（与业务约定对齐） */
export const PATTERNS = {
  /** 字母或数字，至少 7 位 */
  userName: /^[A-Za-z0-9]{7,}$/,
  /** 至少 8 位，含大小写、数字、特殊字符 */
  password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/,
  /** 中国大陆手机号 */
  phone: /^1[3-9]\d{9}$/
}

/** 是否为外链（http(s)/mailto/tel）；兼容误拼的 `/https://...` */
export function isExternal(path) {
  if (path == null || typeof path !== 'string') {
    return false
  }
  return /^(https?:|mailto:|tel:)/.test(path) || /^\/+https?:/i.test(path)
}

/** 是否包含 http:// 或 https:// 协议头 */
export function isHttp(url) {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
}

/** 简单邮箱格式 */
export function isEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

/** 中国大陆手机号（11 位） */
export function isPhone(phone) {
  return PATTERNS.phone.test(String(phone ?? ''))
}

/** 系统用户名 */
export function isUserName(name) {
  return PATTERNS.userName.test(String(name ?? ''))
}

/** 强密码（与后台 PatternConstants 约定一致） */
export function isStrongPassword(password) {
  return PATTERNS.password.test(String(password ?? ''))
}

/** http(s)/ftp URL */
export function isUrl(url) {
  const re = /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i
  return re.test(url)
}

/** 纯数字字符串 */
export function isNumber(num) {
  return /^[0-9]*$/.test(num)
}

/** 整数（可带正负号） */
export function isInteger(num) {
  return /^[-+]?\d+$/.test(num)
}

/** 小数（可带正负号） */
export function isDecimal(num) {
  return /^[-+]?\d+(\.\d+)?$/.test(num)
}

/** 纯英文字母 */
export function isAlphabets(str) {
  return /^[a-zA-Z]*$/.test(str)
}

/** 纯大写字母 */
export function isAlphabetsUpper(str) {
  return /^[A-Z]*$/.test(str)
}

/** 纯小写字母 */
export function isAlphabetsLower(str) {
  return /^[a-z]*$/.test(str)
}

/** 字母与数字 */
export function isAlphanumeric(str) {
  return /^[a-zA-Z0-9]*$/.test(str)
}

/** 纯中文 */
export function isChinese(str) {
  return /^[\u4e00-\u9fa5]*$/.test(str)
}

/** 15/18 位身份证号 */
export function isIdCard(idCard) {
  return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)
}

// ---------- Element Plus 表单规则 ----------

/**
 * @param {string} message
 * @param {'blur'|'change'|Array<'blur'|'change'>} [trigger='blur']
 */
export function requiredRule(message, trigger = 'blur') {
  return { required: true, message, trigger }
}

/** 用户名：必填 + 字母数字且大于 6 位 */
export const userNameRules = [
  requiredRule('用户名不能为空'),
  {
    pattern: PATTERNS.userName,
    message: '用户名须大于6位且仅为字母或数字',
    trigger: 'blur'
  }
]

/** 昵称必填 */
export const nickNameRules = [requiredRule('昵称不能为空')]

/** 密码强度（不含必填，便于编辑时留空） */
export const passwordPatternRule = {
  pattern: PATTERNS.password,
  message: '密码至少8位，且须包含大小写字母、数字与特殊字符',
  trigger: 'blur'
}

/**
 * 密码规则。
 * @param {{ required?: boolean }} [options]
 * - 新增：`required: true`（默认）→ 必填 + 强度
 * - 编辑：`required: false` → 空则跳过，有值则校验强度
 */
export function passwordRules(options = {}) {
  const required = options.required !== false
  if (required) {
    return [requiredRule('密码不能为空'), passwordPatternRule]
  }
  return [
    {
      validator: (_rule, value, callback) => {
        if (value == null || String(value).trim() === '') {
          callback()
          return
        }
        if (!isStrongPassword(value)) {
          callback(new Error(passwordPatternRule.message))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

/** 邮箱格式（允许空） */
export const emailRules = [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]

/** 手机号格式（允许空） */
export const phoneRules = [
  {
    validator: (_rule, value, callback) => {
      if (value == null || String(value).trim() === '') {
        callback()
        return
      }
      if (!isPhone(value)) {
        callback(new Error('手机号格式不正确'))
        return
      }
      callback()
    },
    trigger: 'blur'
  }
]
