<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Watermark API

> 源码：`packages/C7Watermark/`
> 标签：`<C7Watermark>` / `<c7-watermark>`

页面/全屏水印：Canvas 生成平铺图，`image` 优先，失败回落 `text`。**防删**：`tamperResistant === true` 开启 MutationObserver；若仅传 `editable=false` 亦开启。若 **`tamperResistant` 显式传入**（含 `false`），**以其为准**，覆盖 `editable`。**`fullscreenScope`**：仅当 `fullscreen=true` 时生效；`false` 时忽略（见 props 说明）。

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `text` / `text` | 文本或字符串数组（多行）；图片失败时作为回落 | `[String, Array]` | '' |
| `image` / `image` | 图片 URL；优先于 `text`，加载或绘制失败回落文本 | `String` | '' |
| `fullscreen` / `fullscreen` | 全屏固定定位；`false` 为容器模式（水印层 `absolute` 铺满根节点） | `Boolean` | false |
| `fullscreen-scope` / `fullscreenScope` | 全屏覆盖范围：`viewport` 仅视口；`document` 随 `document.documentElement.scrollHeight` 延伸。仅 **`fullscreen=true`** 时生效。 | `String` | 'viewport' |
| `disabled` / `disabled` | 为 `true` 时不渲染水印、不挂载 MutationObserver / 文档尺寸监听 | `Boolean` | false |
| `tamper-resistant` / `tamperResistant` | 显式防删开关。传入任意布尔时 **优先于 `editable`**。未传入时：仅当 **`editable === false`** 时启用防删。 | `Boolean` | undefined |
| `editable` / `editable` | 兼容原始需求命名：`false` 表示开启防删（与 `tamperResistant: true` 同效），缺省或 `true` 表示不按该字段启用防删（仍可由 `tamperResistant` 显式控制）。 | `Boolean` | undefined |
| `z-index` / `zIndex` | 叠放层级（全屏必选；容器模式亦应用于水印层） | `Number` | 4100 |
| `cross-origin` / `crossOrigin` | 传给 `Image.crossOrigin`，如 `'anonymous'`、`''`；`undefined` 不传 | `String` | undefined |
| `font-size` / `fontSize` |  | `Number` | 16 |
| `font-color` / `fontColor` |  | `String` | 'rgba(0 |
| `font-family` / `fontFamily` |  | `String` | 'PingFang SC |
| `opacity` / `opacity` | 全局透明度 0~1 | `Number` | 1 |
| `rotate` / `rotate` | 旋转角（度） | `Number` | -22 |
| `gap-x` / `gapX` |  | `Number` | 100 |
| `gap-y` / `gapY` |  | `Number` | 100 |
| `width` / `width` | 单格内容区宽度（px） | `Number` | 160 |
| `height` / `height` | 单格内容区高度（px） | `Number` | 80 |
| `offset-x` / `offsetX` |  | `Number` | 0 |
| `offset-y` / `offsetY` |  | `Number` | 0 |

## 事件

| 事件 | 说明 |
| --- | --- |
| _无或未解析_ | |

## 插槽

| 插槽 |
| --- |
| `default` |
