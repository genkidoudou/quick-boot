<template>
  <div class="login-page">
    <div class="login-atmosphere" aria-hidden="true">
      <div class="login-atmosphere__blobs">
        <div class="login-atmosphere__blob login-atmosphere__blob--1"></div>
        <div class="login-atmosphere__blob login-atmosphere__blob--2"></div>
        <div class="login-atmosphere__blob login-atmosphere__blob--3"></div>
        <div class="login-atmosphere__blob login-atmosphere__blob--4"></div>
      </div>
      <div class="login-atmosphere__grid"></div>
      <div class="login-atmosphere__dots"></div>
      <div class="login-atmosphere__scan"></div>
      <div class="login-atmosphere__veil"></div>
      <div class="login-atmosphere__grain"></div>
    </div>

    <main class="login-card" aria-label="登录">
      <header class="login-brand">
        <div class="login-brand__logo" aria-hidden="true">
          <svg viewBox="0 0 56 56" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M28 4L48.785 16V40L28 52L7.215 40V16L28 4Z" fill="url(#loginLogoGrad)" />
            <path d="M28 12L41.856 20V36L28 44L14.144 36V20L28 12Z" fill="#fff" fill-opacity="0.18" />
            <path d="M24 20.5L36 28L24 35.5V20.5Z" fill="#fff" />
            <defs>
              <linearGradient id="loginLogoGrad" x1="10" y1="8" x2="46" y2="48" gradientUnits="userSpaceOnUse">
                <stop stop-color="var(--login-primary-light)" />
                <stop offset="1" stop-color="var(--login-primary)" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="login-brand__name">{{ brandName }}</h1>
        <p class="login-brand__hello">欢迎登录</p>
      </header>

      <div class="login-tabs" role="tablist" aria-label="登录方式">
        <button
          type="button"
          role="tab"
          class="login-tabs__item"
          :class="{ 'is-active': activeTab === 'password' }"
          :aria-selected="activeTab === 'password'"
          @click="activeTab = 'password'"
        >
          账号登录
        </button>
        <button
          type="button"
          role="tab"
          class="login-tabs__item"
          :class="{ 'is-active': activeTab === 'phone' }"
          :aria-selected="activeTab === 'phone'"
          @click="activeTab = 'phone'"
        >
          手机登录
        </button>
        <button
          type="button"
          role="tab"
          class="login-tabs__item"
          :class="{ 'is-active': activeTab === 'qrcode' }"
          :aria-selected="activeTab === 'qrcode'"
          @click="switchToQrcode"
        >
          扫码登录
        </button>
      </div>

      <div class="login-card__body">
        <div v-show="activeTab === 'password'" class="login-pane">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            class="login-form"
            @submit.prevent
          >
            <el-form-item prop="username">
              <el-input
                v-model="passwordForm.username"
                type="text"
                size="large"
                clearable
                placeholder="用户名"
                @keyup.enter="handlePasswordLogin"
              >
                <template #prefix>
                  <svg-icon icon-class="user" class="input-icon" />
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="passwordForm.password"
                type="password"
                size="large"
                clearable
                show-password
                placeholder="密码"
                @keyup.enter="handlePasswordLogin"
              >
                <template #prefix>
                  <svg-icon icon-class="password" class="input-icon" />
                </template>
              </el-input>
            </el-form-item>

            <div class="login-form__meta">
              <el-checkbox v-model="passwordForm.rememberMe">记住密码</el-checkbox>
            </div>

            <el-button
              :loading="passwordLoading"
              size="large"
              type="primary"
              class="login-submit"
              @click="handlePasswordLogin"
            >
              {{ passwordLoading ? '登录中...' : '登录' }}
            </el-button>

            <div v-if="oauthProviders.length" class="oauth-list">
              <div class="oauth-list__divider"><span>第三方登录</span></div>
              <div class="oauth-list__row">
                <el-button
                  v-for="p in oauthProviders"
                  :key="p.providerCode"
                  class="oauth-list__btn"
                  @click="goOauthProvider(p)"
                >
                  {{ p.providerName }}
                </el-button>
              </div>
            </div>
          </el-form>
        </div>

        <div v-show="activeTab === 'phone'" class="login-pane">
          <el-form
            ref="phoneFormRef"
            :model="phoneForm"
            :rules="phoneFormRules"
            class="login-form"
            @submit.prevent
          >
            <el-form-item prop="phone">
              <el-input
                v-model="phoneForm.phone"
                type="text"
                size="large"
                clearable
                placeholder="手机号"
                maxlength="11"
              >
                <template #prefix>
                  <svg-icon icon-class="phone" class="input-icon" />
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="smsCode">
              <el-input
                v-model="phoneForm.smsCode"
                size="large"
                clearable
                placeholder="验证码"
                maxlength="6"
                class="sms-input"
              >
                <template #prefix>
                  <svg-icon icon-class="validCode" class="input-icon" />
                </template>
                <template #suffix>
                  <el-button
                    class="sms-inline-btn"
                    link
                    type="primary"
                    :disabled="smsCountdown > 0 || !phoneForm.phone"
                    @click="sendSms"
                  >
                    {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-button
              :loading="phoneLoading"
              size="large"
              type="primary"
              class="login-submit"
              @click="handlePhoneLogin"
            >
              {{ phoneLoading ? '登录中...' : '登录' }}
            </el-button>
          </el-form>
        </div>

        <div v-show="activeTab === 'qrcode'" class="login-pane login-pane--qrcode">
          <div class="qrcode-box">
            <img v-if="qrcodeUrl" :src="qrcodeUrl" alt="登录二维码" class="qrcode-box__img" />
            <p v-else class="qrcode-box__loading">生成二维码中...</p>
          </div>
          <p class="qrcode-tip">使用手机扫码登录，请确保已安装对应应用</p>
        </div>
      </div>

      <footer class="login-card__footer">
        <span>{{ appConfig.copyright }}</span>
      </footer>
    </main>

    <div v-if="captchaVisible" class="tianai-captcha-mask" @click.self="closeCaptcha">
      <div class="tianai-captcha-wrapper">
        <div id="tianai-captcha-box"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 登录页：账号密码 / 手机验证码 / 扫码（占位）三 Tab；
 * 支持天爱行为验证码、记住密码、OAuth 回调 token、第三方 IdP 跳转。
 * 视觉对齐 login-design-preview.html（浅色科技玻璃）。
 */
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Cookies from 'js-cookie'
import { ElMessage } from 'element-plus'
import '@fontsource/manrope/400.css'
import '@fontsource/manrope/500.css'
import '@fontsource/manrope/600.css'
import '@fontsource/outfit/500.css'
import '@fontsource/outfit/600.css'
import '@fontsource/outfit/700.css'
import { getLoginCaptchaConfig, listOauthProviders, phoneLogin, sendSms as sendSmsApi } from '@/api/login'
import { getCaptchaTacUrls } from '@/api/captcha'
import { getToken, setToken } from '@/utils/auth'
import { phoneRules, requiredRule } from '@/utils/validate'
import useUserStore from '@/store/modules/user'
import { appConfig } from '@/config/env'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

/** 品牌展示：优先取标题中的拉丁文并大写，否则用完整标题 */
const brandName = computed(() => {
  const raw = String(appConfig.title || 'Quick Boot').trim()
  const latin = raw.match(/[A-Za-z][A-Za-z0-9\s._-]*/)?.[0]?.trim()
  return (latin || raw).toUpperCase()
})

const passwordFormRef = ref(null)
const phoneFormRef = ref(null)

const activeTab = ref('password')

const passwordForm = ref({
  username: 'admin',
  password: 'admin123',
  rememberMe: false,
  uuid: '',
})

/** 登录只校验必填，不强校验密码复杂度（兼容历史账号） */
const passwordRules = {
  username: [requiredRule('请输入账号')],
  password: [requiredRule('请输入密码')],
}

const passwordLoading = ref(false)
const oauthProviders = ref([])
const captchaVisible = ref(false)
/** 新认证栈：以 /api/captcha/config（qc.captcha.enabled）为准 */
const loginCaptchaEnabled = ref(false)
let tacInstance = null

onMounted(() => {
  const tokenFromQuery = route.query.access_token
  if (tokenFromQuery && !getToken()) {
    setToken(tokenFromQuery)
    router.push({ path: route.query.redirect || '/' })
    return
  }
  listOauthProviders()
    .then((body) => {
      oauthProviders.value = body?.data || []
    })
    .catch(() => {
      oauthProviders.value = []
    })
  getLoginCaptchaConfig()
    .then((body) => {
      loginCaptchaEnabled.value = body?.data?.captchaEnabled === true
    })
    .catch(() => {
      loginCaptchaEnabled.value = false
    })
})

/** OAuth 授权：跳转到后端 IdP 授权入口 */
function goOauthProvider(p) {
  const base = import.meta.env.VITE_APP_BASE_API || ''
  const prefix = base.endsWith('/') ? base.slice(0, -1) : base
  const path = p.authorizePath || `/oauth2/authorization/${p.providerCode}`
  window.location.href = `${prefix.startsWith('http') ? prefix : window.location.origin + (prefix.startsWith('/') ? prefix : '/' + prefix)}${path}`
}

/** 关闭天爱验证码弹层并销毁实例 */
function closeCaptcha() {
  captchaVisible.value = false
  if (tacInstance) {
    tacInstance.destroyWindow()
    tacInstance = null
  }
}

/** 打开天爱行为验证码，校验成功后写入 uuid 并执行登录 */
function openCaptcha() {
  closeCaptcha()
  captchaVisible.value = true
  nextTick(() => {
    const { generateUrl, validateUrl } = getCaptchaTacUrls()
    const config = {
      requestCaptchaDataUrl: generateUrl,
      validCaptchaUrl: validateUrl,
      bindEl: '#tianai-captcha-box',
      validSuccess: (res, _c, tac) => {
        tac.destroyWindow()
        tacInstance = null
        captchaVisible.value = false
        const id = res?.data?.id ?? res?.id
        passwordForm.value.uuid = id || ''
        doLogin()
      },
      validFail: (_res, _c, tac) => {
        tac.reloadCaptcha()
      },
      btnRefreshFun: (_el, tac) => {
        tac.reloadCaptcha()
      },
      btnCloseFun: (_el, tac) => {
        tac.destroyWindow()
        closeCaptcha()
      },
    }
    const style = { logoUrl: null }
    if (typeof window.initTAC !== 'function') {
      ElMessage.error('验证码脚本未加载，请刷新页面重试')
      captchaVisible.value = false
      return
    }
    window
      .initTAC('/tac', config, style)
      .then((tac) => {
        tacInstance = tac
        tac.init()
      })
      .catch((e) => {
        console.error('initTAC failed', e)
        ElMessage.error('验证码初始化失败')
        captchaVisible.value = false
      })
  })
}

const phoneForm = ref({
  phone: '',
  smsCode: '',
})

const phoneFormRules = {
  phone: [requiredRule('请输入手机号'), ...phoneRules],
  smsCode: [requiredRule('请输入验证码')],
}

const phoneLoading = ref(false)
const smsCountdown = ref(0)
const qrcodeUrl = ref('')

function handlePasswordLogin() {
  passwordFormRef.value?.validate((valid) => {
    if (!valid) return
    if (!loginCaptchaEnabled.value) {
      passwordForm.value.uuid = ''
      doLogin()
      return
    }
    openCaptcha()
  })
}

function doLogin() {
  passwordLoading.value = true
  if (passwordForm.value.rememberMe) {
    Cookies.set('username', passwordForm.value.username, { expires: 30 })
    Cookies.set('password', passwordForm.value.password, { expires: 30 })
    Cookies.set('rememberMe', passwordForm.value.rememberMe, { expires: 30 })
  } else {
    Cookies.remove('username')
    Cookies.remove('password')
    Cookies.remove('rememberMe')
  }
  userStore
    .login(passwordForm.value)
    .then(() => {
      passwordLoading.value = false
      const query = route.query
      const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
      router.push({ path: route.query.redirect || '/', query: otherQueryParams })
    })
    .catch(() => {
      passwordLoading.value = false
      passwordForm.value.uuid = ''
    })
}

function sendSms() {
  phoneFormRef.value?.validateField('phone', (valid) => {
    if (!valid) {
      ElMessage.warning('请输入正确的手机号')
      return
    }

    sendSmsApi(phoneForm.value.phone)
      .then(() => {
        ElMessage.success('验证码已发送到您的手机')
        smsCountdown.value = 60

        const timer = setInterval(() => {
          smsCountdown.value--
          if (smsCountdown.value <= 0) {
            clearInterval(timer)
          }
        }, 1000)
      })
      .catch(() => {
        ElMessage.error('发送验证码失败，请重试')
      })
  })
}

function handlePhoneLogin() {
  phoneFormRef.value?.validate((valid) => {
    if (!valid) return
    phoneLoading.value = true

    phoneLogin(phoneForm.value.phone, phoneForm.value.smsCode)
      .then(() => {
        ElMessage.success('登录成功')
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== 'redirect') {
            acc[cur] = query[cur]
          }
          return acc
        }, {})
        router.push({ path: route.query.redirect || '/', query: otherQueryParams })
      })
      .catch(() => {
        phoneLoading.value = false
      })
  })
}

