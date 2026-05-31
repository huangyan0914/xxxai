import { request } from '../http'
import type { PageParams } from './types'

export function listInferenceServices(params: PageParams) {
  return request<any>({ url: '/api/aimodel/inference-services', method: 'GET', params })
}

export function createInferenceService(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/inference-services', method: 'POST', data })
}

export function listDeployableModels(params: Record<string, any> = {}) {
  return request<any>({ url: '/api/aimodel/inference-services/deployable-models', method: 'GET', params })
}

export function startInferenceService(serviceId: number | string) {
  return request<any>({
    url: '/api/aimodel/inference-services/start',
    method: 'PATCH',
    data: { service_id: serviceId }
  })
}

export function stopInferenceService(serviceId: number | string) {
  return request<any>({
    url: '/api/aimodel/inference-services/stop',
    method: 'PATCH',
    data: { service_id: serviceId }
  })
}

export function startInferenceGroup(groupId: number | string) {
  return request<any>({
    url: '/api/aimodel/inference-services/groups/start',
    method: 'PATCH',
    data: { group_id: groupId }
  })
}

export function closeInferenceGroup(groupId: number | string) {
  return request<any>({
    url: '/api/aimodel/inference-services/groups/close',
    method: 'PATCH',
    data: { group_id: groupId }
  })
}

export function deleteInferenceService(serviceId: number | string) {
  return request<any>({
    url: '/api/aimodel/inference-services/services',
    method: 'DELETE',
    data: { service_id: serviceId }
  })
}
