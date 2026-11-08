/**
 * 当前登录人的站内信未读数。顶栏铃铛和「我的消息」共用。
 */
import {defineStore} from 'pinia'
import {readInbox, unreadInboxCount} from '@/api/msg/inbox'

const useInboxStore = defineStore('inbox', {
  state: () => ({
    unreadCount: 0
  }),
  actions: {
    async refreshCount() {
      const res = await unreadInboxCount()
      this.unreadCount = Number(res?.data ?? 0)
    },
    async markRead(id) {
      await readInbox(id)
      await this.refreshCount()
    }
  }
})

export default useInboxStore
