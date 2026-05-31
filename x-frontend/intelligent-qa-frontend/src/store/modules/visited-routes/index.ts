import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'
import { useCachedRouteStore } from '../cached-routes/index'
import { findCachedRoutes } from '../help'
import menusIcon from '@/store/modules/route/icons'
import { useRenderIcon } from '@/hooks'
import { setLocal, getLocal } from '@/utils'
const { renderIcon } = useRenderIcon()

const visitedRoutes = getLocal('visited-routes') || []

const useVisitedRouteStore = defineStore('visited-routes', {
  state: () => {
    return {
      visitedRoutes: visitedRoutes as RouteRecordRaw[],
      isLoadAffix: false
    }
  },
  getters: {
    getVisitedRoutes(state) {
      return state.visitedRoutes.map(it => {
        it.icon = renderIcon(menusIcon[it.meta?.icon as string])
        return it
      })
    }
  },
  actions: {
    initAffixRoutes(affixRoutes: RouteRecordRaw[]) {
      affixRoutes.reverse().forEach(affixRoute => {
        if (!this.visitedRoutes.find(it => it.path === affixRoute.path)) {
          this.visitedRoutes.unshift(affixRoute)
        }
      })
      this.isLoadAffix = true
    },
    addVisitedRoute(route: RouteRecordRaw) {
      return new Promise(resolve => {
        if (!this.visitedRoutes.find(it => it.path === route.path)) {
          // fix '' '/'
          route.name ? this.visitedRoutes.push(route) : ''
          if (route.name) {
            const cachedRoutesStore = useCachedRouteStore()
            if (
              !cachedRoutesStore.cachedRoutes.includes(route.name as string)
            ) {
              cachedRoutesStore.cachedRoutes.push(route.name as string)
            }
          }
          this.persistentVisitedView()
        }
        resolve(route)
      })
    },
    removeVisitedRoute(route: RouteRecordRaw) {
      return new Promise<string>(resolve => {
        this.visitedRoutes.splice(this.visitedRoutes.indexOf(route), 1)
        this.persistentVisitedView()
        if (route.name) {
          const cachedRoutesStore = useCachedRouteStore()
          if (cachedRoutesStore.cachedRoutes.includes(route.name as string)) {
            cachedRoutesStore.cachedRoutes.splice(
              cachedRoutesStore.cachedRoutes.indexOf(route.name as string),
              1
            )
          }
        }
        resolve(this.findLastRoutePath())
      })
    },
    findLastRoutePath() {
      return this.visitedRoutes && this.visitedRoutes.length > 0
        ? this.visitedRoutes[this.visitedRoutes.length - 1].path
        : '/'
    },
    closeLeftVisitedView(selectRoute: RouteRecordRaw) {
      return new Promise(resolve => {
        const selectIndex = this.visitedRoutes.indexOf(selectRoute)
        if (selectIndex !== -1) {
          this.visitedRoutes = this.visitedRoutes.filter((it, index) => {
            return (it.meta && it.meta.affix) || index >= selectIndex
          })
          const cachedRoutesStore = useCachedRouteStore()
          cachedRoutesStore.setCachedRoutes(
            findCachedRoutes(this.visitedRoutes)
          )
          this.persistentVisitedView()
        }
        resolve(selectRoute)
      })
    },
    closeRightVisitedView(selectRoute: RouteRecordRaw) {
      return new Promise(resolve => {
        const selectIndex = this.visitedRoutes.indexOf(selectRoute)
        if (selectIndex !== -1) {
          this.visitedRoutes = this.visitedRoutes.filter((it, index) => {
            return (it.meta && it.meta.affix) || index <= selectIndex
          })
          const cachedRoutesStore = useCachedRouteStore()
          cachedRoutesStore.setCachedRoutes(
            findCachedRoutes(this.visitedRoutes)
          )
          this.persistentVisitedView()
        }
        resolve(selectRoute)
      })
    },
    closeAllVisitedView() {
      return new Promise<void>(resolve => {
        this.visitedRoutes = this.visitedRoutes.filter(it => {
          return it.meta && it.meta.affix
        })
        const cachedRoutesStore = useCachedRouteStore()
        cachedRoutesStore.setCachedRoutes(findCachedRoutes(this.visitedRoutes))
        this.persistentVisitedView()
        resolve()
      })
    },
    persistentVisitedView() {
      const tempPersistendRoutes = this.visitedRoutes.map(it => {
        return {
          fullPath: it.path,
          meta: it.meta,
          name: it.name,
          path: it.path
        }
      })
      setLocal(this.$id, tempPersistendRoutes)
    },
    restoreVisitedView() {
      this.$reset()
    }
  }
  // 由于需要自定义持久化过程，所以这里就不能用插件来实现
  // presist: {
  //   enable: true,
  // },
})

export function useVisitedRoutesContext() {
  return useVisitedRouteStore()
}

export default useVisitedRouteStore
