# 配方：最小页面脚手架

**适用场景**：新建业务页（路由 → 骨架 → 放 `rice-*` 组件）。

**前置**：已完成 `spec/01`（`uni_modules/rice-ui` + 基础样式；蒸汽项目按需开 vapor）。

## 1. 注册路由（pages.json）

```json
{
  "pages": [
    {
      "path": "pages/index/index",
      "style": { "navigationBarTitleText": "首页" }
    }
  ]
}
```

使用 `rice-navbar` 自定义导航时，该页需 `"navigationStyle": "custom"`。

## 2. 页面骨架

```vue
<template>
  <view class="page">
    <view class="content">
      <rice-button type="primary" text="主要按钮" @click="onClick" />
      <rice-tag text="标签" type="primary" />
    </view>
  </view>
</template>

<script setup lang="ts">
function onClick() {
  uni.showToast({ title: '点击了按钮', icon: 'none' })
}
</script>

<style>
.page {
  display: flex;
  flex-direction: column;
  /* #ifndef APP */
  min-height: 100vh;
  /* #endif */
  /* #ifdef APP */
  flex: 1;
  /* #endif */
  background-color: var(--rice-background);
}
.content {
  padding: 16px;
}
</style>
```

## 3. 关键点

| 点 | 说明 |
|---|---|
| 免 import 组件 | `<rice-*>` 靠 uni_modules / easycom |
| 主题背景 | `var(--rice-background)`，暗黑由 `setTheme` 切换 |
| 高度双轨 | App 用 `flex: 1`，非 App 用 `100vh` |
| 官方对照 | 组件用法以 https://riceui.cn/ 对应页为准 |

## 4. 继续

- 组件速查：`spec/04`
- API 卡片：`references/api/rice-<name>.md`
- 弹窗 API：`spec/01` + `spec/02`
