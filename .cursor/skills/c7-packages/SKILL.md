---
name: c7-packages
description: Use when writing or modifying quick-ui admin pages with C7* packages components (C7JsonTable, C7FormDialog, C7Select, C7Radio, MessageBox APIs), CRUD list/dialog scaffolds, or when unsure about C7 props/slots versus Element Plus. Not for RiceUI, uni-app, or odin-web-ui-plus.
---

# quick-ui C7 packages 页面编写规范

> **本技能是「在 quick-ui 业务页使用 `src/packages` C7 组件」的唯一权威入口。**  
> 规范正文：`references/` → `spec/` + `recipes/` + `api/`。  
> 面向**消费者**（业务 `views/`），不涉及改 packages 源码实现。

## 适用场景

- 写/改 `quick-ui` 管理端列表、表单弹窗、字典标签、上传导入导出等
- 使用 `<C7*>` / `<c7-*>` 或 `c7Confirm` 等 MessageBox API
- 不确定该用 `C7FormDialog` 还是 `C7Dialog`、`ElButton` 还是 `C7Button`
- **不适用**：RiceUI / uni-app x；odin-web-ui-plus 前端生成流程

## 强制机制

1. **权威源**：① `quick-ui/src/packages` 源码 → ② `.cursor/rules/quick-ui-style.mdc` → ③ 本技能 `references/api/` → ④ `spec/` / `recipes/`。冲突以前者为准。
2. **标杆页**：CRUD 对齐 `views/system/user/index.vue`；通用骨架可对照 `views/system/dict/type/index.vue`。
3. **recipe-first**：`recipes/` 已有场景必须照抄骨架，禁止另起一套列表/弹窗约定。
4. **写前查卡片**：用任何 `C7*` 前查 `references/api/C7Xxx.md`；卡上没有的 props 不猜。
5. **写后校验**：`node scripts/verify-c7-usage.mjs <页面或目录>`，**0 违规**再交付。
6. **新页按钮**：优先 `ElButton`；`C7Button` 已 deprecated，仅遗留/`C7ButtonGroup` 场景。
7. **风格**：`<script setup>`、不用 TypeScript、2 空格、单引号、无分号（详见 `spec/02`）。

## references 索引

| 文件 | 职责 |
|---|---|
| `spec/01-install-and-setup.md` | `installPackages` 全局注册 |
| `spec/02-page-conventions.md` | 页面风格、权限、字典、API |
| `spec/03-json-table.md` | C7JsonTable |
| `spec/04-form-dialog.md` | C7FormDialog / C7Dialog |
| `spec/05-form-controls.md` | Select/Radio/Date/Upload 等 |
| `spec/06-message-box.md` | c7Confirm 等 |
| `recipes/recipe-crud-page.md` | 标准 CRUD |
| `recipes/recipe-dict-page.md` | 字典类页提示 |
| `api/` | 组件 API 卡片 |
| `scripts/gen-api-cards.mjs` | 从 packages 再生成 |
| `scripts/verify-c7-usage.mjs` | 用法校验 |

## 工作流

1. 读 `spec/01`–`02`（注册与页面约定）
2. 列表+弹窗照抄 `recipe-crud-page`
3. 查 `api/C7JsonTable.md`、`api/C7FormDialog.md` 等卡片
4. 表单控件见 `spec/05`；确认框见 `spec/06`
5. 跑 `verify-c7-usage.mjs` 至 0 违规
