import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
// import routes from 'virtual:generated-pages'
import Layout from '@/layouts/default/index.vue'
const modules = import.meta.glob('../views/**/**.vue')
const routes = [
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/404.vue'),
    meta: {
      title: '404',
      hide: true
    }
  }
]

/*
  routes规则
    面包屑默认默认携带 '/' 首页
      如不想匹配首页，则需要把 '/' 替换为完整路径
      {
        path: '/root',
        name: 'root',
        component: Layout,
        redirect: '/root/account',
        meta: {
          title: 'menus.account',
          icon: 'Atom2'
        },
        children: [
          {
            path: '/root/account',
            name: 'accountManager',
            component: () => import('@/views/Account/AccountManager.vue'),
            meta: {
              title: 'menus.accountManager',
              icon: 'Atom2'
            }
          }
        ]
      }
*/

const router = createRouter({
  history: createWebHistory('/child/IntelligentQA'),
  routes
})

export default router
