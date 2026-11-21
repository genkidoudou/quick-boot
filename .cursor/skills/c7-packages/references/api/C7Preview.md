<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Preview API

> 源码：`packages/C7Preview/`
> 标签：`<C7Preview>` / `<c7-preview>`

附件预览：逗号分隔 `urls`，支持 **图片大图 / 视频弹窗 / 文件新窗口**。**钩子**：**`onPreview`** 返回 **`false` 或 reject** 时 **不**打开预览且 **不** **`emit('preview')`**；通过时 **先** **`emit('preview')`** 再打开。@emits preview — `(url: string, index: number)`@emits close — 视频弹窗关闭且已 **pause + 进度归零** 后触发一次

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `urls` / `urls` | 逗号分隔 URL；空段在解析时丢弃 | `String` | '' |
| `display-type` / `displayType` | `autoDetect=false` 时整条列表按此类型渲染 | `String` | 'image' |
| `auto-detect` / `autoDetect` | 为 `true` 时按扩展名推断每条类型 | `Boolean` | true |
| `cover-type` / `coverType` | `none` 平铺、`button` 聚合、`file` 表格 | `String` | 'none' |
| `width` / `width` | 封面宽度（px） | `Number` | undefined |
| `height` / `height` | 封面高度（px） | `Number` | undefined |
| `preview-text` / `previewText` | `coverType=button` 时按钮文案 | `String` | '预览' |
| `video-dialog-title` / `videoDialogTitle` | 视频弹窗标题 | `String` | '视频预览' |
| `on-preview` / `onPreview` | 预览前拦截；返回 **`false` / `Promise<false>` / reject** 阻止。@type {(url: string, index: number) => boolean ｜ Promise<boolean> ｜ undefined} | `Function` | null |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@preview` | — `(url: string, index: number)` |
| `@close` | — 视频弹窗关闭且已 |

## 插槽

| 插槽 |
| --- |
| `default` |
| `000` |
