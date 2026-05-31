<template>
  <n-layout :native-scrollbar="false">
    <LayoutPages />
  </n-layout>
</template>

<script lang="ts" setup>
import LayoutPages from '@/layouts/pages/index.vue'
import { useThemeStore } from '@/store'
const theme = useThemeStore()
</script>
