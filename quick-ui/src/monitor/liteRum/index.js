/**
 * Lite RUM 占位：已移除 @quickboot/lite-rum 依赖，保留导出名供 request / main 兼容。
 */

/** @type {null} */
let singleton = null

export function isLiteRumEnabled() {
  return false
}

export function loadLiteRumConfig() {
  return {}
}

/** @returns {null} */
export function setupLiteRum() {
  return null
}

export function getLiteRum() {
  return singleton
}

export function clearSessionId() {}

export function getOrCreateSessionId() {
  return ''
}

export function resetSessionId() {}

export function onSessionContextChange() {
  return () => {}
}

export const LiteRum = null
export const createLiteRum = () => null
export const normalizeConfig = (c) => c
export const SDK_VERSION = '0.0.0-stub'

export default setupLiteRum
