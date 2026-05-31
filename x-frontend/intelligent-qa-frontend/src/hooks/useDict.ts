import { ref, onMounted } from 'vue'
import { useDictStore } from '@/store'
import { getDictLabel as _getDictLabel } from '@/api/common'
import type { SelectOption } from '@/api/common'

/**
 * @description: 若依数据字典 Composable
 *
 * @example
 * // 基础用法：获取一个字典
 * const { system_code } = useDict('system_code')
 * // system_code.value => SelectOption[]
 *
 * @example
 * // 获取多个字典
 * const { system_code, project_type } = useDict('system_code', 'project_type')
 *
 * @example
 * // 在表格中转换字典值为名称
 * const { system_code, getDictLabel } = useDict('system_code')
 * getDictLabel('system_code', '001') // => '培训系统A'
 *
 * @param dictTypes 一个或多个字典类型名称
 *   支持：system_code / project_type / resource_category /
 *         publish_status / file_type / tag_type
 */
export function useDict(...dictTypes: string[]) {
  const dictStore = useDictStore()

  // 为每个 dictType 创建响应式 ref
  const dictMap: Record<string, ReturnType<typeof ref<SelectOption[]>>> = {}
  for (const type of dictTypes) {
    dictMap[type] = ref<SelectOption[]>([])
  }

  // 组件挂载时并行加载所有字典
  onMounted(async () => {
    await Promise.all(
      dictTypes.map(async type => {
        dictMap[type].value = await dictStore.fetchDict(type)
      })
    )
  })

  /**
   * 根据字典类型和编码获取字典名称（用于表格展示）
   * @param dictType 字典类型
   * @param value 字典编码
   */
  function getDictLabel(dictType: string, value: string): string {
    const options = dictMap[dictType]?.value ?? []
    return _getDictLabel(value, options)
  }

  return {
    ...dictMap,
    getDictLabel
  }
}
