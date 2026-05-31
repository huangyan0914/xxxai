import type { DataTableColumns } from 'naive-ui'

export type FinetuneRow = Record<string, any>

export const createColumns = (): DataTableColumns<FinetuneRow> => []
