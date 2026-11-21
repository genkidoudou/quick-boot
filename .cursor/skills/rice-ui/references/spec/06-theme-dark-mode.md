# 06 主题与暗黑模式

> 官方：https://riceui.cn/components/quickstart.html「暗黑模式切换」「自定义颜色主题」

## 切换亮/暗

```ts
import { setTheme } from "@/uni_modules/rice-ui"

setTheme('dark')
setTheme('light')
```

须业务侧**手动调用**（例如跟随系统主题时自己监听再 `setTheme`）。  
Web 端会给 `document.documentElement` 切换 `rice-theme-light` / `rice-theme-dark`。

可读状态：

```ts
import { isDark, config } from "@/uni_modules/rice-ui"
// isDark.value / config.theme
```

## 自定义主题色

1. 新建 CSS，覆盖变量（官方示例）：

```css
.rice-theme-light {
  --rice-primary-color: #845ec2;
  --rice-success-color: #4d8076;
  --rice-warning-color: #e6a23c;
  --rice-error-color: #f56c6c;
}

.rice-theme-dark {
  --rice-primary-color: #6235c2;
  --rice-success-color: #0d8063;
  --rice-warning-color: #e6a23c;
  --rice-error-color: #f56c6c;
}
```

更多变量见 `uni_modules/rice-ui/libs/style/variables.css`。

2. **在基础样式之后**引入：

```vue
<style>
@import "@/uni_modules/rice-ui/libs/style/index.css";
@import "@/common/theme.css";
</style>
```

H5 + 样式隔离 2.0 时在 `main` 的 WEB 分支同样先后 import。

## 单位

`config.unit` 支持 `'px' | 'rpx'`（源码 `useConfig.uts`）。改单位需确认组件是否按该配置解析，以源码为准。

## 红线

- 不要在业务里大量写死 `#1989fa` 等与主题脱节的色值
- 覆盖文件必须放在 `index.css` **之后**，否则会被默认变量盖住
