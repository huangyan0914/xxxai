<template>
  <div
    class="wh-full border border-[#bebebe] shadow-md shadow-[#bebebe)] flex flex-col"
  >
    <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
      <span class="leading-40px ml-8px">项目管理</span>
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
        <n-form-item label="类别选择">
          <n-select
            class="w-200px"
            v-model:value="searchForm.type"
            placeholder="类别选择"
            :options="typeOptions"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
          ></n-select>
        </n-form-item>
        <n-form-item label="模糊查询(名称/编码)">
          <n-input
            v-model:value="searchForm.name"
            placeholder="名称或编码"
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
        <n-button attr-type="button" class="ml-10px" @click="downloadFile">
          <template #icon>
            <svg-icon name="export"></svg-icon>
          </template>
          导出
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
      @submit="saveProjectForm"
    >
      <n-form
        ref="projectFormRef"
        label-placement="left"
        :label-width="120"
        :model="projectForm"
        :rules="rules"
        v-if="modelOption.title.includes('项目')"
      >
        <n-form-item label="项目名称" path="name">
          <n-input
            v-model:value="projectForm.name"
            placeholder="项目名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="项目编码" path="code">
          <n-input
            v-model:value="projectForm.code"
            placeholder="项目编码"
            clearable
          />
        </n-form-item>
        <n-form-item label="项目介绍">
          <n-input
            v-model:value="projectForm.desc"
            placeholder="项目介绍"
            clearable
            type="textarea"
          />
        </n-form-item>
        <n-form-item label="类别">
          <n-select
            v-model:value="projectForm.type"
            placeholder="类别"
            :options="typeOptions"
            :label-field="'label'"
            :value-field="'value'"
            clearable
            filterable
          ></n-select>
        </n-form-item>
        <n-form-item label="难度">
          <n-input
            v-model:value="projectForm.level"
            placeholder="难度"
            clearable
          />
        </n-form-item>
      </n-form>
      <n-form
        ref="projectFormRef"
        label-placement="left"
        :label-width="120"
        :model="jiaopeiResourceForm"
        :rules="rules"
        v-else
      >
        <n-form-item label="项目名称" path="name">
          {{ jiaopeiResourceForm.name }}
        </n-form-item>
        <n-form-item
          :label="
            modelOption.title === '解绑教培资源'
              ? '要解绑的教培资源'
              : '教培资源选择'
          "
        >
          <n-select
            v-model:value="jiaopeiResourceForm.jiaopeiResource"
            :placeholder="
              modelOption.title === '解绑教培资源'
                ? '请选择要解绑的资源'
                : '教培资源选择'
            "
            :options="
              modelOption.title === '解绑教培资源'
                ? boundResourceOptions
                : resourceOptions
            "
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
import { ref, reactive, onMounted, h } from 'vue'
import usePage from '@/hooks/basic-page/index'
import { createColumns, RowItem } from './index.ts'
import { DataTableRowKey } from 'naive-ui/lib'
import LayoutDialog from '@/components/common/LayoutDialog.vue'
import { cloneDeep } from 'lodash-es'
import { exportFile } from '@/utils'
import {
  getProjectPage,
  addProject,
  updateProject,
  deleteProject,
  getProjectBoundResources,
  bindProjectResources,
  unbindProjectResource
} from '@/api/projectManagement'
import { getResourceManagementPage } from '@/api/resourceManagement'

// ---- 若依数据字典：类别（project_type） ----
const { project_type: typeOptions, getDictLabel } = useDict('project_type')

// 查询表单
type SearchForm = {
  name: string
  type: string
}
const searchForm = ref<SearchForm>({
  name: '',
  type: ''
})

// 教培资源下拉项
const resourceOptions = ref<{ label: string; value: number }[]>([])
const boundResourceOptions = ref<{ label: string; value: number }[]>([])

onMounted(async () => {
  try {
    const res = await getResourceManagementPage({ pageSize: 1000 })
    resourceOptions.value = (res?.records || []).map((item: any) => ({
      label: item.fileName,
      value: item.id
    }))
  } catch (err) {}
})

