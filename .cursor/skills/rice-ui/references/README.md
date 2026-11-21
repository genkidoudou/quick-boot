# rice-ui references 手册

## 本手册是什么

指导在**任意 uni-app x 项目**（含蒸汽模式）中，使用 [RiceUI](https://riceui.cn/) 编写页面：

```
安装接入 → 页面脚本 → 页面样式 / 主题 → 组件使用 → 三端与蒸汽避错
```

官方入口：

- 站点：https://riceui.cn/
- 快速开始：https://riceui.cn/components/quickstart.html
- 更新日志：https://riceui.cn/components/updatelog.html
- 单组件文档：`https://riceui.cn/components/<slug>.html`（如 `button`、`dialog`、`waterflow`）

## 权威源与优先级

1. **官方文档** https://riceui.cn/
2. **组件库源码** `uni_modules/rice-ui`（MIT 开源，可读）
3. **API 快照卡片** `api/`（由 `scripts/gen-api-cards.mjs` 对照源码 + 官方页生成）
4. 本手册 `spec/` / `recipes/`

冲突时以前者为准。卡片上的「文档」链接可直达官方页。

## 文件索引

| 文件 | 定位 |
|---|---|
| `api/README.md` | 组件 API 卡片索引 |
| `spec/01-install-and-setup.md` | 安装、样式、样式隔离 2.0、dialog 页注册 |
| `spec/02-page-script.md` | 组合式脚本与 API 调用 |
| `spec/03-page-style.md` | CSS 红线与主题变量 |
| `spec/04-component-usage.md` | 组件速查 |
| `spec/05-cross-platform-vapor.md` | 三端与蒸汽 |
| `spec/06-theme-dark-mode.md` | 暗黑与自定义主题 |
| `recipes/recipe-page-scaffold.md` | 最小页面 |

## 自包含声明

- 导入前缀：`@/uni_modules/rice-ui`
- `<rice-*>` 依赖 easycom / uni_modules 自动扫描（见 `spec/01`）
- 不依赖宿主工程其它约定
- 校验器 `scripts/verify-rice-usage.mjs` 零第三方依赖（Node ≥ 18）
