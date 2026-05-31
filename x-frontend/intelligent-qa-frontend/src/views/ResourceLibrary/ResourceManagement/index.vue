<template>
  <div
    class="wh-full border border-[#bebebe] shadow-md shadow-[#bebebe)] flex flex-col"
  >
    <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
      <span class="leading-40px ml-8px">教培资源管理</span>
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
        <n-form-item label="资源名称">
          <n-input
            v-model:value="searchForm.name"
            placeholder="资源名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="上传人">
          <n-select
            class="w-200px"
            v-model:value="searchForm.uploader"
            placeholder="上传人"
            :options="uploaderList"
            :label-field="'label'"
            :value-field="'value'"
            clearable
          ></n-select>
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
      <div class="mb-24px flex flex-row justify-start">
        <n-button attr-type="button" class="ml-10px" @click="addResource">
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
          批量删除
        </n-button>
        <div class="flex-1 flex justify-end">
          <n-button attr-type="button" class="ml-10px" @click="importTableData">
            <template #icon>
              <svg-icon name="import"></svg-icon>
            </template>
            导入
          </n-button>
        </div>
      </div>
      <n-data-table
        :columns="columns"
        :data="list"
        :pagination="pagination"
        :bordered="false"
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
      v-model:modelValue="modalOption.isShow"
      :footer="modalOption.footer"
      :title="modalOption.title"
      @submit="saveResourceForm"
    >
      <n-form
        ref="resourceFormRef"
        label-placement="left"
        :label-width="120"
        :model="resourceForm"
        :rules="rules"
        :disabled="modalOption.title == '详情' ? true : false"
      >
        <n-form-item label="名称" path="fileName">
          <n-input
            v-model:value="resourceForm.fileName"
            placeholder="名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="所属目录" path="catalogId">
          <n-select
            v-model:value="resourceForm.catalogId"
            placeholder="所属目录"
            :options="catalogList"
            :label-field="'title'"
            :value-field="'id'"
            clearable
          ></n-select>
        </n-form-item>
        <n-form-item label="文件类型">
          <n-select
            v-model:value="resourceForm.fileType"
            placeholder="文件类型"
            :options="fileTypeOptions"
            :label-field="fileTypeOptions.name"
            :value-field="fileTypeOptions.code"
            clearable
          ></n-select>
        </n-form-item>
        <n-form-item label="文件大小(Bytes)" path="fileSize">
          <n-input-number
            v-model:value="resourceForm.fileSize"
            placeholder="文件大小"
            clearable
          />
        </n-form-item>
      </n-form>
    </LayoutDialog>

    <LayoutDialog
      class="w-1/2"
      v-model:modelValue="uploadModalOption.isShow"
      :footer="uploadModalOption.footer"
      :title="uploadModalOption.title"
      @submit="uploadResourceData"
    >
      <n-form ref="uploadFormRef" label-placement="left" :label-width="120">
        <n-form-item
          label=""
          path=""
          v-if="uploadModalOption.title == '上传附件' ? false : true"
        >
          <n-button
            attr-type="button"
            class="ml-10px"
            @click="downloadModelFile()"
          >
            <template #icon>
              <svg-icon name="export"></svg-icon>
            </template>
            模板下载
          </n-button>
        </n-form-item>

        <n-form-item label="上传文件">
          <n-upload
            directory-dnd
            :default-upload="false"
            @update:file-list="handleDataFileChange"
            v-model:file-list="uploadDataFileList"
          >
            <n-upload-dragger>
              <div style="margin-bottom: 12px">
                <n-icon size="48" :depth="3">
                  <ArchiveIcon />
                </n-icon>
              </div>
              <n-text style="font-size: 16px">
                点击或者拖动文件到该区域来上传
              </n-text>
              <n-p depth="3" style="margin: 8px 0 0 0">
                <!-- 请不要上传敏感数据，比如你的银行卡号和密码，信用卡号有效期和安全码 -->
              </n-p>
            </n-upload-dragger>
          </n-upload>
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
import { ref, reactive, onMounted, unref } from 'vue'
import usePage from '@/hooks/basic-page/index'
import { createColumns, RowItem } from './index.js'
import { DataTableRowKey, UploadFileInfo } from 'naive-ui/lib'
import LayoutDialog from '@/components/common/LayoutDialog.vue'
import { cloneDeep } from 'lodash-es'
import { ArchiveOutline as ArchiveIcon } from '@vicons/ionicons5'

import {
  getResourceManagementPage,
  addResourceManagement,
  updateResourceManagement,
  deleteResourceManagement
} from '@/api/resourceManagement'
import { getResourceCatalogPage } from '@/api/resourceCatalog'

const { file_type: fileTypeOptions, getDictLabel } = useDict('file_type')

onMounted(async () => {
  try {
    const res: any = await getResourceCatalogPage({ pageSize: 1000 })
    catalogList.value = res?.data?.records || res?.records || []
  } catch (e) {}
})

// 查询表单
type SearchForm = {
  name: string
  uploader: string
}
const searchForm = ref<SearchForm>({
  name: '',
  uploader: ''
})
// 上传人列表
const uploaderList = ref([
  {
    label: '张三',
    value: 'zhangsan'
  },
  {
    label: '李四',
    value: 'lisi'
  }
])
// 资源目录列表
const catalogList = ref([] as any[])
// 上传资源文件列表
const uploadResourceFileList = ref<UploadFileInfo[]>([])
// 上传资源文件列表change事件
function handleFileListChange(fileList: UploadFileInfo[]) {
  setTimeout(() => {
    console.log(fileList)
    console.log(unref(uploadResourceFileList))
  })
}

