<template>
  <n-config-provider
    :theme="theme.naiveTheme"
    :theme-overrides="theme.naiveThemeOverrides"
    :locale="app.lang === 'zh_CN' ? zhCN : enUS"
    :date-locale="app.lang === 'zh_CN' ? dateZhCN : dateEnUS"
    class="h-full"
  >
    <naive-provider>
      <router-view></router-view>
    </naive-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { zhCN, dateZhCN, enUS, dateEnUS } from 'naive-ui'
import {
  useThemeStore,
  useAppStore,
  subscribeStore,
  useRouteStore
} from '@/store'
import router from '@/router'
import { setSession } from '@/utils'
import asyncRouter from '@/router/routes'
// import { useGlobalEvents } from '@/composables'
const routeStore = useRouteStore()
const menu = useRouteStore()
const theme = useThemeStore()
const app = useAppStore()
// router.push(window.$wujie.props.path)

subscribeStore()
localStorage.setItem('IntelligentQA', 'true')
localStorage.removeItem('firstMenuPath')
</script>
