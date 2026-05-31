import { findAffixedRoutes } from '@/store/modules/help'
import useVisitedRouteStore from '@/store/modules/visited-routes'
import { RouteRecordRaw } from 'vue-router'
import router from '..'
import { setLocal, getLocal } from '@/utils'

function useVisitedGuard() {
  router.beforeEach(to => {
    if (
      ['404', '500', '403', 'not-found', 'Login'].includes(to.name as string)
    ) {
      return true
    }
    const visitedRouteStore = useVisitedRouteStore()
    if (!visitedRouteStore.isLoadAffix) {
      const affixRoutes = findAffixedRoutes(router.getRoutes())
      visitedRouteStore.initAffixRoutes(affixRoutes)
    }
    if (to.path.startsWith('/redirect')) {
      return true
    }
    if (to.meta.noShowTabbar) {
      return true
    }
    if (to.query?.noShowTabbar) {
      return true
    }
    window.$wujie?.bus.$emit(
      'tabbar',
      '/MicroApp/TrainingManagement',
      'TrainingManagement',
      to
    )
    visitedRouteStore.addVisitedRoute(to as unknown as RouteRecordRaw)
    return true
  })
}

window.$wujie?.bus.$on('TrainingManagementData', query => {
  sessionStorage.setItem('query', JSON.stringify(query))
})
window.$wujie?.bus.$emit('init', 'TrainingManagement-init')
window.$wujie?.bus.$on('TrainingManagement', (path: string, query: any) => {
  if (query) {
    router.push({
      path: path,
      query: query
    })
  } else {
    router.push(path)
  }
})
export default useVisitedGuard
