# 配方：字典类页面

**对照**：`views/system/dict/type/index.vue`（及 data 子页，若存在）。

在 `recipe-crud-page` 基础上额外注意：

1. 字典项展示：`columnType: 'tag'` + `dictList: xxx.value || []`
2. 状态字段：表单用 `<c7-radio :data-list="sys_normal_disable" v-model="form.status" />`（字典名以项目为准）
3. 搜索下拉：`searchColumns` 里 `type: 'select'` + `dataList: dict.value || []`
4. 仍走 `C7JsonTable` + `C7FormDialog`，不要手写 `el-table` 整页

其余 CRUD 约定同 `recipe-crud-page.md` / `spec/02`。
