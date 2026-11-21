#!/usr/bin/env node
/**
 * gen-api-cards.mjs — RiceUI API 卡片生成器（Node ≥ 18，零依赖）
 *
 * 数据源：
 *   1. 本地 uni_modules/rice-ui 各组件 type.uts + .uvue（props / emits / slots）
 *   2. 官方文档 https://riceui.cn/components/<slug>.html（Props/Events 表，可 --offline 跳过）
 *
 * 用法：
 *   node scripts/gen-api-cards.mjs --lib <rice-ui路径>
 *   node scripts/gen-api-cards.mjs --lib <path> --offline
 *   node scripts/gen-api-cards.mjs --lib <path> --only button
 */

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const SKILL_ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const OUT_DIR = path.join(SKILL_ROOT, 'references', 'api');
const DOC_BASE = 'https://riceui.cn/components';

const GROUPS = [
  ['基础', ['rice-button', 'rice-icon', 'rice-image', 'rice-row', 'rice-col']],
  ['表单', [
    'rice-form', 'rice-form-item', 'rice-input', 'rice-textarea', 'rice-search',
    'rice-switch', 'rice-checkbox', 'rice-checkbox-group', 'rice-radio', 'rice-radio-group',
    'rice-stepper', 'rice-slider', 'rice-rate', 'rice-code-input', 'rice-uploader',
    'rice-picker', 'rice-datetime-picker', 'rice-calendar', 'rice-cascader',
    'rice-color-picker', 'rice-signature'
  ]],
  ['展示', [
    'rice-cell', 'rice-cell-group', 'rice-tag', 'rice-badge', 'rice-avatar', 'rice-divider',
    'rice-collapse', 'rice-collapse-item', 'rice-grid', 'rice-grid-item',
    'rice-progress', 'rice-progress-circle', 'rice-count-to', 'rice-count-down',
    'rice-scroll-x', 'rice-time-format', 'rice-qrcode', 'rice-waterflow', 'rice-waterflow-item'
  ]],
  ['反馈', [
    'rice-loading', 'rice-overlay', 'rice-popup', 'rice-dialog', 'rice-action-sheet',
    'rice-notice-bar', 'rice-swipe-actions', 'rice-swipe-actions-item',
    'rice-float-fab', 'rice-float-panel'
  ]],
  ['导航', ['rice-navbar', 'rice-tabs', 'rice-subsection', 'rice-back-top']],
  ['其他', ['rice-ui']],
];

/** 无独立文档页或 slug 特殊映射 */
const SLUG_MAP = {
  'rice-row': 'layout',
  'rice-col': 'layout',
  'rice-cell-group': 'cell',
  'rice-collapse-item': 'collapse',
  'rice-grid-item': 'grid',
  'rice-form-item': 'form',
  'rice-checkbox-group': 'checkbox',
  'rice-radio-group': 'radio',
  'rice-swipe-actions-item': 'swipe-actions',
  'rice-waterflow-item': 'waterflow',
  'rice-ui': 'quickstart',
};

function parseArgs(argv) {
  const out = { lib: '', offline: false, only: '' };
  for (let i = 2; i < argv.length; i++) {
    const a = argv[i];
    if (a === '--lib') out.lib = argv[++i];
    else if (a === '--offline') out.offline = true;
    else if (a === '--only') out.only = argv[++i];
  }
  return out;
}

function kebabToSlug(compName) {
  if (SLUG_MAP[compName]) return SLUG_MAP[compName];
  return compName.replace(/^rice-/, '');
}

function readText(p) {
  try { return fs.readFileSync(p, 'utf8'); } catch { return ''; }
}

/** 从 type.uts 抽取 Props 类型字段 */
function parsePropsFromType(uts) {
  const props = [];
  // export type XxxProps = { ... }
  const m = uts.match(/export\s+type\s+\w*Props\s*=\s*\{([\s\S]*?)\n\}/);
  if (!m) return props;
  const body = m[1];
  const lineRe = /^\s*([A-Za-z_][\w]*)\s*\??\s*:\s*([^,;\n]+)/gm;
  let hit;
  while ((hit = lineRe.exec(body))) {
    const name = hit[1];
    let type = hit[2].trim().replace(/\s+/g, ' ');
    if (type.endsWith(',')) type = type.slice(0, -1).trim();
    props.push({ name, type, desc: '', defaultValue: '-' });
  }
  return props;
}