function switchToQrcode() {
  activeTab.value = 'qrcode'
  if (!qrcodeUrl.value) {
    qrcodeUrl.value =
      'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0id2hpdGUiLz48cmVjdCB4PSIxMCIgeT0iMTAiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgZmlsbD0iYmxhY2siLz48cmVjdCB4PSIxNTAiIHk9IjEwIiB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIGZpbGw9ImJsYWNrIi8+PHJlY3QgeD0iMTAiIHk9IjE1MCIgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSJibGFjayIvPjwvc3ZnPg=='
  }
}

function getCookie() {
  const username = Cookies.get('username')
  const password = Cookies.get('password')
  const rememberMe = Cookies.get('rememberMe')

  if (username) {
    passwordForm.value.username = username
    passwordForm.value.password = password || ''
    passwordForm.value.rememberMe = Boolean(rememberMe)
  }
}

getCookie()
</script>

<style lang="scss" scoped>
.login-page {
  --login-primary: var(--el-color-primary, #409eff);
  --login-primary-light: var(--el-color-primary-light-3, #79bbff);
  --login-primary-deep: var(--el-color-primary-dark-2, #337ecc);
  --login-primary-soft: color-mix(in srgb, var(--login-primary) 14%, transparent);
  --login-ink: #1e2f45;
  --login-muted: #7b8ea3;
  --login-line: #d5e1ee;
  --login-card: rgba(255, 255, 255, 0.72);
  --login-input-bg: rgba(255, 255, 255, 0.92);
  --login-font-display: 'Outfit', 'Manrope', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  --login-font-body: 'Manrope', 'PingFang SC', 'Microsoft YaHei', sans-serif;

  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px 64px;
  background: #e8f1fa;
  color: var(--login-ink);
  font-family: var(--login-font-body);
  overflow: hidden;
}

.login-atmosphere {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
  background:
    radial-gradient(ellipse 70% 50% at 50% 0%, rgba(160, 210, 255, 0.45), transparent 60%),
    linear-gradient(155deg, #f7fbff 0%, #eaf3fc 42%, #dfeaf7 78%, #d4e4f4 100%);
}

.login-atmosphere__blobs {
  position: absolute;
  inset: -18%;
  filter: blur(80px);
  transform: scale(1.08);
  opacity: 0.9;
}

.login-atmosphere__blob {
  position: absolute;
  border-radius: 50%;
  animation: loginDrift 20s ease-in-out infinite alternate;
}

.login-atmosphere__blob--1 {
  width: 56vmax;
  height: 46vmax;
  top: -20%;
  left: -14%;
  background: radial-gradient(circle, rgba(96, 180, 255, 0.55) 0%, rgba(96, 180, 255, 0) 70%);
}

.login-atmosphere__blob--2 {
  width: 48vmax;
  height: 42vmax;
  top: 8%;
  right: -16%;
  background: radial-gradient(circle, rgba(56, 210, 230, 0.38) 0%, rgba(56, 210, 230, 0) 72%);
  animation-delay: -6s;
  animation-duration: 24s;
}

.login-atmosphere__blob--3 {
  width: 54vmax;
  height: 48vmax;
  bottom: -24%;
  left: 18%;
  background: radial-gradient(circle, rgba(130, 170, 255, 0.32) 0%, rgba(130, 170, 255, 0) 70%);
  animation-delay: -12s;
  animation-duration: 28s;
}

.login-atmosphere__blob--4 {
  width: 36vmax;
  height: 36vmax;
  bottom: 8%;
  right: 12%;
  background: radial-gradient(circle, rgba(180, 220, 255, 0.5) 0%, rgba(180, 220, 255, 0) 70%);
  animation-delay: -3s;
  animation-duration: 22s;
}

.login-atmosphere__grid {
  position: absolute;
  inset: -10% -20% 0;
  background-image:
    linear-gradient(rgba(64, 140, 210, 0.14) 1px, transparent 1px),
    linear-gradient(90deg, rgba(64, 140, 210, 0.14) 1px, transparent 1px);
  background-size: 56px 56px;
  transform: perspective(700px) rotateX(58deg) translateY(-8%);
  transform-origin: center top;
  mask-image: linear-gradient(to bottom, transparent 0%, #000 28%, #000 70%, transparent 100%);
  opacity: 0.42;
  animation: loginGridPulse 8s ease-in-out infinite alternate;
}

.login-atmosphere__dots {
  position: absolute;
  inset: 0;
  opacity: 0.28;
  background-image: radial-gradient(rgba(80, 150, 220, 0.45) 1px, transparent 1px);
  background-size: 22px 22px;
  mask-image: radial-gradient(ellipse 70% 55% at 50% 45%, #000 10%, transparent 75%);
}

.login-atmosphere__scan {
  position: absolute;
  left: -20%;
  width: 140%;
  height: 16%;
  top: -20%;
  background: linear-gradient(
    to bottom,
    transparent,
    rgba(100, 190, 255, 0.1),
    color-mix(in srgb, var(--login-primary) 18%, transparent),
    rgba(100, 190, 255, 0.1),
    transparent
  );
  animation: loginScanSweep 7.5s linear infinite;
  mix-blend-mode: multiply;
}

.login-atmosphere__veil {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    155deg,
    rgba(255, 255, 255, 0.55) 0%,
    rgba(255, 255, 255, 0.22) 45%,
    rgba(230, 240, 250, 0.4) 100%
  );
  backdrop-filter: blur(36px) saturate(1.12);
  -webkit-backdrop-filter: blur(36px) saturate(1.12);
}

.login-atmosphere__grain {
  position: absolute;
  inset: 0;
  opacity: 0.035;
  mix-blend-mode: soft-light;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3' stitchTiles='stitch'/%3E%3CfeColorMatrix type='saturate' values='0'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
  background-size: 140px 140px;
}

@keyframes loginDrift {
  from {
    transform: translate(0, 0) scale(1);
  }
  to {
    transform: translate(2.5%, 3.5%) scale(1.05);
  }
}

@keyframes loginGridPulse {
  from {
    opacity: 0.3;
  }
  to {
    opacity: 0.5;
  }
}

@keyframes loginScanSweep {
  0% {
    transform: translateY(0);
    opacity: 0;
  }
  8% {
    opacity: 1;
  }
  92% {
    opacity: 1;
  }
  100% {
    transform: translateY(620%);
    opacity: 0;
  }
}

.login-card {
  position: relative;
  z-index: 1;
  width: min(380px, 100%);
  padding: 32px 28px 18px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  background: var(--login-card);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, 0.55) inset,
    0 0 32px color-mix(in srgb, var(--login-primary) 12%, transparent),
    0 18px 48px rgba(70, 110, 160, 0.14);
  backdrop-filter: blur(24px) saturate(1.15);
  -webkit-backdrop-filter: blur(24px) saturate(1.15);
  animation: loginCardIn 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.login-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--login-primary) 70%, #fff),
    rgba(255, 255, 255, 0.55) 38%,
    color-mix(in srgb, var(--login-primary) 45%, transparent) 68%,
    rgba(34, 200, 220, 0.4)
  );
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.login-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 12%;
  right: 12%;
  height: 1px;
  background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--login-primary) 70%, #fff), transparent);
  pointer-events: none;
}

@keyframes loginCardIn {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-brand {
  text-align: center;
  margin-bottom: 4px;
  flex-shrink: 0;
}

.login-brand__logo {
  width: 52px;
  height: 52px;
  margin: 0 auto 10px;
  display: grid;
  place-items: center;
  animation: loginLogoSpin 12s linear infinite;
  will-change: transform;

  svg {
    width: 52px;
    height: 52px;
    display: block;
    filter: drop-shadow(0 0 10px color-mix(in srgb, var(--login-primary) 35%, transparent))
      drop-shadow(0 6px 14px rgba(56, 180, 220, 0.2));
  }
}

@keyframes loginLogoSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.login-brand__name {
  margin: 0;
  font-family: var(--login-font-display);
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--login-primary-deep);
  line-height: 1.2;
}

.login-brand__hello {
  margin: 6px 0 0;
  font-size: 0.875rem;
  color: var(--login-muted);
  font-weight: 500;
}

.login-tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
  margin: 16px 0 0;
  border-bottom: 1px solid var(--login-line);
  flex-shrink: 0;
}

