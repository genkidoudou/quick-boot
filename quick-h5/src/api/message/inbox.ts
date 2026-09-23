/**
 * H5 站内信收件箱 API。
 */
import { request } from '../http'
import type { PageInfo, PageRequest } from '../types'

export type MsgInbox = {
  inboxId?: number | string
  messageId?: number | string
  userId?: number | string
  title?: string
  content?: string
  /** 0 未读 / 1 已读 */
  readFlag?: string
  readTime?: string
  createTime?: string
}

export function pageInbox(pageRequest: PageRequest<Partial<MsgInbox>>) {
  return request<PageInfo<MsgInbox>>({
    url: '/message/inbox/page',
    method: 'POST',
    data: pageRequest,
  })
}

export function getInbox(inboxId: number | string) {
  return request<MsgInbox>({
    url: `/message/inbox/${encodeURIComponent(String(inboxId))}`,
    method: 'GET',
  })
}

export function markInboxRead(ids: Array<number | string>) {
  return request<void>({
    url: '/message/inbox/read',
    method: 'PUT',
    data: ids.map(String),
  })
}

export function getInboxUnreadCount() {
  return request<number>({
    url: '/message/inbox/unread-count',
    method: 'GET',
  })
}
