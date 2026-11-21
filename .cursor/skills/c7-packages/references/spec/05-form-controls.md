# 05 表单控件速查

均基于 Element Plus；业务侧统一用 C7 封装以便字典列表、异步加载、树/级联约定一致。

## 选择类

| 组件 | 典型 props | 说明 |
|---|---|---|
| `C7Select` / `c7-select` | `v-model`、`data-list`、`label-key`、`value-key`、`multiple` | 字典或远程选项 |
| `C7Radio` / `c7-radio` | `v-model`、`data-list` | 状态/枚举（标杆用状态） |
| `C7Checkbox` | `v-model`、`data-list` | 多选枚举 |
| `C7TreeSelect` | `fetch-data`、`value-key`、`label-key`、`children-key`、`auto-load` | 部门树等 |
| `C7Cascader` | 同树形异步约定 | 级联 |

字典数据：`const { user_status } = useDict('user_status')`，传 `user_status` 或 `user_status.value || []`（以控件是否解包 ref 为准；标杆 radio 直接传 `user_status`）。

## 日期时间

| 组件 | 说明 |
|---|---|
| `C7DatePicker` | `type="date" \| daterange | …` |
| `C7TimePicker` | 时间 |

搜索区 `daterange` 由 JsonTable 内部接 `C7DatePicker`。

## 展示

| 组件 | 说明 |
|---|---|
| `C7DictTag` | 值 → 字典标签展示 |
| `C7Descriptions` | 详情描述列表 |
| `C7Title` | 区块标题 |
| `C7Copy` | 一键复制 |
| `C7Preview` | 文件/媒体预览 |
| `C7Watermark` | 水印 |
| `C7Card` / `C7CardGrid` | 卡片 / 卡片栅格列表 |
| `C7FormLabel` | 表单标签辅助 |
| `C7Pagination` | 独立分页（JsonTable 已内置时可不必再用） |

## 上传 / Excel

| 组件 | 说明 |
|---|---|
| `C7Upload` | 业务上传（规则/分类） |
| `C7ExcelUpload` | Excel 导入 |
| `C7ExcelDownload` | Excel 导出（JsonTable 导出也可内置） |

## JsonTable 列渲染

- `columnType: 'tag'` + `dictList` → 内部字典标签
- `columnType: 'slot'` → 页面 `#prop` 插槽

完整 props 见各 `api/C7*.md`。
