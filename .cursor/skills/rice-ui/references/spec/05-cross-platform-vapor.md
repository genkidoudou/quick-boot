# 05 三端兼容与蒸汽模式

> 蒸汽说明：https://doc.dcloud.net.cn/uni-app-x/app-vapor.html  
> 组件兼容表以各页「平台兼容性」为准，例如 https://riceui.cn/components/loading.html

## 蒸汽版本门槛（RiceUI 更新日志）

| 平台 | 蒸汽支持（库版本） | HBuilderX |
|---|---|---|
| HarmonyOS | 1.1.0+ | ≥ 5.08 |
| iOS | 1.1.3+ | ≥ 5.11 |
| Android | 1.1.9+ | ≥ 5.21 |

非蒸汽可用更低版本，但新项目建议直接蒸汽 + 较新 HBX。

## 条件编译宏

| 宏 | 含义 |
|---|---|
| `APP` / `APP-ANDROID` / `APP-IOS` / `APP-HARMONY` | App |
| `VUE3-VAPOR` | 蒸汽模式 |
| `WEB` | H5 |
| `MP` / `MP-WEIXIN` | 小程序 |

禁止沿用老 uni-app 的 `APP-PLUS` 等无效宏。

库内复杂组件（picker、tabs、waterflow、loading、swipe 等）已用 `#ifdef VUE3-VAPOR` 分支；业务侧勿假设 VDOM 与蒸汽行为完全一致，以真机为准。

## 红线速查

| # | 场景 | ❌ | ✅ |
|---|---|---|---|
| 1 | 蒸汽 API | Options API / mixin | `<script setup>` |
| 2 | 文字样式 | view 上写 font/color | text 组件或组件 text 属性 |
| 3 | 布局 | block / 依赖默认横向 | flex + direction |
| 4 | 样式隔离 2.0 H5 | 只在 App.uvue @import | main 里 `#ifdef WEB` import |
| 5 | showDialog | 未合并 pages 就调用 | 合并 pages_init 或改用组件式 |
| 6 | waterflow 蒸汽 | 强依赖非官方行为 | 优先官方 waterflow（官方建议） |
| 7 | 混用 UI 库 | rice + ux 同页 | 只用 rice-* |

## 症状 → 修法

| 症状 | 修法 |
|---|---|
| H5 主题/基础样式丢失 | 样式隔离 2.0 下改 main WEB import |
| showDialog 无效 | 检查 dialog 页是否注册 |
| App 文字样式不生效 | 改到 text |
| 蒸汽编译/运行异常 | 升级 HBX 与 rice-ui；查组件页 Vapor 列 |
| loading circular 蒸汽表现差异 | 见 loading 文档：鸿蒙/iOS 蒸汽下部分 mode 走官方 loading |
| 图标不显示 | 核对 icon name / 自定义 font-family（changelog 有 iOS 相关修复） |
