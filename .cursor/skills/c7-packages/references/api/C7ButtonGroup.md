<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7ButtonGroup API

> 源码：`packages/C7ButtonGroup/`
> 标签：`<C7ButtonGroup>` / `<c7-button-group>`

C7 按钮组：在 {@link C7Button} 之上提供 inline / dropdown / auto 布局与「更多」折叠。- **数据模式**：非空 {@code buttons} 时按行配置渲染 {@code C7Button}；{@code hidden} 不占 {@code maxVisible} 名额。- **插槽模式**：{@code buttons} 为空时解析默认插槽内 **直接子级 / Fragment 内** 的 {@code C7Button} VNode（其它节点忽略）；动态插槽结构变化后可调用 {@link forceUpdate}。组级事件 {@code before-command} / {@code after-command} 与单按钮 {@code before-click} / {@code after-click} 等 **并存**；顺序为 {@code before-command} → {@code before-click} → … → {@code after-click} → {@code after-command}（依赖 {@code C7Button.beforePipeline}）。@emits before-command 进入子按钮流水线之前，载荷为 data 项描述或 {@code { slotIndex }}@emits after-command 子按钮流水线结束后，载荷为 {@code { item, success }}

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `buttons` / `buttons` | 非空时启用数据驱动；为空时使用默认插槽中的 C7Button | `Array` | () => [] |
| `mode` / `mode` | inline：全平铺；dropdown：全部进「更多」；auto：最多外露 maxVisible 个 | `String` | 'auto' |
| `max-visible` / `maxVisible` | 仅 mode=auto 时生效；非法或小于 1 时按 1 处理 | `Number` | 2 |
| `spacing` / `spacing` | 间距：预设 sm/md/lg、数字像素或带 px 的字符串 | `[String, Number]` | 'md' |
| `size` / `size` | 透传到子 C7Button（可被单项覆盖） | `String` | '' |
| `responsive` / `responsive` | 为 true 时根节点增加 {@code c7-button-group--responsive}，供业务侧写媒体查询 | `Boolean` | false |
| `more-text` / `moreText` |  | `String` | '更多' |
| `more-icon` / `moreIcon` | Element Plus 图标组件或兼容 ElIcon 的图标对象 | `[Object, String]` | null |
| `more-button-type` / `moreButtonType` |  | `String` | 'default' |
| `more-button-plain` / `moreButtonPlain` |  | `Boolean` | false |
| `trigger` / `trigger` | 同 ElDropdown：hover ｜ click ｜ contextmenu | `String` | 'click' |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@before-command` | 进入子按钮流水线之前，载荷为 data 项描述或 {@code { slotIndex }} |
| `@after-command` | 子按钮流水线结束后，载荷为 {@code { item, success }} |

## 插槽

| 插槽 |
| --- |
| `dropdown` |
