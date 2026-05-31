import router from '@/router'
import { useTitle } from '@vueuse/core'
import { dealFilterTree } from '@/utils'
import { getSession } from '@/utils'
import {
  useThemeStore,
  useAppStore,
  useRouteStore,
  useVisitedRoutesContext,
  useRouteStoreWidthOut
} from '@/store'

import asyncRouter from '@/router/routes'
import { dealOriginRouter } from './utils'
const routeStore = useRouteStoreWidthOut()
const useVisitedRoutes = useVisitedRoutesContext()
const whiteList = ['/auth', '/404']
function usePerimission() {
  router.beforeEach((to, form, next) => {
    // console.log('form', form)
    // console.log('to', to)
    window.$loadingBar?.start()
    if (sessionStorage.getItem('token') || true) {
      if (routeStore.menus?.length) {
        if (router.hasRoute(to.name as string)) {
          next()
          useVisitedRoutes.addVisitedRoute(to)
        } else {
          next({ path: '/404', query: { redirect: to.fullPath } })
        }
      } else {
        if (asyncRouter.length) {
          // 5a响应的路由
          routeStore.setMenuRoute(asyncRouter)
          routeStore.setRoutes(asyncRouter)
          asyncRouter?.forEach(item => {
            router.addRoute(item)
          })
          // const m =
          //   JSON.parse(sessionStorage.getItem('childAppRoutes') as string)[
          //     '/MicroApp/DataServices'
          //   ] || []

          // const routeMenus = dealOriginRouter(m)
          // // 模拟接口数据
          // if (m.length && routeMenus.length) {
          //   // 5a响应的路由
          //   routeStore.setMenuRoute(routeMenus)
          //   routeStore.setRoutes(routeMenus)
          //   routeMenus?.forEach(item => {
          //     router.addRoute(item)
          //   })
          next({ ...to, replace: true })
        } else {
          // 5a没有响应路由
          if (whiteList.indexOf(to.path) > -1) {
            next()
          } else {
            next({ path: '/404', query: { redirect: to.fullPath } })
          }
        }
      }
    } else {
      if (whiteList.indexOf(to.path) > -1) {
        next()
      } else {
        window.location.href = import.meta.env.VITE_AUTHORIZE_HREF
        // next({ path: '/404', query: { redirect: to.fullPath } })
      }
    }
  })

  router.afterEach(to => {
    useTitle(to.meta.title)
    window.$loadingBar?.finish()
  })
}

const dealMenus = (menus: any) => {
  const routes = dealRouter(asyncRouter, [])
  const arr: any = []

  // 对比找出动态路由
  routes.forEach(route => {
    menus.forEach(item => {
      if (item.type === '菜单' && route.path === item.url) {
        delete route.children
        arr.push({
          id: item.id,
          pid: item.pid,
          ...route
        })
      }
    })
  })
  // 生成父子层级
  return dealFilterTree(arr, false, false)?.map(item => {
    item.redirect = item.children && item.children[0].path
    return item
  })
}

// 父子层级数组，转成一维数组
const dealRouter = (routers: any, arr: any) => {
  routers.forEach(item => {
    arr.push(item)
    if (item.children?.length) {
      dealRouter(item.children, arr)
    }
  })
  return arr
}

export default usePerimission
