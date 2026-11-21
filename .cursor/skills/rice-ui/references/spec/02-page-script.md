# 02 页面脚本

> 官方提醒：使用前需了解 uni-app x；蒸汽模式仅支持组合式 API。  
> Dialog 等示例见 https://riceui.cn/components/dialog.html

## 基本约定

- 页面使用 `<script setup>`（组合式）
- 蒸汽模式下页面脚本优先 **TS**（`lang="ts"`）；库内组件仍可能是 `.uts`，可共存
- 组件通过 easycom 使用，业务里一般 **不要** 手动 import `.uvue` 组件文件
- 需要类型或 API 时从入口导入：

```ts
import { setTheme, showDialog, hideDialog, showActionSheet } from "@/uni_modules/rice-ui"
// 按需：dateuts / Coloruts / 各组件 type 等，以源码 index.uts 导出为准
```

## 组件式 vs API 式弹层

**组件式**（页面内声明）：

```vue
<rice-dialog v-model:show="show" title="标题" :message="message" @confirm="onConfirm" />
```

**API 式**（需已注册 dialog 页，见 `spec/01`）：

```ts
import { showDialog } from "@/uni_modules/rice-ui"
showDialog({
  title: '标题',
  message: '内容',
  confirm: () => {},
  cancel: () => {}
})
```

ActionSheet 同理：组件 `rice-action-sheet` 或 `showActionSheet`（官方 quickstart）。

## 模板绑定红线（uni-app x）

| ❌ | ✅ |
|---|---|
| `title="{{ x }}"` | `:title="x"` |
| 在 view 上写文字色/字号指望继承 | 文字放 `<text>` 或组件自带 text 属性 |
| Options API / mixin（蒸汽） | 仅 setup |

## 事件与 v-model

- 以各组件官方文档 / `references/api/rice-*.md` 为准
- 常见：`v-model:show`（dialog/popup）、表单类看卡片是否声明 v-model
- 微信开放能力事件（如 `@getphonenumber`）仅小程序有效，见 button 文档

## 生命周期

使用 Vue / uni-app x 页面生命周期（`onLoad`、`onShow`、`onUnload` 等）。App 返回拦截示例见官方 Dialog 文档中的 `onBackPress`。
