<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Card API

> 源码：`packages/C7Card/`
> 标签：`<C7Card>` / `<c7-card>`

C7 业务卡片：基于 **`ElCard`** 统一 **标题栏**（可选色块 + **`ElText` 语义标签 h1~h5** + 加粗）、**`extra` / 折叠** 与 **内容区 fade**。**`#header`**：若提供，则 **整块 `ElCard` 头部** 仅渲染该插槽，**不**再出现默认色块、标题、`extra`、内置折叠按钮（折叠需在插槽内自行处理）。**受控 / 非受控**：传入 **`modelValue`**（`undefined` 以外）时为受控，展开态以 **`modelValue`** 为准；否则以内部状态为准，初值来自 **`defaultExpanded`（默认 `true`）**。**`toggle` / `expand` / `collapse`**：在受控模式下通过 **`emit('update:modelValue', …)`** 请求父组件更新；并始终 **`emit('change', expanded)`**。**色块显隐**：若 props 中 **传入** **`showColorBlock`**（含 **`false`**），**仅认 `showColorBlock`**；**未传 `showColorBlock`** 时回退 **`isShowColorBlock`**（兼容别名）。@emits update:modelValue@emits change(expanded:boolean)

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `label` / `label` | 标题文案（默认头部） | `String` | '' |
| `text-size` / `textSize` | 标题层级，对应 **`ElText` 的 `tag`**，取 `h1`~`h5` | `String` | 'h4' |
| `is-bold` / `isBold` | 标题是否加粗 | `Boolean` | false |
| `show-color-block` / `showColorBlock` | 是否显示左侧色块（主开关）。若显式传入（含 `false`），**仅**以此为准。 | `Boolean` | undefined |
| `is-show-color-block` / `isShowColorBlock` | 兼容别名：仅在 **未传入 `showColorBlock`** 时生效 | `Boolean` | false |
| `color-block-color` / `colorBlockColor` | 色块背景色，默认 Element 主色变量 | `String` | 'var(--el-color-primary)' |
| `collapsible` / `collapsible` | 是否可折叠内容区（总开关）；为 `false` 时不渲染 `#toggle` 与内置折叠按钮 | `Boolean` | false |
| `default-expanded` / `defaultExpanded` | 非受控初始是否展开 | `Boolean` | true |
| `model-value` / `modelValue` | 受控展开态；`undefined` 表示非受控 | `Boolean` | undefined |
| `expand-text` / `expandText` | 折叠态下默认按钮文案 | `String` | '展开' |
| `collapse-text` / `collapseText` | 展开态下默认按钮文案 | `String` | '收起' |
| `shadow` / `shadow` | 透传 `ElCard` 的 `shadow` | `String` | undefined |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@update:modelValue` |  |
| `@change` |  |

## 插槽

| 插槽 |
| --- |
| `header` |
| `extra` |
| `toggle` |
| `c7-card-fade` |

## 暴露方法

`toggle`、`expand`、`collapse`
