<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7Button API

> 源码：`packages/C7Button/`
> 标签：`<C7Button>` / `<c7-button>`

**【已 deprecated】** 

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `btn-type` / `btnType` | 预设类型：决定默认文案、图标、type、plain（可被显式 props 覆盖） | `String` | '' |
| `click-function` / `clickFunction` | 业务异步函数；失败请 reject，成功 resolve（与项目 request 拦截器一致） | `Function` | （必填） |
| `debounce-delay` / `debounceDelay` | 防抖窗口（ms）：lodash debounce {@code leading: true}、{@code trailing: false}，首击立即响应，窗口内后续点击忽略；与 {@code busy} 互补。 | `Number` | 300 |
| `confirm` / `confirm` | 是否在执行前弹出确认框（或 {@link confirmFn}） | `Boolean` | false |
| `confirm-message` / `confirmMessage` | 默认确认文案（未传 {@link confirmFn} 时使用） | `String` | '确定执行该操作？' |
| `confirm-fn` / `confirmFn` | 自定义确认逻辑，返回 true 继续、false 中止（不发 error 提示）。与 {@link confirm} 同时为 true 时优先本函数。 | `Function` | null |
| `validate` / `validate` | 是否先执行表单校验 | `Boolean` | false |
| `validate-ref` / `validateRef` | Element Plus Form 实例（或 ref），需具备 {@code validate(): Promise<void>}；传父组件中 {@code const formRef = ref()} 绑定到 {@code el-form} 后的 ref 即可。 | `Object` | null |
| `before-click` / `beforeClick` | 返回 false 则中止流水线（不发成功/失败提示），并触发 {@code after-click(false)}。支持同步或异步。 | `Function` | null |
| `before-pipeline` / `beforePipeline` | 在 {@code emit('before-click')} 之前执行；供 {@code C7ButtonGroup} 等包裹组件注入组级逻辑（如 {@code before-command}）。支持同步或返回 Promise；抛错或 reject 将中止流水线并触发 {@code after-click(false)}（失败提示规则与同流水线其它异常一致）。 | `Function` | null |
| `check-success` / `checkSuccess` | 在 {@code clickFunction} resolve 后调用；返回 false 视为业务失败（走 error 提示与 {@code after-click(false)}）。 | `Function` | () => true |
| `success-message` / `successMessage` | 成功后的文案；有值则提示（除非 {@link successNotify} 为 true 时用通知） | `String` | '' |
| `success-notify` / `successNotify` | 为 true 时成功提示用 ElNotification，内容为 {@link successMessage} 或默认「操作成功」 | `Boolean` | false |
| `show-error-toast` / `showErrorToast` | 是否在失败时弹出错误提示（确认取消、校验失败、beforeClick 否决不提示） | `Boolean` | true |
| `error-message` / `errorMessage` | 失败提示兜底文案（catch 时优先于 Error.message） | `String` | '' |
| `label` / `label` | 覆盖预设的按钮文案 | `String` | '' |
| `type` / `type` | 覆盖预设的 Element 按钮 type | `String` | '' |
| `plain` / `plain` | 覆盖预设 plain | `Boolean` | undefined |
| `size` / `size` | 尺寸，同 ElButton | `String` | '' |
| `disabled` / `disabled` | 禁用 | `Boolean` | false |

## 事件

| 事件 | 说明 |
| --- | --- |
| `@before-click` | 流水线开始（校验前） |
| `@after-click` | 流水线结束，载荷为是否整体成功（确认取消 / 校验失败 / veto / 异常 / 业务失败均为 false） |
| `@success` | 业务成功，载荷为 {@code clickFunction} resolve 值 |
| `@error` | 校验失败、用户取消确认、请求 reject、或 {@code checkSuccess} 为 false，载荷为 Error 或其它 reason |

## 插槽

| 插槽 |
| --- |
| `default` |
