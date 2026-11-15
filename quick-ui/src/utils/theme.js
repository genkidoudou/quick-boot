/**
 * 主题色与外观：写入 Element 色阶、--qb-* 令牌，以及明暗 / 玻璃材质。
 */
import { getThemePack } from '@/theme/packages'
import { applyWallpaper, normalizeWallpaperUrl } from '@/utils/wallpaper'

const STATUS_LIGHT = {
  success: '#248A3D',
  warning: '#D97706',
  info: '#007AFF',
  danger: '#DC2626'
}

const STATUS_DARK = {
  success: '#69C59A',
  warning: '#E4B863',
  info: '#7C9CC4',
  danger: '#FF7B7B'
}

/**
 * @param {string} hex
 * @returns {number[]}
 */
function hexToRgb(hex) {
  const h = String(hex || '').replace('#', '')
  if (h.length !== 6) {
    return [141, 81, 249]
  }
  return [parseInt(h.slice(0, 2), 16), parseInt(h.slice(2, 4), 16), parseInt(h.slice(4, 6), 16)]
}

/**
 * @param {string} hex
 * @param {number} level
 */
function getLightColor(hex, level) {
  const [r, g, b] = hexToRgb(hex)
  const nr = Math.round(r + (255 - r) * level)
  const ng = Math.round(g + (255 - g) * level)
  const nb = Math.round(b + (255 - b) * level)
  return `#${[nr, ng, nb].map((n) => n.toString(16).padStart(2, '0')).join('')}`
}

/**
 * @param {string} hex
 * @param {number} level
 */
function getDarkColor(hex, level) {
  const [r, g, b] = hexToRgb(hex)
  const nr = Math.round(r * (1 - level))
  const ng = Math.round(g * (1 - level))
  const nb = Math.round(b * (1 - level))
  return `#${[nr, ng, nb].map((n) => n.toString(16).padStart(2, '0')).join('')}`
}

/**
 * @param {unknown} value
 * @returns {'light'|'dark'|'auto'}
 */
export function normalizeColorScheme(value) {
  if (value === 'dark' || value === 'light' || value === 'auto') {
    return value
  }
  return 'light'
}

/**
 * @param {'light'|'dark'|'auto'} preference
 * @returns {'light'|'dark'}
 */
export function resolveColorScheme(preference = 'light') {
  const normalized = normalizeColorScheme(preference)
  if (normalized !== 'auto') {
    return normalized
  }
  if (typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }
  return 'light'
}

/**
 * 应用页面明暗配色到 html。
 * @param {'light'|'dark'|'auto'} preference
 * @returns {'light'|'dark'}
 */
export function applyColorScheme(preference = 'light') {
  const resolved = resolveColorScheme(preference)
  const el = document.documentElement
  el.setAttribute('data-color-scheme', resolved)
  el.style.colorScheme = resolved
  if (resolved === 'dark') {
    el.classList.add('dark')
  } else {
    el.classList.remove('dark')
  }
  applyStatusColors(resolved)
  return resolved
}

/**
 * 应用玻璃 / 平面材质。
 * @param {boolean|string} glassEnabled
 */
export function applyMaterial(glassEnabled) {
  const material = glassEnabled === true || glassEnabled === 'glass' ? 'glass' : 'plain'
  document.documentElement.setAttribute('data-theme', material)
  return material
}

/**
 * @param {unknown} value
 * @returns {'clear'|'tinted'|'frosted'}
 */
export function normalizeGlassAppearance(value) {
  if (value === 'clear' || value === 'frosted' || value === 'tinted') {
    return value
  }
  return 'tinted'
}

/**
 * @param {unknown} value
 * @returns {'standard'|'balanced'|'high'}
 */
export function normalizeGlassQuality(value) {
  if (value === 'standard' || value === 'high' || value === 'balanced') {
    return value
  }
  return 'balanced'
}

/**
 * 玻璃外观：清透 / 色调 / 磨砂。
 * @param {string} appearance
 */
export function applyGlassAppearance(appearance) {
  const value = normalizeGlassAppearance(appearance)
  document.documentElement.setAttribute('data-glass-appearance', value)
  return value
}

/**
 * 玻璃画质：标准 / 平衡 / 高画质。
 * @param {string} quality
 */
export function applyGlassQuality(quality) {
  const value = normalizeGlassQuality(quality)
  document.documentElement.setAttribute('data-glass-quality', value)
  return value
}

/**
 * 应用侧栏深浅。
 * @param {string} sideTheme
 */
export function applySideTheme(sideTheme) {
  const value = sideTheme === 'theme-light' ? 'theme-light' : 'theme-dark'
  document.documentElement.setAttribute('data-side-theme', value)
  return value
}

/**
 * @param {'light'|'dark'} scheme
 */
