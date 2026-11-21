#!/usr/bin/env node
/**
 * 静态检查 quick-h5/pages/examples 与相关 C7 组件的已知错误用法。
 * Usage: node .cursor/skills/c7-h5-packages/scripts/check-examples.mjs
 */
import fs from 'node:fs'
import path from 'node:path'

const root = path.resolve(process.cwd())
const issues = []

function read(rel) {
  return fs.readFileSync(path.join(root, rel), 'utf8')
}

function fail(file, msg) {
  issues.push(`${file}: ${msg}`)
}

const form = read('quick-h5/pages/examples/form.uvue')
if (/checkVal\s*=\s*ref\("1"\)/.test(form)) {
  fail('pages/examples/form.uvue', 'C7Checkbox 默认 joinValue=false，v-model 应为 string[]')
}
if (/c7-form-label[^>]*class="mt"/.test(form) || /c7-date-picker[^>]*class="mt"/.test(form) || /c7-tree-select[^>]*class="mt"/.test(form)) {
  fail('pages/examples/form.uvue', '自定义组件上勿直接挂 class="mt"，应包一层 view')
}

const feedback = read('quick-h5/pages/examples/feedback.uvue')
if (/formDlg\.value/.test(feedback) || /ref="formDlg"/.test(feedback)) {
  fail('pages/examples/feedback.uvue', 'FormDialog 打开勿依赖 expose.open，改用 getPayload + formShow')
}

const upload = read('quick-h5/components/c7-upload/c7-upload.uvue')
if (/:file-list=/.test(upload)) {
  fail('components/c7-upload', 'rice-uploader 文件列表是 v-model，不是 :file-list')
}
if (/@afterRead=/.test(upload) || /@delete=/.test(upload)) {
  fail('components/c7-upload', '应用 after-read prop 与 @remove')
}
if (!/v-model="fileList"/.test(upload) && !/v-model='fileList'/.test(upload)) {
  fail('components/c7-upload', '缺少 v-model="fileList"')
}

const btn = read('quick-h5/components/c7-button/c7-button.uvue')
if (/['"]normal['"]/.test(btn)) {
  fail('components/c7-button', "勿使用非法 size 'normal'")
}

const cascader = read('quick-h5/components/c7-cascader/c7-cascader.uvue')
if (/<rice-popup/.test(cascader) && /rice-cascader/.test(cascader)) {
  fail('components/c7-cascader', '勿双重弹层：外层 rice-popup + 内层 cascader')
}

const select = read('quick-h5/components/c7-select/c7-select.uvue')
if (!/c7-select__field[^>]*@(click|tap)="onOpen"/.test(select)) {
  fail('components/c7-select', '打开动作应绑在 field 上（@tap/@click）')
}
if (/<rice-picker/.test(select) && !/<rice-popup/.test(select)) {
  // 允许 popup 列表实现；不再强制 rice-picker
}

if (issues.length === 0) {
  console.log('check-examples: 0 问题')
  process.exit(0)
}
console.error(`check-examples: ${issues.length} 问题`)
for (const i of issues) console.error(' -', i)
process.exit(1)
