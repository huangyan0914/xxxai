import { computed, h, onMounted, reactive, ref, watch } from 'vue'
import { NButton, NTag, NSpace, useDialog, type DataTableColumns, type FormInst } from 'naive-ui'
import { useRouter } from 'vue-router'
import {
  addKnowledgeBaseFiles,
  cancelFinetune,
  closeInferenceGroup,
  createDataset,
  createFinetune,
  createInferenceService,
  createKnowledgeBase,
  createModel,
  createModelEvaluation,
  deleteDatasetVersion,
  deleteDataset,
  deleteFinetune,
  deleteKnowledgeBase,
  deleteKnowledgeBaseFile,
  deleteInferenceService,
  deleteModel,
  deleteModelEvaluation,
  listDatasetVersions,
  listDatasets,
  listFinetunes,
  listFinetuneDatasets,
  listFinetuneModels,
  listDeployableModels,
  listInferenceServices,
  listKnowledgeBaseFiles,
  listKnowledgeBases,
  listModelEvaluations,
  listModels,
  pauseFinetune,
  publishDatasetVersion,
  resumeFinetune,
  startInferenceGroup,
  startInferenceService,
  stopInferenceService,
  uploadDatasetFile,
  uploadEvaluationDataset,
  uploadKnowledgeBaseFile,
  updateDatasetTags,
  updateKnowledgeBase,
  updateModel
} from '@/api/lifecycle.ts'
import type { FieldConfig, ModuleKey } from './types'

