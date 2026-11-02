/**
 * 发送明细 API，对齐后端 msg/sendRecordItem。
 */
import request from '@/utils/request'

const BASE = '/msg/sendRecordItem'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageSendRecordItem(data) {
  return request({
    url: `${BASE}/page`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param: omitBlank(data?.param)
    }
  })
}

/**
 * 详情。
 * @param {string|number} id
 */
export function getSendRecordItem(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addSendRecordItem(data) {
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
export function updateSendRecordItem(data) {
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
export function removeSendRecordItem(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}

/** 查询条件里的空字符串不能绑到 Long。 */
function omitBlank(param) {
  const source = param ?? {}
  const result = {}
  Object.keys(source).forEach((key) => {
    const value = source[key]
    if (value !== '' && value != null) {
      result[key] = value
    }
  })
  return result
}
