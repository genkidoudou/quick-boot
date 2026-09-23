/**
 * 动态路由菜单 API。
 */
import request from '@/utils/request'

/** 获取当前用户可访问的路由树 */
export function getMenuRoutes() {
  return request({
    url: '/sys/menu/routes',
    method: 'get'
  })
}