.login-tabs__item {
  appearance: none;
  border: 0;
  background: transparent;
  margin: 0;
  padding: 10px 4px 12px;
  font-family: inherit;
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--login-muted);
  cursor: pointer;
  position: relative;
  transition: color 0.2s ease;

  &:hover {
    color: var(--login-ink);
  }

  &.is-active {
    color: var(--login-primary);
    font-weight: 600;
  }

  &.is-active::after {
    content: '';
    position: absolute;
    left: 18%;
    right: 18%;
    bottom: -1px;
    height: 2px;
    border-radius: 1px;
    background: var(--login-primary);
  }
}

.login-card__body {
  padding-top: 16px;
  min-width: 0;
}

.login-pane {
  min-width: 0;
  animation: loginPaneIn 0.28s ease both;
}

.login-pane--qrcode {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 8px 0 4px;
}

@keyframes loginPaneIn {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-form {
  min-width: 0;

  .el-form-item {
    margin-bottom: 12px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    box-shadow: 0 0 0 1px var(--login-line) inset;
    background: var(--login-input-bg);
    padding-left: 12px;
    transition: box-shadow 0.2s ease, background 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px #c5d0dc inset;
    }

    &.is-focus {
      background: #fff;
      box-shadow:
        0 0 0 1px var(--login-primary) inset,
        0 0 0 3px var(--login-primary-soft);
    }
  }

  :deep(.el-input__inner) {
    height: 42px;
    font-size: 0.95rem;
  }

  .input-icon {
    width: 16px;
    height: 16px;
    color: var(--login-muted);
  }
}