function parseEmitsFromUvue(uvue) {
  const events = [];
  const m = uvue.match(/defineEmits\(\[([^\]]*)\]\)/) ||
    uvue.match(/defineEmits<\s*\{([\s\S]*?)\}\s*>\s*\(/);
  if (!m) {
    // defineEmits(['click', ...])
    const m2 = uvue.match(/defineEmits\(\[([\s\S]*?)\]\)/);
    if (m2) {
      for (const part of m2[1].split(',')) {
        const s = part.replace(/['"`\s]/g, '');
        if (s) events.push({ name: s, desc: '', params: '-' });
      }
    }
    return events;
  }
  if (m[0].includes('defineEmits([')) {
    for (const part of m[1].split(',')) {
      const s = part.replace(/['"`\s]/g, '');
      if (s) events.push({ name: s, desc: '', params: '-' });
    }
  }
  return events;
}

function parseSlotsFromUvue(uvue) {
  const slots = new Set();
  const re = /#(default|[\w-]+)|name=["']([\w-]+)["']/g;
  let hit;
  while ((hit = re.exec(uvue))) {
    const n = hit[1] || hit[2];
    if (n && n !== 'scoped') slots.add(n);
  }
  // template #xxx
  const re2 = /<template\s+#([\w-]+)/g;
  while ((hit = re2.exec(uvue))) slots.add(hit[1]);
  return [...slots].map((name) => ({ name, desc: name === 'default' ? '默认插槽' : '' }));
}

function stripHtml(html) {
  return html
    .replace(/<script[\s\S]*?<\/script>/gi, '')
    .replace(/<style[\s\S]*?<\/style>/gi, '')
    .replace(/<\/?[^>]+>/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&amp;/g, '&')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/\s+/g, ' ')
    .trim();
}

/** 粗解析官方文档 markdown/html 中的 Props 表 */
function parseDocTables(mdLike) {
  const text = mdLike;
  const props = [];
  const events = [];
  // | name | type | desc | default |  or | 参数 | 说明 | 类型 | 默认值 |
  const lines = text.split('\n');
  let mode = '';
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trim();
    if (/^#+/.test(line) || /^\*\*Props/i.test(line) || /###?\s*Props/i.test(line)) {
      if (/props/i.test(line)) mode = 'props';
      else if (/event/i.test(line)) mode = 'events';
      else if (/slot/i.test(line)) mode = 'slots';
      continue;
    }
    if (!line.startsWith('|')) continue;
    if (/^\|\s*-+/.test(line) || /参数|属性名|说明|类型|默认/.test(line) && /\|/.test(line)) {
      // header — detect column order
      continue;
    }
    const cells = line.split('|').map((c) => c.trim()).filter((_, idx, arr) => idx > 0 && idx < arr.length - 1 || (arr[0] === '' && idx > 0 && idx < arr.length - 1));
    const parts = line.replace(/^\|/, '').replace(/\|$/, '').split('|').map((c) => c.trim());
    if (parts.length < 3) continue;
    if (parts.some((p) => /^-+$/.test(p))) continue;
    if (/^(参数|属性名|事件名|名称|说明)$/.test(parts[0])) continue;

    if (mode === 'props' || (!mode && parts.length >= 3)) {
      // try: name, desc, type, default OR name, type, desc, default
      const name = parts[0].replace(/`/g, '').split('/')[0].replace(/^v-model:/, '').trim();
      if (!name || name.length > 48) continue;
      if (/^(事件|插槽|类型)/.test(name)) continue;
      let desc = '', type = '', def = '-';
      if (parts.length >= 4) {
        // 参数 | 说明 | 类型 | 默认值  OR 属性名 | 类型 | 说明 | 默认值
        if (/string|number|boolean|object|function|array/i.test(parts[1]) && !/string|number|boolean/i.test(parts[2])) {
          type = parts[1]; desc = parts[2]; def = parts[3] || '-';
        } else {
          desc = parts[1]; type = parts[2]; def = parts[3] || '-';
        }
      } else {
        desc = parts[1]; type = parts[2] || '';
      }
      if (mode === 'events' || /^@|click|confirm|cancel|change|open|close/i.test(name) && mode === 'events') {
        events.push({ name: name.replace(/^@/, ''), desc, params: type || '-' });
      } else if (mode !== 'events' && mode !== 'slots') {
        props.push({
          name: name.replace(/`/g, ''),
          type: type.replace(/`/g, ''),
          desc: desc.replace(/`/g, ''),
          defaultValue: (def || '-').replace(/`/g, ''),
        });
      }
    }
    if (mode === 'events') {
      const name = parts[0].replace(/`/g, '').replace(/^@/, '');
      events.push({ name, desc: parts[1] || '', params: parts[2] || '-' });
    }
  }
  return { props, events };
}

async function fetchDoc(slug) {
  const url = `${DOC_BASE}/${slug}.html`;
  try {
    const res = await fetch(url, { signal: AbortSignal.timeout(20000) });
    if (!res.ok) return { url, ok: false, raw: '', props: [], events: [] };
    const raw = await res.text();
    // VitePress often embeds content in JSON or plain HTML; also try markdown-ish tables in HTML
    const decoded = raw
      .replace(/&quot;/g, '"')
      .replace(/&lt;/g, '<')
      .replace(/&gt;/g, '>');
    // Extract visible markdown tables if present in page payload
    let md = '';
    const mdMatch = decoded.match(/# [\s\S]{200,80000}/);
    if (mdMatch) md = stripHtml(mdMatch[0]).replace(/\|/g, ' | ');
    // Better: pull pipe tables from HTML text content
    const tableChunks = [];
    const pipeRe = /(\|[^\n]+\|\n)(\|[-:| ]+\|\n)((\|[^\n]+\|\n?)+)/g;
    const plain = decoded.replace(/<br\s*\/?>/gi, '\n');
    // Convert HTML tables to markdown-ish；跳过平台兼容性表
    const htmlTables = plain.match(/<table[\s\S]*?<\/table>/gi) || [];
    for (const t of htmlTables) {
      const rows = [...t.matchAll(/<tr[\s\S]*?<\/tr>/gi)].map((r) => {
        const cells = [...r[0].matchAll(/<t[hd][^>]*>([\s\S]*?)<\/t[hd]>/gi)].map((c) =>
          stripHtml(c[1]).replace(/\|/g, '｜') // 全角，避免二次按 | 分列时拆断 string|number
        );
        return cells.length ? `| ${cells.join(' | ')} |` : '';
      }).filter(Boolean);
      if (!rows.length) continue;
      const head = rows[0] || '';
      if (/Android|HarmonyOS|Vapor|微信小程序/.test(head) && !/参数|属性|类型|默认/.test(head)) {
        continue; // 平台兼容性表
      }
      tableChunks.push(rows.join('\n'));
    }
    // Also search __VP_HASH_MAP__ pages - fallback extract from content div
    const contentMatch = decoded.match(/<div class="vp-doc[\s\S]*?<\/div>\s*<footer/i) ||
      decoded.match(/class="content[\s\S]*?<h1[\s\S]{500,100000}/i);
    const blob = [tableChunks.join('\n\n'), contentMatch ? stripHtml(contentMatch[0]) : '', md].join('\n');
    // Reconstruct lines with pipes from blob by re-fetching as text tables already built
    const fakeMd = [
      '### Props',
      tableChunks[0] || '',
      '### Events',
      tableChunks[1] || '',
      tableChunks.slice(2).join('\n'),
    ].join('\n');
    const parsed = parseDocTables(fakeMd);
    // Heuristic: first big table = props if has 默认值 header
    if (!parsed.props.length && tableChunks.length) {
      for (const chunk of tableChunks) {
        const p = parseDocTables('### Props\n' + chunk);
        if (p.props.length > parsed.props.length) parsed.props = p.props;
      }
    }
    return { url, ok: true, raw: blob.slice(0, 500), props: parsed.props, events: parsed.events };
  } catch (e) {
    return { url, ok: false, raw: String(e), props: [], events: [] };
  }
}

function camelToKebab(name) {
  return name.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase();
}

/** 过滤平台兼容性表等误解析行 */
function isJunkPropName(name) {
  const n = String(name || '').trim();
  if (!n) return true;
  if (/^[√×✖✅❌\-—]+$/.test(n)) return true;
  if (/^(android|ios|harmonyos|h5|web|微信小程序|支付宝|抖音|平台|vapor)/i.test(n)) return true;
  if (n.includes('(Vapor)') || n.includes('Vapor)')) return true;
  if (/^\d+(\.\d+)*/.test(n) && n.includes('+')) return true; // 1.1.9+
  return false;
}

function isLikelyPropType(type) {
  const t = String(type || '');
  if (!t) return true;
  if (/^(android|ios|harmonyos|√)/i.test(t)) return false;
  return true;
}

function mergeProps(localProps, docProps) {
  const cleanedLocal = localProps.filter((p) => !isJunkPropName(p.name) && isLikelyPropType(p.type));
  localProps.length = 0;
  localProps.push(...cleanedLocal);

  const byKey = new Map();
  for (const p of localProps) {
    byKey.set(p.name.toLowerCase(), p);
    byKey.set(camelToKebab(p.name).toLowerCase(), p);
  }
  for (const d of docProps) {
    const raw = d.name.replace(/`/g, '').trim();
    if (isJunkPropName(raw) || !isLikelyPropType(d.type)) continue;
    const variants = raw.split('/').map((s) => s.trim()).filter(Boolean);
    for (const v of variants) {
      if (isJunkPropName(v)) continue;
      const key = v.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
      const local = byKey.get(v.toLowerCase()) || byKey.get(key.toLowerCase());
      if (local) {
        if (d.desc) local.desc = d.desc;
        if (d.defaultValue && d.defaultValue !== '-') local.defaultValue = d.defaultValue;
        if (d.type && isLikelyPropType(d.type)) local.docType = d.type;
      } else if (variants[0] === v) {
        const np = {
          name: v,
          type: isLikelyPropType(d.type) ? (d.type || 'unknown') : 'unknown',
          desc: d.desc || '',
          defaultValue: d.defaultValue || '-',
          fromDoc: true,
        };
        if (isJunkPropName(np.name)) continue;
        byKey.set(v.toLowerCase(), np);
        localProps.push(np);
      }
    }
  }
  return localProps.filter((p) => !isJunkPropName(p.name) && isLikelyPropType(p.type));
}

function renderCard(model) {
  const lines = [];
  lines.push(`<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->`);
  lines.push('');
  lines.push(`# ${model.name} API`);
  lines.push('');
  lines.push(`> 组件名：\`${model.name}\``);
  lines.push(`> 官方文档：${model.docUrl}`);
  lines.push(`> 源码：\`components/${model.name}/\``);
  if (model.docFetched) lines.push(`> 文档抓取：成功（生成时对照 https://riceui.cn ）`);
  else lines.push(`> 文档抓取：未合并或离线；请以官方页为准`);
  lines.push('');
  if (model.summary) {
    lines.push(model.summary);
    lines.push('');
  }
  lines.push('## 属性');
  lines.push('');
  lines.push('| 属性 | 说明 | 类型 | 默认值 |');
  lines.push('| --- | --- | --- | --- |');
  if (!model.props.length) {
    lines.push('| _（未解析到 Props，请打开官方文档）_ | | | |');
  } else {
    for (const p of model.props) {
      const attr = camelToKebab(p.name);
      const label = attr === p.name ? `\`${p.name}\`` : `\`${attr}\` / \`${p.name}\``;
      // 表内避免未转义 | 与 code span 冲突：联合类型用 ｜ 展示
      const typ = (p.docType || p.type || '').replace(/\|/g, '｜').replace(/`/g, '');
      const desc = (p.desc || '').replace(/\|/g, '｜');
      const def = (p.defaultValue || '-').replace(/\|/g, '｜').replace(/`/g, '');
      lines.push(`| ${label} | ${desc} | \`${typ}\` | ${def} |`);
    }
  }
  lines.push('');
  lines.push('## 事件');
  lines.push('');
  lines.push('| 事件 | 说明 | 回调参数 |');
  lines.push('| --- | --- | --- |');
  if (!model.events.length) {
    lines.push('| _以官方文档 Events 为准_ | | |');
  } else {
    for (const e of model.events) {
      lines.push(`| \`@${e.name}\` | ${(e.desc || '').replace(/\|/g, '\\|')} | ${(e.params || '-').replace(/\|/g, '\\|')} |`);
    }
  }
  lines.push('');
  lines.push('## 插槽');
  lines.push('');
  lines.push('| 插槽名 | 说明 |');
  lines.push('| --- | --- |');
  if (!model.slots.length) {
    lines.push('| `default` | 默认插槽（若组件支持） |');
  } else {
    for (const s of model.slots) {
      lines.push(`| \`${s.name}\` | ${(s.desc || '').replace(/\|/g, '\\|')} |`);
    }
  }
  lines.push('');
  return lines.join('\n');
}

function listComponents(libRoot) {
  const dir = path.join(libRoot, 'components');
  return fs.readdirSync(dir, { withFileTypes: true })
    .filter((d) => d.isDirectory() && d.name.startsWith('rice-'))
    .map((d) => d.name)
    .sort();
}

async function main() {
  const args = parseArgs(process.argv);
  if (!args.lib) {
    console.error('Usage: node scripts/gen-api-cards.mjs --lib <rice-ui dir> [--offline] [--only name]');
    process.exit(1);
  }
  const libRoot = path.resolve(args.lib);
  if (!fs.existsSync(path.join(libRoot, 'components'))) {
    console.error('Invalid --lib: components/ not found at', libRoot);
    process.exit(1);
  }

  fs.mkdirSync(OUT_DIR, { recursive: true });
  let names = listComponents(libRoot);
  if (args.only) {
    const key = args.only.startsWith('rice-') ? args.only : `rice-${args.only}`;
    names = names.filter((n) => n === key);
  }

  const models = [];
  const slugCache = new Map();

  for (const name of names) {
    const compDir = path.join(libRoot, 'components', name);
    const typeUts = readText(path.join(compDir, 'type.uts'));
    const uvue = readText(path.join(compDir, `${name}.uvue`));
    let props = parsePropsFromType(typeUts);
    let events = parseEmitsFromUvue(uvue);
    const slots = parseSlotsFromUvue(uvue);
    const slug = kebabToSlug(name);
    const docUrl = `${DOC_BASE}/${slug}.html`;

    let docFetched = false;
    if (!args.offline) {
      if (!slugCache.has(slug)) slugCache.set(slug, await fetchDoc(slug));
      const doc = slugCache.get(slug);
      if (doc.ok) {
        docFetched = true;
        props = mergeProps(props, doc.props);
        if (doc.events.length) {
          // merge events by name
          const map = new Map(events.map((e) => [e.name, e]));
          for (const e of doc.events) {
            if (map.has(e.name)) {
              if (e.desc) map.get(e.name).desc = e.desc;
            } else {
              map.set(e.name, e);
              events.push(e);
            }
          }
        }
      }
      process.stdout.write(doc.ok ? '.' : 'x');
    }

    props = props.filter((p) => !isJunkPropName(p.name) && isLikelyPropType(p.type));
    events = events.filter((e) => !isJunkPropName(e.name) && e.name !== '√');

    const model = {
      name,
      slug,
      docUrl,
      docFetched,
      summary: `RiceUI 组件。完整说明与示例以官方文档为准。`,
      props,
      events,
      slots,
    };
    models.push(model);
    fs.writeFileSync(path.join(OUT_DIR, `${name}.md`), renderCard(model), 'utf8');
  }
  if (!args.offline) process.stdout.write('\n');

  const modelJson = {
    generatedAt: new Date().toISOString(),
    lib: libRoot,
    docBase: DOC_BASE,
    components: models.map((m) => ({
      name: m.name,
      slug: m.slug,
      docUrl: m.docUrl,
      docFetched: m.docFetched,
      props: m.props.map((p) => p.name),
      events: m.events.map((e) => e.name),
    })),
  };
  fs.writeFileSync(path.join(OUT_DIR, 'model.json'), JSON.stringify(modelJson, null, 2), 'utf8');

  // README index
  const idx = [];
  idx.push('# RiceUI API 卡片索引');
  idx.push('');
  idx.push('> 生成：`node scripts/gen-api-cards.mjs --lib <uni_modules/rice-ui>`');
  idx.push('> 官方站点：[riceui.cn](https://riceui.cn/)');
  idx.push('');
  idx.push(`共 **${models.length}** 个组件。写页面时先打开对应卡片，再打开「官方文档」链接核对示例。`);
  idx.push('');
  for (const [group, list] of GROUPS) {
    idx.push(`## ${group}`);
    idx.push('');
    for (const n of list) {
      const m = models.find((x) => x.name === n);
      if (!m) continue;
      idx.push(`- [${n}](./${n}.md) → [官方](${m.docUrl})`);
    }
    idx.push('');
  }
  const known = new Set(GROUPS.flatMap((g) => g[1]));
  const rest = models.filter((m) => !known.has(m.name));
  if (rest.length) {
    idx.push('## 未分组');
    idx.push('');
    for (const m of rest) idx.push(`- [${m.name}](./${m.name}.md) → [官方](${m.docUrl})`);
    idx.push('');
  }
  fs.writeFileSync(path.join(OUT_DIR, 'README.md'), idx.join('\n'), 'utf8');

  console.log(`Wrote ${models.length} cards → ${OUT_DIR}`);
  console.log(`Doc fetch: ${models.filter((m) => m.docFetched).length} ok, ${models.filter((m) => !m.docFetched).length} miss/offline`);
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
