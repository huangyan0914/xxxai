<template>
  <div
    class="wh-full border border-[#bebebe] shadow-md shadow-[#bebebe)] flex flex-col"
  >
    <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
      <span class="leading-40px ml-8px">资源编目</span>
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
        <n-form-item label="名称">
          <n-select
            class="w-200px"
            v-model:value="searchForm.name"
            placeholder="名称"
            :options="nameOptions"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
          ></n-select>
        </n-form-item>
        <n-form-item label="标签">
          <n-select
            class="w-200px"
            v-model:value="searchForm.tag"
            placeholder="标签"
            :options="tagOptions"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
          ></n-select>
        </n-form-item>
        <n-form-item label="上传人">
          <n-select
            class="w-200px"
            v-model:value="searchForm.uploader"
            placeholder="上传人"
            :options="uploaderOptions"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
          ></n-select>
        </n-form-item>
        <n-form-item label="模糊查询">
          <n-input
            v-model:value="searchForm.fuzzyQuery"
            placeholder="模糊查询"
            clearable
          />
        </n-form-item>

        <n-form-item class="inline-block">
          <n-button attr-type="button" @click="loadData(1)">
            <template #icon>
              <svg-icon name="query"></svg-icon>
            </template>
            查询
          </n-button>
          <n-button attr-type="button" class="ml-10px" @click="resetQuery()">
            <template #icon>
              <svg-icon name="reset"></svg-icon>
            </template>
            重置
          </n-button>
        </n-form-item>
      </n-form>
      <div class="mb-24px">
        <n-button attr-type="button" class="ml-10px" @click="addSubject">
          <template #icon>
            <svg-icon name="add"></svg-icon>
          </template>
          新建
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

    <LayoutDialog
      class="w-1/2"
      v-model:modelValue="modelOption.isShow"
      :footer="modelOption.footer"
      :title="modelOption.title"
      @submit="saveSubjectForm"
    >
      <n-form
        ref="subjectFormRef"
        label-placement="left"
        :label-width="120"
        :model="catalogForm"
        :rules="rules"
        :disabled="modelOption.title == '详情' ? true : false"
      >
        <n-form-item label="标题" path="title">
          <n-input
            v-model:value="catalogForm.title"
            placeholder="标题"
            clearable
          />
        </n-form-item>
        <n-form-item label="资源类型">
          <n-select
            v-model:value="catalogForm.category"
            placeholder="资源类型"
            :options="categoryOptions"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
          ></n-select>
        </n-form-item>
        <n-form-item label="简介">
          <n-input
            v-model:value="catalogForm.description"
            placeholder="资源简介"
            clearable
            type="textarea"
          />
        </n-form-item>
        <n-form-item label="发布状态" path="status">
          <n-select
            v-model:value="catalogForm.status"
            placeholder="发布状态"
            :options="statusOptions"
            clearable
          ></n-select>
        </n-form-item>
      </n-form>
    </LayoutDialog>

    <LayoutConfirm
      v-model="showDel"
      title="删除提示"
      content="此操作将永久删除选中行, 是否继续?"
      @submit="submitDel"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, reactive } from 'vue'
import usePage from '@/hooks/basic-page/index'
import { createColumns, RowItem } from './index.ts'
import { DataTableRowKey } from 'naive-ui/lib'
import LayoutDialog from '@/components/common/LayoutDialog.vue'
import { cloneDeep } from 'lodash-es'
import {
  getResourceCatalogPage,
  addResourceCatalog,
  updateResourceCatalog,
  deleteResourceCatalog,
  publishResourceCatalog
} from '@/api/resourceCatalog'
import { useDict } from '@/hooks/useDict'

const { resource_category: categoryOptions, getDictLabel } =
  useDict('resource_category')

// 查询表单
type SearchForm = {
  title: string | null
  category: string | null
  status: string | null
}
const searchForm = ref<SearchForm>({
  title: null,
  category: null,
  status: null
})

const statusOptions = ref([
  { label: '草稿', value: 'draft' },
  { label: '已发布', value: 'published' }
])

