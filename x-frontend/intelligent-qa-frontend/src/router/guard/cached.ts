import { findCachedRoutes } from '@/store/modules/help'
import { useCachedRouteStore } from '@/store'
import router from '..'

function useCachedGuard() {
  router.beforeEach(() => {
    const cachedRouteStore = useCachedRouteStore()
    if (cachedRouteStore.getCachedRouteName.length === 0) {
      cachedRouteStore.initCachedRoute(findCachedRoutes(router.getRoutes()))
    }
    return true
  })
}

export default useCachedGuard
