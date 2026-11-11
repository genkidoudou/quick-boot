/**
 * OAuth 客户端 API，对齐后端 `/sys/oauthclient`。
 */
import request from '@/utils/request'

const BASE = '/sys/oauthclient'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageOauthClient(data) {
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
export function getOauthClient(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addOauthClient(data) {
  return request({
    url: `${BASE}/add`,
    method: 'post',
    data
  })
}

/**
 * 修改。
 * @param {Record<string, any>} data
 */
export function updateOauthClient(data) {
  return request({
    url: `${BASE}/update`,
    method: 'post',
    data
  })
}

/**
 * 批量删除。
 * @param {Array<string|number>} ids
 */
export function removeOauthClient(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}
