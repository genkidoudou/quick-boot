/**
 * 主题色与主题包：写入 Element 主色阶与 --qb-* 设计 token。
 */
import { getThemePack } from '@/theme/packages'

/**
 * @param {string} hex
 * @returns {number[]}
 */
function hexToRgb(hex) {
  const h = String(hex || '').replace('#', '')
  if (h.length !== 6) {
    return [64, 158, 255]
  }
  return [parseInt(h.slice(0, 2), 16), parseInt(h.slice(2, 4), 16), parseInt(h.slice(4, 6), 16)]
}

/**
 * 与白色混合，level 越大越浅（0–1）。
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
 * 与黑色混合，level 越大越深（0–1）。
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
 * 将主色写入 `--el-color-primary` 及 light/dark 色阶。
 * @param {string} theme 十六进制颜色值
 */
export function handleThemeStyle(theme) {
  const el = document.documentElement
  const color = theme || '#409EFF'
  el.style.setProperty('--el-color-primary', color)
  for (let i = 1; i <= 9; i++) {
    el.style.setProperty(`--el-color-primary-light-${i}`, getLightColor(color, i / 10))
  }
  el.style.setProperty('--el-color-primary-dark-2', getDarkColor(color, 0.2))
}

/**
 * 写入主题包自定义 CSS 变量。
 * @param {Record<string, string>} tokens
 */
export function applyThemeTokens(tokens) {
  const el = document.documentElement
  if (!tokens || typeof tokens !== 'object') {
    return
  }
  Object.entries(tokens).forEach(([key, value]) => {
    el.style.setProperty(key, value)
  })
}

/**
 * 应用完整主题包：主色色阶 + token；可选同步到 settingsStore。
 * @param {string} packId
 * @param {{ syncStore?: boolean, settingsStore?: object }} [opts]
 * @returns {object} 主题包
 */
export function applyThemePack(packId, opts = {}) {
  const pack = getThemePack(packId)
  handleThemeStyle(pack.theme)
  applyThemeTokens(pack.tokens)

  const el = document.documentElement
  const radius = pack.tokens?.['--qb-radius']
  if (radius) {
    el.style.setProperty('--el-border-radius-base', radius)
  }

  const store = opts.settingsStore
  if (opts.syncStore && store) {
    store.themePack = pack.id
    store.theme = pack.theme
    store.sideTheme = pack.sideTheme
  }

  return pack
}
