import { NButton, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { PaginationInfo } from 'naive-ui/lib'

export type RowItem = {
  id: number
  name: string
  code: string
  desc: string
  type: string
  category: string
  level: string
  startDate: string
  endDate: string
  boundResourceCount: number
}
export const createColumns = ({
  pagination,
  edit,
  deleteRow,
  setting,
  unbind,
  getDictLabel
}: {
  pagination: PaginationInfo
  edit: (row: RowItem) => void
  deleteRow: (row: RowItem) => void
  setting: (row: RowItem) => void
  unbind: (row: RowItem) => void
  getDictLabel: (dictType: string, value: string) => string
}): DataTableColumns<RowItem> => {
  return [
    {
      type: 'selection'
    },
    {
      align: 'center',
      title: '序号',
      key: 'no',
      width: 60,
      render: (_, index) => {
        return `${(pagination.page - 1) * pagination.pageSize + index + 1}`
      }
    },
    {
      align: 'center',
      title: '项目名称',
      key: 'name'
    },
    {
      align: 'center',
      title: '项目编码',
      key: 'code'
    },
    {
      align: 'center',
      title: '项目介绍',
      key: 'desc'
    },
    {
      align: 'center',
      title: '类型', // projectType -> 项目类型
      key: 'type',
      render: row => {
        if (!row.type) return ''
        return getDictLabel('project_type', row.type)
      }
    },
    {
      align: 'center',
      title: '类别',
      key: 'category'
    },
    {
      align: 'center',
      title: '难度',
      key: 'level'
    },
    {
      align: 'center',
      title: '已绑资源数',
      key: 'boundResourceCount'
    },
    {
      align: 'center',
      title: '操作',
      key: 'actions',
      width: 300,
      render(row) {
        return h(
          'div',
          {
            class: 'flex justify-center '
          },
          [
            h(
              NButton,
              {
                size: 'small',
                text: true,
                type: 'primary',
                class: 'mx-10px',
                onClick: () => edit(row)
              },
              { default: () => '编辑' }
            ),
            h(
              NButton,
              {
                size: 'small',
                text: true,
                type: 'primary',
                class: 'mx-10px',
                onClick: () => deleteRow(row)
              },
              { default: () => '删除' }
            ),
            h(
              NButton,
              {
                size: 'small',
                text: true,
                type: 'primary',
                class: 'mx-10px',
                onClick: () => setting(row)
              },
              { default: () => '绑定教培资源' }
            ),
            h(
              NButton,
              {
                size: 'small',
                text: true,
                type: 'primary',
                class: 'mx-10px',
                onClick: () => unbind(row)
              },
              { default: () => '解绑教培资源' }
            )
          ]
        )
      }
    }
  ]
}
