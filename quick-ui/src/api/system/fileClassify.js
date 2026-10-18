/**
 * 文件分类 API，对齐后端 `/sys/fileClassify`。
 */
import request from '@/utils/request'

const BASE = '/sys/fileClassify'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageFileClassify(data) {
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
export function getFileClassify(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addFileClassify(data) {
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
export function updateFileClassify(data) {
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
export function removeFileClassify(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}