// 表格数据加载
async function fetchCatalogList(params: any) {
  const res: any = await getResourceCatalogPage({
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    title: params.fuzzyQuery || params.title || undefined,
    category: params.category || undefined,
    status: params.status || undefined
  })
  const data = res.data || res
  return {
    list: (data?.records || []).map((item: any) => ({
      id: item.id,
      title: item.title,
      category: item.category,
      categoryName: item.categoryName,
      description: item.description,
      status: item.status || 'draft',
      statusName: item.statusName,
      learnedUserCount: item.learnedUserCount || 0,
      createTime: item.createTime,
      updateTime: item.updateTime
    })),
    page: data?.current || 1,
    size: data?.size || 10,
    total: data?.total || 0
  }
}

// rowkey  唯一标识
const rowKey = (row: RowItem) => row.id
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
// 表格选中行
function handleCheck(rowKeys: DataTableRowKey[]) {
  checkedRowKeysRef.value = rowKeys
}
// 初始化表格
const { loading, list, reset, loadData, pagination } = usePage(
  fetchCatalogList,
  {
    filterOption: searchForm
  }
)

const delRow = ref<RowItem | null>(null)

// 创建表头
const columns = createColumns({
  pagination,
  async publish(row: RowItem) {
    if (row.id) {
      await publishResourceCatalog(row.id)
      loadData()
    }
  },
  soldOut(row: RowItem) {
    window?.$message?.info('系统由于暂无下架接口，该操作在此预留。')
  },
  edit(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '编辑资源编目'
    modelOption.footer = true
    catalogForm.value = cloneDeep(row)
  },
  deleteRow(row: RowItem) {
    delRow.value = row
    delType.value = 'one'
    showDel.value = true
  },
  detail(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '编目详情'
    catalogForm.value = row
    modelOption.footer = false
  },
  getDictLabel
})

// 删除确认
const showDel = ref(false)
const delType = ref('')
// 重置查询
function resetQuery() {
  searchForm.value = {
    title: null,
    category: null,
    status: null
  }
  pagination.page = 1
  loadData()
}
// 弹窗配置
const modelOption = reactive({
  isShow: false,
  footer: true,
  title: ''
})
// 科目表单
const catalogForm = ref<any>({
  id: undefined,
  title: '',
  category: null,
  description: '',
  status: 'draft'
})

// 表单规则
const rules = ref({
  title: [
    {
      required: true,
      message: '请输入资源标题',
      trigger: 'blur'
    }
  ]
})
const subjectFormRef = ref()
// 教培科目新建/编辑保存/项目保存
async function saveSubjectForm() {
  subjectFormRef.value?.validate(async (errors: any) => {
    if (!errors) {
      const payload = {
        title: catalogForm.value.title,
        category: catalogForm.value.category || undefined,
        description: catalogForm.value.description || undefined,
        status: catalogForm.value.status || undefined
      }
      if (modelOption.title == '新建资源编目') {
        await addResourceCatalog(payload)
      } else if (modelOption.title == '编辑资源编目') {
        await updateResourceCatalog(catalogForm.value.id, payload)
      }
      modelOption.isShow = false
      loadData()
    } else {
      console.log(errors)
      window?.$message?.error('表单校验失败')
    }
  })
}
// 打开新建科目弹框
function addSubject() {
  modelOption.isShow = true
  modelOption.title = '新建资源编目'
  modelOption.footer = true
  catalogForm.value = {
    id: undefined,
    title: '',
    category: null,
    description: '',
    status: 'draft'
  }
}
// 批量删除科目
function deleteCheckedRows() {
  if (checkedRowKeysRef.value.length == 0) {
    window?.$message?.info('请选择要删除的数据行')
    return
  }
  showDel.value = true
  delType.value = 'many'
}
// 删除选中数据行
async function submitDel() {
  if (delType.value == 'many') {
    await Promise.all(
      checkedRowKeysRef.value.map(id => deleteResourceCatalog(id as number))
    )
    checkedRowKeysRef.value = []
  } else if (delType.value == 'one' && delRow.value) {
    await deleteResourceCatalog(delRow.value.id)
    delRow.value = null
  }

  showDel.value = false
  loadData()
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
