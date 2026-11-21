<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-uploader API

> 组件名：`rice-uploader`
> 官方文档：https://riceui.cn/components/uploader.html
> 源码：`components/rice-uploader/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `action` | 文件上传地址，会覆盖 prop 中 action 的值 | `String` | - |
| `accept` |  | `UploaderAccept` | - |
| `auto-upload` / `autoUpload` |  | `boolean` | - |
| `name` | 文件对应的 key , 开发者在服务器端通过这个 key 可以获取到文件二进制内容，会覆盖 prop 中 name 的值 | `String` | - |
| `header` | HTTP 请求 Header, header 中不能设置 Referer，会和prop中的 header 合并 | `UTSJSONObject` | - |
| `form-data` / `formData` | HTTP 请求中其他额外的 form data，会和prop中的 formData 合并 | `UTSJSONObject` | - |
| `timeout` |  | `number` | - |
| `max-count` / `maxCount` |  | `number` | - |
| `max-duration` / `maxDuration` |  | `number` | - |
| `show-file-list` / `showFileList` |  | `boolean` | - |
| `preview` |  | `boolean` | - |
| `image-mode` / `imageMode` |  | `string` | - |
| `object-fit` / `objectFit` |  | `string` | - |
| `upload-icon` / `uploadIcon` |  | `string` | - |
| `upload-icon-style` / `uploadIconStyle` |  | `UTSJSONObject` | - |
| `upload-text` / `uploadText` |  | `string` | - |
| `loading-text` / `loadingText` |  | `string` | - |
| `upload-fail-text` / `uploadFailText` |  | `string` | - |
| `show-percent` / `showPercent` |  | `boolean` | - |
| `show-success` / `showSuccess` |  | `boolean` | - |
| `deletable` | 文件是否可以删除，会覆盖 prop 中 deletable 的值 | `String` | - |
| `delete-icon` / `deleteIcon` |  | `string` | - |
| `delete-icon-style` / `deleteIconStyle` |  | `UTSJSONObject` | - |
| `width` |  | `string ｜ number` | - |
| `height` |  | `string ｜ number` | - |
| `bg-color` / `bgColor` |  | `string` | - |
| `disabled` |  | `boolean` | - |
| `readonly` |  | `boolean` | - |
| `camera` |  | `string` | - |
| `page-orientation` / `pageOrientation` |  | `string` | - |
| `upload-icon-size` / `uploadIconSize` |  | `string ｜ number` | - |
| `delete-style` / `deleteStyle` |  | `UTSJSONObject` | - |
| `size-type` / `sizeType` |  | `string[]` | - |
| `source-type` / `sourceType` |  | `string[]` | - |
| `extension` |  | `string[]` | - |
| `success-code` / `successCode` |  | `number` | - |
| `before-read` / `beforeRead` |  | `(files : UploaderFileItem[]) => Promise<any> ｜ any` | - |
| `after-read` / `afterRead` |  | `(files : UploaderFileItem[]) => void` | - |
| `before-upload` / `beforeUpload` |  | `(files : UploaderFileItem) => Promise<any> ｜ any` | - |
| `before-remove` / `beforeRemove` |  | `(file : UploaderFileItem` | - |
| `on-success` / `onSuccess` |  | `(response : UploaderSuccess` | - |
| `on-fail` / `onFail` |  | `(response : UploaderFail` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `url` | 文件地址，必传 | `String` | - |
| `poster` | 文件封面地址，会覆盖url的值 | `String` | - |
| `file-name` / `fileName` | 文件名称，非H5端为空字符串 | `String` | - |
| `status` | 上传状态，值为： ready 、 uploading 、 success failed | `String` | - |
| `message` | 上传中/上传失败时的提示语，会覆盖prop中 loadingText / uploadFailText 的值 | `String` | - |
| `percent` | 上传进度的百分百，会覆盖 message 的值，单prop的 showPercent 为 true 时才有效 | `String` | - |
| `size` | 文件大小，单位：B | `Number` | - |
| `duration` | 视频时长 | `Number` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@max-count` | Number | 最大可上传数量 |
| `@max-duration` | Number | 拍摄视频最长拍摄时间，单位秒。最长支持 60 秒，mode为 video时有效 |
| `@show-file-list` | Boolean | 是否显示已上传文件列表 |
| `@upload-fail-text` | String | 上传失败时的提示文字 |
| `@show-success` | Boolean | 上传成功后是否显示成功的icon |
| `@deletable` | 文件是否可以删除，会覆盖 prop 中 deletable 的值 | 是否显示删除按钮，正在上传时恒不显示 |
| `@page-orientation` | String | 屏幕方向。，可选值：auto、portrait、landscape |
| `@add` | 文件选择完成后触发 | files: UploaderFileItem[] |
| `@remove` | 文件删除完成后触发 | file: UploaderFileItem , index: number |
| `@complete` | 文件全部上传完成后触发 | files: UploaderFileItem[] , result: UploaderComplete[] |
| `@clickPreview` | 点击文件预览时触发，非图片格式的文件需自行实现预览功能 | file: UploaderFileItem , files: UploaderFileItem[] |
| `@error` | 选择文件错误时触发 | errMsg:string |
| `@url` | 文件地址，必传 | String |
| `@poster` | 文件封面地址，会覆盖url的值 | String |
| `@fileName` | 文件名称，非H5端为空字符串 | String |
| `@status` | 上传状态，值为： ready 、 uploading 、 success failed | String |
| `@message` | 上传中/上传失败时的提示语，会覆盖prop中 loadingText / uploadFailText 的值 | String |
| `@percent` | 上传进度的百分百，会覆盖 message 的值，单prop的 showPercent 为 true 时才有效 | String |
| `@size` | 文件大小，单位：B | Number |
| `@duration` | 视频时长 | Number |
| `@name` | 文件对应的 key , 开发者在服务器端通过这个 key 可以获取到文件二进制内容，会覆盖 prop 中 name 的值 | String |
| `@action` | 文件上传地址，会覆盖 prop 中 action 的值 | String |
| `@formData` | HTTP 请求中其他额外的 form data，会和prop中的 formData 合并 | UTSJSONObject |
| `@header` | HTTP 请求 Header, header 中不能设置 Referer，会和prop中的 header 合并 | UTSJSONObject |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `file-success-fill` |  |
| `play` |  |
| `fff` |  |
| `clear` |  |
| `delete` |  |
| `deleteIcon` |  |
| `checked` |  |
| `uploadIcon` |  |
| `ifdef` |  |
| `endif` |  |
| `ifndef` |  |
