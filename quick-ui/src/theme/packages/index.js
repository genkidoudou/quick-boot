/**
 * 主题包注册表。新增包时在此 import 并加入 themePackages。
 */
import defaultPack from './default'
import qinghengPack from './qingheng'
import supabasePack from './supabase'
import linearPack from './linear'
import stripePack from './stripe'
import notionPack from './notion'
import applePack from './apple'

export const themePackages = [
  defaultPack,
  qinghengPack,
  supabasePack,
  linearPack,
  stripePack,
  notionPack,
  applePack
]

const packMap = Object.fromEntries(themePackages.map((p) => [p.id, p]))

/**
 * @param {string} id
 * @returns {typeof defaultPack}
 */
export function getThemePack(id) {
  return packMap[id] || defaultPack
}

export {
  defaultPack,
  qinghengPack,
  supabasePack,
  linearPack,
  stripePack,
  notionPack,
  applePack
}
