<template>
  <div class="w-full h-full">
    <!-- 标签管理 -->
    <div
      class="wh-full border border-[#bebebe] shadow-md shadow-[#bebebe)] flex flex-row"
      v-if="showTagList"
    >
      <div
        class="w-220px h-full border border-[#bebebe] shadow-md shadow-[#bebebe)] p-20px"
      >
        <div class="h-full flex flex-col">
          <div class="mb-15px">
            <span class="leading-34px font-bold text-16px">标签分类</span>
          </div>
          <n-scrollbar class="flex-1">
            <div
              class="mb-5px p-5px flex flex-row items-center border border-[#bebebe] cursor-pointer"
              :class="!searchForm.tagType ? 'bg-[#c7c3c3]' : ''"
              @click="queryByTagType(null)"
            >
              <span class="flex-1 pr-10px">全部</span>
            </div>
            <div
              v-for="(item, index) in tagTypeOption"
              :key="index"
              class="mb-5px p-5px flex flex-row items-center border border-[#bebebe] cursor-pointer"
              :class="searchForm.tagType == item.value ? 'bg-[#c7c3c3]' : ''"
              @click="queryByTagType(item)"
            >
              <span class="flex-1 pr-10px">{{ item.label }}</span>
            </div>
          </n-scrollbar>
        </div>
      </div>
      <div
        class="wh-full border border-[#bebebe] shadow-md shadow-[#bebebe)] flex flex-col"
      >
        <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
          <span class="leading-40px ml-8px">标签管理</span>
        </div>
        <div class="flex-1 flex flex-col p-24px">
          <n-form
            ref="formRef"
            inline
            :label-width="'auto'"
            label-placement="left"
            :model="searchForm"
            class="flex justify-start"
          >
            <n-form-item label="标签分类">
              <n-select
                class="w-200px"
                v-model:value="searchForm.tagType"
                placeholder="标签分类"
                :options="tagTypeOption"
                :label-field="'label'"
                :value-field="'value'"
                clearable
              ></n-select>
            </n-form-item>
            <n-form-item label="模糊查询">
              <n-input
                v-model:value="searchForm.name"
                placeholder="模糊查询"
                clearable
              />
            </n-form-item>

            <n-form-item class="inline-block">
              <n-button attr-type="button" @click="loadData()">
                <template #icon>
                  <svg-icon name="query"></svg-icon>
                </template>
                查询
              </n-button>
              <n-button
                attr-type="button"
                class="ml-10px"
                @click="resetQuery()"
              >
                <template #icon>
                  <svg-icon name="reset"></svg-icon>
                </template>
                重置
              </n-button>
            </n-form-item>
          </n-form>
          <div class="mb-24px">
            <n-button attr-type="button" class="ml-10px" @click="addTag">
              <template #icon>
                <svg-icon name="add"></svg-icon>
              </template>
              新建
            </n-button>
            <n-button
              attr-type="button"
              class="ml-10px"
              @click="
                () => {
                  showTagList = !showTagList
                  taggingPagination.page = 1
                  taggingLoadData()
                }
              "
            >
              <template #icon>
                <svg-icon name="tagging"></svg-icon>
              </template>
              按需打标
            </n-button>
            <n-button
              attr-type="button"
              class="ml-10px"
              type="error"
              secondary
              @click="deleteCheckedRows"
            >
              <template #icon>
                <svg-icon name="delete"></svg-icon>
              </template>
              删除
            </n-button>
          </div>
          <n-data-table
            :columns="columns"
            :data="list"
            :pagination="pagination"
            :bordered="true"
            :loading="loading"
            :single-line="false"
            :single-column="false"
            :row-key="rowKey"
            class="flex-1 border-[#bebebe]"
            flex-height
            @update:checked-row-keys="handleCheck"
          />
        </div>
      </div>
    </div>
    <!-- 按需打标 -->
    <div
      class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)]"
      v-else
    >
      <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5] relative">
        <span class="leading-40px ml-8px">按需打标</span>
        <span
          class="absolute right-20px cursor-pointer leading-40px"
          @click="showTagList = !showTagList"
          >{{ `<<  ` }} 返回</span
        >
      </div>
      <n-data-table
        :columns="taggingColumns"
        :data="taggingList"
        :pagination="taggingPagination"
        :row-key="rowKey"
        :loading="taggingLoading"
        :single-line="false"
        :single-column="false"
        flex-height
        class="flex-1 mx-24px mt-24px"
        :bordered="false"
      />
      <div class="flex items-center justify-center pb-16px">
        <n-button
          attr-type="button"
          class="ml-10px"
          @click="showTagList = !showTagList"
        >
          <template #icon>
            <svg-icon name="cancle"></svg-icon>
          </template>
          取消
        </n-button>
        <n-button attr-type="button" class="ml-10px" @click="saveTagging()">
          <template #icon>
            <svg-icon name="save"></svg-icon>
          </template>
          保存
        </n-button>
      </div>
    </div>

    <LayoutDialog
      class="w-1/2"
      v-model:modelValue="modelOption.isShow"
      :footer="modelOption.footer"
      :title="modelOption.title"
      @submit="saveTagForm"
    >
      <n-form
        ref="tagFormRef"
        label-placement="left"
        :label-width="120"
        :model="tagForm"
        :rules="rules"
        :disabled="modelOption.title == '详情' ? true : false"
      >
        <n-form-item label="标签名称" path="tagName">
          <n-input
            v-model:value="tagForm.tagName"
            placeholder="标签名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="标签分类" path="tagType">
          <n-select
            v-model:value="tagForm.tagType"
            placeholder="标签分类"
            :options="tagTypeOption"
            :label-field="'label'"
            :value-field="'value'"
            clearable
          ></n-select>
        </n-form-item>
        <n-form-item label="标签说明" path="description">
          <n-input
            v-model:value="tagForm.description"
            placeholder="标签说明"
            clearable
          />
        </n-form-item>
      </n-form>
    </LayoutDialog>

    <LayoutConfirm
      v-model="showDel"
      title="删除提示"
      content="此操作将永久删除选中行, 是否继续?"
      @submit="submitDel"
    />

    <LayoutDialog
      class="w-1/3"
      v-model:modelValue="taggingModelOption.isShow"
      :footer="taggingModelOption.footer"
      :title="taggingModelOption.title"
      @submit="saveTaggingForm"
    >
      <n-form label-placement="left" :label-width="120" :model="taggingForm">
        <n-form-item label="标签名称" path="tagName">
          <n-input
            v-model:value="taggingForm.tagName"
            placeholder="标签名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="标签分类" path="tagType">
          <n-select
            v-model:value="taggingForm.tagType"
            placeholder="标签分类"
            :options="tagTypeOption"
            :label-field="'label'"
            :value-field="'value'"
            clearable
          ></n-select>
        </n-form-item>
      </n-form>
    </LayoutDialog>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import usePage from '@/hooks/basic-page/index'
