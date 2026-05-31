<template>
  <n-button
    type="primary"
    :class="[
      { '!right-330px': app.settingDrawerVisible },
      app.settingDrawerVisible ? 'ease-out' : 'ease-in'
    ]"
    class="fixed top-340px right-14px z-10000 w-32px h-32px !p-0 transition-all duration-300"
    @click="app.toggleSettingDrawerVisible"
  >
    <span class="text-25px mt-2px">
      <n-icon size="25px">
        <SettingsOutline />
      </n-icon>
    </span>
    <!-- <icon-ant-design-close-outlined
      v-if="app.settingDrawerVisible"
      class="text-24px"
    />
    <icon-ant-design-setting-outlined v-else class="text-24px" /> -->
  </n-button>
</template>

<script setup lang="ts">
import { SettingsOutline } from '@vicons/ionicons5'
import { useAppStore } from '@/store'

const app = useAppStore()
</script>

<style scoped></style>
