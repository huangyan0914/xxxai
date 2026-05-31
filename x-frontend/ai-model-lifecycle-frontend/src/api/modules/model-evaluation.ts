import { request } from '../http'
import type { PageParams } from './types'

export function listModelEvaluations(params: PageParams) {
  return request<any>({ url: '/api/aimodel/model-evaluations', method: 'GET', params })
}

export function createModelEvaluation(data: Record<string, any>) {
  return request<any>({ url: '/api/aimodel/model-evaluations', method: 'POST', data })
}

export function listEvaluationInferenceModels(params: Record<string, any> = {}) {
  return request<any>({ url: '/api/aimodel/model-evaluations/inference-models', method: 'GET', params })
}

export function uploadEvaluationDataset(files: File[]) {
  const data = new FormData()
  files.forEach(file => data.append('files', file))
  return request<any>({ url: '/api/aimodel/model-evaluations/datasets/upload', method: 'POST', data })
}

export function deleteModelEvaluation(id: number | string) {
  return request<any>({ url: `/api/aimodel/model-evaluations/${id}`, method: 'DELETE' })
}
