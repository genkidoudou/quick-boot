# quick-h5 登录页 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 `quick-h5` 增加 Soft Glass 风格登录页（账号 + 手机），接 quick-boot 登录/验证码 API，H5 用官方 TAC、小程序/App 用原生 captcha UI，登录后进首页。

**Architecture:** 轻量鉴权层：`config/env` + `api/http` + `api/login`/`captcha` + `store/user`；登录页只编排 UI；`components/captcha/captcha-host` 用条件编译分流 H5 TAC 与原生滑块，统一返回 `uuid`。视觉用 RiceUI + Soft Glass 氛围，禁止混用其它 UI 库。

**Tech Stack:** uni-app x（vapor / uts）、RiceUI、`uni.request`、天爱 captcha（H5 Web SDK + 原生 UI）、sa-token Bearer（`accessToken`）。

**Spec:** `docs/superpowers/specs/2026-11-16-quick-h5-login-design.md`

## Global Constraints

- 仅 `rice-*` 组件；写前查 `.cursor/skills/rice-ui/references/api/`；写后 `node .cursor/skills/rice-ui/scripts/verify-rice-usage.mjs quick-h5/pages/login`（及 captcha 相关页）至 0 违规
- 脚本 `lang="uts"`；蒸汽模式兼容；颜色优先 `--rice-*` / 主题，主色目标 `#8D51F9`
- 账号：`POST /login` params：`username`、`password`、`grantType=password`、可选 `uuid`；响应 `data.accessToken`
- Captcha：`GET /api/captcha/config`；generate/validate 与 quick-ui 同路径；H5 官方 TAC；非 H5 原生 UI，**禁止**小程序 web-view 作为唯一方案
- 手机：客户端对齐 `POST /sendSms`、`POST /phoneLogin`（与 quick-ui）；**已知风险：仓库后端当前未见这两接口实现**——UI+客户端必须落地，联调若 404 则 toast 明确错误并在验收标 PENDING，不伪造成功
- 不做扫码、社交、HMAC、完整路由守卫；首页仅「无 token → reLaunch 登录」
- Token 字段优先 `accessToken`（兼容 `access_token`）；`Authorization: Bearer <token>`

---

## File Map

| 文件 | 职责 |
|------|------|
| `quick-h5/config/env.uts` | API base、品牌标题 |
| `quick-h5/api/http.uts` | `uni.request` 封装 |
| `quick-h5/api/login.uts` | login / phoneLogin / sendSms |
| `quick-h5/api/captcha.uts` | config + TAC URL |
| `quick-h5/store/user.uts` | token 读写 / clear |
| `quick-h5/components/captcha/captcha-host.uvue` | `openCaptcha()` 统一入口 |
| `quick-h5/components/captcha/captcha-h5.uvue` | H5 TAC |
| `quick-h5/components/captcha/captcha-native.uvue` | 非 H5 原生滑块 |
| `quick-h5/pages/login/login.uvue` | Soft Glass 登录页 |
| `quick-h5/pages.json` | 登录为首页 |
| `quick-h5/pages/index/index.uvue` | 无 token 回登录；可加退出 |
| `quick-h5/static/tac/*` | H5 TAC 静态资源（按 quick-ui 同源方式引入，路径以实现为准） |

---

### Task 1: env + http + user store

**Files:**
- Create: `quick-h5/config/env.uts`
- Create: `quick-h5/api/http.uts`
- Create: `quick-h5/store/user.uts`

**Interfaces:**
- Produces:
  - `export const API_BASE : string`（开发默认 `http://127.0.0.1:9993` 或与本地后端一致；注释说明小程序需配合法域名）
  - `export const APP_TITLE : string`（如 `QuickBoot`）
  - `export function request(opts : RequestOptions) : Promise<UTSJSONObject>`
  - `export function getToken() : string` / `setToken(token : string)` / `clearToken()` / `hasToken() : boolean`

- [ ] **Step 1: 创建 `config/env.uts`**

```uts
export const API_BASE : string = "http://127.0.0.1:9993"
export const APP_TITLE : string = "QuickBoot"
export const TOKEN_KEY : string = "qb_access_token"
```

