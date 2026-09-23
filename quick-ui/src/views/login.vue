<template>
  <div class="login-page">
    <div class="login-atmosphere" aria-hidden="true">
      <div class="login-atmosphere__wash"></div>
      <div class="login-atmosphere__grid"></div>
    </div>

    <main class="login-stage">
      <header class="login-brand">
        <h1 class="login-brand__name">Quick UI</h1>
        <p class="login-brand__tagline">简洁、可靠的管理后台入口</p>
      </header>

      <section class="login-panel" aria-label="登录">
        <div class="login-tabs" role="tablist">
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

        <div class="login-panel__body">
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
                <el-button
                  v-for="p in oauthProviders"
                  :key="p.providerCode"
                  class="oauth-list__btn"
                  @click="goOauthProvider(p)"
                >
                  {{ p.providerName }}
                </el-button>
              </div>
            </el-form>
          </div>

          <div v-show="activeTab === 'phone'" class="login-pane">
            <el-form
              ref="phoneFormRef"
              :model="phoneForm"
              :rules="phoneRules"
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
                <div class="sms-row">
                  <el-input
                    v-model="phoneForm.smsCode"
                    size="large"
                    clearable
                    placeholder="验证码"
                    maxlength="6"
                    class="sms-row__input"
                  >
                    <template #prefix>
                      <svg-icon icon-class="validCode" class="input-icon" />
                    </template>
                  </el-input>
                  <el-button
                    class="sms-row__btn"
                    size="large"
                    :disabled="smsCountdown > 0 || !phoneForm.phone"
                    @click="sendSms"
                  >
                    {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
                  </el-button>
                </div>
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
      </section>
    </main>

    <footer class="login-footer">
      <span>{{ appConfig.copyright }}</span>
    </footer>

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
 */
import { ref, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Cookies from 'js-cookie'
import { ElMessage } from 'element-plus'
import { getLoginCaptchaConfig, listOauthProviders, phoneLogin, sendSms as sendSmsApi } from '@/api/login'
import { getCaptchaTacUrls } from '@/api/captcha'
import { getToken, setToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'
import { appConfig } from '@/config/env'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const passwordFormRef = ref(null)
const phoneFormRef = ref(null)

const activeTab = ref('password')

const passwordForm = ref({
  username: 'admin',
  password: 'admin123',
  rememberMe: false,
  uuid: '',
})

const passwordRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
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

const phoneRules = {
  phone: [
    { required: true, trigger: 'blur', message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, trigger: 'blur', message: '请输入正确的手机号' },
  ],
  smsCode: [{ required: true, trigger: 'blur', message: '请输入验证码' }],
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
  --login-ink: #1c2430;
  --login-muted: #6b7380;
  --login-line: #e4e8ee;
  --login-surface: rgba(255, 255, 255, 0.86);
  --login-accent: #1a6b4a;
  --login-accent-soft: #e8f4ee;
  --login-bg: #f3f5f7;
  --login-radius: 12px;
  --login-font-display: 'Outfit', 'Manrope', sans-serif;
  --login-font-body: 'Manrope', 'PingFang SC', 'Microsoft YaHei', sans-serif;

  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px 64px;
  background: var(--login-bg);
  color: var(--login-ink);
  font-family: var(--login-font-body);
  overflow: hidden;
}

.login-atmosphere {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.login-atmosphere__wash {
  position: absolute;
  inset: -20%;
  background:
    radial-gradient(ellipse 55% 45% at 18% 22%, rgba(26, 107, 74, 0.09), transparent 60%),
    radial-gradient(ellipse 50% 40% at 82% 78%, rgba(90, 120, 150, 0.08), transparent 55%),
    radial-gradient(ellipse 40% 30% at 70% 18%, rgba(255, 255, 255, 0.7), transparent 70%);
  animation: washFloat 18s ease-in-out infinite alternate;
}

.login-atmosphere__grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(28, 36, 48, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(28, 36, 48, 0.035) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(ellipse 70% 60% at 50% 45%, #000 20%, transparent 75%);
}

@keyframes washFloat {
  from {
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    transform: translate3d(1.5%, -1%, 0) scale(1.03);
  }
}

.login-stage {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 28px;
  animation: stageIn 0.55s ease both;
}

@keyframes stageIn {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-brand {
  text-align: center;
}

.login-brand__name {
  margin: 0;
  font-family: var(--login-font-display);
  font-size: clamp(2.4rem, 6vw, 3rem);
  font-weight: 650;
  letter-spacing: -0.04em;
  line-height: 1.1;
  color: var(--login-ink);
}

.login-brand__tagline {
  margin: 10px 0 0;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--login-muted);
  letter-spacing: 0.01em;
}

.login-panel {
  background: var(--login-surface);
  border: 1px solid var(--login-line);
  border-radius: var(--login-radius);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.8) inset, 0 18px 40px rgba(28, 36, 48, 0.05);
  overflow: hidden;
}

.login-tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  border-bottom: 1px solid var(--login-line);
  background: rgba(255, 255, 255, 0.45);
}

.login-tabs__item {
  appearance: none;
  border: 0;
  background: transparent;
  margin: 0;
  padding: 14px 8px;
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--login-muted);
  cursor: pointer;
  position: relative;
  transition: color 0.2s ease;

  &:hover {
    color: var(--login-ink);
  }

  &.is-active {
    color: var(--login-accent);
    font-weight: 600;
  }

  &.is-active::after {
    content: '';
    position: absolute;
    left: 18%;
    right: 18%;
    bottom: 0;
    height: 2px;
    background: var(--login-accent);
    border-radius: 1px;
  }
}

.login-panel__body {
  padding: 24px 24px 28px;
}

.login-pane {
  animation: paneIn 0.28s ease both;
}

.login-pane--qrcode {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 8px 0 4px;
}

@keyframes paneIn {
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
  .el-form-item {
    margin-bottom: 16px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px var(--login-line) inset;
    background: #fff;
    padding-left: 12px;
    transition: box-shadow 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px #c9d0d8 inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px var(--login-accent) inset;
    }
  }

  :deep(.el-input__inner) {
    height: 44px;
    font-size: 0.95rem;
  }

  .input-icon {
    width: 16px;
    height: 16px;
    color: var(--login-muted);
  }
}

.login-form__meta {
  margin: -4px 0 18px;
  display: flex;
  align-items: center;
  min-height: 24px;

  :deep(.el-checkbox__label) {
    font-size: 0.875rem;
    color: var(--login-muted);
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: var(--login-accent);
    border-color: var(--login-accent);
  }
}

.login-submit {
  width: 100%;
  height: 44px;
  border: 0;
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 0.04em;
  background: var(--login-accent) !important;
  transition: transform 0.18s ease, background 0.18s ease, box-shadow 0.18s ease;

  &:hover,
  &:focus {
    background: #155a3e !important;
    box-shadow: 0 8px 20px rgba(26, 107, 74, 0.18);
  }

  &:active {
    transform: translateY(1px);
  }
}

.oauth-list {
  margin-top: 22px;
}

.oauth-list__divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
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

.oauth-list__btn {
  width: 100%;
  margin-left: 0 !important;
  margin-top: 8px;
  border-radius: 8px;
  border-color: var(--login-line);
  color: var(--login-ink);
  background: #fff;

  &:hover {
    border-color: #c9d0d8;
    background: var(--login-accent-soft);
    color: var(--login-accent);
  }
}

.sms-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.sms-row__input {
  flex: 1;
  min-width: 0;
}

.sms-row__btn {
  flex-shrink: 0;
  min-width: 112px;
  border-radius: 8px;
  border-color: var(--login-line);
  color: var(--login-accent);
  background: var(--login-accent-soft);

  &:hover:not(:disabled) {
    border-color: var(--login-accent);
    color: #155a3e;
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
  border-radius: 10px;
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

.login-footer {
  position: absolute;
  z-index: 1;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 14px 16px calc(14px + env(safe-area-inset-bottom));
  text-align: center;
  font-size: 0.75rem;
  color: var(--login-muted);
}

.tianai-captcha-mask {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(28, 36, 48, 0.42);
  backdrop-filter: blur(2px);
}

.tianai-captcha-wrapper {
  background: #fff;
  border: 1px solid var(--login-line);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 16px 40px rgba(28, 36, 48, 0.16);
  min-width: 320px;
}

@media (max-width: 480px) {
  .login-page {
    padding: 32px 16px 56px;
  }

  .login-stage {
    gap: 22px;
  }

  .login-panel__body {
    padding: 20px 18px 24px;
  }

  .login-tabs__item {
    padding: 12px 4px;
    font-size: 0.8125rem;
  }

  .sms-row {
    flex-direction: column;
  }

  .sms-row__btn {
    width: 100%;
    min-width: 0;
  }
}

@media (max-height: 560px) and (orientation: landscape) {
  .login-page {
    justify-content: flex-start;
    padding-top: 24px;
  }

  .login-footer {
    position: relative;
    margin-top: 24px;
  }
}
</style>