// 上传文件弹窗
const uploadModalOption = reactive({
  isShow: false,
  footer: true,
  title: ''
})
// 上传表格数据
function importTableData() {
  uploadModalOption.isShow = true
  uploadModalOption.title = '上传数据'
}
// 上传下侧表格数据文件列表
const uploadDataFileList = ref<UploadFileInfo[]>([])
// 上传数据模板文件change事件
function handleDataFileChange(fileList: UploadFileInfo[]) {
  setTimeout(() => {
    console.log(fileList)
    console.log(unref(uploadDataFileList))
  })
}
// 数据模板下载
const downloadModelFile = () => {}
// 上传模板数据
function uploadResourceData() {
  if (uploadDataFileList.value?.length < 1) {
    window?.$message?.warning('请选择文件')
    return
  }
  if (uploadModalOption.title == '上传数据') {
    let formData = new FormData()
    formData.append('file', uploadDataFileList.value[0].file as any)
    uploadModalOption.isShow = false
    uploadDataFileList.value = []
    loadData()
  } else if (uploadModalOption.title == '上传附件') {
    let formData = new FormData()
    uploadModalOption.isShow = false
    uploadDataFileList.value = []
    loadData()
  }
}
// 表格数据
async function fetchResourceList(params: any) {
  const res: any = await getResourceManagementPage({
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    fileName: params.name || undefined
  })
  const data = res.data || res
  return {
    list: (data?.records || []).map((item: any) => ({
      id: item.id,
      catalogId: item.catalogId,
      catalogTitle: item.catalogTitle,
      fileName: item.fileName,
      fileType: item.fileType,
      fileTypeName: item.fileTypeName,
      fileSize: item.fileSize,
      boundProjectCount: item.boundProjectCount,
      learnedUserCount: item.learnedUserCount,
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
const { loading, list, loadData, pagination } = usePage(fetchResourceList, {
  filterOption: searchForm
})
// 创建表头
const columns = createColumns({
  pagination,
  edit(row: RowItem) {
    modalOption.isShow = true
    modalOption.title = '编辑教培资源'
    modalOption.footer = true

    resourceForm.value = cloneDeep(row)
  },
  deleteRow(row: RowItem) {
    delType.value = 'one'
    delRow.value = row
    showDel.value = true
  },
  detail(row: RowItem) {
    modalOption.isShow = true
    modalOption.title = '详情'
    resourceForm.value = cloneDeep(row)
    modalOption.footer = false
  },
  uploadFiles(row: RowItem) {
    uploadModalOption.isShow = true
    uploadModalOption.title = '上传附件'
    uploadDataFileList.value = []
  },
  getDictLabel
})

// 删除确认
const showDel = ref(false)
const delType = ref('')
const delRow = ref<RowItem | null>(null)
// 重置查询
function resetQuery() {
  searchForm.value = {
    name: '',
    uploader: ''
  }
  pagination.page = 1
  loadData()
}
// 弹窗配置
const modalOption = reactive({
  isShow: false,
  footer: true,
  title: ''
})

// 教培资源表单
const resourceForm = ref<any>({
  id: undefined,
  fileName: '',
  catalogId: null,
  fileType: null,
  fileSize: null
})

// 表单规则
const rules = ref({
  fileName: [
    {
      required: true,
      message: '请输入名称',
      trigger: 'blur'
    }
  ],
  catalogId: [
    {
      required: true,
      type: 'number',
      message: '请选择所属目录',
      trigger: ['blur', 'change']
    }
  ],
  fileType: [
    {
      required: false,
      message: '请输入文件类型',
      trigger: 'blur'
    }
  ]
})
const resourceFormRef = ref()
// 教培资源新建/编辑保存/项目保存
async function saveResourceForm() {
  resourceFormRef.value?.validate(async (errors: any) => {
    if (!errors) {
      const payload = {
        fileName: resourceForm.value.fileName,
        catalogId: resourceForm.value.catalogId,
        fileType: resourceForm.value.fileType || undefined,
        fileSize: resourceForm.value.fileSize || undefined
      }

      if (modalOption.title == '新建教培资源') {
        await addResourceManagement(payload)
      } else if (modalOption.title == '编辑教培资源') {
        await updateResourceManagement(resourceForm.value.id, payload)
      }
      modalOption.isShow = false
      loadData()
    } else {
      console.log(errors)
      window?.$message?.error('表单校验失败')
    }
  })
}
// 打开新建教培资源弹框
function addResource() {
  modalOption.isShow = true
  modalOption.title = '新建教培资源'
  modalOption.footer = true
  resourceForm.value = {
    id: undefined,
    fileName: '',
    catalogId: null,
    fileType: null,
    fileSize: null
  }
  uploadResourceFileList.value = []
}
// 批量删除教培资源
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
      checkedRowKeysRef.value.map(id => deleteResourceManagement(id as number))
    )
    checkedRowKeysRef.value = []
  } else if (delType.value == 'one' && delRow.value) {
    await deleteResourceManagement(delRow.value.id)
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
