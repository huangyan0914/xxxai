import { http } from '@/utils/http/axios'

/**
 * @description: 上传文件响应数据结构
 */
export interface FileUploadVO {
  /** 文件ID */
  fileId: string
  /** 文件名 */
  fileName: string
  /** 文件大小（字节） */
  size: number
  /** 文件类型 */
  type: string
  /** 文件访问地址 */
  url: string
}

/**
 * @description: 上传文件统一响应结构
 */
export interface RespFileUploadVO {
  code: string
  msg: string
  data: FileUploadVO
}

/**
 * @description: 上传文件
 * @param file 上传的文件（必填）
 */
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.request<RespFileUploadVO>({
    url: '/api/system/files/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * @description: 按 fileId 下载文件
 * @param fileId 文件ID（必填）
 * @param fileName 下载文件名（可选）
 */
export function downloadFile(fileId: string, fileName?: string) {
  return http.request({
    url: `/api/system/files/${fileId}/download`,
    method: 'get',
    params: fileName ? { fileName } : undefined,
    responseType: 'blob'
  })
}

/**
 * @description: 按 fileId 删除文件
 * @param fileId 文件ID（必填）
 */
export function deleteFile(fileId: string) {
  return http.request<{ code: string; msg: string; data: boolean }>(
    { url: `/api/system/files/${fileId}`, method: 'delete' },
    { isShowSuccessMessage: true }
  )
}
