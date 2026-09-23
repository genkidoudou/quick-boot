/**
 * 消息接收人目标 JSON 编解码（与后端 RecipientTargetJson 对齐）。
 */

/**
 * @param {string|object|Array} raw
 * @returns {{ userIds: string[], deptIds: string[] }}
 */
export function parseUserTargets(raw) {
  if (raw == null || raw === '') {
    return { userIds: [], deptIds: [] }
  }
  try {
    const data = typeof raw === 'string' ? JSON.parse(raw) : raw
    if (Array.isArray(data)) {
      return { userIds: data.map(String), deptIds: [] }
    }
    return {
      userIds: (data.userIds || []).map(String),
      deptIds: (data.deptIds || []).map(String)
    }
  } catch {
    return { userIds: [], deptIds: [] }
  }
}

/**
 * @param {string|object|Array} raw
 * @returns {{ contactIds: string[], groupIds: string[] }}
 */
export function parseContactTargets(raw) {
  if (raw == null || raw === '') {
    return { contactIds: [], groupIds: [] }
  }
  try {
    const data = typeof raw === 'string' ? JSON.parse(raw) : raw
    if (Array.isArray(data)) {
      return { contactIds: data.map(String), groupIds: [] }
    }
    return {
      contactIds: (data.contactIds || []).map(String),
      groupIds: (data.groupIds || []).map(String)
    }
  } catch {
    return { contactIds: [], groupIds: [] }
  }
}

/**
 * @param {string[]} userIds
 * @param {string[]} deptIds
 */
export function stringifyUserTargets(userIds, deptIds) {
  return JSON.stringify({
    userIds: (userIds || []).map(String),
    deptIds: (deptIds || []).map(String)
  })
}

/**
 * @param {string[]} contactIds
 * @param {string[]} groupIds
 */
export function stringifyContactTargets(contactIds, groupIds) {
  return JSON.stringify({
    contactIds: (contactIds || []).map(String),
    groupIds: (groupIds || []).map(String)
  })
}