（若项目已有约定端口，以实际后端为准，写进文件注释。）

- [ ] **Step 2: 创建 `api/http.uts`**

实现要点：

```uts
export type HttpMethod = "GET" | "POST"

export type RequestOptions = {
	url : string
	method ?: HttpMethod
	data ?: any
	params ?: UTSJSONObject
	needToken ?: boolean
}

export function request(opts : RequestOptions) : Promise<UTSJSONObject> {
	// 1. 拼 API_BASE + opts.url
	// 2. GET 时把 params 拼 query；POST login 用 params 作 query（对齐 quick-ui /login）
	// 3. header: content-type；needToken!==false 且有 token 时加 Authorization Bearer
	// 4. uni.request；成功解析 body；code!=200 时 reject 并 toast message
}
```

`/login` 必须用 **query/params** 传参（与 `LoginController` + quick-ui 一致），不要只放 body。

- [ ] **Step 3: 创建 `store/user.uts`**

```uts
export function getToken() : string { /* uni.getStorageSync(TOKEN_KEY) */ }
export function setToken(token : string) : void { /* setStorageSync */ }
export function clearToken() : void { /* removeStorageSync */ }
export function hasToken() : boolean { return getToken().length > 0 }
```

- [ ] **Step 4: 冒烟**

在临时页面或控制台逻辑中调用 `hasToken()`；确认 storage 读写无崩。HBuilderX 编译 `quick-h5` 无 uts 报错。

- [ ] **Step 5: Commit**

```bash
git add quick-h5/config/env.uts quick-h5/api/http.uts quick-h5/store/user.uts
git commit -m "feat(quick-h5): add env, http client, and token store"
```

（若 `git commit -m` 注入 `--trailer` 失败，改用 `git.exe commit -F msg.txt`。）

---

### Task 2: login + captcha API 客户端

**Files:**
- Create: `quick-h5/api/login.uts`
- Create: `quick-h5/api/captcha.uts`

**Interfaces:**
- Consumes: `request` from `api/http.uts`；`API_BASE` from `config/env.uts`
- Produces:
  - `loginByPassword(username, password, uuid : string | null) : Promise<string /* accessToken */>`
  - `loginByPhone(phone, smsCode) : Promise<string>`
  - `sendSmsCode(phone) : Promise<void>`
  - `fetchCaptchaEnabled() : Promise<boolean>`
  - `getCaptchaTacUrls() : { generateUrl : string, validateUrl : string }`
  - `parseAccessToken(body : UTSJSONObject) : string`（测 `accessToken` 优先）

- [ ] **Step 1: 实现 `parseAccessToken` + login API**

```uts
export function parseAccessToken(data : UTSJSONObject | null) : string {
	if (data == null) return ""
	const t1 = data.getString("accessToken")
	if (t1 != null && t1.length > 0) return t1
	const t2 = data.getString("access_token")
	if (t2 != null && t2.length > 0) return t2
	return ""
}

export function loginByPassword(username : string, password : string, uuid : string | null) : Promise<string> {
	const params = JSON.parseObject("{}")!
	params.set("username", username)
	params.set("password", password)
	params.set("grantType", "password")
	if (uuid != null && uuid.length > 0) {
		params.set("uuid", uuid)
	}
	return request({
		url: "/login",
		method: "POST",
		params: params,
		needToken: false
	} as RequestOptions).then((res) : string => {
		const data = res.getJSON("data")
		const token = parseAccessToken(data)
		if (token.length == 0) {
			return Promise.reject("登录成功但未返回 token")
		}
		return token
	})
}
```

（UTS Promise API 以项目现有写法为准；保持语义。）

- [ ] **Step 2: phone + sms 客户端（即使后端暂缺也要写出）**

```uts
export function sendSmsCode(phone : string) : Promise<void> { /* POST /sendSms body {phone} needToken false */ }
export function loginByPhone(phone : string, smsCode : string) : Promise<string> { /* POST /phoneLogin → parseAccessToken */ }
```

- [ ] **Step 3: captcha API**

对齐 `quick-ui/src/api/captcha.js`：

