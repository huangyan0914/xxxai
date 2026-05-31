import { createApp } from 'vue'
import naive from 'naive-ui'
import App from './App.vue'
import router from './router'
import './styles/index.less'

let app: ReturnType<typeof createApp> | null = null

function render() {
  app = createApp(App)
  app.use(naive)
  app.use(router)
  app.mount('#app')
}

if (window.__POWERED_BY_WUJIE__) {
  window.__WUJIE_MOUNT = render
  window.__WUJIE_UNMOUNT = () => {
    app?.unmount()
    app = null
  }
  window.__WUJIE?.mount()
} else {
  render()
}
