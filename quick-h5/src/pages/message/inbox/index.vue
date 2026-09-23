<script setup lang="ts">
/**
 * H5 站内信列表：分页拉取当前用户收件箱，点击进入详情。
 */
import { onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { getInboxUnreadCount, pageInbox, type MsgInbox } from '@/api/message/inbox'

const list = ref<MsgInbox[]>([])
const loading = ref(false)
const finished = ref(false)
const current = ref(1)
const unread = ref(0)
const size = 20

async function loadUnread() {
  try {
    unread.value = Number((await getInboxUnreadCount()) || 0)
  }
  catch {
    unread.value = 0
  }
}

async function load(reset = false) {
  if (loading.value) {
    return
  }
  if (reset) {
    current.value = 1
    finished.value = false
    list.value = []
  }
  if (finished.value) {
    return
  }
  loading.value = true
  try {
    const page = await pageInbox({ current: current.value, size, param: {} })
    const rows = page?.records || []
    list.value = reset ? rows : list.value.concat(rows)
    if (rows.length < size) {
      finished.value = true
    }
    else {
      current.value += 1
    }
  }
  finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

function openDetail(item: MsgInbox) {
  uni.navigateTo({ url: `/pages/message/inbox/detail?id=${item.inboxId}` })
}

onShow(() => {
  loadUnread()
  load(true)
})

onPullDownRefresh(() => load(true))
onReachBottom(() => load(false))
</script>

<template>
  <view class="qb-page inbox">
    <view v-if="unread > 0" class="inbox__badge">未读 {{ unread }}</view>
    <view
      v-for="item in list"
      :key="String(item.inboxId)"
      class="inbox__item"
      @click="openDetail(item)"
    >
      <view class="inbox__row">
        <text class="inbox__title" :class="{ unread: item.readFlag !== '1' }">{{ item.title || '（无标题）' }}</text>
        <text class="inbox__flag">{{ item.readFlag === '1' ? '已读' : '未读' }}</text>
      </view>
      <text class="inbox__time">{{ item.createTime || '' }}</text>
    </view>
    <view v-if="!list.length && !loading" class="inbox__empty">暂无消息</view>
    <view v-else class="inbox__foot">{{ finished ? '没有更多了' : (loading ? '加载中…' : '上拉加载') }}</view>
  </view>
</template>

<style scoped lang="scss">
.inbox {
  padding: 24rpx;
}
.inbox__badge {
  margin-bottom: 16rpx;
  color: #c45656;
  font-size: 26rpx;
}
.inbox__item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.inbox__row {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}
.inbox__title {
  font-size: 30rpx;
  color: #303133;
  &.unread {
    font-weight: 600;
  }
}
.inbox__flag {
  font-size: 24rpx;
  color: #909399;
  flex-shrink: 0;
}
.inbox__time {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #c0c4cc;
}
.inbox__empty,
.inbox__foot {
  text-align: center;
  color: #909399;
  padding: 40rpx 0;
  font-size: 26rpx;
}
</style>
