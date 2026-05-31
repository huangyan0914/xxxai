<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <router-view />
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])
window.$message = message

const themeOverrides = {
  common: {
    primaryColor: '#2f4050',
    primaryColorHover: '#3a4f66',
    borderRadius: '4px'
  }
}
</script>
