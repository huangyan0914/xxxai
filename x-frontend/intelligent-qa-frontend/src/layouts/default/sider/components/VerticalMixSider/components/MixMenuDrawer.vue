<template>
  <div class="relative transition-width duration-300 ease-in-out" :style="{}">
    <dark-mode-container
      class="drawer-shadow fixed flex-col-stretch h-full nowrap-hidden"
      :style="{
        width: showDrawer ? theme.sider.mixChildMenuWidth + 'px' : '0px',
        left: app.siderCollapse
          ? theme.sider.mixCollapsedWidth + 'px'
          : siderWidth + 'px'
      }"
    >
      <header
        class="header-height flex-y-center justify-between"
        :style="{ height: theme.header.height + 'px' }"
      >
        <h2 class="text-primary pl-8px text-16px font-bold">{{ title }}</h2>
        <div
          class="px-8px text-16px text-gray-600 cursor-pointer"
          @click="app.toggleMixSiderFixed"
        >
          <span v-if="app.mixSiderFixed">
            <n-icon size="25px">
              <CloseCircleOutline />
            </n-icon>
          </span>
          <span v-else>
            <n-icon size="25px">
              <CheckmarkDoneCircleOutline />
            </n-icon>
          </span>
          <!-- <icon-mdi-pin-off v-if="app.mixSiderFixed" />
          <icon-mdi-pin v-else /> -->
        </div>
      </header>
      <n-scrollbar class="flex-1-hidden">
        <n-menu
          :value="activeKey"
          :options="menus"
          :expanded-keys="expandedKeys"
          :indent="18"
          key-field="path"
          @update:value="handleUpdateMenu"
          @update:expanded-keys="handleUpdateExpandedKeys"
        />
      </n-scrollbar>
    </dark-mode-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { useAppStore, useThemeStore } from '@/store'
import { useAppInfo } from '@/composables'
import { useBasicLayout } from '@/composables'
import {
  CloseCircleOutline,
  CheckmarkDoneCircleOutline
} from '@vicons/ionicons5'
import router from '@/router'
interface Props {
  /** 菜单抽屉可见性 */
  visible: boolean
  /** 子菜单数据 */
  menus: GlobalMenuOption[]
}

const props = defineProps<Props>()

const route = useRoute()
const app = useAppStore()
const theme = useThemeStore()
const { title } = useAppInfo()

const showDrawer = computed(
  () => (props.visible && props.menus.length) || app.mixSiderFixed
)

const activeKey = computed(
  () => (route.meta?.activeMenu ? route.meta.activeMenu : route.path) as string
)
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
const { siderWidth } = useBasicLayout()
</script>

<style scoped>
.drawer-shadow {
  box-shadow: 2px 0 8px 0 rgb(29 35 41 / 5%);
}
</style>
