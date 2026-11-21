<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7FormDialog API

> 源码：`packages/C7FormDialog/`
> 标签：`<C7FormDialog>` / `<c7-form-dialog>`

表单 CRUD 弹窗：在 {@link C7Dialog} 之上统一「open → getPayload 初始化 form → 校验 → submit → 提示 → onSuccess → 关窗」。**壳层**：标题、宽度、`mobile-mode`、footer 等透传给 `C7Dialog`。**打开**：`open(row?)` → 调 `getPayload`（页面自行写 `form`）→ 清校验 → 显示。**提交**：接管 `onConfirm`；不要再传 `:on-confirm`。@prop {object} form 响应式表单（用于判断新增/修改文案、onSuccess）@prop {object|null} formRef `el-form` ref@prop {function(row?): any|Promise} getPayload 初始化：页面闭包写 `form`@prop {function(): Promise} submit 提交：页面闭包读 `form`@prop {function(object): boolean} [isEdit] 默认看 `form.id`@prop {string|function(boolean): string} [successMessage]@prop {function(object, {isEdit: boolean}): any} [onSuccess]

## 双向绑定

| 名称 | 说明 |
| --- | --- |
| `v-model` | type: Boolean, default: false |

## 属性

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `form` / `form` | 响应式表单模型（判断编辑态 / onSuccess） | `Object` | （必填） |
| `form-ref` / `formRef` | el-form ref（推荐传 `formRef`） | `Object` | null |
| `get-payload` / `getPayload` | 初始化：`(row?) => void ｜ Promise`页面内直接 `Object.assign(form, …)` / 拉详情，不要 return 再灌表。 | `Function` | （必填） |
| `submit` / `submit` | 提交：`() => Promise`（直接用页面 `form`） | `Function` | （必填） |
| `is-edit` / `isEdit` |  | `Function` | null |
| `success-message` / `successMessage` |  | `[String, Function]` | undefined |
| `on-success` / `onSuccess` |  | `Function` | null |

## 事件

| 事件 | 说明 |
| --- | --- |
| _无或未解析_ | |

## 插槽

| 插槽 |
| --- |
| `title` |
| `extra` |
| `footer` |

## 暴露方法

`open`、`confirm`
