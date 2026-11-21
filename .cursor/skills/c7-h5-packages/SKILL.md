---
name: c7-h5-packages
description: Use when writing or modifying quick-h5 pages with C7* components (C7JsonTable, C7Select, C7FormDialog, c7Confirm, etc.) under components/c7-*, Soft Glass list/form scaffolds, or when unsure about H5 C7 props versus quick-ui packages / RiceUI. Not for quick-ui admin (use c7-packages) or plain rice-* without C7 wrappers.
---

# quick-h5 C7 packages 页面编写规范

> **本技能是「在 quick-h5 业务页使用 `components/c7-*`」的唯一权威入口。**  
> 规范正文：`references/` → `spec/` + `api/`。  
> 面向**消费者**（`pages/`），不涉及改 RiceUI 源码。

## 适用场景

- 写/改 `quick-h5` 列表、表单弹层、字典标签、上传导入导出等
- 使用 `<C7*>` / `<c7-*>` 或 `c7Confirm` 等 MessageBox API
- 不确定 H5 版与 `quick-ui/src/packages` 的差异（交互改为 Rice / uni API）
- **不适用**：`quick-ui` 管理端（用 `c7-packages`）；纯 `rice-*` 页面脚手架（用 `rice-ui`）

## 强制机制

1. **权威源**：① `quick-h5/components/c7-*` 源码 → ② 本技能 `references/` → ③ `quick-ui/src/packages`（仅对照命名/props 心智）→ ④ `c7-packages` api 卡片。冲突以 **H5 源码**为准。
2. **命名对齐**：组件名、主要 props/events 与 packages 同名（如 `dataList`/`options`/`listFunction`/`v-model`）。
3. **实现底座**：UI 只用 RiceUI（`rice-*`）+ uni API；禁止在业务页直接引 Element Plus。
4. **标杆页**：列表对齐 `pages/system/config/index.uvue`（纯跳转用 `add-path` / `edit-path`，复杂交互用事件）；表单对齐 `pages/system/config/form.uvue`；组件演示见 `pages/examples/`。
5. **写前查卡**：用任何 `C7*` 前查 `references/api/`；卡上没有的 props 以组件源码 `defineProps` 为准，不猜桌面 EP 透传。
6. **写后校验**：`node .cursor/skills/rice-ui/scripts/verify-rice-usage.mjs quick-h5/pages/<页或目录>`，**0 违规**；再跑本技能 `scripts/verify-c7-h5-usage.mjs`。

## 组件目录约定

| 路径 | 说明 |
|---|---|
| `quick-h5/components/c7-*/c7-*.uvue` | 易扫描 easycom 组件 |
| `quick-h5/components/c7-shared/option.uts` | 选项归一化 |
| `quick-h5/components/c7-message-box/index.uts` | `c7Confirm` 等函数式 API |
| `quick-h5/components/c7-json-table/types.uts` | 列表查询类型 |

## references 索引

| 文件 | 职责 |
|---|---|
| `spec/01-overview.md` | 与 packages 差异、注册与入口 |
| `spec/02-form-controls.md` | Select/Radio/Date/Upload 等 |
| `spec/03-list-dialog.md` | JsonTable / Dialog / FormDialog / MessageBox |
| `api/README.md` | 组件清单与路径 |
| `scripts/verify-c7-h5-usage.mjs` | 用法粗检 |

## 工作流

1. 读 `spec/01`（差异与约定）
2. 列表照抄 `pages/system/config/index.uvue` + `C7JsonTable`
3. 表单控件见 `spec/02`；弹层见 `spec/03`
4. 不确定 props 时打开对应 `components/c7-xxx/c7-xxx.uvue`
5. 跑 `verify-rice-usage` + `verify-c7-h5-usage` 至 0 违规
