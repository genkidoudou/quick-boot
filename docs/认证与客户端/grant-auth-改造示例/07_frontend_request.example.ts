/**
 * 示例：改造请求拦截器——去掉 OAuth Client Basic。
 *
 * 对照：
 * - quick-ui/src/utils/request.js
 * - quick-h5/src/api/http.ts
 *
 * 改造前：无用户 Token 时注入 buildObfuscatedBasicAuthorization()
 * 改造后：无 Token 则不带 Authorization；登录/验证码走 ignore-url
 */

// -------- 改造前（删除）--------
// if (getToken() && !isToken) {
//   config.headers['Authorization'] = 'Bearer ' + getToken()
// } else {
//   const basic = buildObfuscatedBasicAuthorization()
//   if (basic) config.headers['Authorization'] = basic
// }

// -------- 改造后 --------
export function applyAuthHeader(config: {
  headers?: Record<string, string>
}, getToken: () => string | undefined, isTokenDisabled: boolean) {
  const headers = config.headers || (config.headers = {})
  const token = getToken()
  if (token && !isTokenDisabled) {
    // 仅用户会话
    headers.Authorization = `Bearer ${token}`
  }
  // 不再设置 Basic；也不再 import oauthClientBasic
  return config
}

/**
 * 可选：所有请求带应用标识（便于网关/审计；不是密钥）
 * 注意：安全仍以后端 grant_types 校验为准，header 可伪造，只能作提示。
 */
export function applyClientIdHeader(
  config: { headers?: Record<string, string> },
  clientId: string,
) {
  const headers = config.headers || (config.headers = {})
  if (clientId) {
    headers['X-Client-Id'] = clientId
  }
  return config
}
