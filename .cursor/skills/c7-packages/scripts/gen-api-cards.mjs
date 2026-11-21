#!/usr/bin/env node
/**
 * gen-api-cards.mjs — 从 quick-ui/src/packages 生成 C7 API 卡片（Node ≥ 18）
 *
 *   node scripts/gen-api-cards.mjs --lib <quick-ui/src/packages>
 *   node scripts/gen-api-cards.mjs --lib <path> --only C7JsonTable
 */

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const SKILL_ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const OUT_DIR = path.join(SKILL_ROOT, 'references', 'api');

const GROUPS = [
  ['列表与弹窗', ['C7JsonTable', 'C7JsonTableColumn', 'C7FormDialog', 'C7Dialog', 'C7Pagination', 'C7CardGrid']],
  ['表单控件', ['C7Select', 'C7Radio', 'C7Checkbox', 'C7Switch', 'C7DatePicker', 'C7TimePicker', 'C7TreeSelect', 'C7Cascader', 'C7FormLabel']],
  ['展示', ['C7DictTag', 'C7Descriptions', 'C7Title', 'C7Copy', 'C7Preview', 'C7Watermark', 'C7Card']],
  ['上传导出', ['C7Upload', 'C7ExcelUpload', 'C7ExcelDownload']],
  ['按钮', ['C7Button', 'C7ButtonGroup']],
  ['函数式 API', ['C7MessageBox']],
];

function parseArgs(argv) {
  const out = { lib: '', only: '' };
  for (let i = 2; i < argv.length; i++) {
    if (argv[i] === '--lib') out.lib = argv[++i];
    else if (argv[i] === '--only') out.only = argv[++i];
  }
  return out;
}

function read(p) {
  try { return fs.readFileSync(p, 'utf8'); } catch { return ''; }
}

function extractBlockCommentBefore(src, index) {
  const head = src.slice(Math.max(0, index - 800), index);
  const m = head.match(/\/\*\*([\s\S]*?)\*\/\s*$/);
  return m ? m[1].replace(/^\s*\*\s?/gm, '').trim() : '';
}

