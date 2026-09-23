/**
 * 调度日志 API。
 */
import { request } from '../http'
import type { PageInfo, PageRequest } from '../types'

/** 调度日志 */
export type SysJobLog = {
  jobLogId?: number | string
  jobName?: string
  jobGroup?: string
  invokeTarget?: string
  jobMessage?: string
  status?: string
  exceptionInfo?: string
  createTime?: string
}

/** 分页列表（POST /page） */
export function pageJobLog(pageRequest: PageRequest<Partial<SysJobLog>>) {
  return request<PageInfo<SysJobLog>>({
    url: '/monitor/jobLog/page',
    method: 'POST',
    data: pageRequest,
  })
}

/**
 * 便捷分页：对齐 usePagedList。
 * @param status 可选；0 成功 / 1 失败；空串不传
 */
export function pageJobLogs(current: number, size: number, jobName?: string, status?: string) {
  return pageJobLog({
    current,
    size,
    param: {
      jobName: jobName || undefined,
      status: status || undefined,
    },
  })
}

/** 详情 */
export function getJobLog(jobLogId: number | string) {
  return request<SysJobLog>({
    url: `/monitor/jobLog/${encodeURIComponent(String(jobLogId))}`,
    method: 'GET',
  })
}

/** 批量删除 */
export function removeJobLog(ids: Array<number | string>) {
  return request<void>({
    url: '/monitor/jobLog/remove',
    method: 'POST',
    data: ids,
  })
}

/** 清空 */
export function cleanJobLog() {
  return request<void>({
    url: '/monitor/jobLog/clean',
    method: 'POST',
  })
}
