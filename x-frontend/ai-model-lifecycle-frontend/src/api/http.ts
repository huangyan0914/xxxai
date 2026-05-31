import axios, { AxiosRequestConfig } from 'axios'

export interface Resp<T> {
  code: string
  msg: string
  data: T
}

const http = axios.create({
  baseURL: '',
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json'
  }
})

http.interceptors.response.use(response => {
  if (response.config.responseType === 'blob') {
    return response.data
  }
  const payload = response.data as Resp<unknown>
  if (payload && payload.code === '0') {
    return payload.data
  }
  const message = payload?.msg || '接口请求失败'
  window.$message?.error(message)
  return Promise.reject(new Error(message))
})

export function request<T>(config: AxiosRequestConfig) {
  if (config.data instanceof FormData) {
    config.headers = { ...(config.headers || {}), 'Content-Type': 'multipart/form-data' }
  }
  return http.request<unknown, T>(config)
}
