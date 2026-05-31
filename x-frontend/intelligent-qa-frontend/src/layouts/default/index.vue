<template>
  <n-layout class="wh-full relative">
    <div class="wh-full flex flex-col">
      <!-- layout头部 -->
      <LayoutHeader v-bind="headerProps" class="w-full"></LayoutHeader>
      <n-layout has-sider class="w-full flex-1">
        <!-- 左侧二级菜单 -->
        <n-layout-sider
          bordered
          v-show="menuOptions.length > 1"
          content-style="padding: 16px 8px;"
          width="212px"
        >
          <!-- <div class="py-16px px-8px"></div> -->
          <n-menu
            :options="menuOptions"
            accordion
            :key-field="'path'"
            :render-icon="renderMenuIcon"
            :render-label="renderMenuLabel"
            @update:value="handleUpdateMenu"
            v-model:value="selectedKey"
          />
        </n-layout-sider>
        <!-- 页面主体 -->
        <n-layout>
          <!-- 面包屑 -->
          <n-layout-header bordered class="h-40px">
            <div class="wh-full flex flex-row">
              <div class="flex-1 flex items-center">
                <tab-bar></tab-bar>
              </div>
              <SvgIcon name="mapMark" class="wh-16px mt-14px mr-4px"></SvgIcon>
              <div class="pr-32px">
                <n-breadcrumb separator=">">
                  <n-breadcrumb-item
                    v-for="(item, index) in route.matched"
                    :key="index"
                  >
                    {{ item.meta.title }}
                  </n-breadcrumb-item>
                </n-breadcrumb>
              </div>
            </div>
          </n-layout-header>
          <!-- 页面内容 -->
          <LayoutMain
            bordered
            position="absolute"
            style="top: 40px; padding: 24px"
            class="bg-[#e2e2e2]"
          />
        </n-layout>
      </n-layout>
    </div>
  </n-layout>
</template>

<script lang="ts" setup>
import { useBasicLayout } from '@/composables'
import LayoutHeader from './header/index.vue'
import LayoutMain from './main/index.vue'
import { useThemeStore, useAppStore, useRouteStore } from '@/store'
import TabBar from './tabbar/index.vue'
import { useRoute } from 'vue-router'
import { computed, watch, h } from 'vue'
import SvgIcon from '@/components/common/SvgIcon.vue'
import router from '@/router'

const { app, layouLeftWidth, siderVisible, headerProps } = useBasicLayout()
const theme = useThemeStore()
const appStore = useAppStore()
const routerStore = useRouteStore()
// 当前路由对象
const route = useRoute()
// 选中的二级菜单
const selectedKey = ref('')
// 二级菜单
const menuOptions: MenuOption[] = ref([])
// 当前路由路径
const menuFullPath = ref([])
watch(
  () => route.path,
  () => {
    selectedKey.value = route.path
    routerStore.routes.forEach(ele => {
      if (
        ele.path == route.path ||
        ele.redirect == route.path ||
        route.matched[0].path == ele.path
      ) {
        menuOptions.value = ele.children
      }
    })
  },
  {
    deep: true,
    immediate: true
  }
)
// 动态图标识
function renderMenuIcon(option: MenuOption) {
  return h(SvgIcon, {
    name: route.path == option.path ? 'camera' : 'edit',
    style: route.path == option.path ? 'color: #409EFF' : ''
  })
}
// 点击菜单跳转路由
function handleUpdateMenu(_key: string, item: MenuOption) {
  router.push(item.path as string)
}
function renderMenuLabel(option: MenuOption) {
  return option.meta.title as string
}
</script>

<style lang="less" scoped>
::v-deep(.n-scrollbar-content) {
  height: 100%;
}
::v-deep(.n-scrollbar) {
  min-width: 0 !important;
}
</style>
