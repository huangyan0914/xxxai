import type { DataTableColumns } from 'naive-ui'

export type InferenceRow = Record<string, any>

export const createColumns = (): DataTableColumns<InferenceRow> => []
