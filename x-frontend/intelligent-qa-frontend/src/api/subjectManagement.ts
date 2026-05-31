import { http } from '@/utils/http/axios'

/**
 * @description: 分页查询培训科目
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @param subjectName 科目名称（模糊）
 * @param systemCode 系统编码
 */
export function getSubjectPage(params: {
  pageNum?: number
  pageSize?: number
  subjectName?: string
  systemCode?: string
}) {
  return http.request({ url: '/api/training/subjects', method: 'get', params })
}

/**
 * @description: 新增培训科目
 * @param subjectName 科目名称（必填）
 * @param subjectDesc 科目描述
 * @param systemCode 系统编码（字典 system_code）
 */
export function addSubject(data: {
  subjectName: string
  subjectDesc?: string
  systemCode?: string
}) {
  return http.request(
    { url: '/api/training/subjects', method: 'post', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 按ID查询培训科目详情
 */
export function getSubjectById(id: number) {
  return http.request({ url: `/api/training/subjects/${id}`, method: 'get' })
}

/**
 * @description: 更新培训科目
 * @param subjectName 科目名称（必填）
 * @param subjectDesc 科目描述
 * @param systemCode 系统编码（字典 system_code）
 */
export function updateSubject(
  id: number,
  data: { subjectName: string; subjectDesc?: string; systemCode?: string }
) {
  return http.request(
    { url: `/api/training/subjects/${id}`, method: 'put', data },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 删除培训科目（逻辑删除）
 */
export function deleteSubject(id: number) {
  return http.request(
    { url: `/api/training/subjects/${id}`, method: 'delete' },
    { isShowSuccessMessage: true }
  )
}

/**
 * @description: 获取科目关联的项目ID列表
 */
export function getSubjectConfig(id: number) {
  return http.request({
    url: `/api/training/subjects/${id}/config`,
    method: 'get'
  })
}

/**
 * @description: 保存科目关联的项目ID列表
 * @param data 项目ID数组
 */
export function saveSubjectConfig(id: number, data: number[]) {
  return http.request(
    { url: `/api/training/subjects/${id}/config`, method: 'put', data },
    { isShowSuccessMessage: true }
  )
}
