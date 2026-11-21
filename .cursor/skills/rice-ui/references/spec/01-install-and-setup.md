# 01 安装与接入

> 对照官方：https://riceui.cn/components/quickstart.html

## 环境前提

- **uni-app x** 项目（组合式 API）
- 蒸汽模式建议 HBuilderX：**鸿蒙 ≥ 5.08**、**iOS ≥ 5.11**、**Android ≥ 5.21**（以官方更新日志与蒸汽文档为准）
- 组件库：`uni_modules/rice-ui`（插件市场导入或拷贝 MIT 源码）
- 引擎声明常见：`uni-app-x ^5.03+`；以你使用的 RiceUI 版本说明为准

## 第一步：放入组件库

仅需业务所需的 `uni_modules/rice-ui`。**不要**把官方 Demo 整包（含示例页版权限制）当业务工程提交应用市场。

```
your-project/
├─ uni_modules/
│  └─ rice-ui/
│     ├─ components/     # rice-* 
│     ├─ libs/           # style / store / use / plugin
│     ├─ pages/          # dialog / action-sheet（API 调用需要）
│     ├─ pages_init.json
│     └─ index.uts
├─ App.uvue
├─ main.uts / main.ts
└─ pages.json
```

插件市场：https://ext.dcloud.net.cn/plugin?id=24907

## 第二步：引入基础样式（必做）

官方要求在 `App.uvue`（文档写作 `APP.vue`）中：

```vue
<style>
@import "@/uni_modules/rice-ui/libs/style/index.css";
</style>
```

### 样式隔离 2.0 + H5

HBuilderX 5.0+ 且开启[样式隔离策略 2.0](https://doc.dcloud.net.cn/uni-app-x/css/common/style-isolation.html) 时，**H5 端**须在 `main.uts` / `main.ts` 引入，而不是只放在 `App.uvue`：

```ts
// #ifdef WEB
import "@/uni_modules/rice-ui/libs/style/index.css";
// import "./style/theme.css"; // 自定义主题，见 spec/06
// #endif
```

## 第三步：使用组件

**无需** `app.use`，**无需**手动 import 组件（uni_modules + easycom）：

```html
<rice-button type="primary" text="确定"></rice-button>
```

若 easycom 未扫到，可在 `pages.json` 显式增加：

```json
{
  "easycom": {
    "autoscan": true,
    "custom": {
      "^rice-(.*)": "@/uni_modules/rice-ui/components/rice-$1/rice-$1.uvue"
    }
  }
}
```

## 第四步：`showDialog` / `showActionSheet` 页面注册

官方说明：在 **App / H5** 若要使用 `showDialog`、`showActionSheet` API，导入时需合并 `pages_init.json` 中的页面；跳过则这两个 API 不可用，其它功能不受影响。小程序端可用条件编译，不依赖这两页。

`uni_modules/rice-ui/pages_init.json` 典型内容：

```json
{
  "pages": [
    { "path": "uni_modules/rice-ui/pages/action-sheet/action-sheet" },
    { "path": "uni_modules/rice-ui/pages/dialog/dialog" }
  ]
}
```

合并进项目 `pages.json` 的 `pages` 数组（或按 HBuilderX「合并 Pages」提示操作）。

## 第五步：暗黑模式（可选）

```ts
import { setTheme } from "@/uni_modules/rice-ui"
setTheme('dark') // 或 'light'
```

细节见 `spec/06-theme-dark-mode.md`。

## 核对清单

- [ ] `uni_modules/rice-ui` 已就位
- [ ] 基础 CSS 已引入（App.uvue 和/或 WEB main）
- [ ] 蒸汽模式版本门槛满足目标端
- [ ] 需要 API 弹窗时已合并 dialog / action-sheet 页
- [ ] 未与其它 UI 库混用
