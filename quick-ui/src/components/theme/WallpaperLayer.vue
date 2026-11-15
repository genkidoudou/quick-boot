<template>
  <div
    v-show="visible"
    class="qb-wallpaper-layer"
    aria-hidden="true"
  />
</template>

<script setup>
/**
 * 玻璃态壁纸层：仅在 glass + 有壁纸 URL 时显示，由 CSS 变量驱动。
 */
import useSettingsStore from '@/store/modules/settings'

const settingsStore = useSettingsStore()

const visible = computed(() => {
  return !!settingsStore.glassEnabled && !!settingsStore.wallpaperUrl
})
</script>
