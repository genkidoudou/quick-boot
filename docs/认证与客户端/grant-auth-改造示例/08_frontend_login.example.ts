/**
 * 示例：登录 API + 按 login-meta 渲染（TypeScript 示意）。
 * quick-ui 用 JS 同理；quick-h5 可直接贴近本文件。
 */

/** 与后端 login-meta 对齐 */
export type LoginMeta = {
  clientId: string
  clientName?: string
  grantTypes: Array<'password' | 'sms' | string>
  checkCaptcha: boolean
}

export type PasswordLoginBody = {
  clientId: string
  grantType: 'password'
  username: string
  password: string
  /** 天爱二次校验 id；checkCaptcha=true 时必填 */
  uuid?: string
}

export type SmsLoginBody = {
  clientId: string
  grantType: 'sms'
  mobile: string
  smsCode: string
  uuid?: string
}

const CLIENT_ID = import.meta.env.VITE_CLIENT_ID || import.meta.env.VITE_OAUTH_CLIENT_ID || 'quick-ui'

/** 拉取本端允许的登录方式（无 Token、无 Basic） */
export async function fetchLoginMeta(
  request: <T>(opt: { url: string; method: string }) => Promise<T>,
  clientId: string = CLIENT_ID,
): Promise<LoginMeta> {
  // 后端若包在 R.data 里，这里按你们 request 封装取 data
  return request<LoginMeta>({
    url: `/oauth/client/${encodeURIComponent(clientId)}/login-meta`,
    method: 'GET',
  })
}

/** 密码登录：必须带 clientId + grantType */
export function loginByPassword(
  request: <T>(opt: Record<string, unknown>) => Promise<T>,
  body: Omit<PasswordLoginBody, 'clientId' | 'grantType'> & { clientId?: string },
) {
  return request({
    url: '/login',
    method: 'POST',
    // 现网 LoginGrantDispatcher 读的是 request.getParameter ——
    // 若后端只吃 form/query，这里用 URLSearchParams 或 qs；若已改 JSON body 则另说。
    data: {
      clientId: body.clientId || CLIENT_ID,
      grantType: 'password',
      username: body.username,
      password: body.password,
      uuid: body.uuid,
    },
    auth: false, // h5：明确不带 Bearer
  })
}

/** 短信登录：仅当 meta.grantTypes 含 sms */
export function loginBySms(
  request: <T>(opt: Record<string, unknown>) => Promise<T>,
  body: Omit<SmsLoginBody, 'clientId' | 'grantType'> & { clientId?: string },
) {
  return request({
    url: '/login',
    method: 'POST',
    data: {
      clientId: body.clientId || CLIENT_ID,
      grantType: 'sms',
      mobile: body.mobile,
      smsCode: body.smsCode,
      uuid: body.uuid,
    },
    auth: false,
  })
}

/**
 * 登录页伪代码：
 *
 * const meta = await fetchLoginMeta(request)
 * showPasswordTab = meta.grantTypes.includes('password')
 * showSmsTab = meta.grantTypes.includes('sms')
 * needCaptcha = meta.checkCaptcha
 *
 * 提交前：若 needCaptcha 先完成行为验证码拿到 uuid，再调 loginByPassword / loginBySms
 */
