import { http } from '@/utils/http/axios'

/**
 * @description: 获取api列表
 */
export function queryApiList(params: any) {
  return http.request({
    url: '/api/dp-resource-api/list',
    method: 'get',
    params
  })
}

/**
 * @description: 通过id删除api列表记录
 */
export function deleteApiById(params: any) {
  return http.request(
    {
      url: `/api/dp-resource-api/remove/${params}`,
      method: 'delete'
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 层级树list
 */
export function geLevelTreeList() {
  return http.request({
    url: '/api/dp-level/listByTree',
    method: 'get'
  })
}
/**
 * @description: 主题tableData
 */
export function getThemeData(params: any) {
  return http.request({
    url: '/api/dp-level-theme/list',
    method: 'get',
    params
  })
}
/**
 * @description: 获取主题下绑定的数据表信息
 */
export function boundTableList(params: any) {
  return http.request({
    url: '/api/dp-model-table/page',
    method: 'get',
    params
  })
}
/**
 * @description: 通过表ID获取字段信息
 */
export function tableColumnsById(params: any) {
  return http.request({
    url: '/api/dp-model-table/listColumnsByTableId',
    method: 'get',
    params
  })
}

/**
 * @description: 通过表ID获取字段脱敏信息
 */
export function desensitizationInfoByTableId(params: any) {
  return http.request({
    url: '/api/dp-resource-api/desensitizationInfoByTableId',
    method: 'get',
    params
  })
}

/**
 * @description: 获取脱敏规则列表
 */
export function desensitizationList(params: any) {
  return http.request({
    url: '/api/dp-desensitization/list',
    method: 'get',
    params
  })
}

/**
 * @description: 保存API资源
 */
export function saveApi(params: any) {
  return http.request(
    {
      url: '/api/dp-resource-api/save',
      method: 'post',
      params
    },
    {
      isShowSuccessMessage: true,
      successMessageText: '保存成功'
    }
  )
}

/**
 * @description: 编辑API资源
 */
export function editApi(params: any) {
  return http.request(
    {
      url: '/api/dp-resource-api/update',
      method: 'put',
      params
    },
    {
      isShowSuccessMessage: true,
      successMessageText: '编辑成功'
    }
  )
}

/**
 * @description: 批量删除API资源
 */
export function batchDeleteApi(params: any) {
  return http.request(
    {
      url: '/api/dp-resource-api/removeByIds',
      method: 'delete',
      params
    },
    {
      isShowSuccessMessage: true,
      successMessageText: '批量删除成功'
    }
  )
}
