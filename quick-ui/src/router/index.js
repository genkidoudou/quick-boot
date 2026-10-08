import { createWebHistory, createRouter } from 'vue-router'
import Layout from '@/layout/index.vue'

export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  // {
  //   path: '/sys',
  //   component: Layout,
  //   redirect: '/sys/menu',
  //   meta: { title: '系统管理', icon: 'dashboard' },
  //   children: [
  //     {
  //       path: 'menu',
  //       component: () => import('@/views/system/menu/index'),
  //       name: 'SysMenu',
  //       meta: { title: '菜单管理', icon: 'dashboard' }
  //     },
  //     {
  //       path: 'dictType',
  //       component: () => import('@/views/system/dict/type/index'),
  //       name: 'SysDictType',
  //       meta: { title: '字典管理', icon: 'dashboard' }
  //     },
  //     {
  //       path: 'dictData',
  //       component: () => import('@/views/system/dict/data/index.vue'),
  //       name: 'SysDictData',
  //       hidden: true,
  //       meta: { title: '字典数据', activeMenu: '/sys/dictType' }
  //     }
  //   ]
  // },
  // 404 必须放最后，否则会抢先匹配后面的业务路由
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/error/404'),
    hidden: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

export default router
