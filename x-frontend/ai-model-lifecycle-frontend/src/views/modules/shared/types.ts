export type ModuleKey =
  | 'knowledgeBases'
  | 'models'
  | 'modelEvaluations'
  | 'inferenceServices'
  | 'finetunes'
  | 'datasets'

export type FieldConfig = {
  key: string
  label: string
  type?: 'input' | 'textarea' | 'select' | 'number'
  required?: boolean
  placeholder?: string
  span?: number
  rows?: number
  min?: number
  max?: number
  step?: number
  precision?: number
  multiple?: boolean
  loading?: boolean
  filterable?: boolean
  options?: Array<{ label: string; value: string | number }>
}