```uts
export function fetchCaptchaEnabled() : Promise<boolean> {
	return request({ url: "/api/captcha/config", method: "GET", needToken: false }).then((res) : boolean => {
		const data = res.getJSON("data")
		return data != null && data.getBoolean("captchaEnabled") == true
	})
}

export function getCaptchaTacUrls() : UTSJSONObject {
	const base = API_BASE.endsWith("/") ? API_BASE.substring(0, API_BASE.length - 1) : API_BASE
	const o = JSON.parseObject("{}")!
	o.set("generateUrl", base + "/api/captcha/generate")
	o.set("validateUrl", base + "/api/captcha/validate")
	return o
}
```

- [ ] **Step 4: Commit**

```bash
git add quick-h5/api/login.uts quick-h5/api/captcha.uts
git commit -m "feat(quick-h5): add login and captcha API clients"
```

---

### Task 3: Captcha host（H5 TAC + 原生骨架）

**Files:**
- Create: `quick-h5/components/captcha/captcha-host.uvue`
- Create: `quick-h5/components/captcha/captcha-h5.uvue`
- Create: `quick-h5/components/captcha/captcha-native.uvue`
- Optionally copy TAC assets under `quick-h5/static/tac/`（从 quick-ui 已用的 `/tac` 静态资源或官方 release 对齐）

**Interfaces:**
- Consumes: `getCaptchaTacUrls`、`fetchCaptchaEnabled`（enabled 判断在登录页更合适）
- Produces: 父组件通过 `ref` 调用 `open() : Promise<string>`（uuid）；取消/失败 reject

- [ ] **Step 1: `captcha-host.uvue`**

用条件编译包一层：

```uvue
<template>
	<!-- #ifdef H5 -->
	<captcha-h5 ref="innerRef"></captcha-h5>
	<!-- #endif -->
	<!-- #ifndef H5 -->
	<captcha-native ref="innerRef"></captcha-native>
	<!-- #endif -->
</template>
```

`defineExpose({ open })`：`open` 转调 `innerRef.open()`。

- [ ] **Step 2: `captcha-h5.uvue`**

对齐 `quick-ui` `openCaptcha`：蒙层 + `#tianai-captcha-box`；`initTAC` + `generateUrl`/`validateUrl`；`validSuccess` 取 `res.data.id` 或 `res.id` resolve Promise；关闭 reject。

静态资源路径按 H5 可加载方式配置（`/static/tac` 或 CDN）；`initTAC` 不可用时 toast「验证码脚本未加载」。

- [ ] **Step 3: `captcha-native.uvue`**

最小可用滑块流程（可参考社区天爱小程序协议，但接口必须打本项目 generate/validate）：

1. 打开弹层 → 请求 generate → 展示背景图/滑块图  
2. 用户拖动 → 组装轨迹 → validate  
3. 成功 resolve id；失败 reload  

若首版时间紧：**至少**完成弹层 + generate 展示 + validate 成功路径的可编译骨架，并在组件顶部注释 PENDING 真机细节；**不得**在小程序用 web-view 嵌 TAC 作为唯一实现。

- [ ] **Step 4: 编译检查**

HBuilderX / CLI：H5 与 mp-weixin 各编译一次，确认条件编译无错。

- [ ] **Step 5: Commit**

```bash
git add quick-h5/components/captcha quick-h5/static/tac
git commit -m "feat(quick-h5): add multi-platform captcha host"
```

---

### Task 4: 登录页 Soft Glass UI + 双 Tab 流程

**Files:**
- Create: `quick-h5/pages/login/login.uvue`
- Modify: `quick-h5/pages.json`

**Interfaces:**
- Consumes: `loginByPassword`、`loginByPhone`、`sendSmsCode`、`fetchCaptchaEnabled`、`setToken`、`captcha-host.open`、`APP_TITLE`
- Produces: 启动页可登录并 `reLaunch` 首页

- [ ] **Step 1: 注册路由**

`pages.json` 将登录置于 `pages` 数组**第一项**：

```json
{
  "path": "pages/login/login",
  "style": {
    "navigationStyle": "custom",
    "navigationBarTitleText": "登录"
  }
}
```

保留 index 与 rice dialog/action-sheet 页。

