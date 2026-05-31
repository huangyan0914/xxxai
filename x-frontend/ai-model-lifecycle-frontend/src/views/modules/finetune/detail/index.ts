import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFinetuneDetail, getFinetuneLog } from '@/api/lifecycle.ts'

export function useFinetuneDetail() {
  const route = useRoute()
  const router = useRouter()
  const loading = ref(false)
  const fullConfigVisible = ref(false)
  const detail = ref<Record<string, any>>({})
  const logs = ref<string[]>([])

  const fullBaseItems = computed(() => [
    { label: '任务名称', value: textValue(detail.value.name) },
    { label: '微调模型', value: textValue(detail.value.target_model_name) },
    { label: '基础模型', value: textValue(detail.value.base_model_name || detail.value.base_model_key) },
    { label: '训练状态', value: finetuneStatusText(detail.value.status) },
    { label: '来源', value: textValue(detail.value.created_from_info) },
    { label: '耗时', value: formatRuntime(detail.value.train_runtime) },
    { label: '训练数据集', value: formatDatasetList(detail.value.dataset_list || detail.value.datasets), wide: true },
    { label: '验证集占比', value: textValue(detail.value.finetune_config?.val_size) },
    { label: '微调类型', value: textValue(detail.value.finetuning_type) },
    { label: '训练模式', value: textValue(detail.value.finetune_config?.training_type) }
  ])

  const hyperParamItems = computed(() => {
    const config = detail.value.finetune_config || {}
    const items = [
      { label: '训练次数', value: textValue(config.num_epochs) },
      { label: '学习率', value: textValue(config.learning_rate) },
      { label: '学习率调整策略', value: textValue(config.lr_scheduler_type) },
      { label: '批次大小', value: textValue(config.batch_size) },
      { label: '序列最大长度', value: textValue(config.cutoff_len) },
      { label: 'LoRA R', value: textValue(config.lora_r) },
      { label: 'LoRA Alpha', value: textValue(config.lora_alpha) }
    ]
    if (config.num_gpus !== undefined && config.num_gpus !== null) {
      items.splice(3, 0, { label: 'GPU 卡数', value: textValue(config.num_gpus) })
    }
    return items
  })

  onMounted(loadDetail)

  async function loadDetail() {
    const id = String(route.params.id || '')
    if (!id) return
    loading.value = true
    try {
      const [detailPayload, logPayload] = await Promise.all([
        getFinetuneDetail(id),
        getFinetuneLog(id).catch(() => null)
      ])
      detail.value = unwrapDetail(detailPayload)
      logs.value = await decodeFinetuneLog(logPayload)
    } finally {
      loading.value = false
    }
  }

  return {
    router,
    loading,
    fullConfigVisible,
    detail,
    logs,
    fullBaseItems,
    hyperParamItems,
    finetuneStatusText,
    formatRuntime,
    formatDatasetList
  }
}

function unwrapDetail(payload: any) {
  return payload?.data || payload?.result || payload || {}
}

async function decodeFinetuneLog(payload: Blob | null) {
  if (!payload) return []
  const text = await payload.text()
  return text
    .replace(/\\n/g, '\n')
    .replace(/\\t/g, '\t')
    .replace(/\\r/g, '\r')
    .replace(/\\"/g, '"')
    .replace(/\\\\/g, '\\')
    .replace(/\\u([0-9a-fA-F]{4})/g, (_match, code) => String.fromCharCode(parseInt(code, 16)))
    .split('\n')
    .map(line => line.trimEnd())
    .filter(line => line.trim() !== '')
}

function textValue(value: any) {
  if (value === undefined || value === null || value === '') return '-'
  if (Array.isArray(value)) return value.map(item => textValue(item)).join('、')
  if (typeof value === 'object') return value.name || value.label || value.id || JSON.stringify(value)
  return String(value)
}

function formatRuntime(value: any) {
  if (value === undefined || value === null || value === '') return '-'
  return `${value}s`
}

function formatDatasetList(value: any) {
  if (!value) return '-'
  const datasets = Array.isArray(value) ? value : [value]
  if (datasets.length === 0) return '-'
  return datasets.map(item => {
    if (item && typeof item === 'object') {
      const name = item.name || item.label || item.data_set_name || item.id
      const version = item.version || item.version_name || item.branch_name
      return version ? `${name} > ${version}` : textValue(name)
    }
    return textValue(item)
  }).join('、')
}

function finetuneStatusText(status: any) {
  const labels: Record<string, string> = {
    InQueue: '排队中',
    Pending: '排队中',
    InProgress: '运行中',
    Download: '下载中',
    Completed: '已完成',
    Failed: '失败',
    Cancel: '已取消',
    Cancelled: '已取消',
    Suspended: '已暂停'
  }
  return labels[String(status)] || textValue(status)
}
