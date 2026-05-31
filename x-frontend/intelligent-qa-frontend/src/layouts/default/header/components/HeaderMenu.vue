<template>
  <div class="flex-1-hidden h-full px-10px">
    <n-scrollbar
      :x-scrollable="true"
      class="flex-1-hidden h-full"
      content-class="h-full"
    >
      <div
        class="flex-y-center h-full"
        :style="{ justifyContent: theme.menu.horizontalPosition }"
      >
        <n-menu
          :value="activeKey"
          mode="horizontal"
          :options="routeStore.menus"
          :inverted="theme.header.inverted"
          key-field="path"
          @update:value="handleUpdateMenu"
        />
      </div>
    </n-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { useThemeStore, useRouteStore } from '@/store'
import router from '@/router'

// import { useRouterPush } from '@/composables'

const route = useRoute()
const routeStore = useRouteStore()
const theme = useThemeStore()
// const { routerPush } = useRouterPush()
// const menus = computed(() => routeStore.menus as GlobalMenuOption[])
const activeKey = computed(
  () => (route.meta?.activeMenu ? route.meta.activeMenu : route.path) as string
)

function handleUpdateMenu(_key: string, item: MenuOption) {
  const menuItem = item as GlobalMenuOption
  router.push(item.path as string)
}
</script>

<style scoped>
:deep(.n-menu-item-content-header) {
  overflow: inherit !important;
}
</style>
