import { http } from '@/utils/http/axios'

/**
 * @description: 培训管理概览
 * 获取项目总数、进行中项目数、科目总数、资源总数、已学习人数汇总
 */
export function getOverview() {
  return http.request({ url: '/api/training/overview', method: 'get' })
}

/**
 * @description: 教培资源统计
 * 按资源类型数量分布、按标签资源数、按已学习人数排行
 */
export function getResourceStatistics() {
  return http.request({
    url: '/api/training/resources/statistics',
    method: 'get'
  })
}
