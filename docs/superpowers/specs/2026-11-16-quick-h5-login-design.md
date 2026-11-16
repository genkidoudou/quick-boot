# quick-h5 登录页设计

日期：2026-11-16  
范围：`quick-h5`（uni-app x + RiceUI + vapor）新增登录页与轻量鉴权  
状态：待实现计划

## 背景

`quick-h5` 当前仅为 RiceUI 演示壳（`pages/index`），无登录、无 HTTP、无 token。管理端 `quick-ui` 已具备账号/手机登录与天爱行为验证码。本需求在 H5 侧补齐同等登录能力，视觉靠拢 Soft Glass，控件使用 RiceUI，并要求验证码在微信小程序与 App 等端可用。

## 决策摘要

| 项 | 选择 |
|----|------|
| 登录方式 | 账号密码 + 手机短信；扫码不做 |
| 后端 | 对齐 quick-boot：`/login`、`/phoneLogin`、`/sendSms`、`/api/captcha/*` |
| 行为验证码 | 与管理端同链路（读 config → generate/validate → uuid 再登录） |
| Captcha 多端 | H5：官方 TAC Web SDK；小程序/App：原生滑块 UI，同一套后端接口 |
| 视觉 | Soft Glass 气质 + RiceUI 组件 |
| 落地路径 | 登录页 + 轻量鉴权层（非完整中台） |
| UI 库 | 仅 `rice-*`，禁止混用 uview 等 |

## 目标与成功标准

- 启动进入登录页；账号（含 captcha 开/关）与手机登录可拿到 token 并进入首页
- Soft Glass 可读（含暗黑）；品牌区清晰
- Captcha：H5 用官方 TAC；非 H5 用原生 UI；统一 `openCaptcha(): Promise<uuid>`
- 首页无 token 时回登录（轻量判断）

## 范围

### 做

- `pages/login/login.uvue` 为启动页；登录成功 `reLaunch` 到 `pages/index/index`
- API：`http` 封装、`login`、`captcha`；`store/user` 持久化 token
- Captcha 双实现 + host 收口（条件编译）
- Soft Glass 氛围 + Rice 表单控件；主题 primary 尽量对齐 `#8D51F9`

### 不做

- 扫码登录、社交 OAuth、强制改密、完整路由/权限守卫
- 请求 HMAC / 加解密白名单（除非联调发现后端强制）
- 新建独立设计体系或引入非 Rice UI

## 页面与交互

### 结构

- 隐藏原生导航栏（或等价），品牌区：Logo 位 + 标题 +「欢迎登录」
- 氛围底 + 大圆角登录卡
- Tab：账号登录 | 手机登录（`rice-subsection` 或 `rice-tabs`，实现时选蒸汽模式更稳的一个）

### 账号登录

1. 校验用户名/密码  
2. `GET /api/captcha/config`：未启用 → 直接登录  
3. 已启用 → `openCaptcha()` → uuid → `POST /login`（params：`username`、`password`、`grantType=password`、可选 `uuid`）  
4. 存 token → 进首页  

### 手机登录

1. 校验手机号 → `POST /sendSms` `{ phone }`，60s 倒计时  
2. `POST /phoneLogin` `{ phone, smsCode }` → 存 token → 进首页  

### 交互细节

- 密码显隐；登录按钮 loading、防重复提交  
- Captcha 蒙层居中；失败可刷新；关闭不登录  
- 错误用 toast / 已有 dialog  

## Captcha 多端策略

后端接口与 quick-ui 一致：

- `GET /api/captcha/config` → `{ captchaEnabled, type? }`
- generate / validate 绝对 URL（由 env base 拼接）

| 端 | 实现 |
|----|------|
| H5 | 官方 TAC Web SDK（`initTAC` 一类，对齐管理端 generate/validate） |
| 微信小程序 / App 等 | 原生可交互 UI（滑块等），请求同一 generate/validate；**不**用 web-view 硬嵌 Web TAC |

统一组件：`components/captcha/captcha-host.uvue` 暴露 `openCaptcha(): Promise<string>`。  
内部 `#ifdef H5` → `captcha-h5`；`#ifndef H5` → `captcha-native`。  
原生实现可参考社区天爱小程序封装，但收口进本仓库并对接现有后端路径（`/api/captcha/generate`、`/api/captcha/validate` 或项目实际路径）。

## API、Token 与目录

```
quick-h5/
  pages/login/login.uvue
  api/http.uts
  api/login.uts
  api/captcha.uts
  store/user.uts
  components/captcha/captcha-host.uvue
  components/captcha/captcha-h5.uvue
  components/captcha/captcha-native.uvue
  config/env.uts
```

- `Authorization: Bearer <token>`；登录类请求不带 token  
- Token：`uni.setStorageSync`；启动时读入 `store/user`  
- Token 字段名实现时对照后端实际登录响应（与 quick-ui 一致优先）  
- 可选：登录后拉 `/auth/me`（可二期）  
- 小程序配置合法 request 域名；H5 开发 env 说明直连或代理  

## 视觉与组件

- 背景：`--rice-background` + 克制主色光斑；暗黑可读  
- 卡片：大圆角、细边框、产品壳气质  
- 主色：尽量 `#8D51F9`（扩展现有 `store/theme` 或页面/主题变量）  
- 组件：`rice-input`、`rice-button`；短信可用 `rice-input`+按钮或 `rice-code-input`  
- 写前查 `.cursor/skills/rice-ui/references/api/`；写后跑 `verify-rice-usage.mjs`  

## 验收

- [ ] H5：captcha 关 → 账号登录成功进首页（待联调）  
- [ ] H5：captcha 开 → TAC 成功后带 uuid 登录（待联调）  
- [ ] H5：手机发码 + 登录 — **PENDING**：后端未见 `/sendSms`、`/phoneLogin`  
- [ ] 暗黑模式登录页可读（待目视）  
- [ ] 无 token 打开首页会回登录（代码已接 `hasToken` 守卫）  
- [ ] 非 H5 条件编译可通过；原生 captcha → login — **PENDING** 真机（骨架已落地）  
- [x] `verify-rice-usage.mjs` 对登录相关页面 0 违规  

## 明确排除

- 扫码 / 社交登录 / 完整鉴权中台  
- 列表 App 化或改 quick-ui DESIGN  
- 在小程序用 web-view 加载 Web TAC 作为唯一方案  
