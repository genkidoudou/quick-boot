/**
 * 在线用户 API。
 */
import { request } from '../http'
import type { PageInfo, PageRequest } from '../types'

/** 在线会话 */
export type SysUserOnline = {
  tokenId?: string
  userId?: number | string
  userName?: string
  deptName?: string
  ipaddr?: string
  loginLocation?: string
  browser?: string
  os?: string
  loginTime?: string
}

/** 分页列表（POST /page） */
export function pageOnlineList(pageRequest: PageRequest<{ userName?: string; ipaddr?: string }>) {
  return request<PageInfo<SysUserOnline>>({
    url: '/monitor/online/page',
    method: 'POST',
    data: pageRequest,
  })
}

export function pageOnline(current: number, size: number, userName?: string) {
  return pageOnlineList({
    current,
    size,
    param: { userName: userName || undefined },
  })
}

/** 强退 */
export function forceLogout(tokenId: string) {
  return request<void>({
    url: '/monitor/online/forceLogout',
    method: 'POST',
    data: { tokenId },
  })
}
