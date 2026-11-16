# Soft Glass 产品壳 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把 quick-ui 默认气质收成 Soft Glass（玻璃默认开 + 壳层/登录/工作台换皮），列表仍走 `C7JsonTable`。

**Architecture:** 不新建主题包。改 `defaultSettings.glassEnabled` 与 `--qb-*` / glass 几何默认值；壳层（侧栏、顶栏）与工作台/登录只调材质、圆角、文案气质；业务 CRUD 页不改交互骨架。已有 localStorage `layout-setting` 的用户保留其开关，仅新用户与「无本地覆盖」路径吃到默认玻璃开。

**Tech Stack:** Vue 3、Pinia、Element Plus、`--qb-*` SCSS 令牌、Vitest、现有 `DESIGN.md` 七类表面原型。

**Spec:** `docs/superpowers/specs/2026-11-16-soft-glass-shell-design.md`  
**预览对照:** `quick-ui/design-previews/consumer-styles.html`（方案 1）、`quick-ui/design-previews/layout-b-vs-c.html`（B）

## Global Constraints

- 主色默认仍 `#8D51F9`；主题包只换 primary
- 字体仍 Inter + Noto Sans SC（`--qb-font-sans`）
- 不新建 `soft-glass` 主题包；不大改 Layout 信息架构；不做摘要卡/双栏列表
- 不改 C7 列表/弹窗 API、路由、菜单、权限
- 组件只消费语义令牌，禁止业务页硬编码灰阶
- blur 只在侧栏、顶栏、外层主卡片；嵌套面用 `muted`
- 验收必须覆盖玻璃浅（含壁纸）、玻璃深、平面浅、平面深 + 桌面/手机宽

---

## File Map

| 文件 | 职责 |
|------|------|
| `quick-ui/src/settings.js` | 默认 `glassEnabled: true` |
| `quick-ui/src/settings.softGlass.test.js` | 断言默认玻璃开（新建） |
| `quick-ui/src/assets/styles/common.scss` | Soft Glass 几何/阴影/表面原型圆角微调；`qb-workbench` 外框 |
| `quick-ui/src/assets/styles/themes/glass.scss` | 玻璃表面透明度/嵌套不二次 blur（按需微调） |
| `quick-ui/src/assets/styles/sidebar.scss` | 侧栏激活态与玻璃阴影对齐 Soft Glass |
| `quick-ui/src/layout/components/Navbar.vue` | 顶栏玻璃表面、次要控件 hover 用令牌 |
| `quick-ui/src/views/index.vue` | 工作台横幅圆角/文案更产品、入口卡编排 |
| `quick-ui/src/views/login.vue` | 登录卡/氛围与全局主色令牌对齐（仅必要微调） |
| `quick-ui/DESIGN.md` | 写明 Soft Glass 为默认方向与玻璃默认开 |

---

### Task 1: 默认玻璃开 + 单测

**Files:**
- Modify: `quick-ui/src/settings.js`
- Create: `quick-ui/src/settings.softGlass.test.js`
- Test: `quick-ui/src/settings.softGlass.test.js`

**Interfaces:**
- Consumes: `defaultSettings` 从 `@/settings` 默认导出
- Produces: `glassEnabled === true` 作为无 localStorage 覆盖时的默认

- [ ] **Step 1: 写失败单测**

创建 `quick-ui/src/settings.softGlass.test.js`:

```js
import { describe, expect, it } from 'vitest'
import defaultSettings from '@/settings'

describe('Soft Glass defaults', () => {
  it('enables glass material by default', () => {
    expect(defaultSettings.glassEnabled).toBe(true)
  })

  it('keeps light side theme for Soft Glass shell', () => {
    expect(defaultSettings.sideTheme).toBe('theme-light')
  })
})
```

- [ ] **Step 2: 跑测确认失败**

Run（在 `quick-ui` 目录）:

```bash
npm test -- src/settings.softGlass.test.js
```

Expected: FAIL — `glassEnabled` 期望 `true`，实际 `false`

- [ ] **Step 3: 改默认配置**

在 `quick-ui/src/settings.js` 将：

```js
glassEnabled: false,
```

改为：

```js
glassEnabled: true,
```

并更新上方注释为：`是否启用玻璃材质（Soft Glass 默认开启）`。

- [ ] **Step 4: 跑测确认通过**

```bash
npm test -- src/settings.softGlass.test.js
```

Expected: PASS（2 tests）

- [ ] **Step 5: Commit**

