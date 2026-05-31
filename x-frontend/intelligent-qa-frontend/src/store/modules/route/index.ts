import store from '@/store'
import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'
import { h, Component } from 'vue'
import { NIcon } from 'naive-ui'
import menusIcon from './icons'
import { useRenderIcon } from '@/hooks'
import SvgIcon from '@/components/common/SvgIcon.vue'

const { renderIcon } = useRenderIcon()
function renderMenuIcon(icon: any, color: any) {
  return () => h(SvgIcon, { name: icon, style: color ? `color:${color}` : '' })
}
interface RouteSate {
  menus: GlobalMenuOption[]
  routes: GlobalMenuOption[]
}

interface IconsMap {
  [key: string]: Component
}

export const useRouteStore = defineStore('route-store', {
  state: (): RouteSate => ({
    menus: [],
    routes: []
  }),
  actions: {
    setMenuRoute(routes: GlobalMenuOption[]) {
      this.menus = dealOneChildren(dealSetChildren(dealMenu(routes, true)))
    },
    setRoutes(routes) {
      this.routes = routes
    }
  }
})

/**
 * 将路由处理成menu的set数据
 * @param routes
 * @param hide 隐藏路由
 * @returns
 */
function dealMenu(routes: GlobalMenuOption[], hide?: boolean): any {
  const resultRoutes = new Set()
  routes.forEach(item => {
    const { path, meta, children } = item
    if (hide && !meta?.hide) {
      const menuItem: GlobalMenuOption = {
        path: path,
        label: meta?.title as string
      }
      if (meta?.icon) {
        menuItem.icon = renderMenuIcon(meta.icon, meta.color)
      }
      if (children && children.length) {
        menuItem.children = []
        menuItem.children = dealMenu(children, hide)
      }
      resultRoutes.add(menuItem)
    }
  })
  return resultRoutes
}

// set转数组
function dealSetChildren(list: any) {
  const arr = [...list]
  return arr.map(item => {
    if (item.children) {
      item.children = dealSetChildren(item.children)
    }
    return item
  })
}

// 去掉children为一的
function dealOneChildren(routes: GlobalMenuOption[]) {
  return routes.map(item => {
    if (item.children?.length === 1) {
      item = item.children[0]
    }
    return item
  })
}

export function useRouteStoreWidthOut() {
  return useRouteStore(store)
}
