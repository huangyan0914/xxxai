import { http } from '@/utils/http/axios'

/**
 * @description: 获取归属用户
 */
export function getUserNameList(params: any) {
  return http.request({
    url: '/api/u5a-sync-account/list',
    method: 'get',
    params
  })
}
/**
 * @description: 获取凭证调用list
 */
export function getStatisticsList(params: any) {
  return http.request({
    url: '/api/dp-certificate/statistics',
    method: 'get',
    params
  })
}
/**
 * @description: 获取服务调用list getLogStatisticsList
 */
export function getServiceStatisticsList(params: any) {
  return http.request({
    url: '/api/dp-service-log/countServiceUseful',
    method: 'get',
    params
  })
}
/**
 * @description: 获取日志调用list
 */
export function getLogStatisticsList(params: any) {
  return http.request({
    url: '/api/dp-service-log/list',
    method: 'get',
    params
  })
}
