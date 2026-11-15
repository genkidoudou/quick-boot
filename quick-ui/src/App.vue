<template>
  <el-config-provider :locale="locale" :size="elementSize" :z-index="2000">
    <WallpaperLayer />
    <GlassOpticalLayer />
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { onMounted, nextTick } from 'vue'
import Cookies from 'js-cookie'
import locale from 'element-plus/es/locale/lang/zh-cn'
import useSettingsStore from '@/store/modules/settings'
import { applyAppearance, handleThemeStyle } from '@/utils/theme'
import WallpaperLayer from '@/components/theme/WallpaperLayer.vue'
import GlassOpticalLayer from '@/components/theme/GlassOpticalLayer.vue'

/** 与历史 Cookie 约定一致，供 Element Plus 全局默认尺寸 */
const elementSize = Cookies.get('size') || 'default'

onMounted(() => {
  nextTick(() => {
    const settingsStore = useSettingsStore()
    applyAppearance({
      themePack: settingsStore.themePack || 'default',
      theme: settingsStore.theme,
      sideTheme: settingsStore.sideTheme,
      colorScheme: settingsStore.colorScheme,
      glassEnabled: settingsStore.glassEnabled,
      glassAppearance: settingsStore.glassAppearance,
      glassQuality: settingsStore.glassQuality,
      wallpaperUrl: settingsStore.wallpaperUrl
    })
    // 包写完后，再用本地主色覆盖（兼容取色器自定义）
    handleThemeStyle(settingsStore.theme)
  })
})
</script>
