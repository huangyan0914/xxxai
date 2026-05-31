<template>
  <div
    class="wh-full border border-[#bebebe] shadow-md shadow-[#bebebe)] flex flex-col"
  >
    <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
      <span class="leading-40px ml-8px">教培科目管理</span>
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
        <n-form-item label="所属分系统">
          <n-select
            class="w-200px"
            v-model:value="searchForm.type"
            placeholder="所属分系统"
            :options="selectOption"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
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
        :model="subjectForm"
        :rules="rules"
        :disabled="modelOption.title == '详情' ? true : false"
        v-if="modelOption.title.includes('项目') ? false : true"
      >
        <n-form-item label="科目名称" path="name">
          <n-input
            v-model:value="subjectForm.name"
            placeholder="科目名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="科目介绍">
          <n-input
            v-model:value="subjectForm.desc"
            placeholder="科目介绍"
            clearable
            type="textarea"
          />
        </n-form-item>
        <n-form-item label="所属分系统">
          <n-select
            v-model:value="subjectForm.system"
            placeholder="所属分系统"
            :options="selectOption"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            multiple
            filterable
          ></n-select>
        </n-form-item>
      </n-form>
      <n-form
        ref="subjectFormRef"
        label-placement="left"
        :label-width="120"
        :model="xiangmuForm"
        :rules="rules"
        :disabled="modelOption.title == '详情' ? true : false"
        v-else
      >
        <n-form-item label="科目名称" path="name">
          {{ xiangmuForm.name }}
        </n-form-item>
        <n-form-item label="配置项目选择">
          <n-select
            v-model:value="xiangmuForm.xiangmu"
            placeholder="配置项目选择"
            :options="projectOption"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            multiple
            filterable
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
import { ref, reactive, onMounted } from 'vue'
import usePage from '@/hooks/basic-page/index'
import { createColumns, RowItem } from './index.ts'
import { DataTableRowKey } from 'naive-ui/lib'
import LayoutDialog from '@/components/common/LayoutDialog.vue'
import { cloneDeep } from 'lodash-es'
import {
  getSubjectPage,
  addSubject as addSubjectApi,
  updateSubject,
  deleteSubject,
  getSubjectConfig,
  saveSubjectConfig
} from '@/api/subjectManagement'
import { getProjectPage } from '@/api/projectManagement'

const projectOption = ref<any[]>([])
onMounted(async () => {
  try {
    const res: any = await getProjectPage({ pageSize: 1000 })
    projectOption.value = (res?.data?.records || res?.records || []).map(
      (item: any) => ({
        label: item.projectName,
        value: item.id
      })
    )
  } catch (e) {}
})

// ---- 若依数据字典：所属分系统 ----
const { system_code: selectOption, getDictLabel } = useDict('system_code')

// 查询表单

type SearchForm = {
  name: string
  type: string
}
const searchForm = ref<SearchForm>({
  name: '',
  type: ''
})

// usePage 接口适配：将表单字段映射为 API 参数，响应转换为 usePage 期望格式

async function fetchSubjectList(params: any) {
  const res = await getSubjectPage({
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    subjectName: params.name || undefined,
    systemCode: params.type || undefined
  })
  const data = res
  return {
    list: (data?.records || []).map((item: any) => ({
      id: item.id,
      name: item.subjectName,
      desc: item.subjectDesc,
      systemCode: item.systemCode || '',
      system: item.systemCode ? [item.systemCode] : []
    })),
    page: data?.current || 1,
    size: data?.size || 10,
    total: data?.total || 0
  }
}

// rowkey 唯一标识
const rowKey = (row: RowItem) => row.id
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
// 表格选中行
function handleCheck(rowKeys: DataTableRowKey[]) {
  checkedRowKeysRef.value = rowKeys
}
// 初始化表格（使用真实接口）
const { loading, list, reset, loadData, pagination } = usePage(
  fetchSubjectList,
  {
    filterOption: searchForm
  }
)

// 删除确认相关
const showDel = ref(false)
const delType = ref('')
const delRow = ref<RowItem | null>(null)

// 弹窗配置
const modelOption = reactive({
  isShow: false,
  footer: true,
  title: ''
})

// 科目表单
const subjectForm = ref<any>({
  id: undefined,
  name: '',
  desc: '',
  system: []
})

// 配置项目表单
const xiangmuForm = ref<{
  id: number | undefined
  name: string
  xiangmu: number[]
}>({
  id: undefined,
  name: '',
  xiangmu: []
})

// 表单规则
const rules = ref({
  name: [{ required: true, message: '请输入科目名称', trigger: 'blur' }]
})
const subjectFormRef = ref()

// 创建表头
const columns = createColumns({
  pagination,
  edit(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '编辑教培科目'
    modelOption.footer = true
    subjectForm.value = cloneDeep(row)
  },
  deleteRow(row: RowItem) {
    delRow.value = row
    delType.value = 'one'
    showDel.value = true
  },
  detail(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '详情'
    subjectForm.value = cloneDeep(row)
    modelOption.footer = false
  },
  async setting(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '配置项目'
    modelOption.footer = true
    xiangmuForm.value.id = row.id
    xiangmuForm.value.name = row.name
    xiangmuForm.value.xiangmu = []
    // 加载已关联的项目 ID 列表
    const res = await getSubjectConfig(row.id)
    xiangmuForm.value.xiangmu = res?.data || []
  },
  getDictLabel
})

// 重置查询
function resetQuery() {
  searchForm.value = { name: '', type: '' }
  pagination.page = 1
  loadData()
}

// 打开新建科目弹框
function addSubject() {
  modelOption.isShow = true
  modelOption.title = '新建教培科目'
  modelOption.footer = true
  subjectForm.value = { id: undefined, name: '', desc: '', system: [] }
}

// 教培科目新建/编辑保存/项目配置保存
async function saveSubjectForm() {
  // 配置项目：无需字段校验，直接提交
  if (modelOption.title === '配置项目') {
    await saveSubjectConfig(xiangmuForm.value.id!, xiangmuForm.value.xiangmu)
    modelOption.isShow = false
    loadData()
    return
  }
  subjectFormRef.value?.validate(async (errors: any) => {
    if (errors) return
    const payload = {
      subjectName: subjectForm.value.name,
      subjectDesc: subjectForm.value.desc || undefined,
      systemCode:
        subjectForm.value.system?.[0] || subjectForm.value.system || undefined
    }
    if (modelOption.title === '新建教培科目') {
      await addSubjectApi(payload)
    } else if (modelOption.title === '编辑教培科目') {
      await updateSubject(subjectForm.value.id, payload)
    }
    modelOption.isShow = false
    loadData()
  })
}

// 批量删除科目
function deleteCheckedRows() {
  if (checkedRowKeysRef.value.length === 0) {
    window?.$message?.info('请选择要删除的数据行')
    return
  }
  showDel.value = true
  delType.value = 'many'
}

// 确认删除
async function submitDel() {
  if (delType.value === 'many') {
    await Promise.all(
      checkedRowKeysRef.value.map(id => deleteSubject(id as number))
    )
    checkedRowKeysRef.value = []
  } else if (delType.value === 'one' && delRow.value) {
    await deleteSubject(delRow.value.id)
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
