<template>
  <dark-mode-container
    class="flex-col-stretch h-full flex"
    :inverted="theme.sider.inverted"
  >
    <!-- <layout-logo
      v-if="!isHorizontalMix"
      :show-title="showTitle"
      :style="{ height: theme.header.height + 'px' }"
    /> -->
    <vertical-menu />
  </dark-mode-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore, useThemeStore } from '@/store'
import { LayoutLogo } from '@/layouts/default/header/components/index'
import { VerticalMenu } from './components'

const app = useAppStore()
const theme = useThemeStore()

const isHorizontalMix = computed(() => theme.layout.mode === 'horizontal-mix')
const showTitle = computed(
  () => !app.siderCollapse && theme.layout.mode !== 'vertical-mix'
)
</script>

<style scoped></style>
