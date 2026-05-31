<template>
  <n-scrollbar class="flex-1-hidden !bg-[#F8F9FA]">
    <div class="text-22px font-sans pb-15px pt-20px px-20px font-style">
      数据服务
    </div>
    <n-menu
      :value="activeKey"
      :collapsed="app.siderCollapse"
      :collapsed-width="theme.sider.collapsedWidth"
      :collapsed-icon-size="22"
      :options="routeStore.menus"
      default-expand-all
      key-field="path"
      label-field="label"
      :indent="18"
      :inverted="theme.sider.inverted"
      @update:value="handleUpdateMenu"
      @update:expanded-keys="handleUpdateExpandedKeys"
    />
  </n-scrollbar>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { useAppStore, useThemeStore, useRouteStore } from '@/store'
import router from '@/router'

const route = useRoute()
const app = useAppStore()
const theme = useThemeStore()
const routeStore = useRouteStore()
const activeKey = computed(
  () => (route.meta?.activeMenu ? route.meta.activeMenu : route.path) as string
)
// console.log(route.path)
const expandedKeys = ref<string[]>([])

function handleUpdateMenu(_key: string, item: MenuOption) {
  const menuItem = item as GlobalMenuOption
  router.push(item.path as string)
}

function handleUpdateExpandedKeys(keys: string[]) {
  expandedKeys.value = keys
}

watch(
  () => route.name,
  () => {
    expandedKeys.value = unref(expandedKeys.value)
  },
  { immediate: true }
)
</script>

<style scoped>
.font-style {
  font-family: '普惠265';
}
</style>
