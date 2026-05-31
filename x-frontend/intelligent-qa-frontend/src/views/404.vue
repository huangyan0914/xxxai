<template>
  <div class="flex flex-col justify-center page-container">
    <div class="text-center text-36px font-900 text-[#4E8CFF]">404</div>
    <div class="text-center">
      <img src="~@/assets/imgs/404.svg" alt="" />
    </div>
    <div class="text-center">
      <h1 class="text-base text-gray-500">非常抱歉，您访问的页面未找到</h1>
      <n-button type="info" @click="goHome">返回首页</n-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()
const path: string = route.query?.redirect
  ? (route.query.redirect as string)
  : '/'
function goHome() {
  router.push(path)
}
</script>

<style lang="less" scoped>
.page-container {
  width: 100%;
  border-radius: 4px;
  padding: 50px 0;
  height: 100vh;

  .text-center {
    h1 {
      color: #4b4b4b;
      padding: 20px 0;
    }
  }

  img {
    width: 268px;
    margin: 0 auto;
  }
}
</style>
