#!/usr/bin/env node
/**
 * 粗检 quick-h5 业务页：禁止 Element Plus / el-*；C7 MessageBox 应从 c7-message-box 引入。
 * Usage: node .cursor/skills/c7-h5-packages/scripts/verify-c7-h5-usage.mjs [path...]
 */
import fs from 'node:fs'
import path from 'node:path'

const roots = process.argv.slice(2)
const targets = roots.length > 0 ? roots : ['quick-h5/pages']

const violations = []

function walk(dir) {
  if (!fs.existsSync(dir)) return
  const st = fs.statSync(dir)
  if (st.isFile()) {
    checkFile(dir)
    return
  }
  for (const name of fs.readdirSync(dir)) {
    if (name.startsWith('.')) continue
    walk(path.join(dir, name))
  }
}

function checkFile(file) {
  if (!/\.(uvue|uts|vue|ts|js)$/.test(file)) return
  const text = fs.readFileSync(file, 'utf8')
  const rel = file.replace(/\\/g, '/')
  if (/from\s+['"]element-plus['"]/.test(text) || /from\s+['"]@element-plus\//.test(text)) {
    violations.push(`${rel}: 禁止引入 element-plus`)
  }
  if (/<(el-[a-z0-9-]+)\b/i.test(text)) {
    violations.push(`${rel}: 禁止使用 el-* 标签`)
  }
  if (/ElMessageBox|ElMessage\b|ElLoading/.test(text) && !rel.includes('c7-message-box')) {
    violations.push(`${rel}: 请使用 c7Confirm/c7Loading，勿直接用 ElMessage*`)
  }
}

for (const t of targets) {
  walk(path.resolve(process.cwd(), t))
}

if (violations.length === 0) {
  console.log('verify-c7-h5-usage: 0 违规')
  process.exit(0)
}

console.error(`verify-c7-h5-usage: ${violations.length} 违规`)
for (const v of violations) console.error(' -', v)
process.exit(1)
