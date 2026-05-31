import type { DataTableColumns } from 'naive-ui'

export type EvaluationRow = Record<string, any>

export const createColumns = (): DataTableColumns<EvaluationRow> => []
