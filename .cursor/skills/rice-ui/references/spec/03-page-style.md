# 03 页面样式

> 主题变量真源：`uni_modules/rice-ui/libs/style/variables.css`  
> 自定义方式见官方 quickstart 与 `spec/06`

## 布局红线（App 原生渲染）

| # | ❌ | ✅ |
|---|---|---|
| 1 | 在 `<view>` 上写 `font-size` / `color` 指望子文本继承 | 样式写在 `<text>`，或用组件的 `text` / `*-text-style` |
| 2 | `display: block` / `inline-block` | `display: flex` + 显式 `flex-direction`（App 默认多为纵向） |
| 3 | 复杂组合选择器（样式隔离 2.0 / App 限制） | 单 class；状态用多 class |
| 4 | 业务色硬编码散落 | `--rice-primary-color` 等变量或主题覆盖文件 |
| 5 | 给组件 `custom-class` 却写在 scoped 里不生效 | 非 scoped 或按文档使用 `custom-style` |

## 满屏高度双轨

```css
.page {
  display: flex;
  flex-direction: column;
  /* #ifndef APP */
  min-height: 100vh;
  /* #endif */
  /* #ifdef APP */
  flex: 1;
  /* #endif */
  background-color: var(--rice-background);
}
```

## 主题 class

库通过 `.rice-theme-light` / `.rice-theme-dark` 挂载 CSS 变量。Web 端 `setTheme` 会改 `document.documentElement` 的 class（见源码 `libs/store/useConfig.uts`）。页面自定义样式优先使用：

- `--rice-primary-color` / `--rice-success-color` / `--rice-warning-color` / `--rice-error-color`
- `--rice-text-color` / `--rice-border-color` / `--rice-background` / `--rice-background-2`

完整列表读 `variables.css`，官方 quickstart 展示了覆盖写法。

## custom-style

多数组件支持 `custom-style`（对象）。App 端注意单位与 UCSS 子集，避免 Web 独有属性。
