import Layout from '@/layouts/default/index.vue'
import { dealFilterTree } from '@/utils'
import routesData from '../routes'

const modules = import.meta.glob('../../views/**/*.vue')

export function dealOriginRouter(routerRes) {
  const routes = dealRouter(routesData, [])

  routerRes = dealRouter(routerRes, []).map(i => {
    i.url = i.path
    return i
  })
  // 资源url
  routerRes = routerRes.map(i => {
    i.resUrl = i.url.replaceAll('/:id', '')
    return i
  })

  const router = routerRes.map(route => {
    const res = {
      id: route.id,
      pid: route.pid,
      orderNo: parseInt(route.orderNo),
      path: route.url,
      name: route.name,
      component: modules[`../../views${route.resUrl}.vue`],
      meta: {
        title: route.name,
        icon: route.icon,
        type: route.url.includes('MicroApp') ? 'micro-app' : '',
        hide: route.meta.hide
      }
    }
    if (res.name.includes('数据组织')) {
      res.path = res.path + '/data-org'
    } else if (res.name.includes('数据接入')) {
      res.path = res.path + '/data-access'
    }
    return res
  })
  // 对比找出动态路由icon
  routes.forEach(route => {
    router.forEach(item => {
      if (route.path === item.path) {
        item.meta.icon = route.meta.icon
      }
    })
  })

  let res = dealFilterTree(router)?.map(item => {
    const originNoChild = Object.assign({}, item)
    delete originNoChild.children
    if (item.children && item.children.length) {
      if (item.children.every(i => i.meta.hide)) {
        item.children = [originNoChild, ...item.children]
      } else {
        item.children = [...item.children]
      }
    } else {
      item.children = [Object.assign({}, item)]
    }

    item.component = Layout
    return item
  })
  res = dealRouterSort(res).map(item => {
    item.redirect =
      item?.children && item?.children.length && item.children[0].path
    return item
  })
  if (res && res.length) {
    res[0].path = '/'
  }
  return res
}

const dealRouterSort = (router: any) => {
  if (router.length > 1) {
    return router.sort((a, b) => {
      a.children && dealRouterSort(a.children)
      b.children && dealRouterSort(b.children)
      return a.orderNo - b.orderNo
    })
  } else if (router.length === 1 && router[0].children) {
    router[0].children = dealRouterSort(router[0].children)
    return router
  } else {
    return router
  }
}

// 父子层级数组，转成一维数组
const dealRouter = (routers, arr) => {
  routers.forEach(item => {
    arr.push(item)
    if (item.children?.length) {
      dealRouter(item.children, arr)
    }
  })
  return arr
}
