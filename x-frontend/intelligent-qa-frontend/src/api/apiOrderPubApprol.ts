import { http } from '@/utils/http/axios'

/**
 * @description: 订阅审批列表
 */
export function getApprolList(params: any) {
  return http.request({
    url: '/api/dp-service/reviewPage',
    method: 'get',
    params
  })
}
/**
 * @description: 上线
 */
export function onLine(params: any) {
  return http.request(
    {
      url: `/api/dp-service/onLine/${params}`,
      method: 'put'
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 下线
 */
export function offLine(params: any) {
  return http.request(
    {
      url: `/api/dp-service/offLine/${params}`,
      method: 'put'
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 订阅审批详情
 */
export function getApiDetail(params: any) {
  return http.request({
    url: '/api/dp-service/reviewDetailApi',
    method: 'get',
    params
  })
}
/**
 * @description: 审批
 */
export function reviewApprove(params: any) {
  return http.request(
    {
      url: '/api/dp-service/review',
      method: 'post',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
