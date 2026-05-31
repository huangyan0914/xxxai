import { NButton, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { PaginationInfo } from 'naive-ui/lib'

export type RowItem = {
  id: number
  catalogId: number
  catalogTitle: string
  fileName: string
  fileType: string
  fileTypeName: string
  fileSize: number
  boundProjectCount: number
  learnedUserCount: number
  createTime: string
  updateTime: string
}
export const createColumns = ({
  pagination,
  edit,
  deleteRow,
  detail,
  uploadFiles,
  getDictLabel
}: {
  pagination: PaginationInfo
  edit: (row: RowItem) => void
  deleteRow: (row: RowItem) => void
  detail: (row: RowItem) => void
  uploadFiles: (row: RowItem) => void
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
      title: '所属资源编目',
      key: 'catalogTitle'
    },
    {
      align: 'center',
      title: '文件名',
      key: 'fileName'
    },
    {
      align: 'center',
      title: '文件类型',
      key: 'fileType',
      render: row => {
        if (!row.fileType) return ''
        return getDictLabel('file_type', row.fileType)
      }
    },
    {
      align: 'center',
      title: '文件大小(Byte)',
      key: 'fileSize'
    },
    {
      align: 'center',
      title: '被项目绑定数',
      key: 'boundProjectCount'
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
      title: '更新时间',
      key: 'updateTime'
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
            ),
            h(
              NButton,
              {
                size: 'small',
                text: true,
                type: 'primary',
                class: 'mx-10px',
                onClick: () => uploadFiles(row)
              },
              { default: () => '附件上传' }
            )
          ]
        )
      }
    }
  ]
}
