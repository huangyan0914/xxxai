<template>
  <hover-container
    class="w-40px h-40px"
    tooltip-content="全屏"
    :inverted="theme.header.inverted"
    @click="toggle"
  >
    <span v-if="isFullscreen" class="h-16px">
      <n-icon size="16px" color="#D3D3D3">
        <FullscreenExitOutlined />
      </n-icon>
    </span>
    <span v-else class="h-16px">
      <n-icon size="16px" color="#D3D3D3">
        <FullscreenOutlined />
      </n-icon>
    </span>
    <!-- <icon-gridicons-fullscreen-exit v-if="isFullscreen" class="text-18px" />
    <icon-gridicons-fullscreen v-else class="text-18px" /> -->
  </hover-container>
</template>

<script lang="ts" setup>
import { useFullscreen } from '@vueuse/core'
import { useThemeStore } from '@/store'
import { FullscreenExitOutlined, FullscreenOutlined } from '@vicons/antd'
const { isFullscreen, toggle } = useFullscreen()
const theme = useThemeStore()
</script>

<style scoped></style>
