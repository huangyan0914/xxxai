import { NButton, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { PaginationInfo } from 'naive-ui/lib'

export type RowItem = {
  id: number
  title: string
  category: string
  categoryName: string
  description: string
  status: string
  statusName: string
  learnedUserCount: number
  createTime: string
  updateTime: string
  tag: string
}
export const createColumns = ({
  pagination,
  publish,
  soldOut,
  edit,
  deleteRow,
  detail,
  getDictLabel
}: {
  pagination: PaginationInfo
  publish: (row: RowItem) => void
  soldOut: (row: RowItem) => void
  edit: (row: RowItem) => void
  deleteRow: (row: RowItem) => void
  detail: (row: RowItem) => void
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
      title: '标题',
      key: 'title'
    },
    {
      align: 'center',
      title: '资源类型',
      key: 'categoryName',
      render: row => {
        return (
          row.categoryName ||
          (row.category ? getDictLabel('resource_category', row.category) : '')
        )
      }
    },
    {
      align: 'center',
      title: '简介',
      key: 'description'
    },
    {
      align: 'center',
      title: '已学习人数',
      key: 'learnedUserCount'
    },
    {
      align: 'center',
      title: '创建时间',
      key: 'createTime'
    },
    {
      align: 'center',
      title: '发布状态',
      key: 'statusName',
      render: (row, index) => {
        return row.statusName || (row.status == 'published' ? '已发布' : '草稿')
      }
    },
    {
      align: 'center',
      title: '操作',
      key: 'actions',
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
                class: 'mx-10px ',
                style: row.status == 'published' ? 'display: none;' : '',
                onClick: () => publish(row)
              },
              { default: () => '发布' }
            ),
            h(
              NButton,
              {
                size: 'small',
                text: true,
                type: 'primary',
                class: 'mx-10px',
                style: row.status == 'published' ? '' : 'display: none;',
                onClick: () => soldOut(row)
              },
              { default: () => '下架' }
            ),
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
                onClick: () => detail(row)
              },
              { default: () => '详情' }
            )
          ]
        )
      }
    }
  ]
}
