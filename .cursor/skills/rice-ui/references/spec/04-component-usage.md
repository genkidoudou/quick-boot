# 04 组件使用速查

> 组件文档统一：`https://riceui.cn/components/<slug>.html`  
> 本地卡片：`references/api/rice-<name>.md`

## 分类（与 Demo / 官方导航对齐）

### 基础

| 组件 | slug | 说明 |
|---|---|---|
| rice-button | button | 按钮（含小程序开放能力） |
| rice-icon | icon | 图标 |
| rice-image | image | 图片 |
| rice-row / rice-col | layout | 布局（文档常归在 layout 示例） |

### 表单

| 组件 | slug |
|---|---|
| rice-form / rice-form-item | form |
| rice-input | input |
| rice-textarea | textarea |
| rice-search | search |
| rice-switch | switch |
| rice-checkbox / rice-checkbox-group | checkbox |
| rice-radio / rice-radio-group | radio |
| rice-stepper | stepper |
| rice-slider | slider |
| rice-rate | rate |
| rice-code-input | code-input |
| rice-uploader | uploader |
| rice-picker | picker |
| rice-datetime-picker | datetime-picker |
| rice-calendar | calendar |
| rice-cascader | cascader |
| rice-color-picker | color-picker |
| rice-signature | signature |

### 展示

| 组件 | slug |
|---|---|
| rice-cell / rice-cell-group | cell |
| rice-tag | tag |
| rice-badge | badge |
| rice-avatar | avatar |
| rice-divider | divider |
| rice-collapse / rice-collapse-item | collapse |
| rice-grid / rice-grid-item | grid |
| rice-progress / rice-progress-circle | progress / progress-circle |
| rice-count-to | count-to |
| rice-count-down | count-down |
| rice-scroll-x | scroll-x |
| rice-time-format | time-format |
| rice-qrcode | qrcode |
| rice-waterflow / rice-waterflow-item | waterflow |

### 反馈

| 组件 | slug |
|---|---|
| rice-loading | loading |
| rice-overlay | overlay |
| rice-popup | popup |
| rice-dialog | dialog |
| rice-action-sheet | action-sheet |
| rice-notice-bar | notice-bar |
| rice-swipe-actions / rice-swipe-actions-item | swipe-actions |
| rice-float-fab | float-fab |
| rice-float-panel | float-panel |

### 导航

| 组件 | slug |
|---|---|
| rice-navbar | navbar |
| rice-tabs | tabs |
| rice-subsection | subsection |
| rice-back-top | back-top |

### 内部 / 配置

| 组件 | 说明 |
|---|---|
| rice-ui | 主题/配置容器（按源码与文档使用，勿与业务页面组件混淆） |

## 库导出（常用）

从 `@/uni_modules/rice-ui`：

- `setTheme` / `config` / `isDark`
- `showDialog` / `hideDialog` / `showActionSheet`（及 action-sheet 相关导出）
- `dateuts`、`Coloruts` 等 plugin
- `useNamespace`、`useSafeArea`、`usePopup` 等 hooks（偏进阶）

以 `index.uts` 实际 export 为准。

## 已知能力缺口（业务需自补）

官方库**无**独立：tabbar、empty、toast、skeleton、steps、pagination、i18n。可用 `uni.showToast` / 自建组件 / 官方内置组件补齐。

## 蒸汽下水瀑

官方 waterflow 文档：App 蒸汽模式**推荐直接用官方 `waterflow`**；`rice-waterflow` 蒸汽下也能用，属性方法对齐。图片高度不定时需在 load/error 调用插槽 `remeasure`。见 https://riceui.cn/components/waterflow.html
