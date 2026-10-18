/**
 * 文件 API，对齐后端 `/sys/file`。
 */
import request from '@/utils/request'

const BASE = '/sys/file'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageFile(data) {
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
export function getFile(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addFile(data) {
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
export function updateFile(data) {
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
export function removeFile(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}

/**
 * 启用中的上传分类。
 */
export function listUploadClassifies() {
  return request({
    url: `${BASE}/upload-classifies`,
    method: 'get'
  })
}

/**
 * 按分类上传并登记。
 * @param {File} file
 * @param {string} classify
 */
export function uploadFile(file, classify) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `${BASE}/upload/${encodeURIComponent(classify)}`,
    method: 'post',
    data: formData
  })
}
