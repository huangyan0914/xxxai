import Layout from '@/layouts/default/index.vue'

export default [
  {
    path: '/',
    name: 'IntelligentQA',
    component: Layout,
    meta: { title: '智能问答', icon: 'APIManage' },
    redirect: '/IntelligentQA/index',
    children: [
      {
        path: '/IntelligentQA/index',
        name: 'IntelligentQAChat',
        component: () => import('@/views/IntelligentQA/index.vue'),
        meta: { title: '问答对话', cacheable: true, icon: 'APIManage' }
      },
      {
        path: '/IntelligentQA/received-shares',
        name: 'ReceivedShares',
        component: () => import('@/views/IntelligentQA/ReceivedShares/index.vue'),
        meta: { title: '收到的分享', cacheable: true, icon: 'APIManage' }
      }
    ]
  }
]
