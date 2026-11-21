<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-button API

> 组件名：`rice-button`
> 官方文档：https://riceui.cn/components/button.html
> 源码：`components/rice-button/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `type` |  | `ButtonType` | - |
| `size` |  | `ButtonSize` | - |
| `text` |  | `string ｜ number` | - |
| `color` |  | `string` | - |
| `dark-color` / `darkColor` |  | `string` | - |
| `text-color` / `textColor` |  | `string` | - |
| `text-size` / `textSize` | 文字大小 | `string｜number` | 14px |
| `icon` |  | `string ｜ number` | - |
| `icon-size` / `iconSize` | 图标大小 | `string｜number` | 16px |
| `icon-color` / `iconColor` |  | `string` | - |
| `icon-font-family` / `iconFontFamily` |  | `string` | - |
| `plain-fill` / `plainFill` | 是否为带浅色背景的朴素按钮 | `boolean` | false |
| `plain` | 是否为朴素按钮 | `boolean` | false |
| `disabled` | 是否禁用 | `boolean` | false |
| `loading` | 是否加载状态 | `boolean` | false |
| `loading-size` / `loadingSize` | 加载图标大小 | `string｜number` | 18px |
| `loading-text` / `loadingText` |  | `string ｜ number` | - |
| `loading-mode` / `loadingMode` |  | `LoadingMode` | - |
| `shape` |  | `ButtonShape` | - |
| `width` |  | `string ｜ number` | - |
| `height` |  | `string ｜ number` | - |
| `open-type` / `openType` |  | `string` | - |
| `form-type` / `formType` |  | `string` | - |
| `lang` |  | `string` | - |
| `session-from` / `sessionFrom` |  | `string` | - |
| `send-message-title` / `sendMessageTitle` |  | `string` | - |
| `send-message-img` / `sendMessageImg` |  | `string` | - |
| `send-message-path` / `sendMessagePath` |  | `string` | - |
| `show-message-card` / `showMessageCard` |  | `string` | - |
| `app-parameter` / `appParameter` |  | `string` | - |
| `to` |  | `string` | - |
| `custom-style` / `customStyle` | 自定义样式 | `Object` | - |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@click` |  | - |
| `@getphonenumber` | 手机号快速验证回调， open-type=getPhoneNumber 时有效，仅微信小程序支持 | - |
| `@getrealtimephonenumber` | 手机号实时验证回调， open-type=getRealtimePhoneNumber 时有效,仅微信小程序支持 | - |
| `@getuserinfo` | 获取用户信息的回调， open-type=getUserInfo 时有效,仅微信小程序支持 | - |
| `@chooseavatar` | 获取用户头像回调， open-type=chooseAvatar 时有效,仅微信小程序支持 | - |
| `@launchapp` | 打开 APP 成功的回调， open-type=launchApp 时有效,仅微信小程序支持 | - |
| `@agreeprivacyauthorization` | 用户同意隐私协议事件回调， open-type=agreePrivacyAuthorization 时有效,仅微信小程序支持 | - |
| `@opensetting` | 在打开授权设置页后回调， open-type=openSetting 时有效 时有效,仅微信小程序支持 | - |
| `@error` | 当使用开放能力时，发生错误的回调， open-type=launchApp 时有效,仅微信小程序支持 | - |
| `@contact` | 打开客服会话回调， open-type=contact 时有效,仅微信小程序支持 | - |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `icon` |  |
| `ifdef` |  |
| `endif` |  |
| `f5f5f5` |  |
| `ifndef` |  |
| `141414` |  |
