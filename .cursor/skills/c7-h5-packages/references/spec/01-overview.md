# 01 · 概览与差异

## 目标

在 `quick-h5` 用与 `quick-ui` **同名**的 C7 组件写业务页，交互适配移动端（底部弹层、picker、toast/modal）。

## 与 packages 的刻意差异

| 点 | packages (EP) | H5 |
|---|---|---|
| 下拉 | ElSelect / Cascader | rice-picker / rice-cascader / popup 列表 |
| 弹窗 | ElDialog / Drawer | rice-popup（默认底部） |
| MessageBox | ElMessageBox | `uni.showModal` 封装 |
| 复制 | Clipboard API | `uni.setClipboardData` |
| 表格列 | el-table-column | 行内 `C7JsonTableColumn` / `#item` 插槽 |
| 水印 | Canvas + 防删 | CSS 平铺简化版 |

## 引入

- 组件：easycom 自动扫描 `components/c7-*`
- API：`import { c7Confirm, c7Alert, c7DangerConfirm, c7Loading } from '@/components/c7-message-box/index.uts'`

## 示例入口

- 我的 → 关于 QuickBoot / C7 组件示例
- `pages/examples/*`