function parseDefinePropsObject(src) {
  const props = [];
  const start = src.search(/defineProps\s*\(\s*\{/);
  if (start < 0) return props;
  let i = src.indexOf('{', start);
  let depth = 0;
  let end = -1;
  for (; i < src.length; i++) {
    const ch = src[i];
    if (ch === '{') depth++;
    else if (ch === '}') {
      depth--;
      if (depth === 0) { end = i; break; }
    }
  }
  if (end < 0) return props;
  const body = src.slice(src.indexOf('{', start) + 1, end);
  // 按「name: { ... }」扫描，配置体允许嵌套括号
  let pos = 0;
  while (pos < body.length) {
    const docM = body.slice(pos).match(/^\s*\/\*\*([\s\S]*?)\*\//);
    let jsdoc = '';
    if (docM) {
      jsdoc = docM[1].replace(/^\s*\*\s?/gm, '').trim();
      pos += docM[0].length;
    }
    const nameM = body.slice(pos).match(/^\s*([A-Za-z_][\w]*)\s*:\s*\{/);
    if (!nameM) {
      pos += 1;
      continue;
    }
    const name = nameM[1];
    pos += nameM[0].length;
    let nest = 1;
    const confStart = pos;
    while (pos < body.length && nest > 0) {
      const ch = body[pos++];
      if (ch === '{') nest++;
      else if (ch === '}') nest--;
      else if (ch === '(') {
        // skip balanced () for default: () => ...
        let pd = 1;
        while (pos < body.length && pd > 0) {
          const c2 = body[pos++];
          if (c2 === '(') pd++;
          else if (c2 === ')') pd--;
        }
      } else if (ch === '[') {
        let bd = 1;
        while (pos < body.length && bd > 0) {
          const c2 = body[pos++];
          if (c2 === '[') bd++;
          else if (c2 === ']') bd--;
        }
      } else if (ch === "'" || ch === '"' || ch === '`') {
        const q = ch;
        while (pos < body.length) {
          const c2 = body[pos++];
          if (c2 === '\\') { pos++; continue; }
          if (c2 === q) break;
        }
      }
    }
    const conf = body.slice(confStart, pos - 1);
    const typeM = conf.match(/type\s*:\s*(\[[^\]]*\]|[^,\n]+)/);
    const reqM = /required\s*:\s*true/.test(conf);
    let defaultValue = '-';
    const defIdx = conf.search(/default\s*:/);
    if (defIdx >= 0) {
      let j = conf.indexOf(':', defIdx) + 1;
      while (j < conf.length && /\s/.test(conf[j])) j++;
      const s0 = j;
      // () => ... 箭头函数：吃掉完整表达式
      if (conf.slice(j).startsWith('()')) {
        j += 2;
        while (j < conf.length && /\s/.test(conf[j])) j++;
        if (conf.slice(j, j + 2) === '=>') {
          j += 2;
          while (j < conf.length && /\s/.test(conf[j])) j++;
          const ch = conf[j];
          if (ch === '{' || ch === '[' || ch === '(') {
            const open = ch;
            const close = open === '{' ? '}' : open === '[' ? ']' : ')';
            let d = 1;
            j++;
            while (j < conf.length && d > 0) {
              const c = conf[j++];
              if (c === open) d++;
              else if (c === close) d--;
            }
          } else {
            while (j < conf.length && conf[j] !== ',' && conf[j] !== '\n') j++;
          }
          defaultValue = conf.slice(s0, j).trim();
        } else {
          defaultValue = '()';
        }
      } else if (conf[j] === '(' || conf[j] === '[' || conf[j] === '{') {
        const open = conf[j];
        const close = open === '(' ? ')' : open === '[' ? ']' : '}';
        let d = 1;
        j++;
        while (j < conf.length && d > 0) {
          const c = conf[j++];
          if (c === open) d++;
          else if (c === close) d--;
        }
        defaultValue = conf.slice(s0, j).trim();
      } else {
        const dm = conf.slice(j).match(/^([^,\n]+)/);
        defaultValue = dm ? dm[1].trim() : '-';
      }
    } else if (reqM) {
      defaultValue = '（必填）';
    }
    props.push({
      name,
      type: typeM ? typeM[1].trim() : 'unknown',
      required: reqM,
      defaultValue,
      desc: jsdoc.split('\n').map((l) => l.trim()).filter((l) => l && !l.startsWith('@'))[0] || '',
    });
  }
  return props;
}

function parseEmits(src) {
  const events = [];
  const m = src.match(/defineEmits\(\[([\s\S]*?)\]\)/);
  if (m) {
    for (const part of m[1].split(',')) {
      const s = part.replace(/['"`\s]/g, '');
      if (s) events.push({ name: s, desc: '' });
    }
  }
  // @emits in file header
  const emitDoc = [...src.matchAll(/@emits\s+([\w-]+)\s+([^\n*]*)/g)];
  for (const e of emitDoc) {
    const name = e[1];
    const desc = e[2].trim();
    const hit = events.find((x) => x.name === name);
    if (hit) hit.desc = desc;
    else events.push({ name, desc });
  }
  return events;
}

function parseModels(src) {
  const models = [];
  const re = /defineModel(?:\s*<[^>]*>)?\s*(?:\(\s*(?:['"]([\w]+)['"]\s*,)?\s*\{([\s\S]*?)\}\s*\)|\(\s*\))/g;
  let m;
  while ((m = re.exec(src))) {
    models.push({ name: m[1] || 'modelValue', conf: (m[2] || '').trim() });
  }
  // defineModel() without args
  if (/defineModel\s*\(\s*\)/.test(src) && !models.length) {
    models.push({ name: 'modelValue', conf: '' });
  }
  if (/defineModel\s*\(\s*\{/.test(src) && !models.some((x) => x.name === 'modelValue')) {
    const dm = src.match(/const\s+\w+\s*=\s*defineModel\s*\(\s*\{([\s\S]*?)\}\s*\)/);
    if (dm) models.push({ name: 'modelValue', conf: dm[1].trim() });
  }
  return models;
}

function parseExpose(src) {
  const m = src.match(/defineExpose\(\{([\s\S]*?)\}\)/);
  if (!m) return [];
  return m[1].split(',')
    .map((s) => s.trim())
    .map((s) => s.split(':')[0].trim())
    .filter((s) => /^[A-Za-z_][\w]*$/.test(s));
}

function parseSlots(src) {
  const slots = new Set();
  const re = /#([\w-]+)|name=["']([\w-]+)["']/g;
  let m;
  while ((m = re.exec(src))) {
    const n = m[1] || m[2];
    if (n && n !== 'scoped') slots.add(n);
  }
  return [...slots];
}

function parseMessageBoxExports(js) {
  const props = []; // treat exported functions as "API"
  const re = /export\s+function\s+([A-Za-z_][\w]*)\s*\(/g;
  let m;
  while ((m = re.exec(js))) {
    const name = m[1];
    const desc = extractBlockCommentBefore(js, m.index).split('\n')[0] || '';
    props.push({ name, type: 'function', required: false, defaultValue: '-', desc });
  }
  return props;
}

function fileSummary(src, name) {
  const deprecated = /@deprecated/.test(src);
  const head = src.match(/\/\*\*\s*([\s\S]*?)\*\//);
  let summary = '';
  if (head) {
    summary = head[1].replace(/^\s*\*\s?/gm, '').trim().split('\n').filter((l) => !l.startsWith('@'))[0] || '';
  }
  if (deprecated) summary = `**【已 deprecated】** ${summary}`;
  if (!summary) summary = `${name} 组件（详见源码 JSDoc）`;
  return summary;
}

function renderCard(model) {
  const lines = [];
  lines.push('<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->');
  lines.push('');
  lines.push(`# ${model.name} API`);
  lines.push('');
  lines.push(`> 源码：\`packages/${model.name}/\``);
  lines.push(`> 标签：\`<${model.name}>\` / \`<${kebab(model.name)}>\``);
  lines.push('');
  lines.push(model.summary);
  lines.push('');
  if (model.models.length) {
    lines.push('## 双向绑定');
    lines.push('');
    lines.push('| 名称 | 说明 |');
    lines.push('| --- | --- |');
    for (const m of model.models) {
      lines.push(`| \`v-model${m.name === 'modelValue' ? '' : ':' + m.name}\` | ${(m.conf || '').replace(/\|/g, '｜') || '默认 model'} |`);
    }
    lines.push('');
  }
  lines.push(model.kind === 'api' ? '## 导出函数' : '## 属性');
  lines.push('');
  lines.push('| 名称 | 说明 | 类型 | 默认/必填 |');
  lines.push('| --- | --- | --- | --- |');
  if (!model.props.length) {
    lines.push('| _（未解析到 props，请读源码）_ | | | |');
  } else {
    for (const p of model.props) {
      const label = model.kind === 'api' ? `\`${p.name}\`` : `\`${kebab(p.name)}\` / \`${p.name}\``;
      lines.push(`| ${label} | ${(p.desc || '').replace(/\|/g, '｜')} | \`${String(p.type).replace(/\|/g, '｜').replace(/`/g, '')}\` | ${(p.defaultValue || '-').replace(/\|/g, '｜')} |`);
    }
  }
  lines.push('');
  if (model.kind !== 'api') {
    lines.push('## 事件');
    lines.push('');
    lines.push('| 事件 | 说明 |');
    lines.push('| --- | --- |');
    if (!model.events.length) lines.push('| _无或未解析_ | |');
    else for (const e of model.events) lines.push(`| \`@${e.name}\` | ${(e.desc || '').replace(/\|/g, '｜')} |`);
    lines.push('');
    lines.push('## 插槽');
    lines.push('');
    lines.push('| 插槽 |');
    lines.push('| --- |');
    if (!model.slots.length) lines.push('| `default` |');
    else for (const s of model.slots) lines.push(`| \`${s}\` |`);
    lines.push('');
    if (model.expose.length) {
      lines.push('## 暴露方法');
      lines.push('');
      lines.push(model.expose.map((x) => `\`${x}\``).join('、'));
      lines.push('');
    }
  }
  return lines.join('\n');
}

function kebab(name) {
  return name.replace(/([a-z0-9])([A-Z])/g, '$1-$2').replace(/^C7-/, 'c7-').toLowerCase().replace(/^c7/, 'c7');
}

function listDirs(lib) {
  return fs.readdirSync(lib, { withFileTypes: true })
    .filter((d) => d.isDirectory() && d.name.startsWith('C7'))
    .map((d) => d.name)
    .sort();
}

function main() {
  const args = parseArgs(process.argv);
  if (!args.lib) {
    console.error('Usage: node scripts/gen-api-cards.mjs --lib <quick-ui/src/packages>');
    process.exit(1);
  }
  const lib = path.resolve(args.lib);
  fs.mkdirSync(OUT_DIR, { recursive: true });
  let names = listDirs(lib);
  if (args.only) names = names.filter((n) => n === args.only || n === `C7${args.only}`);

  const models = [];
  for (const name of names) {
    const dir = path.join(lib, name);
    const vue = read(path.join(dir, 'index.vue'));
    const js = read(path.join(dir, 'index.js'));
    const src = vue || js;
    if (!src) {
      console.warn('skip empty', name);
      continue;
    }
    const kind = name === 'C7MessageBox' || (!vue && js) ? 'api' : 'component';
    const props = kind === 'api' ? parseMessageBoxExports(js || src) : parseDefinePropsObject(src);
    const model = {
      name,
      kind,
      summary: fileSummary(src, name),
      props,
      events: kind === 'api' ? [] : parseEmits(src),
      models: kind === 'api' ? [] : parseModels(src),
      slots: kind === 'api' ? [] : parseSlots(src),
      expose: kind === 'api' ? [] : parseExpose(src),
    };
    models.push(model);
    fs.writeFileSync(path.join(OUT_DIR, `${name}.md`), renderCard(model), 'utf8');
  }

  const modelJson = {
    generatedAt: new Date().toISOString(),
    lib,
    components: models.map((m) => ({
      name: m.name,
      kind: m.kind,
      props: m.props.map((p) => p.name),
      events: m.events.map((e) => e.name),
      expose: m.expose,
    })),
  };
  fs.writeFileSync(path.join(OUT_DIR, 'model.json'), JSON.stringify(modelJson, null, 2), 'utf8');

  const idx = ['# C7 packages API 卡片索引', '', `共 **${models.length}** 项。源码：\`quick-ui/src/packages\`。`, '', '```bash', 'node .cursor/skills/c7-packages/scripts/gen-api-cards.mjs --lib quick-ui/src/packages', '```', ''];
  for (const [g, list] of GROUPS) {
    idx.push(`## ${g}`, '');
    for (const n of list) {
      if (!models.find((m) => m.name === n)) continue;
      idx.push(`- [${n}](./${n}.md)`);
    }
    idx.push('');
  }
  const known = new Set(GROUPS.flatMap((g) => g[1]));
  const rest = models.filter((m) => !known.has(m.name));
  if (rest.length) {
    idx.push('## 未分组', '');
    for (const m of rest) idx.push(`- [${m.name}](./${m.name}.md)`);
    idx.push('');
  }
  fs.writeFileSync(path.join(OUT_DIR, 'README.md'), idx.join('\n'), 'utf8');
  console.log(`Wrote ${models.length} cards → ${OUT_DIR}`);
}

main();
