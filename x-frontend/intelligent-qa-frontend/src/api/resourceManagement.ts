import { http } from '@/utils/http/axios'

/**
 * @description: 教培资源分页查询
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @param catalogId 所属编目ID
 * @param fileName 文件名（模糊）
 * @param fileType 文件类型（字典 file_type）
 */
export function getResourceManagementPage(params: {
  pageNum?: number
  pageSize?: number
  catalogId?: number
  fileName?: string
  fileType?: string
}) {
  return http.request({ url: '/api/training/resources', method: 'get', params })
}

/**
 * @description: 新增教培资源
 * @param catalogId 所属资源编目ID（必填）
 * @param fileName 文件名（必填）
 * @param fileType 文件类型（字典 file_type）
 * @param fileSize 文件大小（字节）
 */
export function addResourceManagement(data: {
  catalogId: number
  fileName: string
  fileType?: string
  fileSize?: number
}) {
  return http.request(
    { url: '/api/training/resources', method: 'post', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 按ID查询教培资源详情
 */
export function getResourceManagementById(id: number) {
  return http.request({ url: `/api/training/resources/${id}`, method: 'get' })
}

/**
 * @description: 更新教培资源
 * @param catalogId 所属资源编目ID（必填）
 * @param fileName 文件名（必填）
 * @param fileType 文件类型（字典 file_type）
 * @param fileSize 文件大小（字节）
 */
export function updateResourceManagement(
  id: number,
  data: {
    catalogId: number
    fileName: string
    fileType?: string
    fileSize?: number
  }
) {
  return http.request(
    { url: `/api/training/resources/${id}`, method: 'put', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 删除教培资源（逻辑删除）
 */
export function deleteResourceManagement(id: number) {
  return http.request(
    { url: `/api/training/resources/${id}`, method: 'delete' },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 批量导入教培资源
 * @param data 资源导入列表（catalogId + fileName 必填）
 */
export function importResourceManagement(
  data: {
    catalogId: number
    fileName: string
    fileType?: string
    fileSize?: number
  }[]
) {
  return http.request(
    { url: '/api/training/resources/import', method: 'post', data },
    { isShowSuccessMessage: true }
  )
}
