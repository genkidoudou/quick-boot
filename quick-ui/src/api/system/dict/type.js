/**
 * 字典类型 API，对齐后端 `/sys/dictType`。
 */
import request, {downloadRequest} from '@/utils/request'

const BASE = '/sys/dictType'

/**
 * 分页查询。
 * @param {{ current?: number, size?: number, param?: Record<string, any> }} data PageRequest
 */
export function pageDictType(data) {
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
export function getDictType(id) {
    return request({
        url: `${BASE}/${id}`,
        method: 'get'
    })
}

/**
 * 新增。
 * @param {Record<string, any>} data
 */
export function addDictType(data) {
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
export function updateDictType(data) {
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
export function removeDictType(ids) {
    return request({
        url: `${BASE}/remove`,
        method: 'post',
        data: ids
    })
}

/**
 * excel导出
 */
export function exportExcelDictType(params) {
    return downloadRequest(`${BASE}/exportExcel`, params)
}

/**
 * 下载导入模板。
 */
export function downloadDictTypeImportTemplate() {
    return request({
        url: `${BASE}/importExcelTemplate`,
        method: 'POST',
        responseType: 'blob',
        returnBlobWithHeaders: true
    })
}

/**
 * 解析便捷文本。
 * @param {string} source
 */
export function parseDictType(source) {
    return request({
        url: `${BASE}/parse`,
        method: 'post',
        data: { source }
    })
}

/**
 * 便捷添加：一次保存类型与字典项。
 * @param {{ dictName: string, dictType: string, remark?: string, status?: string, items: Array }} data
 */
export function quickAddDictType(data) {
    return request({
        url: `${BASE}/quickAdd`,
        method: 'post',
        data
    })
}

/**
 * excel 导入。
 * @param {File} file
 * @param {'overwrite'|'ignore'} strategy
 */
export function importExcelDictType(file, strategy) {
    const form = new FormData()
    form.append('file', file)
    form.append('updateSupport', strategy === 'overwrite' ? 'true' : 'false')
    return request({
        url: `${BASE}/importExcel`,
        method: 'post',
        data: form,
        timeout: 120000
    })
}