// 表格数据加载
async function fetchProjectList(params: any) {
  const res: any = await getProjectPage({
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    projectName: params.name || undefined,
    projectCode: params.name || undefined, // use name for both name/code fuzzy
    projectType: params.type || undefined
  })

  const data = res.data || res // 兼容包裹和未包裹的数据层响应结构
  return {
    list: (data?.records || []).map((item: any) => ({
      id: item.id,
      name: item.projectName,
      code: item.projectCode,
      desc: item.description,
      type: item.projectType || '',
      category: item.category || '',
      level: item.difficulty || '',
      startDate: item.startDate || '',
      endDate: item.endDate || '',
      boundResourceCount: item.boundResourceCount || 0
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
  fetchProjectList,
  {
    filterOption: searchForm
  }
)

// 删除确认
const showDel = ref(false)
const delType = ref('')
const delRow = ref<RowItem | null>(null)

// 弹窗配置
const modelOption = reactive({
  isShow: false,
  footer: true,
  title: ''
})

// 项目表单
const projectForm = ref<any>({
  id: undefined,
  name: '',
  code: '',
  desc: '',
  type: null,
  level: ''
})

const jiaopeiResourceForm = ref<{
  id: number | undefined
  name: string
  jiaopeiResource: number[]
}>({
  id: undefined,
  name: '',
  jiaopeiResource: []
})

// 表单规则
const rules = ref({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入项目编码', trigger: 'blur' }]
})
const projectFormRef = ref()

// 创建表头
const columns = createColumns({
  pagination,
  edit(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '编辑项目'
    projectForm.value = cloneDeep(row)
  },
  deleteRow(row: RowItem) {
    delRow.value = row
    delType.value = 'one'
    showDel.value = true
  },
  async setting(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '绑定教培资源'
    jiaopeiResourceForm.value.id = row.id
    jiaopeiResourceForm.value.name = row.name
    jiaopeiResourceForm.value.jiaopeiResource = []
    try {
      const res = await getProjectBoundResources(row.id)
      jiaopeiResourceForm.value.jiaopeiResource = res?.data || []
    } catch {}
  },
  async unbind(row: RowItem) {
    modelOption.isShow = true
    modelOption.title = '解绑教培资源'
    jiaopeiResourceForm.value.id = row.id
    jiaopeiResourceForm.value.name = row.name
    jiaopeiResourceForm.value.jiaopeiResource = []

    try {
      const boundIds = await getProjectBoundResources(row.id)
      boundResourceOptions.value = resourceOptions.value.filter(opt =>
        boundIds.includes(opt.value)
      )
    } catch {}
  },
  getDictLabel
})

// 重置查询
function resetQuery() {
  searchForm.value = {
    name: '',
    type: ''
  }
  pagination.page = 1
  loadData()
}

// 打开新建项目弹框
function addSubject() {
  modelOption.isShow = true
  modelOption.title = '新建项目'
  projectForm.value = {
    id: undefined,
    name: '',
    code: '',
    desc: '',
    type: null,
    level: ''
  }
}

// 项目新建/编辑保存/配置项目保存
async function saveProjectForm() {
  if (modelOption.title === '绑定教培资源') {
    await bindProjectResources(
      jiaopeiResourceForm.value.id!,
      jiaopeiResourceForm.value.jiaopeiResource
    )
    modelOption.isShow = false
    loadData()
    return
  } else if (modelOption.title === '解绑教培资源') {
    await Promise.all(
      jiaopeiResourceForm.value.jiaopeiResource.map(rId =>
        unbindProjectResource(jiaopeiResourceForm.value.id!, rId)
      )
    )
    modelOption.isShow = false
    loadData()
    return
  }

  projectFormRef.value?.validate(async (errors: any) => {
    if (!errors) {
      const payload = {
        projectName: projectForm.value.name,
        projectCode: projectForm.value.code,
        projectType: projectForm.value.type || undefined,
        difficulty: projectForm.value.level || undefined,
        description: projectForm.value.desc || undefined
      }
      if (modelOption.title == '新建项目') {
        await addProject(payload)
      } else if (modelOption.title == '编辑项目') {
        await updateProject(projectForm.value.id, payload)
      }
      modelOption.isShow = false
      loadData()
    } else {
      console.log(errors)
      window?.$message?.error('表单校验失败')
    }
  })
}

// 批量删除项目
function deleteCheckedRows() {
  if (checkedRowKeysRef.value.length == 0) {
    window?.$message?.info('请选择要删除的数据行')
    return
  }
  showDel.value = true
  delType.value = 'many'
}
// 批量导出
function downloadFile() {
  if (checkedRowKeysRef.value.length == 0) {
    window?.$message?.info('请选择要导出的数据行')
    return
  }
  // 调用导出后台接口
  // downLoadFile().then(res => {
  //   exportFile(res)
  // })
}
// 删除选中数据行
async function submitDel() {
  if (delType.value == 'many') {
    await Promise.all(
      checkedRowKeysRef.value.map(id => deleteProject(id as number))
    )
    checkedRowKeysRef.value = []
  } else if (delType.value == 'one' && delRow.value) {
    await deleteProject(delRow.value.id)
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
