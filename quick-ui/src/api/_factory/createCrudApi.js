/**
 * 通用 CRUD API 工厂：对齐后端 PageRequest / R&lt;PageInfo&gt; 与 POST 写操作约定。
 *
 * @param {string} basePath 如 `/sys/dictType`（须带前导 `/`）
 * @param {{ export?: boolean }} [options]
 */
import request from '@/utils/request'

/**
 * 将扁平 query（含 current/size）转为 PageRequest：`{ current, size, param }`。
 * @param {Record<string, any>} [query]
 */
export function toPageRequest(query = {}) {
  const { current, size, pageNum, pageSize, ...rest } = query
  return {
    current: Number(current ?? pageNum ?? 1),
    size: Number(size ?? pageSize ?? 10),
    param: rest
  }
}

/**
 * @param {string} basePath
 * @param {{ export?: boolean }} [options]
 */
export function createCrudApi(basePath, options = {}) {
  const base = basePath.endsWith('/') ? basePath.slice(0, -1) : basePath

  const api = {
    /**
     * 分页。入参可为 PageRequest，或扁平 query（自动 toPageRequest）。
     * @param {Record<string, any>} body
     */
    page(body) {
      const data =
        body && typeof body === 'object' && ('param' in body || 'current' in body)
          ? {
              current: Number(body.current ?? 1),
              size: Number(body.size ?? 10),
              param: body.param ?? {}
            }
          : toPageRequest(body || {})
      return request({ url: `${base}/page`, method: 'post', data })
    },

    /**
     * @param {string|number} id
     */
    get(id) {
      return request({ url: `${base}/${id}`, method: 'get' })
    },

    /**
     * @param {Record<string, any>} data
     */
    add(data) {
      return request({ url: `${base}/add`, method: 'post', data })
    },

    /**
     * @param {Record<string, any>} data
     */
    update(data) {
      return request({ url: `${base}/update`, method: 'post', data })
    },

    /**
     * @param {Array<string|number>} ids
     */
    remove(ids) {
      return request({ url: `${base}/remove`, method: 'post', data: ids })
    }
  }

  if (options.export) {
    api.export = function (query) {
      return request({
        url: `${base}/export`,
        method: 'post',
        data: query,
        responseType: 'blob',
        returnBlobWithHeaders: true
      })
    }
    api.downloadImportTemplate = function () {
      return request({
        url: `${base}/import/template`,
        method: 'get',
        responseType: 'blob',
        returnBlobWithHeaders: true
      })
    }
    api.importExcel = function (file, strategy) {
      const form = new FormData()
      form.append('file', file)
      form.append('updateSupport', strategy === 'overwrite' ? 'true' : 'false')
      return request({
        url: `${base}/import`,
        method: 'post',
        data: form,
        timeout: 120000
      })
    }
  }

  return api
}
