<!-- 由 scripts/gen-api-cards.mjs 生成；官方文档优先 -->

# rice-calendar API

> 组件名：`rice-calendar`
> 官方文档：https://riceui.cn/components/calendar.html
> 源码：`components/rice-calendar/`
> 文档抓取：成功（生成时对照 https://riceui.cn ）

RiceUI 组件。完整说明与示例以官方文档为准。

## 属性

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| `mode` |  | `CalendarMode` | - |
| `date` |  | `any ｜ null` | - |
| `switch-mode` / `switchMode` |  | `CalendarSwitchMode` | - |
| `min-date` / `minDate` |  | `string` | - |
| `max-date` / `maxDate` |  | `string` | - |
| `lunar` | 是否显示农历 | `Boolean` | false |
| `title` |  | `string` | - |
| `show-title` / `showTitle` |  | `boolean` | - |
| `color` |  | `string` | - |
| `formatter` |  | `CalendarFormatter` | - |
| `show-subtitle` / `showSubtitle` |  | `boolean` | - |
| `show-confirm-btn` / `showConfirmBtn` |  | `boolean` | - |
| `confirm-text` / `confirmText` |  | `string` | - |
| `confirm-disabled-text` / `confirmDisabledText` |  | `string` | - |
| `max-range` / `maxRange` |  | `number` | - |
| `range-prompt` / `rangePrompt` |  | `string` | - |
| `show-range-prompt` / `showRangePrompt` |  | `boolean` | - |
| `allow-same-day` / `allowSameDay` |  | `boolean` | - |
| `row-height` / `rowHeight` |  | `string ｜ number` | - |
| `readonly` |  | `boolean` | - |
| `first-day-of-week` / `firstDayOfWeek` | 设置周起始日,1-7，1表示星期一，以此类推，7表示星期天 | `Number` | 1 |
| `closeable` |  | `boolean` | - |
| `close-icon` / `closeIcon` |  | `string` | - |
| `use-popup` / `usePopup` |  | `boolean` | - |
| `close-on-click-close` / `closeOnClickClose` |  | `boolean` | - |
| `close-on-click-overlay` / `closeOnClickOverlay` | 点击遮罩是否关闭 | `Boolean` | true |
| `safe-area-inset-bottom` / `safeAreaInsetBottom` |  | `boolean` | - |
| `z-index` / `zIndex` | zIndex 层级 | `Number` | 999 |
| `weekly-calendar` / `weeklyCalendar` |  | `boolean` | - |
| `show-weekly-text` / `showWeeklyText` |  | `boolean` | - |
| `show-full-date` / `showFullDate` |  | `boolean` | - |
| `show-month-mark` / `showMonthMark` |  | `boolean` | - |
| `custom-style` / `customStyle` |  | `UTSJSONObject` | - |
| `switch-mode 1.0.5` | 切换模式，可选值：none、month、year-month | `String` | year-month |
| `closeable 1.0.9` | 是否显示关闭按钮级 | `Boolean` | true |
| `close-icon 1.0.9` | 关闭图标的name值级 | `String` | cross |
| `close-on-click-close 1.0.9` | 点击关闭按钮是否关闭popup级 | `Boolean` | true |
| `weekly-calendar 1.1.8` | 是否为周日历 | `Boolean` | false |
| `show-weekly-text 1.1.8` | 周日历时，是否显示头部的第几周 | `Boolean` | true |
| `show-month-mark 1.1.8` | 是否显示月份背景 | `Boolean` | false |

## 事件

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| `@select 1.0.5` | 点击并选中任意日期时触发 | (value:string |
| `@unselect` | 当日历组件的 mode 为 multiple 时，取消选中日期时触发 | (value:string) |
| `@clickDisabledDate` | 点击禁用的日期时触发 | (value:string) |
| `@panelChange 1.1.8` | 面板发生切换时触发,即面板的年月/周发生改变了 | (result: CalendarPanelChangeResult ) |
| `@header` | 自定义头部内容 | {panelDate:当前面板对应的年月,currentWeek:当前面板对应第几周} |

## 插槽

| 插槽名 | 说明 |
| --- | --- |
| `closeIcon` |  |
| `header` |  |
| `double-left` |  |
| `arrow-left` |  |
| `arrow-right` |  |
| `double-right` |  |
| `ifdef` |  |
| `endif` |  |
| `1989fa` |  |
| `141414` |  |
| `ifndef` |  |
| `f5f5f5` |  |
