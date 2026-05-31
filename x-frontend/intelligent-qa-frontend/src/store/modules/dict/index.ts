import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getDict, dictToOptions } from '@/api/common'
import type { SelectOption } from '@/api/common'

/**
 * @description: 若依数据字典 Pinia Store
 * - 维护以 dictType 为 key 的本地缓存，避免重复请求
 * - 对外暴露 fetchDict / clearDict
 */
export const useDictStore = defineStore('dict', () => {
  /** 字典缓存 Map：dictType → SelectOption[] */
  const dictCache = ref<Record<string, SelectOption[]>>({})

  /**
   * 获取指定类型的字典选项
   * - 命中缓存：直接返回，不重复请求
   * - 未命中：请求接口并写入缓存
   * @param dictType 字典类型，如 system_code / project_type 等
   */
  async function fetchDict(dictType: string): Promise<SelectOption[]> {
    if (dictCache.value[dictType]) {
      return dictCache.value[dictType]
    }
    try {
      const res = await getDict(dictType)
      // res 经过 axios transform 后已是 DictItemVO[]
      const options = dictToOptions(res as any)
      dictCache.value[dictType] = options
      return options
    } catch {
      return []
    }
  }

  /**
   * 清除字典缓存
   * @param dictType 指定类型则清除单个，不传则清除全部
   */
  function clearDict(dictType?: string) {
    if (dictType) {
      delete dictCache.value[dictType]
    } else {
      dictCache.value = {}
    }
  }

  return { dictCache, fetchDict, clearDict }
})