import {
  createColumns,
  RowItem,
  createTaggingColumns,
  TagItem
} from './index.js'
import { sys } from 'typescript'
import { DataTableRowKey } from 'naive-ui/lib'
import LayoutDialog from '@/components/common/LayoutDialog.vue'
import { cloneDeep } from 'lodash-es'

import {
  getTagPage,
  addTag as addTagApi,
  updateTag,
  deleteTag,
  deleteBatchTag
} from '@/api/tagManagement'
import { getResourceManagementPage } from '@/api/resourceManagement'
import { applyTagToCatalog } from '@/api/resourceCatalog'

const { tag_type: tagTypeOption } = useDict('tag_type')

const showTagList = ref(true)
// 查询表单
type SearchForm = {
  name: string
  tagType: string | null
}
const searchForm = ref<SearchForm>({
  name: '',
  tagType: null
})
// 重置查询
function resetQuery() {
  searchForm.value = {
    name: '',
    tagType: null
  }
  pagination.page = 1
  loadData()
  activeTagType.value = {}
}

// 标签列表（不分页，全量数据用于下拉框）
const tagListOption = ref<any[]>([])

// 表格数据
async function fetchTagList(params: any) {
  const res: any = await getTagPage({
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    tagName: params.name || undefined,
    tagType: params.tagType || undefined
  })
  const data = res.data || res
  return {
    list: (data?.records || []).map((item: any) => ({
      id: item.id,
      tagName: item.tagName,
      tagType: item.tagType,
      tagTypeName: item.tagTypeName,
      description: item.description,
      usageCount: item.usageCount,
      createTime: item.createTime,
      updateTime: item.updateTime
    })),
    page: data?.current || 1,
    size: data?.size || 10,
    total: data?.total || 0
  }
}

// rowkey  表格唯一标识
const rowKey = (row: RowItem) => row.id
// 选中表格数据行
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
// 表格选中行标识记录
function handleCheck(rowKeys: DataTableRowKey[]) {
  checkedRowKeysRef.value = rowKeys
}
// 初始化标签表格
const { loading, list, reset, loadData, pagination } = usePage(fetchTagList, {
  filterOption: searchForm
})
// 删除确认
const showDel = ref(false)
// 删除一行/多行
const delType = ref('')

// 创建表头
const columns = createColumns({
  pagination,
  edit(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '编辑标签'
    tagForm.value = cloneDeep(row)
  },
  deleteRow(row: RowItem) {
    delType.value = 'one'
    tagForm.value = cloneDeep(row)
    showDel.value = true
  },
  detail(row: RowItem) {
    // window?.$message?.info('详情')
    modelOption.isShow = true
    modelOption.title = '详情'
    tagForm.value = row
    tagForm.footer = false
  }
})

// 标签新建编辑弹窗配置
const modelOption = reactive({
  isShow: false,
  footer: true,
  title: ''
})
// 标签表单
const tagForm = ref<any>({
  tagName: '',
  tagType: null,
  description: ''
})
const tagFormRef = ref()

