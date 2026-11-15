/**
 * 玻璃壁纸：预设与本地上传（独立 localStorage，避免撑爆 layout-setting）。
 */

export const WALLPAPER_STORAGE_KEY = 'qb-wallpaper-url'

export const WALLPAPER_PRESETS = [
  {
    id: 'none',
    name: '无壁纸',
    url: ''
  },
  {
    id: 'dawn',
    name: '晨雾',
    url: 'linear-gradient(155deg, #eef3ff 0%, #f7f2ff 42%, #e8eef8 100%)'
  },
  {
    id: 'violet',
    name: '紫霓',
    url: 'radial-gradient(ellipse at 30% 20%, rgba(141,81,249,0.45), transparent 50%), linear-gradient(160deg, #1a1030, #0b1322 60%, #152238)'
  },
  {
    id: 'ocean',
    name: '深海',
    url: 'radial-gradient(ellipse at 70% 10%, rgba(0,122,255,0.35), transparent 45%), linear-gradient(165deg, #0a1628, #0b1322 55%, #132033)'
  },
  {
    id: 'mint',
    name: '青岚',
    url: 'radial-gradient(ellipse at 40% 0%, rgba(105,197,154,0.35), transparent 48%), linear-gradient(150deg, #e8f7f1, #f2f2f7 55%, #e4eef8)'
  }
]

/**
 * @param {unknown} value
 * @returns {string}
 */
export function normalizeWallpaperUrl(value) {
  if (typeof value !== 'string') return ''
  return value.trim()
}

/** @returns {string} */
export function readWallpaperUrl() {
  if (typeof localStorage === 'undefined') return ''
  return normalizeWallpaperUrl(localStorage.getItem(WALLPAPER_STORAGE_KEY))
}

/**
 * @param {string} url
 */
export function writeWallpaperUrl(url) {
  const next = normalizeWallpaperUrl(url)
  if (!next) {
    localStorage.removeItem(WALLPAPER_STORAGE_KEY)
    return
  }
  localStorage.setItem(WALLPAPER_STORAGE_KEY, next)
}

/**
 * 将壁纸写入文档 CSS 变量。
 * @param {string} url
 * @param {{ glassEnabled?: boolean, colorScheme?: 'light'|'dark' }} [opts]
 */
export function applyWallpaper(url, opts = {}) {
  const el = document.documentElement
  const next = normalizeWallpaperUrl(url)
  const glassOn = opts.glassEnabled === true
  if (!glassOn || !next) {
    el.style.removeProperty('--qb-wallpaper-url')
    el.removeAttribute('data-wallpaper')
    el.style.setProperty('--qb-wallpaper-brightness', '1')
    el.style.setProperty('--qb-wallpaper-contrast', '1')
    return ''
  }

  const isCssImage = next.startsWith('url(') || next.startsWith('data:') || /^https?:\/\//.test(next)
  const cssValue = isCssImage
    ? (next.startsWith('url(') ? next : `url("${next.replace(/"/g, '\\"')}")`)
    : next

  el.style.setProperty('--qb-wallpaper-url', cssValue)
  el.setAttribute('data-wallpaper', 'on')

  if (opts.colorScheme === 'dark') {
    el.style.setProperty('--qb-wallpaper-brightness', '0.72')
    el.style.setProperty('--qb-wallpaper-contrast', '1.05')
  } else {
    el.style.setProperty('--qb-wallpaper-brightness', '1.05')
    el.style.setProperty('--qb-wallpaper-contrast', '1')
  }
  return next
}

/**
 * 压缩图片为 data URL，限制体积。
 * @param {File} file
 * @param {{ maxWidth?: number, quality?: number }} [opts]
 * @returns {Promise<string>}
 */
export function fileToWallpaperDataUrl(file, opts = {}) {
  const maxWidth = opts.maxWidth || 1920
  const quality = opts.quality ?? 0.82
  return new Promise((resolve, reject) => {
    if (!file || !file.type.startsWith('image/')) {
      reject(new Error('请选择图片文件'))
      return
    }
    const reader = new FileReader()
    reader.onerror = () => reject(new Error('读取文件失败'))
    reader.onload = () => {
      const img = new Image()
      img.onerror = () => reject(new Error('图片解析失败'))
      img.onload = () => {
        const scale = Math.min(1, maxWidth / img.width)
        const w = Math.max(1, Math.round(img.width * scale))
        const h = Math.max(1, Math.round(img.height * scale))
        const canvas = document.createElement('canvas')
        canvas.width = w
        canvas.height = h
        const ctx = canvas.getContext('2d')
        if (!ctx) {
          reject(new Error('画布不可用'))
          return
        }
        ctx.drawImage(img, 0, 0, w, h)
        resolve(canvas.toDataURL('image/jpeg', quality))
      }
      img.src = String(reader.result)
    }
    reader.readAsDataURL(file)
  })
}
