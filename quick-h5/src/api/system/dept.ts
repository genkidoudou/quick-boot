/**
 * 系统部门管理 API，对接 /sys/dept 系列接口。
 */
import { request } from '../http'
import type { PageInfo } from '../types'

/** 部门实体（含树形 children） */
export type SysDept = {
  deptId?: number | string
  parentId?: number | string | null
  deptName?: string
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status?: string
  remark?: string
  children?: SysDept[]
}

/** treeselect 节点常见形态 */
export type DeptTreeNode = {
  deptId?: number | string
  id?: number | string
  deptName?: string
  label?: string
  children?: DeptTreeNode[]
}

/**
 * 条件列表查询部门树（POST /page，取 records）。
 * size 取较大值：后端树形列表非物理分页。
 */
export async function listDept(query?: { deptName?: string; status?: string }) {
  const page = await request<PageInfo<SysDept>>({
    url: '/sys/dept/page',
    method: 'POST',
    data: {
      current: 1,
      size: 1000,
      param: query || {},
    },
  })
  return page?.records || []
}

/** 下拉树：treeselect 接口，节点含 label/id 等前端友好字段 */
export function treeselectDept() {
  return request<DeptTreeNode[]>({
    url: '/sys/dept/treeselect',
    method: 'GET',
  })
}

/** 按 deptId 查询部门详情 */
export function getDept(deptId: number | string) {
  return request<SysDept>({
    url: `/sys/dept/${encodeURIComponent(String(deptId))}`,
    method: 'GET',
  })
}

/** 新增部门 */
export function addDept(data: Partial<SysDept>) {
  return request<string | number>({
    url: '/sys/dept/add',
    method: 'POST',
    data,
  })
}

/** 更新部门 */
export function updateDept(data: Partial<SysDept>) {
  return request<boolean>({
    url: '/sys/dept/update',
    method: 'POST',
    data,
  })
}

/** 删除部门 */
export function delDept(deptId: number | string) {
  return request<void>({
    url: `/sys/dept/remove/${encodeURIComponent(String(deptId))}`,
    method: 'GET',
  })
}
