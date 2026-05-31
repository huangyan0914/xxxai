<template>
  <hover-container
    class="w-40px h-full"
    tooltip-content="国际化"
    :inverted="theme.header.inverted"
  >
    <span v-if="app.lang === 'zh_CN'" @click="setLocale('en_US')">中文</span>
    <span v-else @click="setLocale('zh_CN')">English</span>
    <!-- <icon-gridicons-fullscreen-exit v-if="isFullscreen" class="text-18px" />
    <icon-gridicons-fullscreen v-else class="text-18px" /> -->
  </hover-container>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useAppStore, useThemeStore, useRouteStore } from '@/store'
import router from '@/router'

const app = useAppStore()
const theme = useThemeStore()
const r = useRouteStore()
const { locale } = useI18n()
const setLocale = (lang: string) => {
  locale.value = lang
  app.setLocale(lang)
  r.setMenuRoute(router.options.routes)
}
onMounted(() => {
  locale.value = app.lang
  r.setMenuRoute(router.options.routes)
})
</script>