function applyStatusColors(scheme) {
  const el = document.documentElement
  const colors = scheme === 'dark' ? STATUS_DARK : STATUS_LIGHT
  el.style.setProperty('--el-color-success', colors.success)
  el.style.setProperty('--el-color-warning', colors.warning)
  el.style.setProperty('--el-color-info', colors.info)
  el.style.setProperty('--el-color-danger', colors.danger)
  el.style.setProperty('--qb-success-rgb', hexToRgb(colors.success).join(', '))
  el.style.setProperty('--qb-warning-rgb', hexToRgb(colors.warning).join(', '))
  el.style.setProperty('--qb-info-rgb', hexToRgb(colors.info).join(', '))
  el.style.setProperty('--qb-destructive-rgb', hexToRgb(colors.danger).join(', '))
}

/**
 * 将主色写入 `--el-color-primary` 及 soft / ring 令牌。
 * @param {string} theme 十六进制颜色值
 */
export function handleThemeStyle(theme) {
  const el = document.documentElement
  const color = theme || '#8D51F9'
  const [r, g, b] = hexToRgb(color)
  el.style.setProperty('--el-color-primary', color)
  el.style.setProperty('--qb-primary-rgb', `${r}, ${g}, ${b}`)
  el.style.setProperty('--qb-card-accent-rgb', `${r}, ${g}, ${b}`)
  el.style.setProperty('--qb-ring', `color-mix(in oklab, ${color} 68%, transparent)`)
  el.style.setProperty('--qb-primary-soft', `rgba(${r}, ${g}, ${b}, var(--qb-badge-soft-alpha))`)
  el.style.setProperty('--qb-primary-border', `rgba(${r}, ${g}, ${b}, var(--qb-badge-border-alpha))`)
  el.style.setProperty('--qb-menu-active-bg', `rgba(${r}, ${g}, ${b}, var(--qb-badge-soft-alpha))`)
  el.style.setProperty('--qb-brand-soft', `rgba(${r}, ${g}, ${b}, var(--qb-badge-soft-alpha))`)
  for (let i = 1; i <= 9; i++) {
    el.style.setProperty(`--el-color-primary-light-${i}`, getLightColor(color, i / 10))
  }
  el.style.setProperty('--el-color-primary-dark-2', getDarkColor(color, 0.2))
  el.style.setProperty('--el-border-radius-base', getComputedStyle(el).getPropertyValue('--qb-radius').trim() || '12px')
}

/**
 * 写入主题包自定义 CSS 变量（仅允许 soft 类派生，不覆盖统一灰阶）。
 * @param {Record<string, string>} tokens
 */
export function applyThemeTokens(tokens) {
  const el = document.documentElement
  if (!tokens || typeof tokens !== 'object') {
    return
  }
  const allowed = new Set(['--qb-menu-active-bg', '--qb-brand-soft'])
  Object.entries(tokens).forEach(([key, value]) => {
    if (allowed.has(key)) {
      el.style.setProperty(key, value)
    }
  })
}

/**
 * 应用完整主题包：主色 + 侧栏；灰阶一律走 tokens.scss。
 * @param {string} packId
 * @param {{ syncStore?: boolean, settingsStore?: object, colorScheme?: string, glassEnabled?: boolean }} [opts]
 * @returns {object} 主题包
 */
export function applyThemePack(packId, opts = {}) {
  const pack = getThemePack(packId)
  handleThemeStyle(pack.theme)
  applyThemeTokens(pack.tokens)
  applySideTheme(pack.sideTheme)

  if (opts.colorScheme !== undefined) {
    applyColorScheme(opts.colorScheme)
  }
  if (opts.glassEnabled !== undefined) {
    applyMaterial(opts.glassEnabled)
  }

  const store = opts.settingsStore
  if (opts.syncStore && store) {
    store.themePack = pack.id
    store.theme = pack.theme
    store.sideTheme = pack.sideTheme
  }

  return pack
}

/**
 * 启动或恢复时一次性应用外观。
 * @param {{
 *   themePack?: string,
 *   theme?: string,
 *   sideTheme?: string,
 *   colorScheme?: string,
 *   glassEnabled?: boolean,
 *   glassAppearance?: string,
 *   glassQuality?: string,
 *   wallpaperUrl?: string
 * }} settings
 */
export function applyAppearance(settings = {}) {
  const pack = getThemePack(settings.themePack || 'default')
  const scheme = applyColorScheme(settings.colorScheme || 'light')
  const glassOn = !!settings.glassEnabled
  applyMaterial(glassOn)
  applyGlassAppearance(settings.glassAppearance)
  applyGlassQuality(settings.glassQuality)
  applySideTheme(settings.sideTheme || pack.sideTheme)
  handleThemeStyle(settings.theme || pack.theme)
  applyThemeTokens(pack.tokens)
  applyWallpaper(normalizeWallpaperUrl(settings.wallpaperUrl), {
    glassEnabled: glassOn,
    colorScheme: scheme
  })
  return pack
}
