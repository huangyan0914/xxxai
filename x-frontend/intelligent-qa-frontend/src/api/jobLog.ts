import { http } from '@/utils/http/axios'

/**
 * @description: 任务日志tableData
 */
export function getLogList(params: any) {
  return http.request({
    url: '/api/api/dataprocess/listLog',
    method: 'get',
    params
  })
}
/**
 * @description: 清除日志
 */
export function clearLog(params: any) {
  return http.request(
    {
      url: '/api/xxl-job-log/clearLog',
      method: 'post',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 执行日志详情
 */
export function logDetailCat(params: any) {
  return http.request({
    url: '/api/xxl-job-log/logDetailCat',
    method: 'post',
    params
  })
}
/**
 * @description: 根据jobId获取探查表
 */
export function getJobTableNames(params: any) {
  return http.request({
    url: '/api/dp-explore/getJobTableNames',
    method: 'get',
    params
  })
}
/**
 * @description: 根据jobId和探查表获取版本
 */
export function getJobVersions(params: any) {
  return http.request({
    url: '/api/dp-explore/getJobVersions',
    method: 'get',
    params
  })
}
/**
 * @description: 根据jobId、探查表、版本获取版本信息
 */
export function versionInfo(params: any) {
  return http.request({
    url: '/api/dp-explore/versionInfo',
    method: 'get',
    params
  })
}
