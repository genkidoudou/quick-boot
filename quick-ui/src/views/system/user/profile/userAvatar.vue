<template>
  <div class="user-info-head">
    <img :src="avatar" title="用户头像" class="img-circle img-lg" alt="avatar" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import useUserStore from '@/store/modules/user'

defineOptions({ name: 'ProfileUserAvatar' })

const userStore = useUserStore()
const avatar = computed(() => userStore.avatar)
</script>

<style scoped lang="scss">
.user-info-head {
  display: inline-block;
  height: 120px;
}

.img-circle {
  border-radius: 50%;
}

.img-lg {
  width: 120px;
  height: 120px;
  object-fit: cover;
}
</style>
