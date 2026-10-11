/**
 * 部门 API，对齐后端 `/sys/dept`。
 */
import request from '@/utils/request'

const BASE = '/sys/dept'

/**
 * 部门树。后端 `POST /sys/dept/page` 按条件返回树，不是分页。
 * @param {Record<string, any>} [params]
 */
export function listDept(params) {
  return request({
    url: `${BASE}/page`,
    method: 'post',
    data: params || {}
  })
}

/**
 * 上级部门树（表单下拉）。
 * @param {Record<string, any>} [params]
 */
export function treeselectDept(params) {
  return listDept(params)
}

/**
 * 详情。
 * @param {string|number} id
 */
export function getDept(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addDept(data) {
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
export function updateDept(data) {
  return request({
    url: `${BASE}/update`,
    method: 'post',
    data
  })
}

/**
 * 删除。页面传入单个 id，后端 `remove` 要 id 数组。
 * @param {string|number|Array<string|number>} id
 */
export function delDept(id) {
  const ids = id == null ? [] : Array.isArray(id) ? id : [id]
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}

export const removeDept = delDept
