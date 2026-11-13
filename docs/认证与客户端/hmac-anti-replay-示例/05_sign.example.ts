/**
 * 前端 sign 打包（与 SignUtils 对齐）。
 * 标识模式只传 clientId；验签模式传四段。
 */

function toHex(buf: ArrayBuffer): string {
  return [...new Uint8Array(buf)].map((b) => b.toString(16).padStart(2, '0')).join('')
}

function bytesToBase64Url(bytes: Uint8Array): string {
  let bin = ''
  for (const b of bytes) bin += String.fromCharCode(b)
  return btoa(bin).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '')
}

function encodePlain(plain: string): string {
  return `v1.${bytesToBase64Url(new TextEncoder().encode(plain))}`
}

/** 仅客户端标识（1 段） */
export function packIdentitySign(clientId: string): string {
  if (!clientId || clientId.includes('|')) {
    throw new Error('clientId 非法')
  }
  return encodePlain(clientId)
}

export async function sha256Hex(body: string): Promise<string> {
  const dig = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(body ?? ''))
  return toHex(dig)
}

export async function hmacSha256Hex(secret: string, canonical: string): Promise<string> {
  const key = await crypto.subtle.importKey(
    'raw',
    new TextEncoder().encode(secret),
    { name: 'HMAC', hash: 'SHA-256' },
    false,
    ['sign'],
  )
  const sig = await crypto.subtle.sign('HMAC', key, new TextEncoder().encode(canonical))
  return toHex(sig)
}

/** 完整验签（4 段） */
export async function packHmacSign(input: {
  clientId: string
  method: string
  pathAndQuery: string
  timestamp: string
  nonce: string
  rawBody: string
  clientSecret: string
}): Promise<string> {
  const method = input.method.toUpperCase()
  const bodyHash = await sha256Hex(input.rawBody ?? '')
  const canonical = [method, input.pathAndQuery, input.timestamp, input.nonce, bodyHash].join('\n')
  const hmacHex = await hmacSha256Hex(input.clientSecret, canonical)
  const plain = `${input.clientId}|${input.timestamp}|${input.nonce}|${hmacHex}`
  return encodePlain(plain)
}

export function newTsAndNonce(): { timestamp: string; nonce: string } {
  return { timestamp: String(Date.now()), nonce: crypto.randomUUID() }
}