- [ ] **Step 2: 搭建登录 UI**

- Soft Glass：氛围光斑 + 卡片（大圆角、边框、`--rice-background` / 卡片面）
- 品牌：`APP_TITLE` + 「欢迎登录」
- `rice-subsection`：`list=['账号登录','手机登录']`，`@change` 切 Tab
- 账号：`rice-input` 用户名；`rice-input show-password` 密码；`rice-button` 登录
- 手机：`rice-input` 手机号；验证码行 + 「获取验证码」；`rice-button` 登录
- 挂载 `<captcha-host ref="captchaRef" />`

对照 api 卡：`rice-input.md`、`rice-subsection.md`、`rice-button`；禁止自造不存在的 props。

- [ ] **Step 3: 接线登录逻辑**

账号：

```
validate → fetchCaptchaEnabled
  false → loginByPassword(u,p,null) → setToken → reLaunch /pages/index/index
  true  → captchaRef.open() → uuid → loginByPassword(u,p,uuid) → ...
```

手机：

```
sendSms：校验手机 → sendSmsCode → 60s 倒计时
login：loginByPhone → setToken → reLaunch
```

loading / 防重复；错误 toast。短信接口 404 时提示「短信服务暂不可用」类文案。

- [ ] **Step 4: verify-rice-usage**

```bash
node .cursor/skills/rice-ui/scripts/verify-rice-usage.mjs quick-h5/pages/login
```

Expected: 0 违规。

- [ ] **Step 5: Commit**

```bash
git add quick-h5/pages/login/login.uvue quick-h5/pages.json
git commit -m "feat(quick-h5): add Soft Glass login page"
```

---

### Task 5: 首页守卫 + 主题主色 + 验收说明

**Files:**
- Modify: `quick-h5/pages/index/index.uvue`
- Modify: `quick-h5/store/theme.uts`（可选：设置 primary `#8D51F9` 若 Rice `setTheme`/配置支持；否则在登录页 CSS 变量覆盖并注释）
- Create or update: short note in plan report / comment in `config/env.uts` 关于联调端口与短信后端缺口

**Interfaces:**
- Consumes: `hasToken`、`clearToken`

- [ ] **Step 1: 首页 onShow/onLoad**

```uts
import { hasToken, clearToken } from "@/store/user"

onShow(() => {
	if (!hasToken()) {
		uni.reLaunch({ url: "/pages/login/login" })
	}
})
```

增加「退出登录」按钮：`clearToken()` → `reLaunch` 登录页（替换或放在现有演示按钮旁）。

- [ ] **Step 2: Soft Glass 主色**

若 Rice 支持自定义 primary，在 `initAppTheme` 设为 `#8D51F9`；否则登录页用有限 CSS 变量对齐，不引入第二套 UI 字体。

- [ ] **Step 3: 手工验收清单（写入 PR/提交说明或 `docs/superpowers/specs/...` 验收勾选更新）**

1. H5 captcha 关：账号登录  
2. H5 captcha 开：TAC → 登录  
3. 手机发码/登录（后端若无则记 PENDING）  
4. 无 token 进首页回登录  
5. 暗黑可读  
6. mp-weixin 或 App 编译通过；原生 captcha 真机标 PENDING 若无法测  

- [ ] **Step 4: verify + Commit**

```bash
node .cursor/skills/rice-ui/scripts/verify-rice-usage.mjs quick-h5/pages/login quick-h5/pages/index
git add quick-h5/pages/index/index.uvue quick-h5/store/theme.uts
git commit -m "feat(quick-h5): gate home behind login and tune Soft Glass primary"
```

---

## Self-Review (plan vs spec)

| Spec 项 | Task |
|---------|------|
| 启动登录页 + Soft Glass + Rice | Task 4 |
| 账号 + captcha 链路 | Task 2–4 |
| 手机短信 | Task 2 + 4（后端缺口已标明） |
| Captcha H5 / 原生分流 | Task 3 |
| token + 首页回跳 | Task 1 + 5 |
| 不做扫码/社交/HMAC | Global Constraints |
| verify-rice-usage | Task 4–5 |

无 TBD 占位；短信后端缺失作为显式风险而非静默假设。