.login-form__meta {
  margin: 0 0 14px;
  display: flex;
  align-items: center;
  min-height: 22px;

  :deep(.el-checkbox__label) {
    font-size: 0.875rem;
    color: var(--login-muted);
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: var(--login-primary);
    border-color: var(--login-primary);
  }
}

.login-submit {
  width: 100%;
  height: 42px;
  border: 0 !important;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 0.04em;
  background: linear-gradient(
    135deg,
    var(--login-primary-light) 0%,
    var(--login-primary) 45%,
    var(--login-primary-deep) 100%
  ) !important;
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, 0.18) inset,
    0 8px 22px color-mix(in srgb, var(--login-primary) 42%, transparent);
  transition: transform 0.15s ease, box-shadow 0.2s ease, filter 0.2s ease;

  &:hover,
  &:focus {
    filter: brightness(1.04);
    box-shadow:
      0 0 0 1px rgba(255, 255, 255, 0.22) inset,
      0 10px 28px color-mix(in srgb, var(--login-primary) 50%, transparent);
  }

  &:active {
    transform: translateY(1px);
  }
}

.oauth-list {
  margin-top: 14px;
}

.oauth-list__divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
  color: var(--login-muted);
  font-size: 0.75rem;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: var(--login-line);
  }
}

.oauth-list__row {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  gap: 8px;
  width: 100%;
  min-width: 0;
}

