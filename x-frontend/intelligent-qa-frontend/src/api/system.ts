import { http } from '@/utils/http/axios'

// ===== 分页查询系统用户（用于分享时选择接收人）=====
export function listUsers(params: { pageNum?: number; pageSize?: number; userName?: string; nickName?: string }) {
  return http.request<any>({ url: '/api/system/sysusers', method: 'get', params })
}
