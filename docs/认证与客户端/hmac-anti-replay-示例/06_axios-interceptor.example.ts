/**
 * quick-ui：默认只打「1 段标识」sign；需要验签时再改用 packHmacSign。
 */
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import { newTsAndNonce, packHmacSign, packIdentitySign } from './05_sign.example'

const CLIENT_ID = import.meta.env.VITE_CLIENT_ID || 'quick-ui'
const CLIENT_SECRET = import.meta.env.VITE_CLIENT_SECRET || ''
/** false=只传 clientId；true=四段 HMAC */
const REQUIRE_HMAC = import.meta.env.VITE_SIGN_REQUIRE_HMAC === 'true'

function toBackendPathAndQuery(url: string): string {
  const u = new URL(url, 'http://local.invalid')
  let path = u.pathname + (u.search || '')
  return path.replace(/^\/dev-api/, '') || '/'
}

function serializeBody(data: unknown): string {
  if (data == null || data === '') return ''
  if (typeof data === 'string') return data
  if (typeof FormData !== 'undefined' && data instanceof FormData) return ''
  return JSON.stringify(data)
}

export function attachSignInterceptor(service: AxiosInstance) {
  service.interceptors.request.use(async (config: InternalAxiosRequestConfig) => {
    if (!CLIENT_ID) return config

    config.headers = config.headers || {}
    if (!REQUIRE_HMAC) {
      config.headers.sign = packIdentitySign(CLIENT_ID)
      return config
    }

    const method = (config.method || 'get').toUpperCase()
    const pathAndQuery = toBackendPathAndQuery(config.url || '/')
    const rawBody = ['GET', 'HEAD', 'DELETE'].includes(method) ? '' : serializeBody(config.data)
    const { timestamp, nonce } = newTsAndNonce()
    config.headers.sign = await packHmacSign({
      clientId: CLIENT_ID,
      method,
      pathAndQuery,
      timestamp,
      nonce,
      rawBody,
      clientSecret: CLIENT_SECRET,
    })
    return config
  })
}
