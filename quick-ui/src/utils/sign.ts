/**
 * 前端加签工具（对齐后端 SignUtils.buildSignBody / parser）。
 *
 * sign = 'v1.' + base64url(plain)
 * plain = clientId | clientId|ts|nonce|hmacHex
 */

import {InternalAxiosRequestConfig} from "axios";

/**
 * 拼签名原文（必须与后端 `SignUtils.buildSignBody` 字段顺序、分隔符完全一致）。
 * 任一段不一致都会导致 604 签名错误。
 */
function buildSignBody(
    clientId: string,
    method: string,
    path: string,
    timestamp: string,
    nonce: string,
    rawBody: string
): string {
    return [clientId, (method || '').toUpperCase(), path || '', timestamp || '', nonce || '', rawBody || ''].join('\n')
}

/** 字符串 → UTF-8 字节。 */
function utf8(s: string): Uint8Array {
    return new TextEncoder().encode(s)
}

/**
 * Web Crypto：HMAC-SHA256(secret, payload) → 小写 hex。
 */
async function hmacSha256Hex(secret: string, payload: string): Promise<string> {
    const key = await crypto.subtle.importKey(
        'raw',
        utf8(secret),
        {name: 'HMAC', hash: 'SHA-256'},
        false,
        ['sign']
    )
    const sig = await crypto.subtle.sign('HMAC', key, utf8(payload))
    return [...new Uint8Array(sig)].map((b) => b.toString(16).padStart(2, '0')).join('')
}

/** 字节数组 → Base64URL（无 padding）。 */
function toBase64Url(bytes: Uint8Array): string {
    let bin = ''
    bytes.forEach((b) => {
        bin += String.fromCharCode(b)
    })
    return btoa(bin).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '')
}

/**
 * 打 4 段验签票：`v1.` + base64url(clientId|ts|nonce|hmacHex)。
 */
export function packHmac(clientId: string, timestamp: string, nonce: string, hmacHex: string): string {
    const plain = `${clientId}|${timestamp}|${nonce}|${hmacHex}`
    return 'v1.' + toBase64Url(utf8(plain))
}

/**
 * 打 1 段标识票：`v1.` + base64url(clientId)。
 * 后端只解析出 clientId，不做时间窗 / nonce / HMAC。
 */
export function packIdentity(clientId: string): string {
    if (!clientId || clientId.includes('|')) {
        throw new Error('clientId 非法')
    }
    console.log("clientId", clientId, utf8(clientId.trim()), toBase64Url(utf8(clientId.trim())))
    return 'v1.' + toBase64Url(utf8(clientId.trim()))
}

/**
 * 用显式 clientId/secret 生成 4 段 sign 头。
 */
export async function signWithSecret(opts: {
    clientId: string
    clientSecret: string
    method: string
    path: string
    body?: string
}): Promise<string> {
    const timestamp = String(Date.now())
    const nonce = crypto.randomUUID().replace(/-/g, '')
    const payload = buildSignBody(
        opts.clientId,
        opts.method,
        opts.path,
        timestamp,
        nonce,
        opts.body || ''
    )
    const hmacHex = await hmacSha256Hex(opts.clientSecret, payload)
    return packHmac(opts.clientId, timestamp, nonce, hmacHex)
}

/**
 * 便捷加签：从 env 读 CLIENT_ID / CLIENT_SECRET。
 * - 未配置 SECRET → 1 段标识
 * - 已配置 SECRET → 4 段 HMAC
 */
export async function sign(opts: {
    method: string
    path: string
    body?: string
}): Promise<string> {
    const clientId = import.meta.env.VITE_SIGN_CLIENT_ID || ''
    const clientSecret = import.meta.env.VITE_SIGN_CLIENT_SECRET || ''
    if (!clientSecret) {
        return packIdentity(clientId)
    }
    return signWithSecret({
        clientId,
        clientSecret,
        method: opts.method,
        path: opts.path,
        body: opts.body
    })
}



/**
 * form表单参数处理
 * @param params
 */
export function canonicalQuery(params: Record<string, unknown> | URLSearchParams | undefined | null): string {
    if (params == null) {
        return ''
    }
    const flat: Record<string, string[]> = {}
    if (params instanceof URLSearchParams) {
        params.forEach((v, k) => {
            ;(flat[k] ||= []).push(v)
        })
    } else {
        for (const [k, v] of Object.entries(params)) {
            if (v === undefined || v === null) {
                continue
            }
            // 数组：多个同名 key；其它：转字符串
            if (Array.isArray(v)) {
                flat[k] = v.map((x) => String(x))
            } else {
                flat[k] = [String(v)]
            }
        }
    }
    const keys = Object.keys(flat).sort()
    const pairs: string[] = []
    for (const k of keys) {
        for (const v of flat[k]) {
            pairs.push(`${formEncode(k)}=${formEncode(v)}`)
        }
    }
    return pairs.join('&')
}

