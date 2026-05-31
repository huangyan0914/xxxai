import { http } from '@/utils/http/axios'

/**
 * @description: 订阅审批列表
 */
export function getApprolList(params: any) {
  return http.request({
    url: '/api/dp-service/getServiceApproval',
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
 * @description: 订阅审批详情  数据服务
 */
export function getSourceList(params: any) {
  return http.request({
    url: '/api/dp-service/reviewDetail',
    method: 'get',
    params
  })
}
/**
 * @description: 订阅审批详情  数据库
 */
export function getDbOutDetail(params: any) {
  return http.request({
    url: '/api/dp-service-out-table/reviewDetail',
    method: 'get',
    params
  })
}
/**
 * @description: 审批    数据服务
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

/**
 * @description: 审批    数据库
 */
export function reviewDbOutApprove(params: any) {
  return http.request(
    {
      url: '/api/dp-service-out-table/review',
      method: 'post',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
