<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7CardGrid API

> 源码：`packages/C7CardGrid/`
> 标签：`<C7CardGrid>` / `<c7-card-grid>`

卡片网格列表壳：搜索区 + 可选工具栏 + 卡片网格 + 分页。数据请求与分页逻辑对齐 {@link C7JsonTable}，展示层由 `#card` 插槽自定义。@prop {function(object): Promise<unknown>} listFunction 列表请求函数

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `list-function` / `listFunction` |  | `Function` | （必填） |
| `search-columns` / `searchColumns` |  | `Array` | () => [] |
| `default-search-param` / `defaultSearchParam` |  | `Object` | () => ({}) |
| `rows-key` / `rowsKey` |  | `String` | 'data.records' |
| `total-key` / `totalKey` |  | `String` | 'data.total' |
| `row-key` / `rowKey` |  | `String` | 'id' |
| `page-sizes` / `pageSizes` |  | `Array` | () => [12, 24, 48] |
| `default-page-size` / `defaultPageSize` |  | `Number` | 12 |
| `empty-text` / `emptyText` |  | `String` | '暂无数据' |
| `min-card-width` / `minCardWidth` | 网格最小列宽（px），用于 auto-fill | `Number` | 260 |
| `grid-gap` / `gridGap` | 卡片间距（px） | `Number` | 16 |
| `show-toolbar` / `showToolbar` |  | `Boolean` | true |
| `show-search-button` / `showSearchButton` |  | `Boolean` | true |
| `show-reset-button` / `showResetButton` |  | `Boolean` | true |
| `search-button-text` / `searchButtonText` |  | `String` | '查询' |
| `reset-button-text` / `resetButtonText` |  | `String` | '重置' |
| `search-button-props` / `searchButtonProps` |  | `Object` | () => ({}) |
| `reset-button-props` / `resetButtonProps` |  | `Object` | () => ({}) |
| `on-search` / `onSearch` |  | `Function` | undefined |
| `on-reset` / `onReset` |  | `Function` | undefined |
| `before-fetch` / `beforeFetch` |  | `Function` | undefined |
| `show-add-button` / `showAddButton` |  | `Boolean` | false |
| `add-button-permi` / `addButtonPermi` |  | `Array` | () => [] |
| `add-button-text` / `addButtonText` |  | `String` | '新增' |
| `add-button-props` / `addButtonProps` |  | `Object` | () => ({}) |
| `on-add` / `onAdd` |  | `Function` | undefined |
| `show-add-card` / `showAddCard` | 是否在网格首位展示「新增」占位卡 | `Boolean` | false |
| `add-card-permi` / `addCardPermi` |  | `Array` | () => [] |
| `add-card-text` / `addCardText` |  | `String` | '新增' |
| `on-add-card` / `onAddCard` |  | `Function` | undefined |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:searchParam` |  |
| `@before-fetch` |  |
| `@after-fetch` |  |
| `@fetch-error` |  |
| `@add-click` |  |
| `@add-card-click` |  |
| `@search-submit` |  |
| `@search-reset` |  |

## 插槽

| 插槽 |
| --- |
| `search-extra` |
| `toolbar-left` |
| `toolbar-right` |
| `add-card` |
| `card` |

## 暴露方法

`refreshData`、`getDataList`、`searchParam`、`currentPage`、`currentPageSize`、`total`、`cardRows`
