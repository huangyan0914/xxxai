<template>
  <!-- <n-popover
    class="w-100px"
    trigger="hover"
    placement="bottom"
    :show-arrow="false"
  >
    <template #trigger>
      <div class="w-auto h-full flex items-center">
        <img
          class="w-30px h-30px rounded-1/2 ml-5px mr-10px"
          :src="userInfo?.photo"
          alt=""
        />
        <span class="mr-10px cursor-default">{{ userInfo?.name}}</span>
      </div>
    </template>
    <div class="text-center">
      <div class="cursor-pointer" @click="showModal = !showModal">个人信息</div>
    </div> -->
  <div
    class="text-center whitespace-nowrap w-auto text-[#E5E5E5] flex flex-row"
  >
    <svg-icon class="!w-32px !h-32px inline-block" :name="'user'" />
    <div class="leading-32px pl-10px">欢迎，{{ userInfo?.name || '张三' }}</div>
  </div>
  <!-- <div class="text-center">
      {{ userInfo.org?.name }}
    </div>
  </n-popover>

  <DealAccountInfo v-model="showModal" title="个人信息" :userInfo="userInfo" /> -->
</template>

<script lang="ts" setup>
import { useThemeStore, useAppStore, useUserStoreContext } from '@/store'
// import { getUserInfo } from '@/api/system'

const userStore = useUserStoreContext()
const userInfo = ref({})

if (userStore.userInfo && userStore.userInfo.id) {
  userInfo.value = userStore.userInfo
} else {
  // getUserInfo().then(res => {
  //   userStore.saveUser(res)
  //   userInfo.value = res
  // })
}
console.log(userInfo)
const showModal = ref(false)
</script>

<style scoped></style>
