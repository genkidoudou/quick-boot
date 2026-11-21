# 03 C7JsonTable

> 卡片：`api/C7JsonTable.md` · 源码：`packages/C7JsonTable/index.vue`

## 职责

列表页一站式：搜索区 + 工具栏（增删改/导入导出）+ 表格 + 分页。  
数据通过 **`listFunction`** 拉取；响应默认读 `data.records` / `data.total`（可用 `rowsKey` / `totalKey` 改）。

## 最小用法（标杆）

```vue
<C7JsonTable
  ref="tableRef"
  :list-function="pageUser"
  :table-columns="tableColumns"
  :search-columns="searchColumns"
  row-key="id"
  show-add-button
  show-edit-button
  :delete-function="removeUser"
  :add-button-permi="['system:user:add']"
  :edit-button-permi="['system:user:edit']"
  :delete-button-permi="['system:user:remove']"
  @add-click="() => formDialogRef?.open()"
  @edit-click="(row) => formDialogRef?.open(row)"
>
  <template #actions="{ row }">
    <el-button link type="primary" :icon="Delete" v-hasPermi="['system:user:remove']"
      @click="tableRef?.removeRows(row, { confirmMessage: `确认删除「${row.id}」？` })">
      删除
    </el-button>
  </template>
</C7JsonTable>
```

## tableColumns（computed）

常用字段：

| 字段 | 说明 |
|---|---|
| `prop` / `label` | 列字段与标题 |
| `minWidth` / `width` / `fixed` / `align` | 布局 |
| `columnType: 'tag'` | 字典标签，配 `dictList` |
| `columnType: 'slot'` | 自定义列，插槽名 = `prop` |
| `showOverflowTooltip` | 超长省略 |

操作列惯例：`{ prop: 'actions', label: '操作', columnType: 'slot', width, fixed: 'right' }`。

## searchColumns（computed）

| `type` | 控件 |
|---|---|
| `input`（默认） | `el-input` |
| `select` | `C7Select`，`dataList` / `options` |
| `date` / `daterange` | `C7DatePicker` |
| `slot` | 具名插槽（`prop`） |

可用 `span`、`props` 透传控件属性。

## 权限与工具栏

- `show-add-button` / `show-edit-button` + `*-button-permi` 数组（空数组 = 不校验权限）
- 有 `deleteFunction` 时删除按钮可按权限显示
- 导入/导出：配 `importFunction` / `exportFunction` 等（见卡片）

## 重要方法（defineExpose）

- `refreshData()` — 提交成功后刷新
- `removeRows(row|rows, { confirmMessage })` — 行内/批量删除
- `getDataList` / 选中行相关：以源码 `defineExpose` 为准

## 事件

常用：`add-click`、`edit-click`、`selection-change`、`search-submit`、`delete-success` 等（见卡片）。

## 红线

- `tableColumns` / `searchColumns` 必须是 `computed`
- 不要把审计字段放进列配置
- 行内删除优先 `removeRows`，不要自造一套确认+请求（除非特殊业务）
