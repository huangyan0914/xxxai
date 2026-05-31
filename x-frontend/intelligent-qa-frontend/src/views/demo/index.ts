import { NButton, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { PaginationInfo } from 'naive-ui/lib'

export type Song = {
  no: number
  title: string
  length: string
}
export const createColumns = ({
  pagination,
  play
}: {
  pagination: PaginationInfo
  play: (row: Song) => void
}): DataTableColumns<Song> => {
  return [
    {
      align: 'center',
      title: 'No',
      key: 'no',
      width: 80,
      render: (_, index) => {
        return `${(pagination.page - 1) * pagination.pageSize + index + 1}`
      }
    },
    {
      align: 'center',
      title: 'Title',
      key: 'title'
    },
    {
      align: 'center',
      title: 'Length',
      key: 'length'
    },
    {
      align: 'center',
      title: '操作',
      key: 'actions',
      render(row) {
        return h(
          NButton,
          {
            size: 'small',
            text: true,
            type: 'primary',
            onClick: () => play(row)
          },
          { default: () => 'Play' }
        )
      }
    }
  ]
}