export function useLifecycleWorkspace(props: { moduleKey: ModuleKey }) {
  const dialog = useDialog()
  const router = useRouter()
  const formRef = ref<FormInst | null>(null)
  const loading = ref(false)
  const saving = ref(false)
  const keyword = ref('')
  const qtype = ref('mine')
  const records = ref<any[]>([])
  const drawerVisible = ref(false)
  const drawerMode = ref<'create' | 'edit'>('create')
  const currentRow = ref<any>(null)
  const formModel = reactive<Record<string, any>>({})
  const advancedJson = ref('')
  const uploadedDatasetPaths = ref<string[]>([])
  const knowledgeFilesVisible = ref(false)
  const knowledgeFileRecords = ref<any[]>([])
  const knowledgeFileLoading = ref(false)
  const selectedKnowledgeBase = ref<any>(null)
  const datasetVersionsVisible = ref(false)
  const datasetVersionRecords = ref<any[]>([])
  const datasetVersionLoading = ref(false)
  const selectedDataset = ref<any>(null)
  const datasetVersionType = ref('branch')
  const finetuneOptionLoading = ref(false)
  const finetuneModelOptions = ref<Array<{ label: string; value: string | number }>>([])
  const finetuneDatasetOptions = ref<Array<{ label: string; value: string | number }>>([])
  const finetuneModelMeta = ref<Record<string, any>>({})
  const finetuneDatasetMeta = ref<Record<string, any>>({})
  const inferenceModelLoading = ref(false)
  const inferenceModelOptions = ref<Array<{ label: string; value: string | number }>>([])
  const inferenceModelMeta = ref<Record<string, any>>({})

  const pagination = reactive({
    page: 1,
    pageSize: 10,
    itemCount: 0,
    showQuickJumper: true,
    showSizePicker: true,
    pageSizes: [10, 20, 50],
    prefix({ itemCount }: { itemCount: number }) {
      return `共 ${itemCount} 条记录`
    },
    prev() {
      return '上一页'
    },
    next() {
      return '下一页'
    }
  })
  const knowledgeFilePagination = reactive({
    page: 1,
    pageSize: 10,
    itemCount: 0
  })
  const datasetVersionPagination = reactive({
    page: 1,
    pageSize: 10,
    itemCount: 0
  })

  const evaluationScopes = [
    { label: '我的任务', value: 'mine' },
    { label: '组内任务', value: 'group' }
  ]

  const modelTypeOptions = [
    { label: '大模型', value: 'localLLM' },
    { label: '在线模型', value: 'OnlineLLM' },
    { label: '向量模型', value: 'Embedding' },
    { label: '重排模型', value: 'reranker' },
    { label: '文字识别', value: 'OCR' },
    { label: '语音转文字', value: 'STT' },
    { label: '文字转语音', value: 'TTS' },
    { label: '视觉问答', value: 'VQA' }
  ]
  const modelKindOptions = [
    { label: '大模型', value: 'OnlineLLM' },
    { label: '本地大模型', value: 'localLLM' },
    { label: '向量模型', value: 'Embedding' },
    { label: '重排模型', value: 'reranker' }
  ]
  const modelFromOptions = [
    { label: 'huggingface', value: 'huggingface' },
    { label: 'modelscope', value: 'modelscope' },
    { label: '上传模型', value: 'localModel' },
    { label: '平台在线模型', value: 'online' }
  ]
  const datasetFormatOptions = [
    { label: 'Alpaca 预训练', value: 'Alpaca_pre_train' },
    { label: 'Alpaca 微调', value: 'Alpaca_fine_tuning' },
    { label: 'OpenAI 微调', value: 'Openai_fine_tuning' },
    { label: 'ShareGPT 微调', value: 'Sharegpt_fine_tuning' }
  ]
  const datasetUploadOptions = [
    { label: '本地文件', value: 'local' },
    { label: 'URL导入', value: 'url' }
  ]
  const datasetVersionTypeOptions = [
    { label: '分支版本', value: 'branch' },
    { label: '标签版本', value: 'tag' }
  ]
  const datasetFormatAlias: Record<string, string> = {
    Alpaca_fine_tuning: 'DATASET_FORMAT_ALPACA',
    Alpaca_pre_train: 'DATASET_FORMAT_ALPACA',
    Openai_fine_tuning: 'DATASET_FORMAT_OPENAI',
    Sharegpt_fine_tuning: 'DATASET_FORMAT_SHAREGPT'
  }
  const trainingTypeOptions = ['PT', 'SFT', 'RM', 'PPO', 'DPO'].map(value => ({ label: value, value }))
  const finetuningTypeOptions = [
    { label: 'LoRA', value: 'LoRA' },
    { label: 'QLoRA', value: 'QLoRA' },
    { label: 'Full', value: 'Full' }
  ]
  const lrSchedulerTypeOptions = [
    'linear',
    'cosine',
    'cosine_with_restarts',
    'polynomial',
    'constant',
    'constant_with_warmup',
    'inverse_sqrt',
    'reduce_lr_on_plateau',
    'cosine_with_min_lr',
    'warmup_stable_decay'
  ].map(value => ({ label: value, value }))
  const evaluationTypeOptions = [
    { label: '在线推理', value: 'online' },
    { label: '离线结果', value: 'offline' }
  ]
  const evaluationMethodOptions = [
    { label: '人工测评', value: 'manual' },
    { label: 'AI测评', value: 'ai' }
  ]

  const moduleMeta = {
    knowledgeBases: {
      name: '知识库管理',
      searchPlaceholder: '知识库名称',
      scrollX: 1100,
      list: listKnowledgeBases,
      create: createKnowledgeBase,
      update: updateKnowledgeBase,
      remove: (row: any) => deleteKnowledgeBase({ kb_id: row.id, id: row.id }),
      fields: [
        { key: 'name', label: '名称', required: true },
        { key: 'description', label: '简介', type: 'textarea', span: 2 },
        { key: 'tag_names', label: '标签', placeholder: '多个标签用英文逗号分隔', span: 2 }
      ] as FieldConfig[],
      columns: [
        { title: '名称', key: 'name', width: 180 },
        { title: '标签', key: 'tags', width: 220, render: renderTags },
        { title: '描述', key: 'description', ellipsis: { tooltip: true } },
        { title: '创建人', key: 'user_name', width: 120 },
        { title: '更新时间', key: 'updated_at', width: 180 },
        {
          title: '操作',
          key: 'actions',
          width: 220,
          fixed: 'right',
          render: (row: any) => actionGroup([
            actionButton('编辑', () => openEdit(row)),
            actionButton('文件', () => openKnowledgeFiles(row)),
            actionButton('删除', () => removeRow(row), true)
          ])
        }
      ]
    },
    models: {
      name: '模型列表',
      searchPlaceholder: '模型名称',
      scrollX: 1260,
      list: listModels,
      create: createModel,
      update: updateModel,
      remove: (row: any) => deleteModel({ model_id: row.id, qtype: row.user_id === '00000000-0000-0000-0000-000000000000' ? 'builtin' : 'mine' }),
      fields: [
        { key: 'model_name', label: '模型名称', required: true },
        { key: 'model_type', label: '模型类型', type: 'select', required: true, options: modelTypeOptions },
        { key: 'model_kind', label: '模型类别', type: 'select', options: modelKindOptions },
        { key: 'model_from', label: '模型来源', type: 'select', options: modelFromOptions },
        { key: 'model_url', label: '模型地址', span: 2 },
        { key: 'tag_names', label: '标签', placeholder: '多个标签用英文逗号分隔', span: 2 }
      ] as FieldConfig[],
      columns: [
        { title: '模型名称', key: 'model_name', width: 220, render: (row: any) => row.model_name || row.model_brand },
        { title: '类型', key: 'model_type_display', width: 130 },
        { title: '来源', key: 'model_from', width: 120 },
        { title: '标签', key: 'tags', width: 220, render: renderTags },
        { title: '创建人', key: 'user_name', width: 120 },
        { title: '更新时间', key: 'updated_at', width: 180 }
      ]
    },
    modelEvaluations: {
      name: '模型评测',
      searchPlaceholder: '评测任务名称',
      scrollX: 1260,
      list: listModelEvaluations,
      create: createModelEvaluation,
      remove: (row: any) => deleteModelEvaluation(row.id),
      fields: [
        { key: 'task_name', label: '任务名称', required: true },
        { key: 'model_name', label: '测评模型', required: true, placeholder: 'LazyCraft 模型名称或模型标识' },
        { key: 'evaluation_type', label: '测评数据集', type: 'select', options: evaluationTypeOptions },
        { key: 'evaluation_method', label: '测评方式', type: 'select', options: evaluationMethodOptions },
        { key: 'dataset_id', label: '数据集ID', required: true, span: 2, placeholder: '多个数据集用英文逗号分隔' }
      ] as FieldConfig[],
      columns: [
        { title: '任务名称', key: 'name', width: 180 },
        { title: '测评模型', key: 'model_name', width: 180, ellipsis: { tooltip: true } },
        { title: '测评方式', key: 'evaluation_method', width: 120, render: (row: any) => row.evaluation_method === 'manual' ? '人工测评' : 'AI测评' },
        { title: '进度', key: 'process', width: 100 },
        { title: '状态', key: 'status_zh', width: 120 },
        { title: '创建人', key: 'creator', width: 120 },
        { title: '创建时间', key: 'created_time', width: 180 },
        { title: '操作', key: 'actions', width: 150, fixed: 'right', render: (row: any) => actionGroup([actionButton('复制新增', () => openEdit(row)), actionButton('删除', () => removeRow(row), true)]) }
      ]
    },
    inferenceServices: {
      name: '推理服务',
      searchPlaceholder: '服务或模型名称',
      scrollX: 1380,
      list: listInferenceServices,
      create: createInferenceService,
      fields: [
        { key: 'model_type', label: '模型类型', type: 'select', required: true, options: modelTypeOptions },
        { key: 'model_id', label: '模型', type: 'select', required: true, span: 2, placeholder: '请选择可部署模型' },
        { key: 'service_name', label: '推理服务名称', required: true },
        { key: 'model_num_gpus', label: 'GPU 数量', type: 'number', min: 0, step: 1, precision: 0 }
      ] as FieldConfig[],
      columns: [
        { title: '模型名称', key: 'model_name', width: 220 },
        { title: '模型类型', key: 'model_type_display', width: 140 },
        { title: '在线/总数', key: 'service_count', width: 110, render: (row: any) => `${row.online_count || 0}/${row.service_count || 0}` },
        { title: '服务', key: 'services', minWidth: 440, render: renderServices },
        { title: '创建人', key: 'user_name', width: 120 },
        {
          title: '操作',
          key: 'actions',
          width: 180,
          fixed: 'right',
          render: (row: any) => actionGroup([
            actionButton('启动组', () => operateGroup(row.id || row.gid, 'start')),
            actionButton('关闭组', () => operateGroup(row.id || row.gid, 'close'))
          ])
        }
      ]
    },
    finetunes: {
      name: '模型微调',
      searchPlaceholder: '微调任务名称',
      scrollX: 1420,
      list: listFinetunes,
      create: createFinetune,
      remove: (row: any) => deleteFinetune(row.id),
      fields: [
        { key: 'name', label: '任务名称', required: true },
        { key: 'base_model_key', label: '基础模型', type: 'select', required: true, span: 2, placeholder: '请选择基础模型' },
        { key: 'target_model_name', label: '微调模型名称', required: true },
        { key: 'training_type', label: '训练模式', type: 'select', options: trainingTypeOptions },
        { key: 'datasets', label: '训练数据集', type: 'select', required: true, multiple: true, span: 2, placeholder: '请选择训练数据集' },
        { key: 'finetuning_type', label: '微调方式', type: 'select', options: finetuningTypeOptions },
        { key: 'val_size', label: '验证集占比', type: 'number', min: 0, max: 0.99, step: 0.01, precision: 2 },
        { key: 'num_epochs', label: '训练轮数', type: 'number', min: 1, step: 1, precision: 0 },
        { key: 'learning_rate', label: '学习率', type: 'number', min: 0, step: 0.00001, precision: 6 },
        { key: 'lr_scheduler_type', label: '学习率调度', type: 'select', options: lrSchedulerTypeOptions },
        { key: 'cutoff_len', label: '截断长度', type: 'number', min: 1, step: 1, precision: 0 },
        { key: 'batch_size', label: '批大小', type: 'number', min: 1, step: 1, precision: 0 },
        { key: 'lora_r', label: 'LoRA R', type: 'select', options: [2, 4, 8, 16, 32, 64].map(value => ({ label: String(value), value })) },
        { key: 'lora_alpha', label: 'LoRA Alpha', type: 'number', min: 1, step: 1, precision: 0 }
      ] as FieldConfig[],
      columns: [
        { title: '任务名称', key: 'name', width: 180 },
        { title: '基础模型', key: 'base_model_key', width: 190 },
        { title: '微调模型', key: 'target_model_name', width: 220 },
        { title: '状态', key: 'status_label', width: 110, render: renderStatus },
        { title: '训练时长', key: 'train_runtime', width: 110, render: (row: any) => row.train_runtime ? `${row.train_runtime}s` : '-' },
        { title: '创建人', key: 'created_by_account', width: 120, render: (row: any) => row.created_by_account?.name || row.created_by },
        { title: '创建时间', key: 'created_at', width: 180 },
        { title: '操作', key: 'actions', width: 220, fixed: 'right', render: renderFinetuneActions }
      ]
    },
    datasets: {
      name: '数据集管理',
      searchPlaceholder: '数据集名称',
      scrollX: 1220,
      list: listDatasets,
      create: createDataset,
      update: updateDatasetTags,
      remove: (row: any) => deleteDataset({ data_set_id: row.id }),
      fields: [
        { key: 'name', label: '数据名称', required: true },
        { key: 'tag_names', label: '标签', placeholder: '多个标签用英文逗号分隔' },
        { key: 'description', label: '简介', type: 'textarea', span: 2 },
        { key: 'data_type', label: '数据类型', type: 'select', options: [{ label: '文本数据', value: 'doc' }] },
        { key: 'data_format', label: '数据格式', type: 'select', options: datasetFormatOptions },
        { key: 'upload_type', label: '导入方式', type: 'select', options: datasetUploadOptions },
        { key: 'file_urls', label: '文件URL', span: 2, placeholder: '多个 URL 用英文逗号分隔' }
      ] as FieldConfig[],
      columns: [
        { title: '名称', key: 'name', width: 180 },
        { title: '标签', key: 'tags', width: 220, render: renderTags },
        { title: '数据类型', key: 'data_type', width: 120 },
        { title: '数据格式', key: 'data_format', width: 180 },
        { title: '来源', key: 'from_type', width: 120, render: (row: any) => row.from_type === 'upload' ? '数据上传' : row.from_type },
        { title: '创建人', key: 'user_name', width: 120 },
        { title: '创建时间', key: 'created_at', width: 180 },
        {
          title: '操作',
          key: 'actions',
          width: 230,
          fixed: 'right',
          render: (row: any) => actionGroup([
            actionButton('编辑标签', () => openEdit(row)),
            actionButton('版本', () => openDatasetVersions(row)),
            actionButton('删除', () => removeRow(row), true)
          ])
        }
      ]
    }
  } as const

  const knowledgeFileColumns = computed<DataTableColumns<any>>(() => [
    { title: '文件名', key: 'name', minWidth: 220, render: (row: any) => row.name || row.file_name || row.filename || '-' },
    { title: '状态', key: 'status', width: 120, render: renderStatus },
    { title: '更新时间', key: 'updated_at', width: 180 },
    {
      title: '操作',
      key: 'actions',
      width: 100,
      fixed: 'right',
      render: (row: any) => actionGroup([actionButton('删除', () => removeKnowledgeFile(row), true)])
    }
  ])
  const datasetVersionColumns = computed<DataTableColumns<any>>(() => [
    { title: '版本名称', key: 'name', minWidth: 200 },
    { title: '状态', key: 'status', width: 120, render: renderDatasetVersionStatus },
    { title: '处理中动作', key: 'version_doing', width: 140 },
    { title: '更新时间', key: 'updated_at', width: 180 },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (row: any) => actionGroup([
        actionButton('发布', () => publishVersion(row), false, Number(row.status) !== 2),
        actionButton('删除', () => removeDatasetVersion(row), true)
      ])
    }
  ])

  const moduleKey = computed(() => props.moduleKey)
  const config = computed(() => moduleMeta[moduleKey.value])
  const formFields = computed(() => {
    const fields = [...config.value.fields] as FieldConfig[]
    if (moduleKey.value === 'inferenceServices') {
      return fields.map(field => {
        if (field.key === 'model_id') {
          return { ...field, options: inferenceModelOptions.value, loading: inferenceModelLoading.value }
        }
        return field
      })
    }
    if (moduleKey.value !== 'finetunes') {
      return fields
    }
    return fields.map(field => {
      if (field.key === 'base_model_key') {
        return { ...field, options: finetuneModelOptions.value, loading: finetuneOptionLoading.value }
      }
      if (field.key === 'datasets') {
        return { ...field, options: finetuneDatasetOptions.value, loading: finetuneOptionLoading.value }
      }
      return field
    })
  })
  const editingSupported = computed(() => Boolean((config.value as any).update))
  const drawerTitle = computed(() => `${drawerMode.value === 'create' || !editingSupported.value ? '新增' : '编辑'}${config.value.name}`)
  const columns = computed<DataTableColumns<any>>(() => {
    const baseColumns = [...config.value.columns] as any[]
    const hasAction = baseColumns.some(column => column.key === 'actions')
    if (!hasAction) {
      baseColumns.push({
        title: '操作',
        key: 'actions',
        width: 150,
        fixed: 'right',
        render: (row: any) => actionGroup([
          actionButton('编辑', () => openEdit(row)),
          actionButton('删除', () => removeRow(row), true)
        ])
      })
    }
    return baseColumns as DataTableColumns<any>
  })
  const summaryCards = computed(() => [
    { label: '当前记录', value: records.value.length },
    { label: '总数', value: pagination.itemCount },
    { label: '页码', value: pagination.page },
    { label: '数据源', value: 'LazyCraft' }
  ])

  watch(() => props.moduleKey, () => reset(), { immediate: false })
  watch(() => formModel.model_type, (value, oldValue) => {
    if (moduleKey.value !== 'inferenceServices' || !drawerVisible.value || value === oldValue) return
    formModel.model_id = ''
    loadInferenceModelOptions()
  })
  onMounted(loadData)

  async function loadData() {
    loading.value = true
    try {
      const data = await config.value.list({
        pageNum: pagination.page,
        pageSize: pagination.pageSize,
        keyword: keyword.value,
        qtype: qtype.value
      })
      const normalized = normalizePage(data)
      records.value = ensureRows(normalized.records)
      pagination.itemCount = normalized.total
    } finally {
      loading.value = false
    }
  }

  function reload() {
    pagination.page = 1
    loadData()
  }

  function reset() {
    keyword.value = ''
    qtype.value = 'mine'
    pagination.page = 1
    loadData()
  }

  function updatePage(page: number) {
    pagination.page = page
    loadData()
  }

  function updatePageSize(pageSize: number) {
    pagination.pageSize = pageSize
    pagination.page = 1
    loadData()
  }

  function openCreate() {
    drawerMode.value = 'create'
    currentRow.value = null
    fillForm(defaultForm(moduleKey.value))
    advancedJson.value = defaultAdvancedJson(moduleKey.value)
    uploadedDatasetPaths.value = []
    drawerVisible.value = true
    if (moduleKey.value === 'finetunes') loadFinetuneOptions()
    if (moduleKey.value === 'inferenceServices') loadInferenceModelOptions()
  }

  function openEdit(row: any) {
    drawerMode.value = editingSupported.value ? 'edit' : 'create'
    currentRow.value = row
    fillForm(rowToForm(row))
    advancedJson.value = JSON.stringify(rowToAdvanced(row), null, 2)
    uploadedDatasetPaths.value = arrayFrom(row.file_paths)
    drawerVisible.value = true
    if (moduleKey.value === 'finetunes') loadFinetuneOptions()
    if (moduleKey.value === 'inferenceServices') loadInferenceModelOptions()
  }

  async function submitForm() {
    await formRef.value?.validate()
    saving.value = true
    try {
      const payload = buildPayload()
      if (drawerMode.value === 'edit' && editingSupported.value) {
        await (config.value as any).update(payload)
      } else {
        await (config.value as any).create(payload)
      }
      window.$message?.success('保存成功')
      drawerVisible.value = false
      loadData()
    } finally {
      saving.value = false
    }
  }

  function removeRow(row: any) {
    const remove = (config.value as any).remove
    if (!remove) {
      window.$message?.warning('当前模块暂无删除封装')
      return
    }
    confirm(`删除${config.value.name}`, async () => {
      await remove(row)
      window.$message?.success('删除成功')
      loadData()
    })
  }

  function buildPayload() {
    const advanced = parseAdvancedJson()
    const payload: Record<string, any> = { ...advanced, ...stripEmpty({ ...formModel }) }
    if (moduleKey.value === 'finetunes') {
      return buildFinetunePayload(payload)
    }
    if (drawerMode.value === 'edit' && currentRow.value?.id) {
      payload.id = currentRow.value.id
      payload.target_id = currentRow.value.id
      if (moduleKey.value === 'models') payload.model_id = currentRow.value.id
      if (moduleKey.value === 'knowledgeBases') payload.kb_id = currentRow.value.id
    }
    normalizeArrayField(payload, 'tag_names')
    normalizeArrayField(payload, 'file_urls')
    normalizeArrayField(payload, 'dataset_id')
    parseJsonField(payload, 'services')
    if (moduleKey.value === 'inferenceServices') {
      return buildInferenceServicePayload(payload)
    }
    if (moduleKey.value === 'datasets') {
      payload.from_type = payload.from_type || 'upload'
      payload.data_type = payload.data_type || 'doc'
      payload.upload_type = payload.upload_type || 'local'
      if (uploadedDatasetPaths.value.length > 0) payload.file_paths = uploadedDatasetPaths.value
    }
    return payload
  }

  function buildInferenceServicePayload(payload: Record<string, any>) {
    const selectedModel = inferenceModelMeta.value[String(payload.model_id)] || {}
    payload.model_type = payload.model_type || selectedModel.model_type || selectedModel.model_kind || 'localLLM'

    const serviceName = payload.service_name
    const modelNumGpus = payload.model_num_gpus
    const firstService = Array.isArray(payload.services) ? payload.services[0] || {} : {}
    payload.services = [stripEmpty({
      ...firstService,
      name: serviceName,
      model_num_gpus: modelNumGpus ?? firstService.model_num_gpus ?? 1
    })]

    delete payload.service_name
    delete payload.model_num_gpus
    return payload
  }

  function buildFinetunePayload(payload: Record<string, any>) {
    const datasets = arrayFrom(payload.datasets)
    const datasetsType = datasets.map(dataset => getDatasetFormat(dataset))
    return {
      base: {
        name: payload.name,
        base_model: Number(payload.base_model ?? 0),
        base_model_key: payload.base_model_key,
        target_model_name: payload.target_model_name,
        datasets,
        datasets_type: datasetsType,
        finetuning_type: payload.finetuning_type || 'LoRA',
        created_from: payload.created_from ?? 1,
        created_from_info: payload.created_from_info || '模型微调'
      },
      finetune_config: {
        training_type: payload.training_type || 'SFT',
        val_size: payload.val_size ?? 0.01,
        num_epochs: payload.num_epochs ?? 2,
        learning_rate: String(payload.learning_rate ?? 0.00005),
        lr_scheduler_type: payload.lr_scheduler_type || 'cosine',
        cutoff_len: payload.cutoff_len ?? 1024,
        batch_size: payload.batch_size ?? 2,
        lora_r: payload.lora_r ?? 8,
        lora_alpha: payload.lora_alpha ?? 8
      }
    }
  }

  function fillForm(values: Record<string, any>) {
    Object.keys(formModel).forEach(key => delete formModel[key])
    formFields.value.forEach(field => {
      formModel[field.key] = values[field.key] ?? ''
    })
  }

  function rowToForm(row: any) {
    const values: Record<string, any> = { ...row }
    const firstService = Array.isArray(row.services) ? row.services[0] || {} : {}
    values.tag_names = arrayToText(row.tags || row.label)
    values.model_name = row.model_name || row.model_brand || row.name
    values.file_urls = arrayToText(row.file_urls)
    values.dataset_id = arrayToText(row.dataset_id)
    values.datasets = arrayFrom(row.datasets || row.dataset_id)
    values.batch_size = row.batch_size ?? row.per_device_train_batch_size
    values.service_name = firstService.name || ''
    values.model_num_gpus = firstService.model_num_gpus ?? 1
    values.services = row.services ? JSON.stringify(row.services, null, 2) : ''
    return { ...defaultForm(moduleKey.value), ...values }
  }

  function rowToAdvanced(row: any) {
    const keep = { ...row }
    delete keep.tags
    delete keep.label
    delete keep.services
    return keep
  }

  function defaultForm(moduleValue: ModuleKey) {
    const defaults: Record<ModuleKey, Record<string, any>> = {
      knowledgeBases: { name: '', description: '', tag_names: '' },
      models: { model_name: '', model_type: 'localLLM', model_kind: 'OnlineLLM', model_from: 'huggingface', model_url: '', tag_names: '' },
      modelEvaluations: { task_name: '', model_name: '', evaluation_type: 'online', evaluation_method: 'manual', dataset_id: '' },
      inferenceServices: { model_type: 'localLLM', model_id: '', service_name: '', model_num_gpus: 1 },
      finetunes: {
        name: '',
        base_model_key: '',
        target_model_name: '',
        datasets: [],
        training_type: 'SFT',
        finetuning_type: 'LoRA',
        val_size: 0.01,
        num_epochs: 2,
        learning_rate: 0.00005,
        lr_scheduler_type: 'cosine',
        cutoff_len: 1024,
        batch_size: 2,
        lora_r: 8,
        lora_alpha: 8
      },
      datasets: { name: '', tag_names: '', description: '', data_type: 'doc', data_format: 'Alpaca_pre_train', upload_type: 'local', file_urls: '' }
    }
    return defaults[moduleValue]
  }

  function defaultAdvancedJson(moduleValue: ModuleKey) {
    const defaults: Record<ModuleKey, Record<string, any>> = {
      knowledgeBases: {},
      models: { model_list: [] },
      modelEvaluations: { dimensions: [{ dimension_name: '', dimension_description: '', choose_num: 3, options: [{ option_name: '', option_value: '' }] }] },
      inferenceServices: {},
      finetunes: {},
      datasets: {}
    }
    return JSON.stringify(defaults[moduleValue], null, 2)
  }

  function normalizePage(payload: any) {
    const pageContainer = extractPageContainer(payload)
    const rows = pageContainer ? ensureRows(pageContainer.data) : extractRows(payload)
    const total = extractTotal(payload, rows)
    return { records: rows, total }
  }

  function ensureRows(value: any): any[] {
    if (Array.isArray(value)) return value
    if (value && typeof value === 'object' && Array.isArray(value.value)) {
      return value.value
    }
    return []
  }

  async function loadInferenceModelOptions() {
    inferenceModelLoading.value = true
    try {
      const payload = await listDeployableModels({
        modelType: 'local',
        modelKind: formModel.model_type || 'localLLM',
        qtype: 'already'
      })
      buildInferenceModelOptions(payload)
    } finally {
      inferenceModelLoading.value = false
    }
  }

  function buildInferenceModelOptions(payload: any) {
    const meta: Record<string, any> = {}
    const options = unwrapRows(payload).map(item => {
      const label = item.model_name || item.model || item.name || item.label || item.model_brand || item.model_id || item.id
      const value = item.id ?? item.model_id ?? item.value ?? item.model_name ?? item.name
      if (value === undefined || value === null || value === '') return null
      meta[String(value)] = item
      return {
        label: String(label || value),
        value
      }
    }).filter(Boolean) as Array<{ label: string; value: string | number }>

    inferenceModelMeta.value = meta
    inferenceModelOptions.value = options
  }

  async function loadFinetuneOptions() {
    finetuneOptionLoading.value = true
    try {
      const [models, datasets] = await Promise.all([listFinetuneModels(), listFinetuneDatasets('mine')])
      buildFinetuneModelOptions(models)
      buildFinetuneDatasetOptions(datasets)
    } finally {
      finetuneOptionLoading.value = false
    }
  }

  function buildFinetuneModelOptions(payload: any) {
    const meta: Record<string, any> = {}
    const options = unwrapRows(payload).map(item => {
      const modelName = item.model || item.model_name || item.name || item.label || item.base_model_key
      const source = item.source || item.file_name || item.model_source || ''
      const displayName = item.display_name || ''
      const value = formatBaseModelKey(item.base_model_key, modelName, displayName)
      meta[String(value)] = item
      return {
        label: source ? `${modelName} / ${source}` : String(modelName),
        value
      }
    }).filter(option => option.value)
    finetuneModelMeta.value = meta
    finetuneModelOptions.value = options
  }

  function formatBaseModelKey(baseModelKey: any, modelName: any, displayName: any) {
    const key = baseModelKey ? String(baseModelKey) : ''
    if (key.includes(':')) return key
    const name = String(modelName || key)
    if (!name) return key
    return `${name}:${displayName || name}`
  }

  function buildFinetuneDatasetOptions(payload: any) {
    const meta: Record<string, any> = {}
    const options: Array<{ label: string; value: string | number }> = []
    collectDatasetOptions(unwrapRows(payload), options, meta)
    finetuneDatasetMeta.value = meta
    finetuneDatasetOptions.value = options
  }

  function collectDatasetOptions(rows: any[], options: Array<{ label: string; value: string | number }>, meta: Record<string, any>, prefix = '') {
    rows.forEach(row => {
      const label = row.label || row.name || row.data_set_name || row.val_key
      const value = row.val_key || row.value || row.id || row.data_set_version_id
      const display = prefix ? `${prefix} / ${label}` : String(label)
      if (value) {
        meta[String(value)] = row
        options.push({ label: display, value })
      }
      const children = row.child || row.children || []
      if (Array.isArray(children) && children.length > 0) {
        collectDatasetOptions(children, options, meta, display)
      }
    })
  }

  function unwrapRows(payload: any): any[] {
    return extractRows(payload)
  }

  function extractRows(payload: any): any[] {
    // axios 响应拦截器会将 Resp.data 直接返回，部分接口因此是顶层数组。
    if (Array.isArray(payload)) return payload

    const pageContainer = extractPageContainer(payload)
    if (pageContainer) return ensureRows(pageContainer.data)

    const result = payload?.result || payload?.data || payload
    if (Array.isArray(result)) return result

    const rows = result?.tasks || result?.files || result?.versions || result?.result || result?.children || payload?.files || payload?.data || result?.data || []
    return ensureRows(rows)
  }

  function extractTotal(payload: any, records: any[]) {
    const pageContainer = extractPageContainer(payload)
    const total = Number(pageContainer?.total)
    if (!Number.isNaN(total)) return total

    const result = payload?.result || payload?.data || payload
    const fallbackTotal = Number(result?.total ?? payload?.total)
    if (!Number.isNaN(fallbackTotal)) return fallbackTotal

    return records.length
  }

  function extractPageContainer(payload: any) {
    if (isCurrentPageContainer(payload)) return payload
    if (isCurrentPageContainer(payload?.data)) return payload.data
    if (isCurrentPageContainer(payload?.result?.data)) return payload.result.data
    if (isCurrentPageContainer(payload?.result)) return payload.result
    return null
  }

  function isCurrentPageContainer(value: any) {
    if (!value || typeof value !== 'object' || Array.isArray(value)) return false
    return value.total !== undefined && Array.isArray(value.data) && (value.page !== undefined || value.pageNum !== undefined || value.page_num !== undefined)
  }

  function getDatasetFormat(dataset: any) {
    const meta = finetuneDatasetMeta.value[String(dataset)] || {}
    return normalizeDatasetFormat(meta.datasets_type || meta.dataset_type || meta.data_format || meta.format)
  }

  function normalizeDatasetFormat(value: any) {
    if (!value) return 'DATASET_FORMAT_ALPACA'
    const text = String(value)
    return datasetFormatAlias[text] || text
  }

  function arrayFrom(value: any) {
    if (Array.isArray(value)) return value
    return value ? [value] : []
  }

  function extractUploadedPaths(payload: any) {
    const result = payload?.result || payload?.data || payload
    const filePath = result?.file_path || result?.path
    if (filePath) return [filePath]
    const files = result?.files || payload?.files || []
    return Array.isArray(files) ? files.map((item: any) => item.file_path || item.path).filter(Boolean) : []
  }

  function extractUploadedFileIds(payload: any) {
    const result = payload?.result || payload?.data || payload
    const files = result?.files || payload?.files || []
    return Array.isArray(files) ? files.map((item: any) => item.id || item.file_id).filter(Boolean) : []
  }

  function parseAdvancedJson() {
    const text = advancedJson.value.trim()
    if (!text) return {}
    try {
      return JSON.parse(text)
    } catch {
      throw new Error('高级参数 JSON 格式不正确')
    }
  }

  function stripEmpty(value: Record<string, any>) {
    const result: Record<string, any> = {}
    Object.keys(value).forEach(key => {
      if (value[key] !== '' && value[key] !== undefined && value[key] !== null) result[key] = value[key]
    })
    return result
  }

  function normalizeArrayField(payload: Record<string, any>, key: string) {
    if (typeof payload[key] === 'string') {
      payload[key] = payload[key].split(',').map((item: string) => item.trim()).filter(Boolean)
    }
  }

  function parseJsonField(payload: Record<string, any>, key: string) {
    if (typeof payload[key] === 'string' && payload[key].trim()) {
      payload[key] = JSON.parse(payload[key])
    }
  }

  function arrayToText(value: any) {
    return Array.isArray(value) ? value.join(',') : value || ''
  }

  function requiredRule(field: FieldConfig) {
    return {
      required: true,
      trigger: ['blur', 'change'],
      validator: (_rule: unknown, value: unknown) => {
        if (Array.isArray(value)) {
          return value.length > 0
        }
        return value !== '' && value !== undefined && value !== null
      },
      message: `${field.label}不能为空`
    }
  }

  function renderTags(row: any) {
    const tags = row.tags || row.label || []
    if (!Array.isArray(tags) || tags.length === 0) return '-'
    return h(NSpace, { size: 6 }, { default: () => tags.map((tag: string) => h(NTag, { size: 'small' }, { default: () => tag })) })
  }

  function renderStatus(row: any) {
    const color = row.status === 'Completed' ? 'success' : row.status === 'Failed' ? 'error' : 'warning'
    return h(NTag, { type: color, size: 'small' }, { default: () => row.status_label || row.status || '-' })
  }

  function renderDatasetVersionStatus(row: any) {
    const status = Number(row.status)
    const color = status === 2 ? 'success' : status === 3 ? 'error' : 'warning'
    const label = status === 1 ? '处理中' : status === 2 ? '已完成' : status === 3 ? '处理失败' : row.status || '-'
    return h(NTag, { type: color, size: 'small' }, { default: () => label })
  }

  function renderServices(row: any) {
    const services = Array.isArray(row.services) ? row.services : []
    if (services.length === 0) return '暂无服务'
    return h(NSpace, { vertical: true, size: 6 }, {
      default: () => services.map((service: any) => h('div', { class: 'service-line' }, [
        h('span', `${service.name || service.gid} (${service.status})`),
        actionGroup([
          actionButton('启动', () => operateService(service.id, 'start'), false, service.status !== 'Cancelled'),
          actionButton('停止', () => operateService(service.id, 'stop'), false, service.status === 'Cancelled'),
          actionButton('删除', () => removeInferenceService(service.id), true)
        ])
      ]))
    })
  }

  function renderFinetuneActions(row: any) {
    return actionGroup([
      actionButton('详情', () => openFinetuneDetail(row)),
      actionButton('复制新增', () => openEdit(row)),
      actionButton('暂停', () => operateFinetune(row.id, 'pause'), false, row.status !== 'InProgress'),
      actionButton('恢复', () => operateFinetune(row.id, 'resume'), false, row.status !== 'Suspended'),
      actionButton('取消', () => operateFinetune(row.id, 'cancel'), true, ['Completed', 'Failed', 'Cancelled'].includes(row.status)),
      actionButton('删除', () => removeRow(row), true)
    ])
  }

  function actionGroup(children: any[]) {
    return h(NSpace, { size: 6 }, { default: () => children })
  }

  function actionButton(label: string, onClick: () => void, danger = false, disabled = false) {
    return h(NButton, { text: true, size: 'small', type: danger ? 'error' : 'primary', disabled, onClick }, { default: () => label })
  }

  async function openFinetuneDetail(row: any) {
    const id = row.id
    if (!id) return
    router.push(`/model-management/finetune/detail/${id}`)
  }

  async function operateFinetune(id: number | string, action: 'pause' | 'resume' | 'cancel') {
    if (action === 'pause') await pauseFinetune(id)
    if (action === 'resume') await resumeFinetune(id)
    if (action === 'cancel') await cancelFinetune(id)
    window.$message?.success('操作成功')
    loadData()
  }

  async function operateService(id: number | string, action: 'start' | 'stop') {
    if (action === 'start') await startInferenceService(id)
    if (action === 'stop') await stopInferenceService(id)
    window.$message?.success('操作成功')
    loadData()
  }

  async function operateGroup(id: number | string, action: 'start' | 'close') {
    if (!id) return
    if (action === 'start') await startInferenceGroup(id)
    if (action === 'close') await closeInferenceGroup(id)
    window.$message?.success('操作成功')
    loadData()
  }

  function removeInferenceService(id: number | string) {
    confirm('删除推理服务', async () => {
      await deleteInferenceService(id)
      window.$message?.success('删除成功')
      loadData()
    })
  }

  function openKnowledgeFiles(row: any) {
    selectedKnowledgeBase.value = row
    knowledgeFilePagination.page = 1
    knowledgeFilesVisible.value = true
    loadKnowledgeFiles()
  }

  async function loadKnowledgeFiles() {
    if (!selectedKnowledgeBase.value?.id) return
    knowledgeFileLoading.value = true
    try {
      const data = await listKnowledgeBaseFiles(selectedKnowledgeBase.value.id, {
        pageNum: knowledgeFilePagination.page,
        pageSize: knowledgeFilePagination.pageSize
      })
      const normalized = normalizePage(data)
      knowledgeFileRecords.value = ensureRows(normalized.records)
      knowledgeFilePagination.itemCount = normalized.total
    } finally {
      knowledgeFileLoading.value = false
    }
  }

  function updateKnowledgeFilePage(page: number) {
    knowledgeFilePagination.page = page
    loadKnowledgeFiles()
  }

  async function handleKnowledgeFileUpload(options: any) {
    try {
      const payload = await uploadKnowledgeBaseFile(options.file.file as File)
      const fileIds = extractUploadedFileIds(payload)
      if (fileIds.length > 0 && selectedKnowledgeBase.value?.id) {
        await addKnowledgeBaseFiles(selectedKnowledgeBase.value.id, fileIds)
      }
      options.onFinish()
      window.$message?.success('上传成功')
      loadKnowledgeFiles()
    } catch {
      options.onError()
    }
  }

  function removeKnowledgeFile(row: any) {
    confirm('删除知识库文件', async () => {
      await deleteKnowledgeBaseFile({
        id: row.id || row.file_id,
        file_id: row.file_id || row.id,
        knowledge_base_id: selectedKnowledgeBase.value?.id
      })
      window.$message?.success('删除成功')
      loadKnowledgeFiles()
    })
  }

  async function handleDatasetUpload(options: any) {
    try {
      const payload = await uploadDatasetFile(options.file.file as File, formModel.data_type || 'doc')
      uploadedDatasetPaths.value.push(...extractUploadedPaths(payload))
      options.onFinish()
      window.$message?.success('上传成功')
    } catch {
      options.onError()
    }
  }

  async function handleEvaluationDatasetUpload(options: any) {
    try {
      const payload = await uploadEvaluationDataset([options.file.file as File])
      const result = payload?.result || payload?.data || payload
      const datasetId = result?.dataset_id || result?.datasetId || result?.result?.dataset_id
      if (datasetId) {
        const current = arrayFrom(formModel.dataset_id)
        formModel.dataset_id = [...current, datasetId].join(',')
      }
      options.onFinish()
      window.$message?.success('上传成功')
    } catch {
      options.onError()
    }
  }

  function openDatasetVersions(row: any) {
    selectedDataset.value = row
    datasetVersionPagination.page = 1
    datasetVersionsVisible.value = true
    loadDatasetVersions()
  }

  async function loadDatasetVersions() {
    if (!selectedDataset.value?.id) return
    datasetVersionLoading.value = true
    try {
      const data = await listDatasetVersions(selectedDataset.value.id, {
        pageNum: datasetVersionPagination.page,
        pageSize: datasetVersionPagination.pageSize,
        versionType: datasetVersionType.value
      })
      const normalized = normalizePage(data)
      datasetVersionRecords.value = ensureRows(normalized.records)
      datasetVersionPagination.itemCount = normalized.total
    } finally {
      datasetVersionLoading.value = false
    }
  }

  function updateDatasetVersionPage(page: number) {
    datasetVersionPagination.page = page
    loadDatasetVersions()
  }

  async function publishVersion(row: any) {
    await publishDatasetVersion(row.id)
    window.$message?.success('发布成功')
    loadDatasetVersions()
  }

  function removeDatasetVersion(row: any) {
    confirm('删除数据集版本', async () => {
      await deleteDatasetVersion({ data_set_version_id: row.id })
      window.$message?.success('删除成功')
      loadDatasetVersions()
    })
  }

  function confirm(title: string, action: () => Promise<void>) {
    dialog.warning({
      title,
      content: '该操作会同步调用 LazyCraft 后端接口。',
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: action
    })
  }

  return {
    moduleKey,
    config,
    formRef,
    loading,
    saving,
    keyword,
    qtype,
    records,
    drawerVisible,
    drawerMode,
    formModel,
    advancedJson,
    uploadedDatasetPaths,
    knowledgeFilesVisible,
    knowledgeFileRecords,
    knowledgeFileLoading,
    datasetVersionsVisible,
    datasetVersionRecords,
    datasetVersionLoading,
    datasetVersionType,
    pagination,
    knowledgeFilePagination,
    datasetVersionPagination,
    evaluationScopes,
    datasetVersionTypeOptions,
    formFields,
    editingSupported,
    drawerTitle,
    columns,
    summaryCards,
    knowledgeFileColumns,
    datasetVersionColumns,
    loadData,
    reload,
    reset,
    updatePage,
    updatePageSize,
    openCreate,
    submitForm,
    requiredRule,
    handleDatasetUpload,
    handleEvaluationDatasetUpload,
    handleKnowledgeFileUpload,
    updateKnowledgeFilePage,
    loadDatasetVersions,
    updateDatasetVersionPage
  }
}
