import { http } from '@/utils/http/axios'

/**
 * @description: 标签分页查询
 */
export function getTagPage(params: {
  pageNum?: number
  pageSize?: number
  tagName?: string
  tagType?: string
}) {
  return http.request({ url: '/api/system/tags', method: 'get', params })
}

/**
 * @description: 按主键查询标签详情
 */
export function getTagById(id: number) {
  return http.request({ url: `/api/system/tags/${id}`, method: 'get' })
}

/**
 * @description: 新增标签
 */
export function addTag(data: {
  tagName: string
  tagType?: string
  description?: string
}) {
  return http.request(
    { url: '/api/system/tags', method: 'post', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 更新标签
 */
export function updateTag(
  id: number,
  data: { tagName: string; tagType?: string; description?: string }
) {
  return http.request(
    { url: `/api/system/tags/${id}`, method: 'put', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 删除标签
 */
export function deleteTag(id: number) {
  return http.request(
    { url: `/api/system/tags/${id}`, method: 'delete' },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 批量删除标签
 */
export function deleteBatchTag(data: number[]) {
  return http.request(
    { url: '/api/system/tags/batch', method: 'delete', data },
    { isShowSuccessMessage: true }
  )
}
