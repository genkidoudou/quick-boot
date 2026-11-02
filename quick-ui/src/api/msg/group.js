/**
 * 消息群组 API，对齐后端 msg/group。
 */
import request from '@/utils/request'

const BASE = '/msg/group'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageGroup(data) {
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
export function getGroup(id) {
  return request({
    url: `${BASE}/${id}`,
    method: 'get'
  })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addGroup(data) {
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
export function updateGroup(data) {
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
export function removeGroup(ids) {
  return request({
    url: `${BASE}/remove`,
    method: 'post',
    data: ids
  })
}

/**
 * 已选用户成员。
 * @param {string|number} groupId
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data
 */
export function pageChooseUser(groupId, data) {
  return request({
    url: `${BASE}/chooseUser/${groupId}`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param: data?.param ?? {}
    }
  })
}

/**
 * 未选用户。
 * @param {string|number} groupId
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data
 */
export function pageUnChooseUser(groupId, data) {
  return request({
    url: `${BASE}/unChooseUser/${groupId}`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param: data?.param ?? {}
    }
  })
}

/**
 * 导入用户成员。
 * @param {string|number} groupId
 * @param {Array<string|number>} userIds
 */
export function importGroupUser(groupId, userIds) {
  return request({
    url: `${BASE}/importUser/${groupId}`,
    method: 'post',
    data: userIds
  })
}

/**
 * 删除已选用户成员。
 * @param {string|number} groupId
 * @param {Array<string|number>} memberIds 群组成员主键
 */
export function removeGroupUser(groupId, memberIds) {
  return request({
    url: `${BASE}/removeUser/${groupId}`,
    method: 'post',
    data: memberIds
  })
}

/**
 * 候选部门。已选部门 chosen 为 true。
 * @param {string|number} groupId
 */
export function listGroupDeptTree(groupId) {
  return request({
    url: `${BASE}/deptTree/${groupId}`,
    method: 'post',
    data: {}
  })
}

/**
 * 按勾选结果保存部门成员。
 * @param {string|number} groupId
 * @param {Array<string|number>} deptIds
 */
export function saveGroupDept(groupId, deptIds) {
  return request({
    url: `${BASE}/saveDept/${groupId}`,
    method: 'post',
    data: deptIds
  })
}

/**
 * 已添加的外部人。
 * @param {string|number} groupId
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data
 */
export function pageGroupExternal(groupId, data) {
  return request({
    url: `${BASE}/pageExternal/${groupId}`,
    method: 'post',
    data: {
      current: Number(data?.current ?? 1),
      size: Number(data?.size ?? 10),
      param: data?.param ?? {}
    }
  })
}

/**
 * 添加外部人。
 * @param {string|number} groupId
 * @param {{ name?: string, phone?: string, email?: string }} data
 */
export function addGroupExternal(groupId, data) {
  return request({
    url: `${BASE}/addExternal/${groupId}`,
    method: 'post',
    data
  })
}

/**
 * 删除外部人。
 * @param {string|number} groupId
 * @param {Array<string|number>} memberIds
 */
export function removeGroupExternal(groupId, memberIds) {
  return request({
    url: `${BASE}/removeExternal/${groupId}`,
    method: 'post',
    data: memberIds
  })
}
