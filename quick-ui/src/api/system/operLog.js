/**
 * 操作日志 API，对齐后端 `/sys/operLog`。仅分页与详情。
 */
import request from '@/utils/request'

const BASE = '/sys/operLog'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageOperLog(data) {
  return request({
    url: `${BASE}/page`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param: data?.param ?? {}
    }
  })
}

/**
 * 详情。
 * @param {string|number} id
 */
export function getOperLog(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}
