<template>
  <component :is="type" v-bind="linkProps()">
    <slot />
  </component>
</template>

<script setup>
/**
 * 菜单链接包装：外链用 <a> 新标签打开；meta.link 内嵌页强制 router-link。
 */
import { isExternal } from '@/utils/validate'

const props = defineProps({
  to: {
    type: [String, Object],
    required: true
  },
  /** 侧栏菜单 meta；含 link 时强制 router 内嵌，不走新标签页 */
  meta: {
    type: Object,
    default: null
  }
})

/**
 * 解析跳转地址。
 * 兼容误拼成 `/https://...` 的情况（曾被 router-link 当成站内 path）。
 */
function resolveHref(to) {
  let href = ''
  if (typeof to === 'string') {
    href = to
  } else if (to && typeof to === 'object' && typeof to.path === 'string') {
    href = to.path
  }
  if (/^\/+https?:/i.test(href)) {
    return href.replace(/^\/+/, '')
  }
  return href
}

/**
 * 新标签外链：以 to 是否 http(s) 为准。
 * InnerLink 内嵌页的 path 已被后端改成非 http（如 chu7/com），只会走 router-link；
 * 不能因 meta.link 有值就禁用外链，否则 isFrame=1 时也会被当成站内路由。
 */
const isExt = computed(() => isExternal(resolveHref(props.to)))

const type = computed(() => {
  if (isExt.value) {
    return 'a'
  }
  return 'router-link'
})

function linkProps() {
  if (isExt.value) {
    return {
      href: resolveHref(props.to),
      target: '_blank',
      rel: 'noopener'
    }
  }
  return {
    to: props.to
  }
}
</script>
