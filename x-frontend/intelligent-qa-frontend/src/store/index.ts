import { createPinia } from 'pinia'

/** setup vue store plugin: pinia. - [安装vue状态管理插件：pinia] */
const store = createPinia()

export default store
export * from './modules'
export * from './subscribe'
