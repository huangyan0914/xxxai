import { http } from '@/utils/http/axios'

/**
 * @description: 字典项数据结构（后端 DictItemVO）
 */
export interface DictItemVO {
  /** 字典编码（value） */
  code: string
  /** 字典名称（label） */
  name: string
}

/**
 * @description: 统一下拉选项格式（适配 naive-ui n-select）
 */
export interface SelectOption {
  label: string
  value: string
}

/**
 * @description: 根据字典类型查询字典项列表
 * @param type 字典类型，如 system_code / project_type / resource_category 等
 * 支持：system_code、project_type、resource_category、publish_status、file_type、tag_type
 */
export function getDict(type: string) {
  return http.request<DictItemVO[]>({
    url: `/api/system/dict/${type}`,
    method: 'get'
  })
}

/**
 * @description: 将 DictItemVO 数组转换为 n-select 所需的 SelectOption 格式
 */
export function dictToOptions(list: DictItemVO[]): SelectOption[] {
  return (list || []).map(item => ({ label: item.name, value: item.code }))
}

/**
 * @description: 在字典列表中根据 value 查找对应的 label
 * @param value 字典编码
 * @param options 下拉选项列表
 */
export function getDictLabel(value: string, options: SelectOption[]): string {
  return options.find(o => o.value === value)?.label ?? value
}
