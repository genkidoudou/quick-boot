/**
 * 消息渠道 API，对齐后端 msg/channel。
 */
import request from '@/utils/request'

const BASE = '/msg/channel'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageChannel(data) {
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
export function getChannel(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addChannel(data) {
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
export function updateChannel(data) {
  return request({
    url: `${BASE}/update`,
    method: 'post',
    data
  })
}

/**
 * 按渠道测试发送。
 * @param {{ channelId: string|number, title: string, body: string, address?: string[], msgType?: string }} data
 */
export function testChannel(data) {
  return request({
    url: `${BASE}/test`,
    method: 'post',
    data
  })
}

/**
 * 批量删除。
 * @param {Array<string|number>} ids
 */
export function removeChannel(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}
