/**
 * 角色 API，对齐后端 `/sys/role`。
 */
import request from '@/utils/request'

const BASE = '/sys/role'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageRole(data) {
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
export function getRole(id) {
    return request({
        url: `${BASE}/${id}`,
        method: 'get'
    })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addRole(data) {
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
export function updateRole(data) {
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
export function removeRole(ids) {
    return request({
        url: `${BASE}/remove`,
        method: 'post',
        data: ids
    })
}


/**
 * 查询可授权的角色列表
 * @param id
 * @returns {*}
 */
export function assignableRoles() {
    return request({
        url: `${BASE}/assignable`,
        method: 'get'

    })
}