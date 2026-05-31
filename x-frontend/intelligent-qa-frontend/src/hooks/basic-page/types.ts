import { Ref } from 'vue'
// Api接口类型
export interface ResponseDataType<T = any> {
  list: any[]
  size: number
  page: number
  total: number
}

export interface ListReturn<T = any> {
  list: T[]
}
export interface OptionsType<T = any> {
  requestError?: () => void // 请求失败
  requestSuccess?: () => void // 请求成功
  transformResFn?: (...args: any) => ListReturn // 处理返回数据
  immediate?: boolean // 加载执行
  filterOption?: Ref<T> // 查询条件
  paginationF?: boolean // 是否显示分页
}
