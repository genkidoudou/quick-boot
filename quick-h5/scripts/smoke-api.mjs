/**
 * H5 管理模块 API 冒烟：登录 + 各模块 page。
 * node quick-h5/scripts/smoke-api.mjs
 *
 * 环境变量：
 * - API_BASE（默认 http://127.0.0.1:9993）
 * - SMOKE_USER / SMOKE_PASS（默认 adminqc / Admin@123）
 * - TOKEN：若已有 accessToken，跳过登录（验证码开启时推荐）
 *
 * 验证码：application-dev.yml 中 qc.captcha.enabled=false 并重启后端后可直登。
 */
import crypto from "node:crypto"

const BASE = process.env.API_BASE || "http://127.0.0.1:9993"
const USER = process.env.SMOKE_USER || "adminqc"
const PASS = process.env.SMOKE_PASS || "Admin@123"
const CLIENT_ID = "app"
const CLIENT_SECRET = "dbb43fe9b1eb4ff29eaed631009b6d1f"

function formEncode(s) {
	return encodeURIComponent(s).replace(/%20/g, "+")
}

function canonicalQuery(rawQuery) {
	if (!rawQuery) return ""
	const pairs = rawQuery.split("&").filter(Boolean)
	const items = pairs.map((pair) => {
		const eq = pair.indexOf("=")
		if (eq < 0) {
			return [decodeURIComponent(pair.replace(/\+/g, " ")), ""]
		}
		return [
			decodeURIComponent(pair.slice(0, eq).replace(/\+/g, " ")),
			decodeURIComponent(pair.slice(eq + 1).replace(/\+/g, " "))
		]
	})
	items.sort((a, b) => (a[0] > b[0] ? 1 : a[0] < b[0] ? -1 : 0))
	return items.map(([k, v]) => `${formEncode(k)}=${formEncode(v)}`).join("&")
}

function pathAndQueryForSign(fullUrl) {
	let u = fullUrl
	const scheme = u.indexOf("://")
	if (scheme >= 0) {
		const slash = u.indexOf("/", scheme + 3)
		u = slash >= 0 ? u.slice(slash) : "/"
	}
	for (const prefix of ["/dev-api", "/prod-api"]) {
		if (u.startsWith(prefix)) {
			u = u.slice(prefix.length) || "/"
			if (!u.startsWith("/")) u = "/" + u
		}
	}
	const qIdx = u.indexOf("?")
	if (qIdx < 0) return u || "/"
	const path = u.slice(0, qIdx)
	const query = canonicalQuery(u.slice(qIdx + 1))
	return query ? `${path}?${query}` : path
}

function toBase64Url(buf) {
	return Buffer.from(buf)
		.toString("base64")
		.replace(/\+/g, "-")
		.replace(/\//g, "_")
		.replace(/=+$/g, "")
}

function buildSign(method, fullUrl, rawBody) {
	const timestamp = `${Date.now()}`
	const nonce = `${Date.now()}${Math.floor(Math.random() * 1e9)}`
	const path = pathAndQueryForSign(fullUrl)
	const payload = [CLIENT_ID, method.toUpperCase(), path, timestamp, nonce, rawBody || ""].join("\n")
	const hmacHex = crypto.createHmac("sha256", CLIENT_SECRET).update(payload, "utf8").digest("hex")
	const plain = `${CLIENT_ID}|${timestamp}|${nonce}|${hmacHex}`
	return "v1." + toBase64Url(plain)
}

async function request(method, pathAndQuery, bodyObj, token) {
	const rawBody = bodyObj == null ? "" : JSON.stringify(bodyObj)
	const url = BASE + pathAndQuery
	const headers = {
		"Content-Type": "application/json;charset=UTF-8",
		sign: buildSign(method, url, rawBody)
	}
	if (token) headers.Authorization = `Bearer ${token}`
	const res = await fetch(url, {
		method,
		headers,
		body: method === "GET" ? undefined : rawBody || undefined
	})
	const json = await res.json().catch(() => ({}))
	return { status: res.status, json }
}

function okCode(code) {
	return code == 200 || code == 0
}

const pages = [
	["用户", "/sys/user/page"],
	["角色", "/sys/role/page"],
	["菜单", "/sys/menu/page"],
	["部门", "/sys/dept/page"],
	["字典类型", "/sys/dictType/page"],
	["字典数据", "/sys/dict/data/page"],
	["客户端", "/sys/oauthclient/page"],
	["参数", "/sys/config/page"],
	["登录日志", "/sys/logininfor/page"],
	["操作日志", "/sys/operLog/page"],
	["定时任务", "/job/job/page"],
	["任务日志", "/job/jobLog/page"],
	["文件", "/sys/file/page"],
	["文件分类", "/sys/fileClassify/page"],
	["站内信(我的)", "/msg/inbox/mine/page"]
]

async function main() {
	console.log(`API_BASE=${BASE} user=${USER}`)
	let token = process.env.TOKEN || ""
	if (token.length == 0) {
		const q =
			"username=" +
			formEncode(USER) +
			"&password=" +
			formEncode(PASS) +
			"&grantType=password"
		const login = await request("POST", "/login?" + q, null, null)
		const data = login.json?.data
		token = data?.accessToken || data?.access_token || data?.token || ""
		if (!okCode(login.json?.code) || !token) {
			console.error("LOGIN FAIL", JSON.stringify(login.json))
			console.error(
				"提示：若 code=30012，请将 qc.captcha.enabled 设为 false 并重启后端，或传入 TOKEN=..."
			)
			process.exit(1)
		}
		console.log("LOGIN OK")
	} else {
		console.log("LOGIN SKIP (TOKEN env)")
	}

	const pageBody = { current: 1, size: 5, param: {} }
	let fail = 0
	for (const [name, path] of pages) {
		const r = await request("POST", path, pageBody, token)
		const code = r.json?.code
		const ok = okCode(code)
		const total = r.json?.data?.total
		const rec = r.json?.data?.records
		const n = Array.isArray(rec)
			? rec.length
			: Array.isArray(r.json?.data)
				? r.json.data.length
				: "?"
		console.log(`${ok ? "OK  " : "FAIL"} ${name.padEnd(10)} ${path} code=${code} total=${total ?? "-"} rows=${n}`)
		if (!ok) {
			console.log("      ", r.json?.msg || "")
			fail++
		}
	}

	for (const path of ["/sys/user/profile", "/auth/me"]) {
		const pr = await request("GET", path, null, token)
		const ok = okCode(pr.json?.code)
		console.log(`${ok ? "OK  " : "FAIL"} 资料/me     ${path} code=${pr.json?.code}`)
		if (!ok) fail++
		else break
	}

	console.log(fail == 0 ? "\nSMOKE PASS" : `\nSMOKE FAIL count=${fail}`)
	process.exit(fail == 0 ? 0 : 2)
}

main().catch((e) => {
	console.error(e)
	process.exit(1)
})
