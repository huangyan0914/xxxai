<template>
  <n-layout-sider
    :native-scrollbar="false"
    :collapsed="app.siderCollapse"
    bordered
    :width="siderWidth"
    :collapsed-width="siderCollapsedWidth"
    style="z-index: 1002"
    class="h-full box-s"
  >
    <vertical-mix-sider v-if="isVerticalMix" class="global-sider" />
    <vertical-sider v-else class="global-sider" />
  </n-layout-sider>
</template>

<script lang="ts" setup>
import { useThemeStore, useAppStore } from '@/store'
import { useBasicLayout } from '@/composables'
import { VerticalSider, VerticalMixSider } from './components'
const app = useAppStore()
const theme = useThemeStore()
const isVerticalMix = computed(() => theme.layout.mode === 'vertical-mix')
watch(
  () => theme.layout.mode,
  v => {
    if (v === 'vertical-mix') {
      app.setSiderCollapse(false)
    }
  }
)
const { siderWidth, siderCollapsedWidth } = useBasicLayout()
</script>

<style lang="less">
// .box-s {
//   box-shadow: 0px 0px 10px rgb(0 0 0 / 10%);
// }
</style>
