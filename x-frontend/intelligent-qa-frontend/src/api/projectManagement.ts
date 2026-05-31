import { http } from '@/utils/http/axios'

/**
 * @description: 培训项目分页查询
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @param projectName 项目名称（模糊）
 * @param projectCode 项目编码（模糊）
 * @param projectType 项目类型（字典 project_type）
 * @param category 类别
 * @param difficulty 难度
 */
export function getProjectPage(params: {
  pageNum?: number
  pageSize?: number
  projectName?: string
  projectCode?: string
  projectType?: string
  category?: string
  difficulty?: string
}) {
  return http.request({ url: '/api/training/projects', method: 'get', params })
}

/**
 * @description: 新增培训项目（可同时关联科目）
 * @param projectName 项目名称（必填）
 * @param projectCode 项目编码（必填）
 * @param subjectIds 关联科目ID列表
 */
export function addProject(data: {
  projectName: string
  projectCode: string
  projectType?: string
  category?: string
  difficulty?: string
  startDate?: string
  endDate?: string
  description?: string
  subjectIds?: number[]
}) {
  return http.request(
    { url: '/api/training/projects', method: 'post', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 按ID查询培训项目详情（含关联科目、已绑定资源数）
 */
export function getProjectById(id: number) {
  return http.request({ url: `/api/training/projects/${id}`, method: 'get' })
}

/**
 * @description: 更新培训项目及关联科目
 * @param projectName 项目名称（必填）
 * @param projectCode 项目编码（必填）
 */
export function updateProject(
  id: number,
  data: {
    projectName: string
    projectCode: string
    projectType?: string
    category?: string
    difficulty?: string
    startDate?: string
    endDate?: string
    description?: string
    subjectIds?: number[]
  }
) {
  return http.request(
    { url: `/api/training/projects/${id}`, method: 'put', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 删除培训项目（逻辑删除）
 */
export function deleteProject(id: number) {
  return http.request(
    { url: `/api/training/projects/${id}`, method: 'delete' },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 查询项目已绑定的教培资源ID列表
 */
export function getProjectBoundResources(id: number) {
  return http.request({
    url: `/api/training/projects/${id}/resources`,
    method: 'get'
  })
}

/**
 * @description: 为项目批量绑定教培资源
 * @param data 资源ID数组
 */
export function bindProjectResources(id: number, data: number[]) {
  return http.request(
    { url: `/api/training/projects/${id}/resources`, method: 'post', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 为项目解绑指定教培资源
 */
export function unbindProjectResource(id: number, resourceId: number) {
  return http.request(
    {
      url: `/api/training/projects/${id}/resources/${resourceId}`,
      method: 'delete'
    },
    { isShowSuccessMessage: true }
  )
}
