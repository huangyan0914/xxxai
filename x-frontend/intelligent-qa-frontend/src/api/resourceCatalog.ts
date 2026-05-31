import { http } from '@/utils/http/axios'

/**
 * @description: 资源编目分页查询
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @param title 资源标题（模糊）
 * @param category 资源类型（字典 resource_category）
 * @param status 发布状态（draft-草稿 / published-已发布）
 */
export function getResourceCatalogPage(params: {
  pageNum?: number
  pageSize?: number
  title?: string
  category?: string
  status?: string
}) {
  return http.request({ url: '/api/training/catalogs', method: 'get', params })
}

/**
 * @description: 新增资源编目
 * @param title 资源标题（必填）
 * @param category 资源类型（字典 resource_category）
 * @param description 资源简介
 */
export function addResourceCatalog(data: {
  title: string
  category?: string
  description?: string
}) {
  return http.request(
    { url: '/api/training/catalogs', method: 'post', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 按ID查询资源编目详情
 */
export function getResourceCatalogById(id: number) {
  return http.request({ url: `/api/training/catalogs/${id}`, method: 'get' })
}

/**
 * @description: 更新资源编目
 * @param title 资源标题（必填）
 * @param category 资源类型（字典 resource_category）
 * @param description 资源简介
 */
export function updateResourceCatalog(
  id: number,
  data: { title: string; category?: string; description?: string }
) {
  return http.request(
    { url: `/api/training/catalogs/${id}`, method: 'put', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 删除资源编目（逻辑删除）
 */
export function deleteResourceCatalog(id: number) {
  return http.request(
    { url: `/api/training/catalogs/${id}`, method: 'delete' },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 发布资源编目（草稿 → 已发布）
 */
export function publishResourceCatalog(id: number) {
  return http.request(
    { url: `/api/training/catalogs/${id}/publish`, method: 'put' },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 按需打标（为编目/教培资源批量添加或移除标签）
 * @param targetType 目标类型：catalog-编目 / item-教培资源
 * @param targetIds 目标ID列表
 * @param remove 是否移除标签（true-移除，false-添加）
 */
export function applyTagToCatalog(
  id: number,
  data: { targetType: string; targetIds: number[]; remove?: boolean }
) {
  return http.request(
    { url: `/api/training/catalogs/${id}/apply`, method: 'post', data },
    { isShowSuccessMessage: true }
  )
}