```bash
git add quick-ui/src/settings.js quick-ui/src/settings.softGlass.test.js
git commit -m "$(cat <<'EOF'
feat(quick-ui): default Soft Glass material on

EOF
)"
```

**Note:** 已有 `localStorage.layout-setting` 且显式存了 `glassEnabled: false` 的浏览器不会自动翻开；验收时用无该键的隐私窗口，或清掉 `layout-setting` 后再测默认。

---

### Task 2: Soft Glass 令牌与表面原型微调

**Files:**
- Modify: `quick-ui/src/assets/styles/common.scss`
- Modify: `quick-ui/src/assets/styles/themes/glass.scss`（仅当 tinted 默认观感偏弱时）

**Interfaces:**
- Consumes: 现有 `--qb-radius-*`、`--qb-shadow-glass`、`.qb-workbench` / `.qb-banner-card` / `.qb-media-surface`
- Produces: 壳层与列表面板默认更接近预览（更大入口圆角、玻璃阴影略柔）

- [ ] **Step 1: 调整 tinted 玻璃默认光学（common.scss）**

将 `html[data-glass-appearance='tinted']` 块改为更接近 Soft Glass 预览：

```scss
html[data-glass-appearance='tinted'] {
  --qb-glass-blur: 22px;
  --qb-glass-saturate: 1.35;
  --qb-glass-surface-opacity: 0.72;
}
```

将 `--qb-shadow-glass` 微调为：

```scss
--qb-shadow-glass: 0 0 0 1px rgba(255, 255, 255, 0.35) inset, 0 8px 28px rgba(11, 19, 34, 0.1);
```

- [ ] **Step 2: 表面原型默认圆角上提一档（入口/横幅/工作台外框）**

在 `common.scss` 中，对以下选择器把 `border-radius: var(--qb-radius)` 改为 `border-radius: var(--qb-radius-lg)`（20px）：

- `.qb-media-surface`
- `.qb-banner-card`
- `.qb-card-colorful, .app-card-colorful`
- `.qb-workbench`

`.qb-card-shell`、`.qb-ops-card` 保持 `var(--qb-radius)`（12px），避免所有卡片一并变大。

- [ ] **Step 3: 确认 glass.scss 仍禁止嵌套二次 blur**

打开 `themes/glass.scss`，确认仍有类似规则（保留，勿删）：

```scss
html[data-theme='glass'] .el-card .el-card,
html[data-theme='glass'] .muted-panel,
html[data-theme='glass'] .el-table,
html[data-theme='glass'] .el-table th.el-table__cell,
html[data-theme='glass'] .qb-workbench__toolbar {
  /* 静态吸收，无二次 backdrop-filter */
}
```

若 `qb-workbench__toolbar` 未列入，补上与表头相同的「清除 blur / 使用 muted」处理，与 `DESIGN.md` 一致。

- [ ] **Step 4: 目视烟测**

Run:

```bash
npm run dev
```

用隐私窗口打开工作台与任一 `qb-workbench` 列表页，切换玻璃开/关，确认外框圆角与阴影变化、表头无双模糊。

- [ ] **Step 5: Commit**

```bash
git add quick-ui/src/assets/styles/common.scss quick-ui/src/assets/styles/themes/glass.scss
git commit -m "$(cat <<'EOF'
style(quick-ui): tune Soft Glass tokens and surface radii

EOF
)"
```

---

### Task 3: 侧栏 + 顶栏壳层

**Files:**
- Modify: `quick-ui/src/assets/styles/sidebar.scss`
- Modify: `quick-ui/src/layout/components/Navbar.vue`

**Interfaces:**
- Consumes: `--qb-sidebar-*`、`--el-color-primary`、`--qb-card`、`--qb-border`、`--qb-shadow`
- Produces: 浅色玻璃侧栏激活胶囊 + 顶栏与侧栏同一表面语言

- [ ] **Step 1: 侧栏激活态改为 soft + 前景加深（对齐 spec）**

在 `sidebar.scss` 的 `.sidebar-container` 变量区，将激活文字从纯 primary 改为更深前景（保留 soft 底）：

```scss
.sidebar-container {
  --qb-sidebar-item-radius: 14px;
  --qb-sidebar-active-bg: color-mix(in srgb, var(--el-color-primary) 14%, transparent);
  --qb-sidebar-active-fg: var(--qb-sidebar-text-active);
}
```

确保菜单 active 项使用上述变量（若当前写死 `var(--el-color-primary)` 作文字色，改为 `var(--qb-sidebar-active-fg)`；图标可保留 primary 点缀或同色，二选一保持整列一致）。

