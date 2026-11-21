<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-image API

> 组件名：`rice-image`
> 官方文档：https://riceui.cn/components/image.html
> 源码：`components/rice-image/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `src` |  | `string` | - |
| `mode` | 图片裁剪、缩放的模式，等同于官方 image 组件的 mode | `string` | scaleToFill |
| `width` |  | `string ｜ number` | - |
| `height` |  | `string ｜ number` | - |
| `round` | 是否为圆形 | `boolean` | false |
| `radius` |  | `string ｜ number` | - |
| `show-error` / `showError` | 是否展示图片加载失败的提示 | `boolean` | true |
| `show-loading` / `showLoading` | 是否展示图片加载中的提示 | `boolean` | true |
| `error-icon` / `errorIcon` |  | `string` | - |
| `loading-icon` / `loadingIcon` |  | `string` | - |
| `icon-size` / `iconSize` |  | `string` | - |
| `icon-color` / `iconColor` |  | `string` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `preview` | 是否开启图片预览功能 | `boolean` | false |
| `preview-src-list` / `previewSrcList` |  | `string[]` | - |
| `preview-index` / `previewIndex` |  | `number` | - |
| `lazy-load` / `lazyLoad` |  | `boolean` | - |
| `fade-show` / `fadeShow` |  | `boolean` | - |
| `show-menu-by-longpress` / `showMenuByLongpress` |  | `boolean` | - |
| `draggable` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@load` | 图片加载完成时触发 | e:UniImageLoadEvent |
| `@error` | 图片加载错误时触发 | e:UniImageErrorEvent |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `error` |  |
| `errorIcon` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
| `loading` |  |
| `loadingIcon` |  |
