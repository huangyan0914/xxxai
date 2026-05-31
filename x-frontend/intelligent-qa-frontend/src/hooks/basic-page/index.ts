import { ResponseDataType, OptionsType } from './types'
export default function usePage<
  T extends (...args: any) => Promise<ResponseDataType<any>>
>(listRequestFn: T, options: OptionsType = {}) {
  const {
    immediate = true,
    filterOption = ref(),
    transformResFn = undefined,
    paginationF = true
  } = options

  // 加载状态
  const loading = ref(false)
  // 数据
  const list: any = ref([])

  const pagination = reactive({
    page: 1,
    pageSize: 10,
    itemCount: 0,
    showQuickJumper: true,
    showSizePicker: true,
    pageSizes: [10, 20, 30],
    prefix({ itemCount }) {
      return `共 ${itemCount} 条记录`
    },
    prev() {
      return '上一页'
    },
    next() {
      return '下一页'
    },
    onChange: (page: number) => {
      pagination.page = page
      loadData()
    },
    onUpdatePageSize: (pageSize: number) => {
      pagination.pageSize = pageSize
      pagination.page = 1
      loadData()
    }
  })

  const reset = () => {
    if (!filterOption.value) return
    const keys = Reflect.ownKeys(filterOption.value)
    keys.forEach(key => {
      Reflect.set(filterOption.value!, key, undefined)
    })
    loadData()
  }

  const loadData = () => {
    return new Promise(async (resolve, reject) => {
      loading.value = true
      try {
        const result = await listRequestFn(
          paginationF
            ? Object.assign(filterOption.value, {
                pageNum: pagination.page,
                pageSize: pagination.pageSize
              })
            : filterOption.value
        )

        const transformResult = transformResFn ? transformResFn(result) : result
        const data = paginationF ? transformResult.list : transformResult

        if (paginationF) {
          pagination.page = result.page
          pagination.pageSize = result.size
          pagination.itemCount = result.total
        }
        if (data) {
          list.value = data
        } else {
          list.value = []
        }
        options?.requestSuccess?.()
        resolve({
          list: data
        })
      } catch (error) {
        options?.requestError?.()
      } finally {
        loading.value = false
      }
    })
  }

  onMounted(() => {
    if (immediate) {
      loadData()
    }
  })

  return {
    loading,
    list,
    reset,
    loadData,
    pagination
  }
}
