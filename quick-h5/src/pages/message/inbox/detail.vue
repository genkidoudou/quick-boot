<script setup lang="ts">
/**
 * H5 站内信详情：加载后自动标已读。
 */
import { onLoad } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { getInbox, markInboxRead, type MsgInbox } from '@/api/message/inbox'

const detail = ref<MsgInbox>({})
const loading = ref(true)

onLoad(async (query) => {
  const id = query?.id
  if (!id) {
    uni.showToast({ title: '缺少消息 ID', icon: 'none' })
    loading.value = false
    return
  }
  try {
    detail.value = (await getInbox(id)) || {}
    if (detail.value.readFlag !== '1' && detail.value.inboxId != null) {
      await markInboxRead([detail.value.inboxId])
      detail.value.readFlag = '1'
    }
  }
  catch (e: any) {
    uni.showToast({ title: e?.message || '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <view class="qb-page detail">
    <view v-if="loading" class="detail__hint">加载中…</view>
    <template v-else>
      <text class="detail__title">{{ detail.title || '（无标题）' }}</text>
      <text class="detail__meta">{{ detail.createTime || '' }}</text>
      <text class="detail__content">{{ detail.content || '' }}</text>
    </template>
  </view>
</template>

<style scoped lang="scss">
.detail {
  padding: 32rpx;
  background: #fff;
  min-height: 100vh;
}
.detail__hint {
  color: #909399;
}
.detail__title {
  font-size: 36rpx;
  font-weight: 600;
  color: #303133;
}
.detail__meta {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #c0c4cc;
}
.detail__content {
  display: block;
  margin-top: 32rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: #606266;
  white-space: pre-wrap;
}
</style>