.oauth-list__btn {
  flex: 1 1 0;
  min-width: 0;
  width: auto !important;
  height: 36px;
  margin: 0 !important;
  border-radius: 10px;
  border-color: var(--login-line);
  color: var(--login-ink);
  background: #fff;
  font-size: 0.8125rem;
  font-weight: 500;

  &:hover {
    border-color: #c9d0d8;
    background: var(--login-primary-soft);
    color: var(--login-primary);
  }
}

.sms-input {
  width: 100%;

  :deep(.el-input__wrapper) {
    padding-right: 6px;
  }

  :deep(.el-input__suffix) {
    margin-left: 4px;
  }
}

.sms-inline-btn {
  height: 30px !important;
  margin: 0 !important;
  padding: 0 10px !important;
  border-radius: 8px !important;
  background: var(--login-primary-soft) !important;
  color: var(--login-primary) !important;
  font-size: 0.75rem !important;
  font-weight: 600 !important;
  white-space: nowrap;

  &:hover:not(:disabled) {
    background: color-mix(in srgb, var(--login-primary) 20%, transparent) !important;
  }

  &:disabled {
    opacity: 0.55;
  }
}

.qrcode-box {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--login-line);
  border-radius: 12px;
  background: #fff;
  overflow: hidden;
}

.qrcode-box__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.qrcode-box__loading,
.qrcode-tip {
  margin: 0;
  color: var(--login-muted);
  font-size: 0.8125rem;
  text-align: center;
  line-height: 1.5;
}

