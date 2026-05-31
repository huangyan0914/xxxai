<template>
  <n-layout class="tm-layout-root">
    <div class="tm-layout-container">
      <n-layout-header bordered class="tm-layout-header">
        <div class="tm-layout-header-title">AI模型全生命周期管理平台</div>
        <n-menu
          mode="horizontal"
          class="tm-top-menu"
          inverted
          :value="activeTopMenu"
          :options="topMenuOptions"
          :theme-overrides="topMenuThemeOverrides"
          @update:value="handleTopMenuChange"
        />
      </n-layout-header>

      <n-layout has-sider class="tm-layout-body">
        <n-layout-sider
          v-if="leftMenuOptions.length > 1"
          bordered
          width="212"
          content-style="padding: 16px 8px"
        >
          <n-menu
            accordion
            :value="activeLeftMenu"
            :options="leftMenuOptions"
            @update:value="handleLeftMenuChange"
          />
        </n-layout-sider>

        <n-layout class="tm-layout-main">
          <n-layout-content class="tm-layout-content">
            <router-view />
          </n-layout-content>
        </n-layout>
      </n-layout>
    </div>
  </n-layout>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuOption } from 'naive-ui'
import { useRoute, useRouter } from 'vue-router'

type MenuBranch = {
  key: string
  label: string
  target: string
  children: Array<{ key: string; label: string }>
}

const router = useRouter()
const route = useRoute()

const menuBranches: MenuBranch[] = [
  {
    key: '/dataset-management',
    label: '数据集管理',
    target: '/dataset-management/index',
    children: [{ key: '/dataset-management/index', label: '数据集管理' }]
  },
  {
    key: '/model-management',
    label: '模型管理',
    target: '/model-management/model-list/index',
    children: [
      { key: '/model-management/model-list/index', label: '模型列表' },
      { key: '/model-management/finetune/index', label: '模型微调' },
      { key: '/model-management/model-evaluation/index', label: '模型评测' },
      { key: '/model-management/inference-service/index', label: '推理服务' }
    ]
  },
  {
    key: '/knowledge-base',
    label: '知识库管理',
    target: '/knowledge-base/index',
    children: [{ key: '/knowledge-base/index', label: '知识库管理' }]
  }
]

const topMenuThemeOverrides = {
  itemTextColorHorizontal: '#d8e0e7',
  itemTextColorHoverHorizontal: '#ffffff',
  itemTextColorActiveHorizontal: '#ffffff',
  itemTextColorChildActiveHorizontal: '#ffffff',
  itemTextColorPressHorizontal: '#ffffff',
  itemColorHoverHorizontal: 'rgba(255, 255, 255, 0.1)',
  itemColorActiveHorizontal: 'rgba(255, 255, 255, 0.18)',
  itemColorActiveHoverHorizontal: 'rgba(255, 255, 255, 0.22)',
  borderColorHorizontal: 'rgba(255, 255, 255, 0.18)'
}

const topMenuOptions = computed<MenuOption[]>(() =>
  menuBranches.map(item => ({
    key: item.key,
    label: item.label
  }))
)

const activeTopMenu = computed(() => resolveTopKey(route.path))
const leftMenuOptions = computed<MenuOption[]>(() => {
  const branch = menuBranches.find(item => item.key === activeTopMenu.value)
  if (!branch || branch.children.length <= 1) {
    return []
  }
  return branch.children.map(item => ({ key: item.key, label: item.label }))
})

const activeLeftMenu = computed(() => {
  const routeMeta = route.meta as Record<string, unknown>
  const activeMenu = String(routeMeta.activeMenu || route.path)
  if (activeMenu.startsWith('/model-management/finetune/detail/')) {
    return '/model-management/finetune/index'
  }
  return activeMenu
})

function resolveTopKey(path: string) {
  const matched = menuBranches.find(item => path.startsWith(item.key))
  return matched?.key || '/dataset-management'
}

function handleTopMenuChange(key: string) {
  const branch = menuBranches.find(item => item.key === key)
  if (!branch) {
    return
  }
  router.push(branch.target)
}

function handleLeftMenuChange(key: string) {
  router.push(key)
}
</script>
