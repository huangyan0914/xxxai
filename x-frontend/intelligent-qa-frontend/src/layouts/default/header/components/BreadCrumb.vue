<template>
  <n-breadcrumb>
    <n-breadcrumb-item v-for="item of breadcrumbs" :key="item.key">
      <n-dropdown
        v-if="item.children && item.children.length > 0"
        :options="item.children"
        @select="handleSelect"
      >
        <span>
          {{ item.label }}
          <component
            :is="item.icon"
            v-if="theme.header.crumb.showIcon"
            class="inline-block align-text-bottom mr-4px text-16px"
            :class="{ 'text-#BBBBBB': theme.header.inverted }"
          />
          <n-icon>
            <ChevronDown />
          </n-icon>
        </span>
      </n-dropdown>
      <span v-else>
        {{ item.label }}
        <component
          :is="item.icon"
          v-if="theme.header.crumb.showIcon"
          class="inline-block align-text-bottom mr-4px text-16px"
          :class="{ 'text-#BBBBBB': theme.header.inverted }"
        />
      </span>
    </n-breadcrumb-item>
  </n-breadcrumb>
</template>

<script lang="ts" setup>
import { onMounted, reactive, watch } from 'vue'
import type { Component } from 'vue'
import { RouteRecordNormalized, useRoute, useRouter } from 'vue-router'
import { ChevronDown } from '@vicons/ionicons5'
import menusIcon from '@/store/modules/route/icons'
import { useThemeStore } from '@/store'

import { useRenderIcon } from '@/hooks'
const theme = useThemeStore()

const { renderIcon } = useRenderIcon()

interface DropItem {
  icon: Component
  label: string
  key: string
  children?: DropItem[]
}
const breadcrumbs = reactive([] as Array<DropItem>)
const route = useRoute()
const router = useRouter()
function handlePath(path: string) {
  return path.split('/').reduce((pre: string[], cur: string) => {
    if (cur) {
      const lastItem = pre[pre.length - 1]
      if (lastItem) {
        pre.push(lastItem + '/' + cur)
      } else {
        pre.push('/' + cur)
      }
    }
    return pre
  }, [])
}
function generatorDropdown(
  routes: Array<RouteRecordNormalized> | undefined,
  parentPath = '/'
) {
  if (!routes) return
  const tempArray: DropItem[] = []
  routes.forEach(it => {
    if (!it.meta.hide) {
      const tempItem: DropItem = {
        icon: renderIcon(menusIcon[it.meta?.icon as string]),
        label: it.meta?.title as string,
        key: it.path.startsWith('/') ? it.path : parentPath + '/' + it.path,
        children: []
      }
      if (it.children && it.children.length > 0) {
        tempItem.children = generatorDropdown(
          it.children as RouteRecordNormalized[],
          tempItem.key
        )
      } else {
        delete tempItem.children
      }
      tempArray.push(tempItem)
    }
  })
  return tempArray
}
function findRoute(paths: string[]) {
  const selectRoutes: Array<RouteRecordNormalized> = []
  let tempOrigin = router.getRoutes()
  paths.forEach(it => {
    const selectRoute = tempOrigin.find(pIt => pIt.path === it)
    if (selectRoute) {
      tempOrigin = selectRoute.children as unknown as RouteRecordNormalized[]
      selectRoutes.push(selectRoute)
    }
  })
  return selectRoutes
}
function generatorBreadcrumb() {
  if (route.meta.hide) return
  breadcrumbs.length = 0
  // console.log(findRoute(handlePath(route.path)), handlePath(route.path), route)
  const findedRoutes = findRoute(handlePath(route.path))
  const aa = generatorDropdown(findedRoutes)
  if (aa) {
    breadcrumbs.push(...aa)
  }
}
function handleSelect(key: string) {
  router.push(key)
}
onMounted(() => {
  generatorBreadcrumb()
})
watch(
  () => route.path,
  () => {
    if (
      route.path.startsWith('/redirect') ||
      ['/login', '/404', '/405', '/403', '/500'].includes(route.path)
    )
      return
    generatorBreadcrumb()
  }
)
</script>
