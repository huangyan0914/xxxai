import { NButton, useMessage, NSelect } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { PaginationInfo } from 'naive-ui/lib'

export type RowItem = {
  id: number
  tagName: string
  tagType: string
  tagTypeName: string
  description: string
  usageCount: number
  createTime: string
  updateTime: string
}
// 标签列表
export const createColumns = ({
  pagination,
  edit,
  deleteRow,
  detail
}: {
  pagination: PaginationInfo
  edit: (row: RowItem) => void
  deleteRow: (row: RowItem) => void
  detail: (row: RowItem) => void
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
      title: '标签名称',
      key: 'tagName'
    },
    {
      align: 'center',
      title: '类别名称',
      key: 'tagTypeName'
    },
    {
      align: 'center',
      title: '标签说明',
      key: 'description'
    },
    {
      align: 'center',
      title: '被资源使用次数',
      key: 'usageCount'
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
            )
          ]
        )
      }
    }
  ]
}

export type TagItem = {
  id: number
  name: string
}
// 按需打标
export const createTaggingColumns = ({
  pagination,
  newTag,
  onChangeTag,
  tagList
}: {
  pagination: PaginationInfo
  newTag: (row: any) => void
  onChangeTag?: (row: any, tags: any[]) => void
  tagList: any
}): DataTableColumns<any> => {
  return [
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
      title: '教培名称',
      key: 'name'
    },
    {
      align: 'center',
      title: '选择已有标签',
      key: 'tags',
      render: row => {
        return h(NSelect, {
          labelField: 'tagName',
          valueField: 'id',
          options: tagList.value,
          class: 'w-full',
          multiple: true,
          placeholder: '请选择标签',
          value: row.tags,
          'onUpdate:value': value => {
            const added = value.filter((v: number) => !row.tags.includes(v))
            const removed = row.tags.filter((v: number) => !value.includes(v))
            row.tags = value
            if (onChangeTag) {
              onChangeTag(row, added, removed)
            }
          }
        })
      }
    },
    {
      align: 'center',
      title: '操作',
      key: 'actions',
      width: '180px',
      render(row, index) {
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
                onClick: () => newTag(row, index)
              },
              { default: () => '新建标签' }
            )
          ]
        )
      }
    }
  ]
}