/**
 * 与 Java `URLEncoder.encode(..., UTF_8)` 对齐的编码。
 * - 普通 `encodeURIComponent` 空格是 `%20`，Java URLEncoder 空格是 `+`
 * - 这里把 `%20` 换成 `+`，并补上 Java 不编码但 encodeURIComponent 会编码的差异字符若需要可再扩
 */
export function formEncode(s: string): string {
    return encodeURIComponent(s).replace(/%20/g, '+')
}

/**
 * 从 axios config 解析「后端看到的 path」（去掉 /dev-api）以及「扁平 query 参数」。
 * @param config
 */
function resolvePathAndParams(config: InternalAxiosRequestConfig): {
    path: string
    params: Record<string, unknown>
} {

    const base = import.meta.env.VITE_APP_BASE_API || ''
    // 原始url
    let rawUrl = config.url || '/'
    // 去掉代理前缀，保证与 request.getRequestURI() 一致
    if (base && rawUrl.startsWith(base)) {
        rawUrl = rawUrl.slice(base.length) || '/'
    }

    // 拆开url自己带的参数
    const qIndex = rawUrl.indexOf("?");
    let path = qIndex >= 0 ? rawUrl.slice(0, qIndex) : rawUrl
    const inlineQuery = qIndex >= 0 ? rawUrl.slice(qIndex + 1) : ''

    if (!path.startsWith("/")) {
        path = "/" + path;
    }
    // 合并：url 内联 query + config.params
    const params: Record<string, unknown> = {}
    if (inlineQuery) {
        const usp = new URLSearchParams(inlineQuery);
        usp.forEach((v, k) => {
            // 同名多值：收成数组
            if (params[k] === undefined) {
                params[k] = v
            } else if (Array.isArray(params[k])) {
                ;(params[k] as unknown[]).push(v)
            } else {
                params[k] = [params[k], v]
            }
        })
    }
    if (config.params && typeof config.params === 'object') {
        Object.assign(params, config.params as Record<string, unknown>)
    }
    return {path, params}
}

/**
 * 拼第 3 段 pathAndQuery。
 *
 * @param path  必须以 `/` 开头，且已去掉 `/dev-api` 等代理前缀
 * @param query {@link canonicalQuery} 结果；空则不加 `?`
 */
export function pathAndQuery(path: string, query: string): string {
    const p = path || '/'
    if (!query) {
        return p
    }
    return `${p}?${query}`
}

/**
 * 得到与「真正发出的 body」一致的字符串。
 *
 * | 场景 | 行为 |
 * |------|------|
 * | GET / HEAD | `''`（query 走 pathAndQuery，不进 body） |
 * | data 已是 string | 原样使用（调用方已序列化好 JSON/form） |
 * | FormData / multipart | 返回 `null`；上层按空 body 做 HMAC（与后端约定） |
 * | Content-Type 含 form-urlencoded | `URLSearchParams` 编码，勿 JSON.stringify |
 * | 其它对象 | `JSON.stringify`（默认 JSON API） |
 */
function resolveRawBody(config: InternalAxiosRequestConfig, method: string): string | null {
    if (method === 'GET' || method === 'HEAD') {
        return ''
    }
    const data = config.data
    if (data == null) {
        return ''
    }
    if (typeof data === 'string') {
        return data
    }
    if (typeof FormData !== 'undefined' && data instanceof FormData) {
        // multipart 不整包加签：返回 null，上层可降级为仅标识或直接不改 headers
        return null
    }

    const headers = config.headers || {}
    const ct = String(
        (headers as any)['Content-Type'] ||
        (headers as any)['content-type'] ||
        ''
    ).toLowerCase()

    if (ct.includes('application/x-www-form-urlencoded')) {
        return new URLSearchParams(data as Record<string, string>).toString()
    }
    // 默认 JSON：与 axios 实际序列化一致
    return JSON.stringify(data)
}

/**
 * 给请求挂上 `sign` 头。
 *
 * 流程：
 * 1. 规范化 path + 合并 query → pathAndQuery
 * 2. 按 Content-Type 得到 rawBody
 * 3. 有 SECRET → 4 段 HMAC；否则 1 段标识
 */
export async function attachSignHeader(config: InternalAxiosRequestConfig) {
    const clientId = import.meta.env.VITE_SIGN_CLIENT_ID || ''
    const clientSecret = import.meta.env.VITE_SIGN_CLIENT_SECRET || ''
    const method = (config.method || 'get').toUpperCase()
    const {path, params} = resolvePathAndParams(config)
    //  query
    const query = canonicalQuery(params);
    const pathAndQuery2 = pathAndQuery(path, query);
    // multipart / FormData：不整包加签，与后端 RequestSignFilter 约定按空 body 验 HMAC
    const rawBody = resolveRawBody(config, method) ?? ''

    // sign() 的字段名是 path（第 3 段），此处传入已拼好的 pathAndQuery
    const sign2 = clientSecret
        ? await sign({
            method,
            path: pathAndQuery2,
            body: rawBody
        })
        : packIdentity(clientId)

    config.headers = config.headers || {}
    config.headers.sign = sign2
    return config
}