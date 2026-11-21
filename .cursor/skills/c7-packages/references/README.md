# c7-packages references

指导在 **quick-ui** 业务页中正确使用 `src/packages` 下的 C7 业务增强组件（Element Plus 之上）。

```
接入 → 页面约定 → JsonTable → FormDialog → 表单控件 → MessageBox
```

## 权威源

1. `quick-ui/src/packages/**`
2. `.cursor/rules/quick-ui-style.mdc`
3. 标杆：`views/system/user/index.vue`、`views/system/dict/type/index.vue`
4. 本手册 `api/` 快照与 `spec/` / `recipes/`

## 文件索引

| 文件 | 定位 |
|---|---|
| `api/README.md` | API 卡片索引 |
| `spec/01-install-and-setup.md` | 全局注册 |
| `spec/02-page-conventions.md` | 页面/API 风格 |
| `spec/03-json-table.md` | 列表 |
| `spec/04-form-dialog.md` | 弹窗 |
| `spec/05-form-controls.md` | 表单控件 |
| `spec/06-message-box.md` | 函数式弹窗 |
| `recipes/recipe-crud-page.md` | CRUD 配方 |
| `recipes/recipe-dict-page.md` | 字典页提示 |

## 自包含

- 组件名全局注册为 `C7Xxx`（亦可 `c7-xxx`）
- 也可按需 `import C7Select from '@/packages/C7Select/index.vue'`
- MessageBox：`import { c7Confirm, ... } from '@/packages'`
