<template>
  <router-view
    v-if="routeInfo.meta?.type !== 'micro-app'"
    v-slot="{ Component, route }"
  >
    <transition :name="theme.pageAnimateMode" mode="out-in" appear>
      <component :is="Component" :key="route.fullPath" class="bg-[#e2e2e2]" />
    </transition>
  </router-view>
  <router-view v-else class="bg-[#e2e2e2]"></router-view>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/store'
const theme = useThemeStore()
const routeInfo = useRoute()
</script>
