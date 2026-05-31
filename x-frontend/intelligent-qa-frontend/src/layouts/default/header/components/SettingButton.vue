<template>
  <hover-container
    class="w-40px h-full"
    tooltip-content="主题配置"
    :inverted="theme.header.inverted"
    @click="app.toggleSettingDrawerVisible"
  >
    <!-- <icon-ant-design-setting-outlined class="text-20px" /> -->
    <div class="h-25px">
      <n-icon size="25px">
        <SettingsOutline />
      </n-icon>
    </div>
  </hover-container>
</template>

<script setup lang="ts">
import { useAppStore, useThemeStore } from '@/store'
import { SettingsOutline } from '@vicons/ionicons5'

const app = useAppStore()
const theme = useThemeStore()
</script>

<style scoped></style>
