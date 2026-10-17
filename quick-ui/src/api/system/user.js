/**
 * 系统用户 API，对齐后端 `/sys/user`。
 */
import request from '@/utils/request'

const BASE = '/sys/user'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageUser(data) {
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
export function getUser(id) {
    return request({
        url: `${BASE}/${id}`,
        method: 'get'
    })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addUser(data) {
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
export function updateUser(data) {
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
export function removeUser(ids) {
    return request({
        url: `${BASE}/remove`,
        method: 'post',
        data: ids
    })
}

/**
 * 重置密码
 * @param id  用户id
 * @returns {*}
 */
export function resetPassword(id) {
    return request({
        url: `${BASE}/resetPassword/` + id,
        method: 'post'
    })
}

/**
 * 二级认证：校验当前登录密码并 openSafe。
 * @param {string} password 当前登录密码
 */
export function openSafe(password) {
    return request({
        url: `${BASE}/openSafe`,
        method: 'post',
        params: {password}
    })
}

/**
 * 修改当前登录用户密码。
 * @param {{ oldPassword: string, newPassword: string }} data
 */
export function updatePassword(data) {
    return request({
        url: `${BASE}/updatePassword`,
        method: 'post',
        data
    })
}

/**
 * 当前登录用户个人资料。
 */
export function getUserProfile() {
    return request({
        url: `${BASE}/profile`,
        method: 'get'
    })
}

/**
 * 更新当前登录用户个人资料。
 * @param {{ nickName?: string, email?: string, phonenumber?: string, sex?: string }} data
 */
export function updateUserProfile(data) {
    return request({
        url: `${BASE}/updateProfile`,
        method: 'post',
        data
    })
}