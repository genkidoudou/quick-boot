<template>
  <span class="c7-form-label">
    <el-tooltip v-if="tip" :content="tip" placement="top">
      <el-icon class="c7-form-label__icon"><QuestionFilled/></el-icon>
    </el-tooltip>
    <slot>{{ text }}</slot>
  </span>
</template>

<script setup>
/**
 * 表单项标签：文字 + 可选问号 tooltip 说明。
 */
import {QuestionFilled} from '@element-plus/icons-vue'

defineOptions({name: 'C7FormLabel'})

defineProps({
  /** 标签文字；也可用默认插槽覆盖 */
  text: {type: String, default: ''},
  /** 悬停说明；为空则不显示问号图标 */
  tip: {type: String, default: ''}
})
</script>

<style scoped>
.c7-form-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.c7-form-label__icon {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: help;
  vertical-align: middle;
}
</style>
