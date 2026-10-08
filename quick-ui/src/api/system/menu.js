/**
 * 系统菜单 API，对齐后端 `/sys/menu`。
 */
import { createCrudApi } from '@/api/_factory/createCrudApi'
import request from '@/utils/request'

const BASE = '/sys/menu'
const crud = createCrudApi(BASE)

/**
 * 菜单树。后端 `POST /sys/menu/page` 按条件返回树，不是分页。
 * @param {Record<string, any>} [params]
 */
export function listMenu(params) {
  return request({
    url: `${BASE}/page`,
    method: 'post',
    data: params || {}
  })
}

/**
 * 父级菜单树（表单下拉）。
 * @param {Record<string, any>} [params]
 */
export function treeselectMenu(params) {
  return listMenu(params)
}

export const getMenu = crud.get
export const addMenu = crud.add
export const updateMenu = crud.update

/**
 * 删除。页面传入单个 id，后端 `remove` 要 id 数组。
 * @param {string|number|Array<string|number>} id
 */
export function delMenu(id) {
  const ids = id == null ? [] : Array.isArray(id) ? id : [id]
  return crud.remove(ids)
}

/**
 * 一次保存菜单页及其按钮。
 * @param {{ menu: Record<string, any>, buttons?: Array<Record<string, any>> }} data
 */
export function batchAddMenu(data) {
  return request({
    url: `${BASE}/batch`,
    method: 'post',
    data
  })
}

/**
 * 解析 Controller 源码为菜单 + 按钮。
 * @param {string} source
 */
export function parseMenuController(source) {
  return request({
    url: `${BASE}/parse`,
    method: 'post',
    data: { source }
  })
}

/**
 * 保存排序。
 * @param {{ menuIds: Array<string|number>, orderNums: Array<number> }} data
 */
export function updateMenuSort(data) {
  return request({
    url: `${BASE}/sort`,
    method: 'post',
    data
  })
}

/**
 * 导出 Excel。
 * @param {Record<string, any>} [query]
 */
export function exportMenu(query) {
  return request({
    url: `${BASE}/exportExcel`,
    method: 'post',
    data: query || {},
    responseType: 'blob',
    returnBlobWithHeaders: true
  })
}

/** 下载导入模板。 */
export function downloadMenuImportTemplate() {
  return request({
    url: `${BASE}/importExcelTemplate`,
    method: 'post',
    responseType: 'blob',
    returnBlobWithHeaders: true
  })
}

/**
 * 导入 Excel。
 * @param {File} file
 * @param {'overwrite'|'ignore'} strategy
 */
export function importMenu(file, strategy) {
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
