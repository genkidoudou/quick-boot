/**
 * 定时任务日志 API，对齐后端 `/job/jobLog`。
 */
import request from '@/utils/request'

const BASE = '/job/jobLog'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageJobLog(data) {
  const param = {...(data?.param ?? {})}
  if (param.jobId === '' || param.jobId == null) {
    delete param.jobId
  }
  return request({
    url: `${BASE}/page`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param
    }
  })
}

/**
 * 详情。
 * @param {string|number} id
 */
export function getJobLog(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 增量拉取过程日志（文件过程日志）。
 * @param {string|number} id 日志 id
 * @param {number} [fromLine=1] 起始行（1-based）；之后传上次 toLine + 1
 */
export function catJobLog(id, fromLine = 1) {
  return request({
    url: `${BASE}/${id}/cat`,
    method: 'get',
    params: {fromLine: Number(fromLine) || 1}
  })
}

/**
 * 批量删除。
 * @param {Array<string|number>} ids
 */
export function removeJobLog(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}
