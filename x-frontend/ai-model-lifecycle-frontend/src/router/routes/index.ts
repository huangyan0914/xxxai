import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layouts/default/index.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'aiModelLifecycle',
    component: Layout,
    redirect: '/dataset-management/index',
    children: [
      {
        path: '/dataset-management/index',
        name: 'datasetManagement',
        component: () => import('@/views/modules/dataset-management/index.vue'),
        meta: {
          title: '数据集管理',
          cacheable: true,
          icon: 'APIManage'
        }
      },
      {
        path: '/model-management/model-list/index',
        name: 'modelManagementList',
        component: () => import('@/views/modules/model-management/index.vue'),
        meta: {
          title: '模型列表',
          cacheable: true,
          icon: 'APIManage'
        }
      },
      {
        path: '/model-management/finetune/index',
        name: 'modelFinetune',
        component: () => import('@/views/modules/finetune/index.vue'),
        meta: {
          title: '模型微调',
          cacheable: true,
          icon: 'APIManage'
        }
      },
      {
        path: '/model-management/model-evaluation/index',
        name: 'modelEvaluation',
        component: () => import('@/views/modules/model-evaluation/index.vue'),
        meta: {
          title: '模型评测',
          cacheable: true,
          icon: 'APIManage'
        }
      },
      {
        path: '/model-management/inference-service/index',
        name: 'inferenceService',
        component: () => import('@/views/modules/inference-service/index.vue'),
        meta: {
          title: '推理服务',
          cacheable: true,
          icon: 'APIManage'
        }
      },
      {
        path: '/model-management/finetune/detail/:id',
        name: 'finetuneDetail',
        component: () => import('@/views/modules/finetune/detail/index.vue'),
        meta: {
          title: '任务详情',
          hide: true,
          activeMenu: '/model-management/finetune/index',
          icon: 'APIManage'
        }
      },
      {
        path: '/knowledge-base/index',
        name: 'knowledgeBase',
        component: () => import('@/views/modules/knowledge-base/index.vue'),
        meta: {
          title: '知识库管理',
          cacheable: true,
          icon: 'APIManage'
        }
      }
    ]
  },
  { path: '/dataset-management', redirect: '/dataset-management/index' },
  { path: '/model-management', redirect: '/model-management/model-list/index' },
  { path: '/knowledge-base', redirect: '/knowledge-base/index' },
  { path: '/datasets', redirect: '/dataset-management/index' },
  { path: '/models', redirect: '/model-management/model-list/index' },
  { path: '/finetunes', redirect: '/model-management/finetune/index' },
  {
    path: '/finetunes/:id',
    redirect: to => `/model-management/finetune/detail/${to.params.id}`
  },
  { path: '/model-evaluations', redirect: '/model-management/model-evaluation/index' },
  { path: '/inference-services', redirect: '/model-management/inference-service/index' },
  { path: '/knowledge-bases', redirect: '/knowledge-base/index' }
]

export default routes
