<template>
  <dark-mode-container class="flex h-full" :inverted="theme.sider.inverted">
    <div class="flex-1 flex-col-stretch h-full">
      <n-scrollbar class="flex-1-hidden">
        <div class="mb-30px"></div>
        <mix-menu-detail
          v-for="item in firstDegreeMenus"
          :key="item.path"
          :route-name="item.path"
          :active-route-name="activeParentRouteName"
          :label="item.label"
          :icon="item.icon"
          :is-mini="app.siderCollapse"
          @click="handleMixMenu(item.path, item.hasChildren)"
        />
      </n-scrollbar>
      <!-- <mix-menu-collapse /> -->
    </div>
    <mix-menu-drawer
      :visible="drawerVisible"
      :menus="activeChildMenus"
      @mouseleave="resetFirstDegreeMenus"
    />
  </dark-mode-container>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore, useThemeStore, useRouteStore } from '@/store'
import { useBoolean } from '@/hooks'
import { LayoutLogo } from '@/layouts/default/header/components/index'
import { MixMenuDetail, MixMenuDrawer, MixMenuCollapse } from './components'
import router from '@/router'
import { RouteRecordRaw } from 'vue-router'

const route = useRoute()
const app = useAppStore()
const theme = useThemeStore()
const routeStore = useRouteStore()
const {
  bool: drawerVisible,
  setTrue: openDrawer,
  setFalse: hideDrawer
} = useBoolean()

const activeParentRouteName = ref('')
function setActiveParentRouteName(routeName: string) {
  activeParentRouteName.value = routeName
}

const firstDegreeMenus = computed(() =>
  routeStore.menus.map(item => {
    const { path, label } = item
    const icon = item?.icon
    const hasChildren = Boolean(item.children && item.children.length)

    return {
      path,
      label,
      icon,
      hasChildren
    }
  })
)

function getActiveParentRouteName() {
  firstDegreeMenus.value.some(item => {
    const key = (
      route.meta?.activeMenu ? route.meta.activeMenu : route.name
    ) as string
    const flag = key?.includes(item.path)
    if (flag) {
      setActiveParentRouteName(item.path)
    }
    return flag
  })
}

function handleMixMenu(routeName: string, hasChildren: boolean) {
  setActiveParentRouteName(routeName)
  if (hasChildren) {
    openDrawer()
  } else {
    router.push({ path: routeName })
  }
}

function resetFirstDegreeMenus() {
  getActiveParentRouteName()
  hideDrawer()
}

const activeChildMenus = computed(() => {
  const menus: any = []
  routeStore.menus.some(item => {
    const flag =
      item.path === activeParentRouteName.value &&
      Boolean(item.children?.length)
    if (flag) {
      menus.push(...(item.children || []))
    }
    return flag
  })
  console.log(menus)
  return menus
})

watch(
  () => route.name,
  () => {
    getActiveParentRouteName()
  },
  { immediate: true }
)
</script>

<style scoped></style>
