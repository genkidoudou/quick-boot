<template>
  <section class="app-main" :class="{ 'app-main--fullscreen': route.meta?.fullScreen }">
    <router-view v-slot="{ Component, route: innerRoute }">
      <div v-if="pageError" class="app-main__error">{{ pageError }}</div>
      <component
        v-else-if="Component && !innerRoute?.meta?.link"
        :is="Component"
        :key="innerRoute.fullPath"
      />
      <div v-else-if="!innerRoute?.meta?.link" class="app-main__error">
        未匹配到页面组件：{{ innerRoute?.fullPath }}
      </div>
    </router-view>
    <iframe-toggle />
  </section>
</template>

<script setup>
/**
 * 主内容区：直接渲染匹配到的页面。
 */
import IframeToggle from './IframeToggle/index.vue'
import useTagsViewStore from '@/store/modules/tagsView'

const route = useRoute()
const tagsViewStore = useTagsViewStore()
const pageError = ref('')

onErrorCaptured((err) => {
  pageError.value = err?.stack || err?.message || String(err)
  console.error('[AppMain]', err)
  return false
})

watch(
  () => route.path,
  () => {
    pageError.value = ''
    if (route.meta?.link) {
      tagsViewStore.addIframeView(route)
    }
  },
  { immediate: true }
)
</script>

<style lang="scss" scoped>
.app-main {
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
}

.app-main__error {
  padding: 24px;
  color: #f56c6c;
  white-space: pre-wrap;
  font-size: 13px;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.app-main--fullscreen {
  min-height: 100vh;
  padding-top: 0 !important;
  overflow: hidden;
}

.hasTagsView {
  .app-main {
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 6px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}
</style>
