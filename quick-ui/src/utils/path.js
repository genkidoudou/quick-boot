/**
 * 路由 path 规范化（自 ruoyi 拆出）。
 */

/**
 * 规范化路由 path：去除重复斜杠、去掉末尾斜杠。
 * 外链（http/https/mailto/tel）原样返回，避免把 https:// 里的 // 误替换。
 * @param {string} p
 * @returns {string}
 */
export function getNormalPath(p) {
  if (!p || p.length === 0 || p === 'undefined') {
    return p
  }
  if (/^(https?:|mailto:|tel:)/.test(p)) {
    return p
  }
  // 站内 path 无协议：折叠全部重复斜杠（含开头的 //dept）
  let res = String(p).replace(/\/{2,}/g, '/')
  if (res.length > 1 && res[res.length - 1] === '/') {
    return res.slice(0, res.length - 1)
  }
  return res
}
