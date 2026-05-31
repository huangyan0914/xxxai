import type { DataTableColumns } from 'naive-ui'

export type ModelRow = Record<string, any>

export const createColumns = (): DataTableColumns<ModelRow> => []
