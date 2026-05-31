<template>
  <n-layout-header
    :style="{
      height: theme.header.navbar.visible
        ? 40 + theme.header.height + 'px'
        : theme.header.height + 'px'
    }"
    bordered
  >
    <dark-mode-container
      class="global-header w-full h-full relative"
      :inverted="theme.header.inverted"
    >
      <div
        class="header-top w-full flex-y-center"
        :style="{
          height: theme.header.height + 'px',
          backgroundColor: theme.header.bgColor
        }"
      >
        <layout-logo
          v-if="showLogo"
          :show-title="true"
          class="h-full"
          :style="{ width: theme.header.logo.width + 'px' }"
          :name="'logo'"
        />
        <div class="flex-1-hidden flex-y-center h-full flex-row text-[#fff]">
          <span class="text-24px">智能问答软件</span>
          <div
            class="flex-1 flex justify-center items-end text-16px text-[#E5E5E5] h-64px"
          >
            <div
              v-for="(item, index) in menu.menus"
              class="w-166px h-56px flex justify-center items-center cursor-pointer rounded-t-8px"
              :key="index"
              @click="clickFirstMenu(item, index)"
              :class="
                activeFirstMenuIndex == index ? 'text-[#1756BB] bg-[#fff]' : ''
              "
            >
              {{ item.label }}
            </div>
          </div>
        </div>
        <div class="flex justify-end h-full mr-30px flex-y-center">
          <full-screen />
          <user-info />
        </div>
      </div>
    </dark-mode-container>
  </n-layout-header>
</template>

<script setup lang="ts">
import { useThemeStore, useAppStore, useRouteStore } from '@/store'
import { useBasicLayout } from '@/composables'
import TabBar from '../tabbar/index.vue'
import {
  LayoutLogo,
  MenuCollapse,
  BreadCrumb,
  HeaderMenu,
  FullScreen,
  ThemeMode,
  SettingButton,
  LocalesToggle,
  UserInfo
} from './components'
import router from '@/router'
import { computed, watch, h } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const emit = defineEmits(['changeFirstMenu'])
const menu = useRouteStore()
interface Props {
  /** 显示logo */
  showLogo: GlobalHeaderProps['showLogo']
  /** 显示头部菜单 */
  showHeaderMenu: GlobalHeaderProps['showHeaderMenu']
  /** 显示菜单折叠按钮 */
  showMenuCollapse: GlobalHeaderProps['showMenuCollapse']
}

defineProps<Props>()

const theme = useThemeStore()

const { layouLeftWidth } = useBasicLayout()

const isProd = import.meta.env.PROD
// 激活的一级菜单索引
const activeFirstMenuIndex = ref(0)
// 点击一级菜单
const clickFirstMenu = (menuInfo, index) => {
  router.push(menuInfo.path)
  activeFirstMenuIndex.value = index
  console.log('22', menuInfo.path)
  localStorage.setItem('firstMenuPath', menuInfo.path)
  emit('changeFirstMenu', menuInfo.path)
}

watch(
  () => route.path,
  () => {
    menu.routes.forEach((ele, index) => {
      if (route.matched[0].path == ele.path) {
        activeFirstMenuIndex.value = index
      }
    })
  },
  {
    deep: true,
    immediate: true
  }
)
</script>

<style scoped>
.global-header {
  box-shadow: 0 1px 2px rgb(0 21 41 / 8%);
}
.header-top {
  border-bottom: 1px solid #d9d9d9;
}
</style>
