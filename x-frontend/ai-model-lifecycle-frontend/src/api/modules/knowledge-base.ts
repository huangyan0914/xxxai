import { request } from '../http'
import type { PageParams } from './types'

export function listKnowledgeBases(params: PageParams) {
  return request<any>({ url: '/api/aimodel/knowledge-bases', method: 'GET', params })
}

export function createKnowledgeBase(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/knowledge-bases', method: 'POST', data })
}

export function uploadKnowledgeBaseFile(file: File) {
  const data = new FormData()
  data.append('file', file)
  return request<any>({ url: '/api/aimodel/knowledge-bases/files/upload', method: 'POST', data })
}

export function addKnowledgeBaseFiles(id: number | string, fileIds: Array<number | string>) {
  return request<any>({
    url: `/api/aimodel/knowledge-bases/${id}/files`,
    method: 'POST',
    data: { file_ids: fileIds }
  })
}

export function listKnowledgeBaseFiles(id: number | string, params: { pageNum: number; pageSize: number }) {
  return request<any>({ url: `/api/aimodel/knowledge-bases/${id}/files`, method: 'GET', params })
}

export function deleteKnowledgeBaseFile(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/knowledge-bases/files', method: 'DELETE', data })
}

export function updateKnowledgeBase(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/knowledge-bases', method: 'PUT', data })
}

export function deleteKnowledgeBase(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/knowledge-bases', method: 'DELETE', data })
}
