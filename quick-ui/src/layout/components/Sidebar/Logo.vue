<template>
  <div class="sidebar-logo-container" :class="{ collapse: collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <span class="sidebar-mark" aria-hidden="true" />
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <span class="sidebar-mark" aria-hidden="true" />
        <div class="sidebar-wordmark">
          <h1 class="sidebar-title">{{ title }}</h1>
          <span class="sidebar-edition">v3</span>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
/** 侧栏顶部品牌：紫标 + 字标，对齐 MoviePilot 导航气质 */
import { appConfig } from '@/config/env'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = appConfig.title
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 0.35s ease;
}

.sidebarLogoFade-enter-from,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 8px;
  background: transparent;
  border-bottom: 1px solid var(--qb-border-subtle);
}

.sidebar-logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  min-width: 0;
  padding: 10px 10px;
  text-decoration: none;
  border-radius: 14px;
}

.sidebar-mark {
  flex: none;
  width: 28px;
  height: 28px;
  border-radius: 9px;
  background:
    linear-gradient(135deg, var(--el-color-primary) 0%, color-mix(in srgb, var(--el-color-primary) 55%, #7c9cc4) 100%);
  box-shadow: 0 6px 14px color-mix(in srgb, var(--el-color-primary) 28%, transparent);
}

.sidebar-wordmark {
  min-width: 0;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.sidebar-title {
  margin: 0;
  color: var(--qb-sidebar-text-active);
  font-weight: 700;
  font-size: 15px;
  letter-spacing: 0;
  font-family: var(--qb-font-sans);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar-edition {
  flex: none;
  color: var(--qb-faint);
  font-size: 11px;
  font-weight: 600;
  font-family: var(--qb-font-sans);
  text-transform: lowercase;
}

.collapse {
  padding: 0;
  justify-content: center;

  .sidebar-logo-link {
    justify-content: center;
    padding: 10px 0;
  }
}
</style>
