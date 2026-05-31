import type { DataTableColumns } from 'naive-ui'

export type DatasetRow = Record<string, any>

export const createColumns = (): DataTableColumns<DatasetRow> => []
