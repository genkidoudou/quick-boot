# 01 安装与接入

## 入口

`quick-ui/src/main.js` 已调用：

```js
import { installPackages } from '@/packages'
// …
installPackages(app)
```

`installPackages` 会把 packages 内组件以 **同名** 全局注册（如 `C7Button`、`C7JsonTable`）。

## 导出

`@/packages`（即 `src/packages/index.js`）同时：

- `export` 各组件与 MessageBox API
- `export function installPackages(app)`

业务页一般 **无需再 import 组件**；按需局部导入亦可（标杆 user 页对 `C7Select` 有局部 import，两种都合法）。

## 依赖

- Vue 3 + Element Plus
- 部分能力依赖项目内：`@/utils/dict`、`@/utils/validate`、`v-hasPermi`、axios `request` 封装

## 核对

- [ ] 应用已 `installPackages`
- [ ] 新 CRUD 页走 `C7JsonTable` + `C7FormDialog`
- [ ] 未把 packages Demo/测试文件当业务依赖
