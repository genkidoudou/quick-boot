#!/usr/bin/env node
/**
 * verify-c7-usage.mjs — 校验页面中 C7* / c7-* 用法
 *
 *   node scripts/verify-c7-usage.mjs <file-or-dir>
 */

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const SKILL_ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const DEFAULT_MODEL = path.join(SKILL_ROOT, 'references', 'api', 'model.json');

function walk(target, acc = []) {
  const st = fs.statSync(target);
  if (st.isFile()) {
    if (/\.(vue|js|ts)$/.test(target)) acc.push(target);
    return acc;
  }
  for (const name of fs.readdirSync(target)) {
    if (['node_modules', 'dist', 'unpackage'].includes(name) || name.startsWith('.')) continue;
    walk(path.join(target, name), acc);
  }
  return acc;
}

function toPascalTag(tag) {
  // c7-json-table -> C7JsonTable ; C7JsonTable stays
  if (tag.startsWith('C7')) return tag;
  const parts = tag.split('-'); // c7, json, table
  if (parts[0] !== 'c7') return tag;
  return 'C7' + parts.slice(1).map((p) => p.charAt(0).toUpperCase() + p.slice(1)).join('');
}

function kebab(name) {
  return name.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase();
}

function loadPropMap(modelPath) {
  const model = JSON.parse(fs.readFileSync(modelPath, 'utf8'));
  const map = new Map();
  const apiDir = path.dirname(modelPath);
  for (const c of model.components || []) {
    if (c.kind === 'api') continue;
    const props = new Set((c.props || []).map((p) => p.toLowerCase()));
    for (const p of [...props]) {
      props.add(kebab(p).toLowerCase());
    }
    const card = path.join(apiDir, `${c.name}.md`);
    if (fs.existsSync(card)) {
      const text = fs.readFileSync(card, 'utf8');
      const section = text.split('## 事件')[0] || text;
      const re = /`([a-zA-Z][\w-]*)`/g;
      let m;
      while ((m = re.exec(section))) {
        props.add(m[1].toLowerCase());
      }
    }
    // 公共 + 常见 EP 透传（多数 C7 控件 inheritAttrs / v-bind 转发）
    for (const common of [
      'class', 'style', 'key', 'ref', 'id', 'title', 'width', 'height', 'placeholder',
      'clearable', 'filterable', 'disabled', 'multiple', 'size', 'readonly',
      'mobile-mode', 'mobilemode', 'destroy-on-close', 'destroyonclose',
      'append-to-body', 'appendtobody', 'check-strictly', 'checkstrictly',
      'collapse-tags', 'collapsetags', 'remote', 'loading', 'teleported',
    ]) {
      props.add(common);
    }
    map.set(c.name, props);
  }
  return map;
}

function checkFile(file, propMap, errors) {
  const text = fs.readFileSync(file, 'utf8');
  // skip packages source itself for unknown-component noise? still validate attrs
  const tagRe = /<\/?((?:C7|c7-)[\w-]*)\b([^>]*)/g;
  let m;
  const seenC7 = /<(?:C7|c7-)[\w-]*/.test(text);
  while ((m = tagRe.exec(text))) {
    if (m[0].startsWith('</')) continue;
    const rawTag = m[1];
    const pascal = toPascalTag(rawTag);
    const attrs = m[2] || '';
    if (!propMap.has(pascal)) {
      // allow if not in model (shouldn't happen)
      continue;
    }
    const allowed = propMap.get(pascal);
    const attrRe = /(?:^|\s)(?:v-bind:|:)([\w-]+)|(?:^|\s)([\w-]+)(?:=|\s|\/|$)/g;
    let a;
    while ((a = attrRe.exec(attrs))) {
      let name = (a[1] || a[2] || '').toLowerCase();
      if (!name || name.startsWith('v-') || name.startsWith('@') || name === '/') continue;
      if (name.startsWith('on')) continue;
      if (['class', 'style', 'key', 'ref', 'id'].includes(name)) continue;
      if (allowed.size > 5 && !allowed.has(name) && !allowed.has(name.replace(/-/g, ''))) {
        // camelCase check
        const camel = name.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
        if (!allowed.has(camel.toLowerCase())) {
          errors.push({ file, level: 'warn', msg: `<${rawTag}> 可能未知属性 "${name}"（核对 api/${pascal}.md）` });
        }
      }
    }
  }

  // soft convention: C7JsonTable without list-function
  if (/<C7JsonTable|<c7-json-table/.test(text) && !/list-function|listFunction|:list-function/.test(text)) {
    errors.push({ file, level: 'error', msg: 'C7JsonTable 缺少 list-function' });
  }
  // C7FormDialog should have get-payload + submit for CRUD
  if (/<C7FormDialog|<c7-form-dialog/.test(text)) {
    if (!/get-payload|getPayload|:get-payload/.test(text)) {
      errors.push({ file, level: 'error', msg: 'C7FormDialog 缺少 get-payload' });
    }
    if (!/:submit|submit=/.test(text)) {
      errors.push({ file, level: 'error', msg: 'C7FormDialog 缺少 submit' });
    }
  }
  // deprecate soft warn if using C7Button in views (not packages)
  if (seenC7 && /views[/\\]/.test(file) && /<C7Button|<c7-button[\s>]/.test(text) && !/C7ButtonGroup|c7-button-group/.test(text)) {
    errors.push({ file, level: 'warn', msg: '新页优先 ElButton；C7Button 已 deprecated' });
  }
}

function main() {
  const args = process.argv.slice(2);
  let target = '';
  let modelPath = DEFAULT_MODEL;
  let strict = false;
  for (let i = 0; i < args.length; i++) {
    if (args[i] === '--model') modelPath = path.resolve(args[++i]);
    else if (args[i] === '--strict') strict = true;
    else if (!args[i].startsWith('-')) target = path.resolve(args[i]);
  }
  if (!target) {
    console.error('Usage: node scripts/verify-c7-usage.mjs <file-or-dir> [--strict]');
    process.exit(1);
  }
  if (!fs.existsSync(modelPath)) {
    console.error('Missing model.json，先跑 gen-api-cards.mjs');
    process.exit(1);
  }
  const propMap = loadPropMap(modelPath);
  const files = walk(target);
  const errors = [];
  for (const f of files) checkFile(f, propMap, errors);

  const hard = errors.filter((e) => e.level === 'error');
  const soft = errors.filter((e) => e.level === 'warn');
  for (const e of soft) console.warn(`warn: ${e.file}: ${e.msg}`);
  for (const e of hard) console.error(`error: ${e.file}: ${e.msg}`);

  if (hard.length || (strict && soft.length)) {
    console.error(`失败：error=${hard.length} warn=${soft.length}`);
    process.exit(1);
  }
  console.log(`OK: 0 违规（扫描 ${files.length} 文件；warn=${soft.length} 不阻断）`);
}

main();
