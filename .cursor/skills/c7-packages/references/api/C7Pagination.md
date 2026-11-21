<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Pagination API

> 源码：`packages/C7Pagination/`
> 标签：`<C7Pagination>` / `<c7-pagination>`

C7 业务分页：在 `ElPagination` 上统一 **双绑**、**切换 pageSize 后回第一页（`autoReset`）** 与 **`change(page, pageSize)` 单次汇总**。**`change` 语义**：每次用户操作导致的 **最终** `currentPage` + `pageSize` 只通知一次。`autoReset=true` 且用户改条数时，内部会忽略 Element Plus 在同一更新周期内可能产生的 **中间态** `change`，并在 `nextTick` 中 **`emit('change', 1, newPageSize)`**。**`autoReset=false`**：不强制回第 1 页；页码纠正与 `ElPagination` 一致。@emits update:currentPage@emits update:pageSize@emits current-change 当前页变化（与 EP 一致）@emits size-change 每页条数变化@emits prev-click / next-click@emits change(page, pageSize) 汇总后的最终态

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `current-page` / `currentPage` | 当前页（从 1 起），与 `v-model:currentPage` 同步 | `Number` | （必填） |
| `page-size` / `pageSize` | 每页条数，与 `v-model:pageSize` 同步 | `Number` | （必填） |
| `total` / `total` | 总条数，与 `ElPagination` 一致 | `Number` | undefined |
| `page-sizes` / `pageSizes` | 可选每页条数列表 | `Array` | undefined |
| `layout` / `layout` | 布局字符串，未传则使用 Element Plus 默认 | `String` | undefined |
| `auto-reset` / `autoReset` | 为 `true`（默认）时，用户切换 `pageSize` 后将 `currentPage` 置为 1。为 `false` 时不强制回第一页。 | `Boolean` | true |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:currentPage` |  |
| `@update:pageSize` |  |
| `@current-change` | 当前页变化（与 EP 一致） |
| `@size-change` | 每页条数变化 |
| `@prev-click` | / next-click |
| `@next-click` |  |
| `@change` |  |

## 插槽

| 插槽 |
| --- |
| `default` |
