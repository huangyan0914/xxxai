import { request } from '../http'
import type { PageParams } from './types'

export function listModels(params: PageParams) {
  return request<any>({ url: '/api/aimodel/models', method: 'GET', params })
}

export function createModel(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/models', method: 'POST', data })
}

export function listModelDefaultIcons() {
  return request<any>({ url: '/api/aimodel/models/default-icons', method: 'GET' })
}

export function listExistingModels(params: Record<string, any> = {}) {
  return request<any>({ url: '/api/aimodel/models/existing', method: 'GET', params })
}

export function updateModel(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/models', method: 'PUT', data })
}

export function deleteModel(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/models', method: 'DELETE', data })
}
