<template>
  <h2
    class="text-primary logo"
    :style="{ 'line-height': theme.header.height + 'px' }"
  >
    <svg-icon class="!w-39px !h-35.6px inline-block" :name="props.name" />
  </h2>
</template>

<script lang="ts" setup>
import { useAppStore, useThemeStore } from '@/store'
const app = useAppStore()
const theme = useThemeStore()

interface Props {
  /** 图标名称 */
  name: string
}

const props = withDefaults(defineProps<Props>(), {
  name: 'logo'
})
</script>

<style lang="less" scoped>
.logo {
  text-align: center;
  font-size: 30px;
  font-weight: bold;
}
</style>
