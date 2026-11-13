/** quick-h5：默认 1 段标识 */
import { packIdentitySign, packHmacSign, newTsAndNonce } from './05_sign.example'

const CLIENT_ID = import.meta.env.VITE_CLIENT_ID || 'quick-h5'
const CLIENT_SECRET = import.meta.env.VITE_CLIENT_SECRET || ''
const REQUIRE_HMAC = import.meta.env.VITE_SIGN_REQUIRE_HMAC === 'true'

export async function buildUniSignHeader(
  method: string,
  pathAndQuery: string,
  data?: unknown,
): Promise<{ sign: string }> {
  if (!REQUIRE_HMAC) {
    return { sign: packIdentitySign(CLIENT_ID) }
  }
  const m = method.toUpperCase()
  const rawBody = data == null || m === 'GET' || m === 'HEAD' ? '' : JSON.stringify(data)
  const { timestamp, nonce } = newTsAndNonce()
  return {
    sign: await packHmacSign({
      clientId: CLIENT_ID,
      method: m,
      pathAndQuery,
      timestamp,
      nonce,
      rawBody,
      clientSecret: CLIENT_SECRET,
    }),
  }
}
