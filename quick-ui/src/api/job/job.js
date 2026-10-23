/**
 * 定时任务 API，对齐后端 `/job/job`。
 */
import request from '@/utils/request'

const BASE = '/job/job'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageJob(data) {
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
export function getJob(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addJob(data) {
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
export function updateJob(data) {
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
export function removeJob(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}

/**
 * 修改启停状态。
 * @param {{ id: string|number, status: string }} data
 */
export function changeJobStatus(data) {
  return request({
    url: `${BASE}/changeStatus`,
    method: 'post',
    data
  })
}

/**
 * 立即执行一次。
 * @param {string|number} id
 */
export function runJob(id) {
  return request({
    url: `${BASE}/run/${id}`,
    method: 'post'
  })
}

/**
 * 列出实现 ITask 的 Spring Bean。
 * @returns {Promise<{ data?: Array<{ beanName: string, className: string }> }>}
 */
export function listJobTaskBeans() {
  return request({
    url: `${BASE}/taskBeans`,
    method: 'get'
  })
}
