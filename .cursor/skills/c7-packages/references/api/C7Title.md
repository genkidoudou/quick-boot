<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Title API

> 源码：`packages/C7Title/`
> 标签：`<C7Title>` / `<c7-title>`

**【已 deprecated】** 

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `tag` / `tag` | 语义标签；**默认 `undefined`**（与「显式传 `h4`」区分，见组件说明）。@type {'h1'｜'h2'｜'h3'｜'h4'｜'h5'｜'h6'｜'div'｜'p'｜undefined} | `String` | undefined |
| `label` / `label` | 主标题文案（优先于 **`title`**） | `String` | '' |
| `title` / `title` | *`label` 的兼容别名** | `String` | '' |
| `label-size` / `labelSize` | 预设层级 **`h1`~`h6`** 或自定义字号（如 **`20px`**、**`1.2rem`**）。@default 'h4' | `String` | 'h4' |
| `decoration-color` / `decorationColor` | 装饰线/底边颜色（优先于 **`labelColor`**） | `String` | undefined |
| `label-color` / `labelColor` | *`decorationColor` 的兼容别名** | `String` | undefined |
| `show-border` / `showBorder` | 是否显示底部线（与「装饰线」同一元素） | `Boolean` | true |
| `icon` / `icon` | `@element-plus/icons-vue` 导出组件名（PascalCase，如 **`Setting`**） | `String` | '' |

## 事件

| 事件 | 说明 |
| --- | --- |
| _无或未解析_ | |

## 插槽

| 插槽 |
| --- |
| `icon` |
| `title` |