玻璃开启时侧栏阴影与 `common` 的 `--qb-shadow` 对齐：在 `html[data-theme='glass']` 下若侧栏仍写死双层黑影，改为：

```scss
html[data-theme='glass'] #app .sidebar-container {
  box-shadow: var(--qb-shadow);
  background-color: color-mix(
    in srgb,
    var(--qb-sidebar-bg) calc(var(--qb-glass-surface-opacity) * 100%),
    transparent
  ) !important;
}
```

（若 `glass.scss` 已覆盖侧栏背景，则本块只改 shadow，避免重复打架。）

- [ ] **Step 2: Navbar 表面与 hover 令牌化**

在 `Navbar.vue` scoped 样式中：

1. `.navbar` 保持 `background: var(--qb-card)`；增加与玻璃一致的底边：

```scss
.navbar {
  height: 50px;
  background: var(--qb-card);
  border-bottom: 1px solid var(--qb-border-subtle);
  box-shadow: none; // 避免与侧栏双阴影抢戏；玻璃态由全局表面承担
}
```

2. 将 `.hamburger-container:hover` 与 `.right-menu-item.hover-effect:hover` 的 `rgba(0,0,0,0.025)` 改为：

```scss
background: var(--qb-sidebar-hover);
```

3. 头像圆角与 Soft Glass 一致：`.user-avatar { border-radius: 12px; }`（已有 10px 可改为 12px）。

- [ ] **Step 3: 目视对照预览 B**

打开 `design-previews/layout-b-vs-c.html` 的 B，与真实 Layout 并排：侧栏浮起圆角卡、激活 soft、顶栏安静。

- [ ] **Step 4: Commit**

```bash
git add quick-ui/src/assets/styles/sidebar.scss quick-ui/src/layout/components/Navbar.vue
git commit -m "$(cat <<'EOF'
style(quick-ui): Soft Glass sidebar and navbar shell

EOF
)"
```

---

### Task 4: 工作台首页产品化

**Files:**
- Modify: `quick-ui/src/views/index.vue`

**Interfaces:**
- Consumes: `.qb-media-surface`、`.qb-banner-card`、`.qb-card-colorful`、`.qb-ops-card`、`.qb-card-shell`
- Produces: 更产品向文案 + 横幅使用更大圆角（依赖 Task 2 的 `.qb-media-surface`）

- [ ] **Step 1: 更新 hero 文案与结构（去「后台说明书」感）**

将模板中 hero 改为（保留 `appConfig.loginTitle`）：

```vue
<section class="qb-media-surface home-hero no-blur" aria-label="欢迎">
  <p class="qb-on-media-muted home-hero__eyebrow">今天 · 产品工作台</p>
  <h1 class="qb-on-media home-hero__title">欢迎进入 {{ appConfig.loginTitle }}</h1>
  <p class="qb-on-media-muted home-hero__sub">从常用应用与运行状态开始，而不是从一张空表格开始。</p>
</section>
```

卡片文案同步略偏产品（示例）：

- 认证与客户端 → 副文案：`客户端、签名与授权，一处看清`
- 消息通道 → 保持摘要卡，副文案：`邮件 / Webhook / 机器人通道`
- 运维概览 → `任务、日志与监控入口`
- 分组设置 → 保持 `app-grouped-list`，勿改成表格

- [ ] **Step 2: hero 样式贴近 Soft Glass**

scoped 中：

```scss
.home-hero {
  min-height: 200px;
  padding: 40px 28px;
  /* 圆角由全局 .qb-media-surface 的 radius-lg 提供 */
  background-image:
    radial-gradient(ellipse at 30% 20%, rgba(141, 81, 249, 0.55), transparent 50%),
    linear-gradient(145deg, #182235, #0b1322 60%, #1a2740);
}

.home-grid {
  gap: 16px;
}
```

不要引入第二套展示字体。

- [ ] **Step 3: 浏览器核验**

登录后打开首页：横幅 + 2×2 入口卡；玻璃浅/深切换时 `on-media` 文字仍可读。

- [ ] **Step 4: Commit**

```bash
git add quick-ui/src/views/index.vue
git commit -m "$(cat <<'EOF'
feat(quick-ui): Soft Glass workbench home copy and hero

EOF
)"
```

---

### Task 5: 登录页与全局令牌对齐

**Files:**
- Modify: `quick-ui/src/views/login.vue`

