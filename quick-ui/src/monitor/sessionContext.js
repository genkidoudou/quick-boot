/**
 * 会话 ID 占位：原委托 @quickboot/lite-rum，现为无操作兼容导出。
 */

export function getOrCreateSessionId() {
  return ''
}

export function resetSessionId() {}

export function clearSessionId() {}

export function onSessionContextChange() {
  return () => {}
}

export function resetSessionContextForTest() {}

export default {
  getOrCreateSessionId,
  resetSessionId,
  clearSessionId,
  onSessionContextChange,
  resetSessionContextForTest
}
