<template>
  <hover-container
    class="w-40px h-full"
    :inverted="theme.header.inverted"
    @click="app.toggleSiderCollapse"
  >
    <n-icon v-if="app.siderCollapse" class="transition-all" size="25">
      <AlertTwotone color="#0e7a0d" />
    </n-icon>
    <n-icon v-else size="25" class="transition-all duration-500">
      <AlertOutlined color="#f40" />
    </n-icon>
  </hover-container>
</template>

<script lang="ts" setup>
import { useAppStore, useThemeStore } from '@/store'
import { AlertTwotone, AlertOutlined } from '@vicons/antd'
const app = useAppStore()
const theme = useThemeStore()
</script>

<style scoped></style>
