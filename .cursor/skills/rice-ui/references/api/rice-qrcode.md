<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-qrcode API

> 组件名：`rice-qrcode`
> 官方文档：https://riceui.cn/components/qrcode.html
> 源码：`components/rice-qrcode/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `value` | 二维码的值 | `String` | - |
| `color` | 二维码颜色 | `String` | #000 |
| `bg-color` / `bgColor` |  | `string` | - |
| `icon` | logo 地址，支持本地和网络图片 | `String` | - |
| `icon-option` / `iconOption` | logo的宽高 | `UTSJSONObject` | {height:30,width:30} |
| `level` |  | `'L' ｜ 'M' ｜ 'Q' ｜ 'H'` | - |
| `auto-export` / `autoExport` | 二维码绘制完成后是否生成图片，可在complete中拿到 | `Boolean` | false |
| `padding` | 二维码的边距(静区) | `Number` | 2个色块的码边距 |
| `eye-shape` / `eyeShape` |  | `'square' ｜ 'circle' ｜ 'round'` | - |
| `dot-shape` / `dotShape` | 点的形状 square\dot | `String` | square |
| `size` | 二维码的尺寸 | `Number` | 160 |
| `status-class` / `statusClass` | 自定义状态区域的class | `String` | - |
| `show-status` / `showStatus` | 是否显示状态区域 | `Boolean` | false |
| `bg-color 1.1.6` | 二维码背景色 | `String` | #fff |
| `eye-shaper` | 码眼的形状 square\circle\round | `String` | square |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@complete` | 二维码绘制完成后触发，只要二维码改变了就会触发，不管是形状还是value值 | payload: QRCodeCompletePayload |
| `@error` | 二维码绘制失败时触发 | err: QRCodeError |
| `@getTempFilePath` | 获取二维码图片的地址 | 异步方法，App端返回的是临时地址，web/小程序返回的是base64 ，图片生成失败会返回空字符串 |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `status` |  |
| `000` |  |
| `fff` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
