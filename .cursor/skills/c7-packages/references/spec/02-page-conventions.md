# 02 页面约定

> 对齐 `.cursor/rules/quick-ui-style.mdc` 与 `views/system/user/index.vue`。

## 格式

- `<script setup>`，**不用 TypeScript**
- 2 空格；单引号；语句**不加分号**
- import 花括号内不加空格：`import {computed, ref} from 'vue'`
- 文件头一行中文 JSDoc，写明对齐哪个后端资源
- `defineOptions({name: 'SysXxx'})`，与实体名一致

## 列表与弹窗

- 列表：`C7JsonTable`（`list-function` / `delete-function` / 权限 props / `@add-click` `@edit-click`）
- 表单弹窗：优先 **`C7FormDialog`**；`C7Dialog` 仅作壳或非 CRUD
- 表单：`label-width="96px"`
- `tableColumns` / `searchColumns` / `rules` 必须是 **`computed`**
- 字典：`useDict`；列 `columnType: 'tag'` + `dictList: xxx.value || []`；状态用 `c7-radio`
- 操作列：`#actions`；删除用 `tableRef.removeRows(row, {confirmMessage})`
- 列表/表单**不展示** `createBy` / `createTime` / `updateBy` / `updateTime` / `delFlag`

## 表单模型

```js
const form = reactive(emptyForm())
// 打开时先
Object.assign(form, emptyForm())
// 再填详情
```

## API 模块

- 手写 `request`，**禁止** API 工厂
- `BASE` **带**前导 `/`，与 Controller 一致
- 函数名：`pageXxx` / `getXxx` / `addXxx` / `updateXxx` / `removeXxx`
- 每个导出函数一行中文 JSDoc

## 按钮

| 场景 | 用法 |
|---|---|
| 新页工具栏/操作列 | `ElButton`（可加 `v-hasPermi`） |
| 遗留流水线按钮 | `C7Button`（已 deprecated） |
| 成组命令 | `C7ButtonGroup` |