// 标签表单规则
const rules = ref({
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  tagType: [
    { required: true, message: '请选择标签分类', trigger: ['blur', 'change'] }
  ]
})

// 标签新建/编辑保存保存
async function saveTagForm() {
  tagFormRef.value?.validate(async (errors: any) => {
    if (!errors) {
      const payload = {
        tagName: tagForm.value.tagName,
        tagType: tagForm.value.tagType,
        description: tagForm.value.description
      }
      if (modelOption.title == '新建标签') {
        await addTagApi(payload)
      } else if (modelOption.title == '编辑标签') {
        await updateTag(tagForm.value.id, payload)
      }
      modelOption.isShow = false
      loadData()
    } else {
      console.log(errors)
      window?.$message?.error('校验失败')
    }
  })
}
// 打开新建标签弹框
function addTag() {
  modelOption.isShow = true
  modelOption.title = '新建标签'
  tagForm.value = {
    tagName: '',
    tagType: null,
    description: ''
  }
}
// 批量删除标签
function deleteCheckedRows() {
  if (checkedRowKeysRef.value.length == 0) {
    window?.$message?.info('请选择要删除的数据行')
    return
  }
  // 打开删除确认弹框
  showDel.value = true
  delType.value = 'many'
}
// 删除选中数据行
async function submitDel() {
  if (delType.value == 'many') {
    await deleteBatchTag(checkedRowKeysRef.value as number[])
    checkedRowKeysRef.value = []
  } else if (delType.value == 'one' && tagForm.value?.id) {
    await deleteTag(tagForm.value.id)
  }

  showDel.value = false
  loadData()
}

// 获取打标列表数据
async function fetchTaggingList(params: any) {
  const res: any = await getResourceManagementPage({
    pageNum: params.pageNum,
    pageSize: params.pageSize
  })
  // 并行拉取全量用于下拉：
  try {
    const tagsRes: any = await getTagPage({
      pageNum: params.pageNum,
      pageSize: params.pageSize
    })
    tagListOption.value = (
      tagsRes?.data?.records ||
      tagsRes?.records ||
      []
    ).map((t: any) => ({
      tagName: t.tagName,
      id: t.id
    }))
  } catch (e) {}

  const data = res.data || res
  return {
    list: (data?.records || []).map((item: any) => ({
      id: item.id,
      name: item.fileName,
      tags: item.tags || []
    })),
    page: data?.current || 1,
    size: data?.size || 10,
    total: data?.total || 0
  }
}

const {
  loading: taggingLoading,
  list: taggingList,
  reset: resetTagging,
  loadData: taggingLoadData,
  pagination: taggingPagination
} = usePage(fetchTaggingList, {
  filterOption: ref({})
})
const taggingModelOption = reactive({
  isShow: false,
  footer: true,
  title: '新建标签'
})
// 按需打标中的新建标签弹框引用的表单数据
const taggingForm = ref({
  tagName: '',
  tagType: null
})

// 按需打标保存
const saveTaggingForm = async () => {
  // 假设标签可以这样快速创建，根据实际调整
  try {
    const payload = {
      tagName: taggingForm.value.tagName,
      tagType: taggingForm.value.tagType
    }
    await addTagApi(payload)
    taggingModelOption.isShow = false
    // 刷新下拉列表数据和表格数据
    taggingLoadData()
  } catch (e) {}
}

// 创建按需打标表头
const taggingColumns = createTaggingColumns({
  pagination: taggingPagination,
  newTag(row: any, index: number) {
    // 新建标签
    console.log(row, index)
    taggingModelOption.isShow = true
    taggingForm.value = { tagName: '', tagType: null }
  },
  onChangeTag: async (row: any, added: number[], removed: number[]) => {
    // 调用后台保存关联关系
    try {
      const promises = []
      for (const tagId of added) {
        promises.push(
          applyTagToCatalog(tagId, {
            targetType: 'item',
            targetIds: [row.id],
            remove: false
          })
        )
      }
      for (const tagId of removed) {
        promises.push(
          applyTagToCatalog(tagId, {
            targetType: 'item',
            targetIds: [row.id],
            remove: true
          })
        )
      }
      if (promises.length > 0) {
        await Promise.all(promises)
        window?.$message?.success('打标同步成功')
      }
    } catch (e) {}
  },
  tagList: tagListOption
})

// 切换标签分类
function handleUpdateValue(value: string, option: SelectOption) {
  // window?.$message.info(`value: ${JSON.stringify(value)}`)
  // window?.$message.info(`option: ${JSON.stringify(option)}`)
  activeTagType.value = option
}

function saveTagging() {
  window?.$message?.success('保存完毕退出打标模式')
  showTagList.value = true
}
</script>
<style lang="less" scoped>
.title {
  position: relative;
}
.title::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: #007eff;
  display: inline-block;
  top: 12px;
  position: absolute;
}
</style>
