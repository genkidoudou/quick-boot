---
name: rice-ui
description: Use when writing or modifying uni-app x pages with RiceUI (rice-* components), setting up rice-ui install/theme/dark mode, showDialog/showActionSheet, vapor/steam mode compatibility, or style isolation 2.0 with RiceUI. Official docs https://riceui.cn/. Not for uview (u-*/ux-*) or classic uni-app vue pages.
---

# RiceUI 页面编写规范（rice-ui）

> **本技能是「使用 RiceUI 编写 uni-app x 页面」的唯一权威入口。**  
> 规范正文在 `references/`：`README.md` → `spec/` + `recipes/` + `api/`。  
> 官方文档：https://riceui.cn/ （快速开始：https://riceui.cn/components/quickstart.html）  
> 面向**消费者**（业务项目），不涉及改组件库源码。

## 适用场景

- uni-app x 项目（含**蒸汽模式**）中使用 `<rice-*>` 写业务页
- 接入样式、暗黑主题（`setTheme`）、`showDialog` / `showActionSheet`
- 排查蒸汽模式 / 样式隔离 2.0 / 三端样式问题
- **不适用**：普通 uni-app 的 uview-pro（`u-*`）、uview-pro-x（`ux-*`）；不要与其它全家桶 UI 混用

## 强制机制（动手前必读）

1. **权威源优先级**：① https://riceui.cn/ 官方文档 → ② 项目内 `uni_modules/rice-ui` 源码 → ③ 本技能 `references/api/` 快照 → ④ `spec/` / `recipes/`。冲突时以前者为准。
2. **自包含**：可整体复制到任意使用 rice-ui 的 uni-app x 项目；导入前缀统一 `@/uni_modules/rice-ui`。
3. **recipe-first**：`references/recipes/` 已有场景必须照抄，禁止自造接入写法。
4. **取色走主题**：颜色用 `--rice-*` CSS 变量或覆盖 `.rice-theme-light` / `.rice-theme-dark`；页面业务色勿硬编码一长串散落色值。
5. **写前查卡片**：写任何 `<rice-*>` 前查 `references/api/rice-<name>.md`；卡上没有的属性不猜，去官方文档核对。
6. **写后校验**：`node scripts/verify-rice-usage.mjs <页面或目录>`，**0 违规**再交付。
7. **禁止混用**：同一页面不要同时使用 `rice-*` 与 `ux-*` / `u-*` / TMUI 等其它 UI 库组件。

## references 结构索引

| 文件 | 职责 |
|---|---|
| `references/spec/01-install-and-setup.md` | 安装、样式引入、样式隔离 2.0、dialog/action-sheet 页面注册 |
| `references/spec/02-page-script.md` | 组合式脚本、蒸汽/TS 注意、API 调用 |
| `references/spec/03-page-style.md` | App CSS 子集、主题变量、布局红线 |
| `references/spec/04-component-usage.md` | 组件分类速查、导出工具 |
| `references/spec/05-cross-platform-vapor.md` | 三端 + 蒸汽版本门槛、症状速查 |
| `references/spec/06-theme-dark-mode.md` | 暗黑与自定义主题色 |
| `references/recipes/recipe-page-scaffold.md` | 最小页面脚手架 |
| `references/api/` | 组件 API 卡片（对照官方文档生成） |
| `scripts/gen-api-cards.mjs` | 从源码 + https://riceui.cn 再生成卡片 |
| `scripts/verify-rice-usage.mjs` | 页面 `rice-*` 用法校验 |

## 工作流

1. 读 `spec/01` 完成接入（样式 / 蒸汽 / dialog 页）
2. 照抄 `recipe-page-scaffold` 起页
3. 每个 `<rice-*>` 先查 `api/rice-<name>.md`，复杂行为再打开对应官方页 `https://riceui.cn/components/<slug>.html`
4. 按 `spec/02`–`04` 写脚本与组件；主题见 `spec/06`
5. 收尾按 `spec/05` 自查
6. 跑 `verify-rice-usage.mjs` 至 0 违规
