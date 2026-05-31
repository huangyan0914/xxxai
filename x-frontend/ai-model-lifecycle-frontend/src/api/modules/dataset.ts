import { request } from '../http'
import type { PageParams } from './types'

export function listDatasets(params: PageParams) {
  return request<any>({ url: '/api/aimodel/datasets', method: 'GET', params })
}

export function createDataset(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/datasets', method: 'POST', data })
}

export function uploadDatasetFile(file: File, fileType = 'doc') {
  const data = new FormData()
  data.append('file', file)
  data.append('fileType', fileType)
  return request<any>({ url: '/api/aimodel/datasets/files/upload', method: 'POST', data })
}

export function getDatasetDetail(id: number | string) {
  return request<any>({ url: `/api/aimodel/datasets/${id}`, method: 'GET' })
}

export function listDatasetVersions(id: number | string, params: { pageNum: number; pageSize: number; versionType?: string }) {
  return request<any>({ url: `/api/aimodel/datasets/${id}/versions`, method: 'GET', params })
}

export function listDatasetTagVersions(id: number | string) {
  return request<any>({ url: `/api/aimodel/datasets/${id}/tag-versions`, method: 'GET' })
}

export function createDatasetVersionFromTag(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/datasets/versions/from-tag', method: 'POST', data })
}

export function publishDatasetVersion(versionId: number | string) {
  return request<any>({ url: `/api/aimodel/datasets/versions/${versionId}/publish`, method: 'PATCH' })
}

export function deleteDatasetVersion(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/datasets/versions', method: 'DELETE', data })
}

export function updateDatasetTags(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/datasets', method: 'PUT', data })
}

export function deleteDataset(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/datasets', method: 'DELETE', data })
}
