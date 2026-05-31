import { http } from '@/utils/http/axios'

export interface ShareCreateParams {
  sessionId: string
  sessionTitle?: string
  toUserId: string
}

// ===== 转发会话给指定用户 =====
export function createShare(data: ShareCreateParams) {
  return http.request<number>({ url: '/api/qa/shares', method: 'post', data }, { isShowSuccessMessage: true })
}

// ===== 我发出的分享列表 =====
export function listSentShares(params?: { pageNo?: number; pageSize?: number; sessionTitle?: string }) {
  return http.request<any>({ url: '/api/qa/shares/sent', method: 'get', params })
}

// ===== 我收到的分享列表 =====
export function listReceivedShares(params?: { pageNo?: number; pageSize?: number; sessionTitle?: string }) {
  return http.request<any>({ url: '/api/qa/shares/received', method: 'get', params })
}

// ===== 查看分享详情 =====
export function getShareDetail(shareId: number) {
  return http.request<any>({ url: `/api/qa/shares/${shareId}`, method: 'get' })
}

// ===== 删除分享 =====
export function deleteShare(shareId: number) {
  return http.request<void>({ url: `/api/qa/shares/${shareId}`, method: 'delete' }, { isShowSuccessMessage: true })
}
