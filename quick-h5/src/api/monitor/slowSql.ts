/**
 * 慢 SQL 日志 API。
 */
import { request } from '../http'
import type { PageInfo, PageRequest } from '../types'

/** 慢 SQL 记录 */
export type SysSlowSql = {
  slowId?: number | string
  sqlSource?: string
  sqlType?: string
  mapperId?: string
  sqlText?: string
  costTime?: number
  traceId?: string
  requestUri?: string
  operName?: string
  createTime?: string
}

/** 分页列表（POST /page） */
export function pageSlowSqlList(pageRequest: PageRequest<{ operName?: string; requestUri?: string }>) {
  return request<PageInfo<SysSlowSql>>({
    url: '/monitor/slowSql/page',
    method: 'POST',
    data: pageRequest,
  })
}

export function pageSlowSql(current: number, size: number, keyword?: string) {
  return pageSlowSqlList({
    current,
    size,
    param: { requestUri: keyword || undefined },
  })
}

/** 详情 */
export function getSlowSql(slowId: number | string) {
  return request<SysSlowSql>({
    url: `/monitor/slowSql/${encodeURIComponent(String(slowId))}`,
    method: 'GET',
  })
}

/** 批量删除 */
export function removeSlowSql(ids: Array<number | string>) {
  return request<void>({
    url: '/monitor/slowSql/remove',
    method: 'POST',
    data: ids,
  })
}