**Interfaces:**
- Consumes: 页面内已有 `--login-primary` 等（应继续桥接 `--el-color-primary` / `--qb-primary-rgb`）
- Produces: 登录卡大圆角、主 CTA 单一、品牌名第一视觉（结构已有则只做令牌/圆角校准）

- [ ] **Step 1: 定位登录卡圆角与主色变量**

在 `login.vue` 的 `<style>` 中找到 `.login-card` / `--login-primary`。确保：

```scss
.login-card {
  border-radius: var(--qb-radius-xl, 24px);
  background: color-mix(in srgb, var(--qb-card) 86%, transparent);
  border: 1px solid var(--qb-border);
  box-shadow: var(--qb-shadow);
}

.login-page {
  --login-primary: var(--el-color-primary);
  --login-primary-light: var(--el-color-primary-light-3);
}
```

若现有实现已等价，则只改明显硬编码 hex 主色处，改为上述变量。**不要**重写整页氛围动画。

- [ ] **Step 2: 文案克制检查**

保持「欢迎登录」类短文案；删除或避免新增长篇平台介绍。品牌名继续用配置标题。

- [ ] **Step 3: 核验**

打开 `/login`，切换主题包主色，确认 Logo 渐变与按钮跟随；玻璃与平面下卡片皆可读。

- [ ] **Step 4: Commit**

```bash
git add quick-ui/src/views/login.vue
git commit -m "$(cat <<'EOF'
style(quick-ui): align login card with Soft Glass tokens

EOF
)"
```

---

### Task 6: 列表换皮烟测 + DESIGN.md

**Files:**
- Modify: `quick-ui/DESIGN.md`
- Verify only: 任一已有 `qb-workbench` 页（如 `views/system/user/index.vue` 或 `views/system/oauthClient/index.vue`）— **不改业务逻辑**；若页面根节点缺少 `qb-workbench` 类导致外框无 Soft Glass 圆角，仅补 class，不改表格列。

**Interfaces:**
- Consumes: Task 2 的 `.qb-workbench` 样式
- Produces: 文档声明 Soft Glass 默认；列表页外框一致

- [ ] **Step 1: 抽查用户列表页根节点**

打开 `quick-ui/src/views/system/user/index.vue` 根元素：若为 `<div class="app-container">` 且无 `qb-workbench`，改为：

```vue
<div class="app-container qb-workbench">
```

不要改 `C7JsonTable` props / 列定义。

- [ ] **Step 2: 更新 DESIGN.md 方向段**

在 `quick-ui/DESIGN.md`「方向」第一节补一句（保持中文文档风格）：

```markdown
默认材质为 Soft Glass：新用户与未写入本地覆盖的配置下 `glassEnabled` 为 true；侧栏浅色玻璃、工作台横幅与入口卡承担产品温度，列表/表单保持安静运维面与 C7 交互骨架。
```

在「主题、材质与壁纸」中写明：默认玻璃开；用户仍可在布局设置关闭。

- [ ] **Step 3: 四态手工验收（桌面 + 窄屏）**

Checklist：

1. 隐私窗口登录 → 确认玻璃默认开  
2. 玻璃浅 + 壁纸开  
3. 玻璃深  
4. 平面浅  
5. 平面深  
6. 用户列表：搜 → 编辑弹窗 → 关闭  
7. 对照 `design-previews/consumer-styles.html` 方案 1 与 `layout-b-vs-c.html` 的 B  

- [ ] **Step 4: Commit**

```bash
git add quick-ui/DESIGN.md quick-ui/src/views/system/user/index.vue
git commit -m "$(cat <<'EOF'
docs(quick-ui): declare Soft Glass as default visual direction

EOF
)"
```

（若 Step 1 无需改 user 页，则 commit 只含 `DESIGN.md`。）

---

## Self-Review (plan vs spec)

| Spec 要求 | 对应 Task |
|-----------|-----------|
| 玻璃默认开 | Task 1 |
| 令牌/圆角/阴影 Soft Glass | Task 2 |
| 侧栏/顶栏壳层 | Task 3 |
| 工作台横幅 + 入口卡 | Task 4 |
| 登录品牌氛围对齐 | Task 5 |
| 列表换皮不改 C7 API | Task 2 + Task 6 |
| 更新 DESIGN.md | Task 6 |
| 不做主题包 / 不做 C 结构 / 不换字体 | Global Constraints + 各 Task 未引入 |
| 四态验收 | Task 6 Step 3 |

无 TBD；localStorage 旧用户行为已在 Task 1 Note 写明。
