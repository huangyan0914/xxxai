import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router'
import store from '@/store'
import '@/styles/index.less'
import 'virtual:windi.css'
import useRouterGuard from './router/guard'
import 'virtual:svg-icons-register'
import '@/icons/iconfont/iconfont.css'
import 'element-plus/dist/index.css'
if (window.__POWERED_BY_WUJIE__) {
  let instance: any
  window.__WUJIE_MOUNT = () => {
    instance = createApp(App).use(router).use(store).use(router)
    instance.mount('#app')
  }
  window.__WUJIE_UNMOUNT = () => {
    instance.unmount()
  }
  // module脚本异步加载，应用主动调用生命周期
  window.__WUJIE.mount()
} else {
  createApp(App).use(router).use(store).use(router).mount('#app')
}
useRouterGuard()
