import { createRouter, createWebHistory } from 'vue-router'
import routes from './routes'

export default createRouter({
  history: createWebHistory('/child/ai-model-lifecycle-frontend'),
  routes
})
