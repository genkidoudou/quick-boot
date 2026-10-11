<template>
  <div v-if="!item.hidden">
    <template
      v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow"
    >
      <app-link v-if="onlyOneChild.meta" :meta="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query, onlyOneChild.meta)">
        <el-menu-item :index="resolvePath(onlyOneChild.path, onlyOneChild.query, onlyOneChild.meta)" :class="{ 'submenu-title-noDropdown': !isNest }">
          <svg-icon :icon-class="onlyOneChild.meta.icon || (item.meta && item.meta.icon)" />
          <template #title>
            <span class="menu-title" :title="hasTitle(onlyOneChild.meta.title)">{{ onlyOneChild.meta.title }}</span>
          </template>
        </el-menu-item>
      </app-link>
    </template>

    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)" teleported>
      <template v-if="item.meta" #title>
        <svg-icon :icon-class="item.meta && item.meta.icon" />
        <span class="menu-title" :title="hasTitle(item.meta.title)">{{ item.meta.title }}</span>
      </template>

      <sidebar-item
        v-for="(child, index) in item.children"
        :key="child.path + index"
        :is-nest="true"
        :item="child"
        :base-path="resolveNestBasePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
/**
 * 侧栏菜单项递归组件：单子节点折叠、外链/内嵌 iframe 路由解析。
 */
import { isExternal } from '@/utils/validate'
import AppLink from './Link.vue'
import { getNormalPath, parseRouteQuery } from '@/utils/ruoyi'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  isNest: {
    type: Boolean,
    default: false
  },
  basePath: {
    type: String,
    default: ''
  }
})

const onlyOneChild = ref({})

/**
 * 判断是否仅有一个可见子路由（可折叠为单菜单项）。
 * ParentView/Layout 空目录不当作叶子，避免进入无组件路由。
 */
function hasOneShowingChild(children = [], parent) {
  if (!children) {
    children = []
  }
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      onlyOneChild.value = item
      return true
    }
  })

  if (showingChildren.length === 1) {
    return true
  }

  if (showingChildren.length === 0) {
    // ParentView/Layout 空目录不能当成可点击叶子，否则会进到无组件路由 → 404
    if (parent.component === 'ParentView' || parent.component === 'Layout' || parent.alwaysShow) {
      return false
    }
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

/**
 * 嵌套子项的 basePath：外链 path 不可与父 path 拼接，否则会变成 /sys/https://...
 */
function resolveNestBasePath(childPath) {
  if (isExternal(childPath)) {
    return childPath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  return getNormalPath(`${props.basePath}/${childPath}`)
}

/** 拼接 basePath 与路由 path；http(s) 外链原样返回供新标签打开 */
function resolvePath(routePath, routeQuery, routeMeta) {
  let path = isExternal(routePath)
    ? routePath
    : isExternal(props.basePath)
      ? props.basePath
      : getNormalPath(`${props.basePath}/${routePath}`)

  // 纠正 `/https://...` 这种被拼坏的地址
  if (/^\/+https?:/i.test(path)) {
    path = path.replace(/^\/+/, '')
  }

  // 新标签外链：直接返回完整 URL
  if (isExternal(path)) {
    return path
  }

  // 内嵌 iframe（meta.link + 非 http path）走站内路由
  if (routeMeta?.link) {
    const query = parseRouteQuery(routeQuery)
    return query ? { path, query } : path
  }
  const query = parseRouteQuery(routeQuery)
  return query ? { path, query } : path
}

function hasTitle(title) {
  if (title && title.length > 5) {
    return title
  } else {
    return ''
  }
}
</script>
