/**
 * 向 Redis 写入天爱二次校验 ticket，再调登录拿 token。
 * 仅本地联调；需能访问 application-dev 中的 Redis。
 */
import crypto from "node:crypto"
import { createClient } from "redis"
import { createRequire } from "node:module"

// reuse sign helpers by inlining minimal copy
const BASE = process.env.API_BASE || "http://127.0.0.1:9993"
const USER = process.env.SMOKE_USER || "adminqc"
const PASS = process.env.SMOKE_PASS || "Admin@123"
const CLIENT_ID = "app"
const CLIENT_SECRET = "dbb43fe9b1eb4ff29eaed631009b6d1f"
const REDIS_URL = process.env.REDIS_URL || "redis://192.168.50.105:6379/7"

function formEncode(s) {
	return encodeURIComponent(s).replace(/%20/g, "+")
}
function canonicalQuery(rawQuery) {
	if (!rawQuery) return ""
	const items = rawQuery
		.split("&")
		.filter(Boolean)
		.map((pair) => {
			const eq = pair.indexOf("=")
			if (eq < 0) return [decodeURIComponent(pair.replace(/\+/g, " ")), ""]
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
	const qIdx = u.indexOf("?")
	if (qIdx < 0) return u || "/"
	const path = u.slice(0, qIdx)
	const query = canonicalQuery(u.slice(qIdx + 1))
	return query ? `${path}?${query}` : path
}
function toBase64Url(buf) {
	return Buffer.from(buf).toString("base64").replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "")
}
function buildSign(method, fullUrl, rawBody) {
	const timestamp = `${Date.now()}`
	const nonce = `${Date.now()}${Math.floor(Math.random() * 1e9)}`
	const path = pathAndQueryForSign(fullUrl)
	const payload = [CLIENT_ID, method.toUpperCase(), path, timestamp, nonce, rawBody || ""].join("\n")
	const hmacHex = crypto.createHmac("sha256", CLIENT_SECRET).update(payload, "utf8").digest("hex")
	return "v1." + toBase64Url(`${CLIENT_ID}|${timestamp}|${nonce}|${hmacHex}`)
}

async function main() {
	const uuid = crypto.randomUUID().replace(/-/g, "")
	const client = createClient({ url: REDIS_URL })
	await client.connect()
	const prefixes = [
		`captcha:secondary:${uuid}`,
		`captcha:secondary:${uuid}:`,
		`CAPTCHA:SECONDARY:${uuid}`
	]
	for (const k of prefixes) {
		await client.set(k, "1", { EX: 120 })
		console.log("SET", k)
	}
	// also dump matching keys pattern
	const sample = await client.keys("captcha:secondary*")
	console.log("existing secondary keys", sample.slice(0, 10), "n=", sample.length)
	await client.quit()

	const q =
		"username=" +
		formEncode(USER) +
		"&password=" +
		formEncode(PASS) +
		"&grantType=password&uuid=" +
		formEncode(uuid)
	const url = BASE + "/login?" + q
	const res = await fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/json;charset=UTF-8",
			sign: buildSign("POST", url, "")
		}
	})
	const json = await res.json()
	console.log(JSON.stringify(json))
	const token = json?.data?.accessToken || json?.data?.access_token || ""
	if (token) {
		console.log("TOKEN_OK")
		console.log(token)
	} else {
		process.exit(2)
	}
}

main().catch((e) => {
	console.error(e)
	process.exit(1)
})
