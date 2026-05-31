import { http } from '@/utils/http/axios'

/**
 * @description: 获取数据资源编目tree数据
 */
export function getCatalogList(params: any) {
  return http.request({
    url: '/api/dp-resource-catalog/list',
    method: 'get',
    params
  })
}
/**
 * @description: 获取API资源编目tree数据
 */
export function getApiCatalogList() {
  return http.request({
    url: '/api/dp-resource-catalog-api/list',
    method: 'get'
  })
}
/**
 * @description: 数据服务list
 */
export function getDataServiceList(params: any) {
  return http.request({
    url: '/api/dp-service/getServicePage',
    method: 'get',
    params
  })
}
/**
 * @description: API数据服务list
 */
export function getApiServiceList(params: any) {
  return http.request({
    url: '/api/dp-service/apiServicePage',
    method: 'get',
    params
  })
}
/**
 * @description: 删除组织
 */
export function themeDel(id: any) {
  return http.request({
    url: `/api/dp-level-theme/remove/${id}`,
    method: 'delete'
  })
}
/**
 * @description: 批量删除组织
 */
export function themeBatchDel(params: any) {
  return http.request({
    url: `/api/dp-level-theme/removeByIds`,
    method: 'delete',
    params
  })
}
/**
 * @description: 添加组织
 */
export function themeAdd(params: any) {
  return http.request(
    {
      url: '/api/dp-level-theme/save',
      method: 'post',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 编辑组织
 */
export function themePut(params: any) {
  return http.request(
    {
      url: '/api/dp-level-theme/update',
      method: 'put',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 获取AK,SK
 */
export function getDpCertificateByuserName(params: any) {
  return http.request({
    url: '/api/dp-certificate/getDpCertificateByuserName',
    method: 'get',
    params
  })
}
/**
 * @description: 获取统计信息  resourceType:01-数据服务  02-API服务
 */
export function getServiceStatistics(params: any) {
  return http.request({
    url: '/api/dp-service/serviceStatistics',
    method: 'get',
    params
  })
}
/**
 * @description: 在线测试    -- 数据资源
 */
export function sendTest(params: any) {
  return http.request({
    url: '/api/service/jdbc/test',
    method: 'get',
    params
  })
}
/**
 * @description: 在线测试  --API资源
 */
export function sendTestApi(params: any) {
  return http.request({
    url: '/api/service/jdbc/test-api',
    method: 'get',
    params
  })
}
/**
 * @description: 查看--数据服务接口
 */
export function lookServiceDetail(params: any) {
  return http.request({
    url: '/api/dp-service/serviceDetail',
    method: 'get',
    params
  })
}
/**
 * @description: 查看--数据库接出
 */
export function lookTableOutDetail(params: any) {
  return http.request({
    url: '/api/dp-service-out-table/serviceDetail',
    method: 'get',
    params
  })
}

/**
 * @description: 查看--API服务
 */
export function lookApiDetail(id: any) {
  return http.request({
    url: `/api/dp-resource-api/getById/${id}`,
    method: 'get'
  })
}

/**
 * @description: 数据库接出上下线
 */
export function TableOutOnOffLine(id, params: any) {
  return http.request(
    {
      url: `/api/dp-service-out-table/onOffLine/${id}`,
      method: 'put',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
