<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7JsonTable API

> 源码：`packages/C7JsonTable/`
> 标签：`<C7JsonTable>` / `<c7-json-table>`

一体化 JSON 配置表格：包含搜索区、工具栏、表格、分页、列设置、删除等能力。取消列表请求请使用 `beforeFetch` 属性，返回 `false` 或 `Promise<false>` 可阻止调用 `listFunction`。`before-fetch` 事件仅用于监听参数，返回值不会参与拦截。@prop {function(object): Promise<unknown>} listFunction 列表请求函数，入参对齐后端 {@code PageRequest}：`{ current, size, param }`；`param` 为搜索条件，并在有排序时附带 `orderByColumn`、`isAsc`。

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `list-function` / `listFunction` | 列表请求函数 | `Function` | （必填） |
| `table-columns` / `tableColumns` | 表格列配置（传给 C7JsonTableColumn） | `Array` | （必填） |
| `search-columns` / `searchColumns` | 搜索列配置（JsonForm 字段子集） | `Array` | () => [] |
| `default-search-param` / `defaultSearchParam` | 搜索默认值；重置时恢复此快照 | `Object` | () => ({}) |
| `rows-key` / `rowsKey` | 从响应中提取行数组的路径（默认对齐 `{ data: { records, total } }`） | `String` | 'data.records' |
| `total-key` / `totalKey` | 从响应中提取总数的路径 | `String` | 'data.total' |
| `row-key` / `rowKey` | 行主键字段名，透传给 `el-table` 的 `row-key` | `String` | 'id' |
| `border` / `border` |  | `Boolean` | true |
| `stripe` / `stripe` |  | `Boolean` | true |
| `show-selection` / `showSelection` |  | `Boolean` | true |
| `show-index` / `showIndex` |  | `Boolean` | false |
| `column-setting-key` / `columnSettingKey` | 列显隐持久化 key；不传则不展示列设置 | `String` | '' |
| `delete-function` / `deleteFunction` | 批量删除 API：入参为 id 数组 | `Function` | undefined |
| `export-function` / `exportFunction` | 导出：返回 Blob 或 `{ data, headers }`。入参为搜索条件快照；有勾选时附带 `ids`（rowKey 数组）。 | `Function` | undefined |
| `export-default-file-name` / `exportDefaultFileName` | 导出默认文件名（响应头无 Content-Disposition 时） | `String` | 'export.xlsx' |
| `export-loading-options` / `exportLoadingOptions` | 导出 Loading：默认在表格区域遮罩（文案「导出中」）。`false` 关闭区域遮罩（仅按钮 loading）；传对象可覆盖 ElLoading 配置；`true` 恢复旧版全屏遮罩。 | `[Boolean, Object]` | undefined |
| `import-function` / `importFunction` | 导入：uploadFn(file, strategy) | `Function` | undefined |
| `import-template-download-fn` / `importTemplateDownloadFn` | 导入模板下载 | `Function` | undefined |
| `import-template-file-name` / `importTemplateFileName` |  | `String` | 'import-template.xlsx' |
| `import-max-size-mb` / `importMaxSizeMb` |  | `Number` | 10 |
| `before-delete` / `beforeDelete` | 删除前钩子，返回 `false` 可取消 | `Function` | undefined |
| `check-delete-success` / `checkDeleteSuccess` | 自定义删除成功判定 | `Function` | undefined |
| `delete-confirm-message` / `deleteConfirmMessage` |  | `String` | '确认删除选中记录吗？' |
| `empty-text` / `emptyText` | 透传给 C7JsonTableColumn 的表格空文案 | `String` | undefined |
| `page-sizes` / `pageSizes` | 分页可选条数 | `Array` | () => [10, 20, 50, 100] |
| `lazy` / `lazy` |  | `Boolean` | undefined |
| `load` / `load` |  | `Function` | undefined |
| `tree-props` / `treeProps` |  | `Object` | undefined |
| `before-fetch` / `beforeFetch` | 列表请求前钩子；返回 `false` 或 `Promise<false>` 时不调用 `listFunction`。@param {Record<string, unknown>} params 即将传给 listFunction 的参数 | `Function` | undefined |
| `show-search-button` / `showSearchButton` |  | `Boolean` | true |
| `show-reset-button` / `showResetButton` |  | `Boolean` | true |
| `search-button-text` / `searchButtonText` |  | `String` | '搜索' |
| `reset-button-text` / `resetButtonText` |  | `String` | '重置' |
| `search-button-props` / `searchButtonProps` |  | `Object` | () => ({}) |
| `reset-button-props` / `resetButtonProps` |  | `Object` | () => ({}) |
| `on-search` / `onSearch` |  | `Function` | undefined |
| `on-reset` / `onReset` |  | `Function` | undefined |
| `show-add-button` / `showAddButton` |  | `Boolean` | false |
| `show-edit-button` / `showEditButton` |  | `Boolean` | false |
| `show-delete-button` / `showDeleteButton` |  | `Boolean` | undefined |
| `show-export-button` / `showExportButton` |  | `Boolean` | undefined |
| `show-import-button` / `showImportButton` |  | `Boolean` | undefined |
| `add-button-permi` / `addButtonPermi` | 工具栏「新增」所需权限（与 v-hasPermi 一致）；非空时无权限则不展示 | `Array` | () => [] |
| `edit-button-permi` / `editButtonPermi` |  | `Array` | () => [] |
| `delete-button-permi` / `deleteButtonPermi` |  | `Array` | () => [] |
| `export-button-permi` / `exportButtonPermi` |  | `Array` | () => [] |
| `import-button-permi` / `importButtonPermi` |  | `Array` | () => [] |
| `add-button-text` / `addButtonText` |  | `String` | '新增' |
| `edit-button-text` / `editButtonText` |  | `String` | '修改' |
| `delete-button-text` / `deleteButtonText` |  | `String` | '删除' |
| `export-button-text` / `exportButtonText` |  | `String` | '导出' |
| `import-button-text` / `importButtonText` |  | `String` | '导入' |
| `add-button-props` / `addButtonProps` |  | `Object` | () => ({}) |
| `edit-button-props` / `editButtonProps` |  | `Object` | () => ({}) |
| `delete-button-props` / `deleteButtonProps` |  | `Object` | () => ({}) |
| `on-add` / `onAdd` |  | `Function` | undefined |
| `on-edit` / `onEdit` |  | `Function` | undefined |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:searchParam` |  |
| `@before-fetch` |  |
| `@after-fetch` |  |
| `@fetch-error` |  |
| `@selection-change` |  |
| `@sort-change` |  |
| `@delete-success` |  |
| `@export-success` |  |
| `@import-success` |  |
| `@add-click` |  |
| `@edit-click` |  |
| `@search-submit` |  |
| `@search-reset` |  |

## 插槽

| 插槽 |
| --- |
| `search-extra` |
| `toolbar-left` |
| `exportDefaultFileName` |
| `toolbar-right` |
| `table-columns` |
| `name` |
| `importTemplateFileName` |
| `909399` |
| `fff` |
| `e4e7ed` |
| `ebeef5` |
| `f0f2f5` |
| `303133` |

## 暴露方法

`refreshData`、`getDataList`、`selectedRows`、`searchParam`、`currentPage`、`currentPageSize`、`total`、`tableRef`
