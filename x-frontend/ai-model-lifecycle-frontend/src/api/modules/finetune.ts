import { request } from '../http'
import type { PageParams } from './types'

export function listFinetunes(params: PageParams) {
  return request<any>({ url: '/api/aimodel/finetunes', method: 'GET', params })
}

export function createFinetune(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/finetunes', method: 'POST', data })
}

export function listFinetuneModels(params: Record<string, any> = {}) {
  return request<any>({ url: '/api/aimodel/finetunes/models', method: 'GET', params })
}

export function listFinetuneDatasets(qtype = 'mine') {
  return request<any>({ url: '/api/aimodel/finetunes/datasets', method: 'GET', params: { qtype } })
}

export function getFinetuneDetail(id: number | string) {
  return request<any>({ url: `/api/aimodel/finetunes/${id}/detail`, method: 'GET' })
}

export function getFinetuneLog(id: number | string) {
  return request<Blob>({ url: `/api/aimodel/finetunes/${id}/log`, method: 'GET', responseType: 'blob' })
}

export function deleteFinetune(id: number | string) {
  return request<any>({ url: `/api/aimodel/finetunes/${id}`, method: 'DELETE' })
}

export function pauseFinetune(id: number | string) {
  return request<any>({ url: `/api/aimodel/finetunes/${id}/pause`, method: 'PATCH' })
}

export function resumeFinetune(id: number | string) {
  return request<any>({ url: `/api/aimodel/finetunes/${id}/resume`, method: 'PATCH' })
}

export function cancelFinetune(id: number | string) {
  return request<any>({ url: `/api/aimodel/finetunes/${id}/cancel`, method: 'PATCH' })
}
