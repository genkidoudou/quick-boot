#!/usr/bin/env node
/**
 * verify-rice-usage.mjs — 校验页面中 rice-* 组件属性是否在 API 快照内
 *
 *   node scripts/verify-rice-usage.mjs <file-or-dir>
 *   node scripts/verify-rice-usage.mjs <path> --model references/api/model.json
 */

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const SKILL_ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const DEFAULT_MODEL = path.join(SKILL_ROOT, 'references', 'api', 'model.json');

function walk(target, acc = []) {
  const st = fs.statSync(target);
  if (st.isFile()) {
    if (/\.(uvue|vue|uts|ts|js)$/.test(target)) acc.push(target);
    return acc;
  }
  for (const name of fs.readdirSync(target)) {
    if (name === 'node_modules' || name === 'unpackage' || name.startsWith('.')) continue;
    walk(path.join(target, name), acc);
  }
  return acc;
}

function loadPropSet(modelPath) {
  const model = JSON.parse(fs.readFileSync(modelPath, 'utf8'));
  /** @type {Map<string, Set<string>>} */
  const map = new Map();
  // Prefer per-card markdown for fuller props if model only has names
  const apiDir = path.dirname(modelPath);
  for (const c of model.components || []) {
    const props = new Set((c.props || []).map((p) => p.toLowerCase()));
    // also add kebab forms
    for (const p of [...props]) {
      props.add(p.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase());
    }
    // enrich from card file if present
    const card = path.join(apiDir, `${c.name}.md`);
    if (fs.existsSync(card)) {
      const text = fs.readFileSync(card, 'utf8');
      const re = /`([a-zA-Z][\w-]*)`/g;
      let m;
      const inProps = text.split('## 事件')[0] || text;
      while ((m = re.exec(inProps))) {
        props.add(m[1].toLowerCase());
        props.add(m[1].replace(/-([a-z])/g, (_, x) => x.toUpperCase()).toLowerCase());
      }
    }
    // always allow class/style/key/ref/id
    for (const common of ['class', 'style', 'key', 'ref', 'id', 'custom-style', 'customstyle']) {
      props.add(common);
    }
    map.set(c.name, props);
  }
  return map;
}

function camelize(kebab) {
  return kebab.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
}

function checkFile(file, propMap, errors) {
  const text = fs.readFileSync(file, 'utf8');
  const tagRe = /<(rice-[\w-]+)([^>]*)\/?>/g;
  let m;
  while ((m = tagRe.exec(text))) {
    const tag = m[1];
    const attrs = m[2] || '';
    if (!propMap.has(tag)) {
      errors.push({ file, msg: `未知组件 <${tag}>（不在 API 快照中）` });
      continue;
    }
    const allowed = propMap.get(tag);
    const attrRe = /(?:^|\s)(?:v-bind:|:)([\w-]+)|(?:^|\s)([\w-]+)(?:=|\s|$)/g;
    let a;
    while ((a = attrRe.exec(attrs))) {
      const name = (a[1] || a[2] || '').toLowerCase();
      if (!name || name.startsWith('v-') || name.startsWith('@') || name === 'on') continue;
      if (name.startsWith('data-') || name.startsWith('aria-')) continue;
      const ok =
        allowed.has(name) ||
        allowed.has(camelize(name).toLowerCase()) ||
        name.startsWith('on');
      // v-model:show → show
      if (attrs.includes('v-model') && name === 'model') continue;
      if (!ok && !['hover-class', 'hover-stop-propagation'].includes(name)) {
        // soft: only error if snapshot has some props (avoid false positive empty)
        if (allowed.size > 8) {
          errors.push({ file, msg: `<${tag}> 可能未知属性 "${name}"（核对官方文档 / 卡片）` });
        }
      }
    }
  }
  // ban mixing ux-/u- with rice in same file (rough)
  if (/<(rice-[\w-]+)/.test(text) && /<(ux-|u-)[\w-]/.test(text)) {
    errors.push({ file, msg: '禁止在同一文件混用 rice-* 与 u-/ux-* 组件' });
  }
}

function main() {
  const args = process.argv.slice(2);
  let target = '';
  let modelPath = DEFAULT_MODEL;
  for (let i = 0; i < args.length; i++) {
    if (args[i] === '--model') modelPath = path.resolve(args[++i]);
    else if (!args[i].startsWith('-')) target = path.resolve(args[i]);
  }
  if (!target) {
    console.error('Usage: node scripts/verify-rice-usage.mjs <file-or-dir>');
    process.exit(1);
  }
  if (!fs.existsSync(modelPath)) {
    console.error('Missing model.json — run gen-api-cards.mjs first:', modelPath);
    process.exit(1);
  }
  const propMap = loadPropSet(modelPath);
  const files = walk(target);
  const errors = [];
  for (const f of files) checkFile(f, propMap, errors);

  if (!errors.length) {
    console.log(`OK: 0 违规（扫描 ${files.length} 个文件，${propMap.size} 个组件快照）`);
    process.exit(0);
  }
  console.error(`发现 ${errors.length} 条问题：`);
  for (const e of errors) console.error(`- ${e.file}: ${e.msg}`);
  process.exit(1);
}

main();
