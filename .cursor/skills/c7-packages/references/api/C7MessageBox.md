<!-- 由 scripts/gen-api-cards.mjs 从 packages 源码生成 -->

# C7MessageBox API

> 源码：`packages/C7MessageBox/`
> 标签：`<C7MessageBox>` / `<c7-message-box>`

C7MessageBox：在 Element Plus `ElMessageBox` / `ElLoading` 之上提供函数式封装。- 统一 **`setMessageBoxDefaults`** 浅合并默认 options。- 对话框类 API **始终 resolve** 为 **`{ action, value? }`**，用户取消/关闭路径 **不**向业务抛 **reject**（与 EP 常见「点取消则 reject」行为解耦）。- **`asyncConfirm`**：在 **`beforeClose`** 内 **await** 异步任务；优先 **`confirmButtonLoading`**；失败 **不** `done()` 保持弹窗。依赖 **element-plus ^2.10**：`MessageBoxData.action` 取值与 **`beforeClose(action, instance, done)`** 契约以该版本为准。

## 导出函数

| 名称 | 说明 | 类型 | 默认/必填 |
| --- | --- | --- | --- |
| `setMessageBoxDefaults` | @type {Record<string, unknown>} */ | `function` | - |
| `mergeMessageBoxOptions` | @type {Record<string, unknown>} */ | `function` | - |
| `mapMessageBoxResolve` | 合并模块默认与单次入参（浅合并，后者优先）。@param {Record<string, unknown>} perCall 单次 `c7*` 的 options@returns {Record<string, unknown>}/ | `function` | - |
| `mapMessageBoxReject` | @type {{ action?: string, value?: unknown }} */ (data) | `function` | - |
| `splitTitleAndOptions` | @param {Promise<unknown>} promise EP 返回的 Promise@returns {Promise<{ action: 'confirm'｜'cancel'｜'close', value?: string }>}/ | `function` | - |
| `c7Confirm` | 确认对话框（取消/关闭不 reject）。@param {string｜import('vue').VNode｜(() => import('vue').VNode)} message@param {string｜import('element-plus').ElMessageBoxOptions｜undefined} [title]@param {import('element-plus').ElMessageBoxOptions & {  asyncConfirm?: () => Promise<unknown>,  errorNotify?: (err: unknown) => void,  asyncConfirmLoadingText?: string}} [options]@returns {Promise<{ action: 'confirm'｜'cancel'｜'close', value?: string }>} | `function` | - |
| `c7Alert` | 仅确定按钮。@param {string｜import('vue').VNode｜(() => import('vue').VNode)} message@param {string｜import('element-plus').ElMessageBoxOptions｜undefined} [title]@param {import('element-plus').ElMessageBoxOptions} [options]@returns {Promise<{ action: 'confirm'｜'cancel'｜'close', value?: string }>} | `function` | - |
| `c7Prompt` | 带输入框；校验字段仅透传 EP。@param {string｜import('vue').VNode｜(() => import('vue').VNode)} message@param {string｜import('element-plus').ElMessageBoxOptions｜undefined} [title]@param {import('element-plus').ElMessageBoxOptions} [options]@returns {Promise<{ action: 'confirm'｜'cancel'｜'close', value?: string }>} | `function` | - |
| `c7DangerConfirm` | 危险操作确认：在 `c7Confirm` 上叠加 `type: 'warning'` 与危险按钮样式（可被入参覆盖 class）。@param {string｜import('vue').VNode｜(() => import('vue').VNode)} message@param {string｜import('element-plus').ElMessageBoxOptions｜undefined} [title]@param {import('element-plus').ElMessageBoxOptions & {  asyncConfirm?: () => Promise<unknown>,  errorNotify?: (err: unknown) => void,  asyncConfirmLoadingText?: string}} [options]@returns {Promise<{ action: 'confirm'｜'cancel'｜'close', value?: string }>} | `function` | - |
| `c7Loading` | 全屏/局部 Loading，封装 `ElLoading.service`。@param {string} [text] 展示文案，写入 `options.text`@param {import('element-plus').LoadingOptions} [options] 透传 EP（除 `text` 外）@returns {{ close: () => void }} | `function` | - |
