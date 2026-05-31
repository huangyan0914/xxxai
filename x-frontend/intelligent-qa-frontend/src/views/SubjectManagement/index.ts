import { NButton, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { PaginationInfo } from 'naive-ui/lib'

export type RowItem = {
  id: number
  name: string
  desc: string
  systemCode: string
  system: string[]
}
export const createColumns = ({
  pagination,
  edit,
  deleteRow,
  detail,
  setting,
  getDictLabel
}: {
  pagination: PaginationInfo
  edit: (row: RowItem) => void
  deleteRow: (row: RowItem) => void
  detail: (row: RowItem) => void
  setting: (row: RowItem) => void
  /** 字典 label 转换函数，来自 useDict */
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
      width: 80,
      render: (_, index) => {
        return `${(pagination.page - 1) * pagination.pageSize + index + 1}`
      }
    },
    {
      align: 'center',
      title: '科目名称',
      key: 'name'
    },
    {
      align: 'center',
      title: '科目介绍',
      key: 'desc'
    },
    {
      align: 'center',
      title: '所属分系统',
      key: 'system',
      render: row => {
        // 将每个 systemCode 转为字典名称后用逗号拼接
        return row.system
          .map(code => getDictLabel('system_code', code))
          .join('，')
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
                class: 'mx-10px',
                onClick: () => edit(row)
              },
              { default: () => '编辑' }
            ),
            ,
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
                onClick: () => setting(row)
              },
              { default: () => '配置项目' }
            )
          ]
        )
      }
    }
  ]
}
