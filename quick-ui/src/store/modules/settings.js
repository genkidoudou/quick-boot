/**
 * 布局设置 Store：主题包、主题色、明暗、玻璃材质、壁纸、导航模式等；
 * 优先读取 localStorage layout-setting，与 defaultSettings 合并。
 */
import defaultSettings from '@/settings'
import { getThemePack } from '@/theme/packages'
import {
  applyAppearance,
  applyThemePack,
  normalizeColorScheme,
  normalizeGlassAppearance,
  normalizeGlassQuality
} from '@/utils/theme'
import { readWallpaperUrl } from '@/utils/wallpaper'
import { useDynamicTitle } from '@/utils/dynamicTitle'
import { defineStore } from 'pinia'

const {
  sideTheme,
  showSettings,
  navType,
  topNav,
  tagsView,
  fixedHeader,
  sidebarLogo,
  dynamicTitle,
  colorScheme,
  glassEnabled,
  glassAppearance,
  glassQuality
} = defaultSettings

const storageSetting = JSON.parse(localStorage.getItem('layout-setting')) || ''

function resolveNavType(storage) {
  if (storage?.navType !== undefined && storage.navType !== null && storage.navType !== '') {
    return Number(storage.navType)
  }
  if (storage?.topNav === true) {
    return 2
  }
  return navType
}

/**
 * 有 themePack 时加载包；主色 / 侧栏允许被本地覆盖（取色器改过仍保留）。
 */
function resolveThemeState(storage) {
  const packId = storage?.themePack || 'default'
  const pack = getThemePack(packId)
  return {
    themePack: pack.id,
    theme: storage?.theme || pack.theme || '#8D51F9',
    sideTheme: storage?.sideTheme || pack.sideTheme || sideTheme,
    colorScheme: normalizeColorScheme(storage?.colorScheme ?? colorScheme),
    glassEnabled: storage?.glassEnabled === true || storage?.glassEnabled === false
      ? storage.glassEnabled
      : !!glassEnabled,
    glassAppearance: normalizeGlassAppearance(storage?.glassAppearance ?? glassAppearance),
    glassQuality: normalizeGlassQuality(storage?.glassQuality ?? glassQuality),
    wallpaperUrl: readWallpaperUrl()
  }
}

const themeState = resolveThemeState(storageSetting)

const useSettingsStore = defineStore(
  'settings',
  {
    state: () => ({
      title: '',
      themePack: themeState.themePack,
      theme: themeState.theme,
      sideTheme: themeState.sideTheme,
      colorScheme: themeState.colorScheme,
      glassEnabled: themeState.glassEnabled,
      glassAppearance: themeState.glassAppearance,
      glassQuality: themeState.glassQuality,
      wallpaperUrl: themeState.wallpaperUrl,
      showSettings: showSettings,
      navType: resolveNavType(storageSetting),
      topNav: storageSetting.topNav === undefined ? topNav : storageSetting.topNav,
      tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
      fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
      sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
      dynamicTitle: storageSetting.dynamicTitle === undefined ? dynamicTitle : storageSetting.dynamicTitle
    }),
    actions: {
      changeSetting(data) {
        const { key, value } = data
        if (Object.prototype.hasOwnProperty.call(this, key)) {
          this[key] = value
        }
      },
      /** 一键切换主题包并同步主色 / 侧栏 */
      applyThemePack(packId) {
        applyThemePack(packId, {
          syncStore: true,
          settingsStore: this,
          colorScheme: this.colorScheme,
          glassEnabled: this.glassEnabled
        })
      },
      /** 应用完整外观（启动 / 恢复配置） */
      applyAppearance() {
        applyAppearance({
          themePack: this.themePack,
          theme: this.theme,
          sideTheme: this.sideTheme,
          colorScheme: this.colorScheme,
          glassEnabled: this.glassEnabled,
          glassAppearance: this.glassAppearance,
          glassQuality: this.glassQuality,
          wallpaperUrl: this.wallpaperUrl
        })
      },
      setTitle(title) {
        this.title = title
        useDynamicTitle()
      }
    }
  })

export default useSettingsStore