.login-card__footer {
  margin-top: 16px;
  text-align: center;
  font-size: 0.72rem;
  color: #9aabbc;
  line-height: 1.5;
  flex-shrink: 0;
}

.tianai-captcha-mask {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(30, 47, 69, 0.42);
  backdrop-filter: blur(2px);
}

.tianai-captcha-wrapper {
  background: #fff;
  border: 1px solid var(--login-line);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 16px 40px rgba(70, 110, 160, 0.16);
  min-width: 320px;
}

@media (prefers-reduced-motion: reduce) {
  .login-brand__logo,
  .login-atmosphere__blob,
  .login-atmosphere__grid,
  .login-atmosphere__scan {
    animation: none;
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: 32px 16px 56px;
  }

  .login-card {
    padding: 28px 18px 16px;
  }

  .login-brand__logo {
    width: 48px;
    height: 48px;
    margin-bottom: 8px;

    svg {
      width: 48px;
      height: 48px;
    }
  }

  .login-tabs__item {
    font-size: 0.8125rem;
    padding: 10px 2px 12px;
  }

  .sms-inline-btn {
    padding: 0 8px !important;
    font-size: 0.6875rem !important;
  }
}

@media (max-height: 560px) and (orientation: landscape) {
  .login-page {
    justify-content: flex-start;
    padding-top: 24px;
  }
}
</style>
