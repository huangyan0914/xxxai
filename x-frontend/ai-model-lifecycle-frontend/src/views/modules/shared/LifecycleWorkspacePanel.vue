<template>
  <section class="tm-page">
    <div class="tm-title-bar">
      <span>{{ config.name }}</span>
    </div>

    <div class="tm-page-body">
      <div class="tm-toolbar">
        <n-form inline :show-feedback="false" @submit.prevent="reload">
          <n-form-item label="关键词">
            <n-input v-model:value="keyword" clearable :placeholder="config.searchPlaceholder" />
          </n-form-item>
          <n-form-item v-if="moduleKey === 'modelEvaluations'" label="任务范围">
            <n-select v-model:value="qtype" :options="evaluationScopes" style="width: 140px" />
          </n-form-item>
          <n-form-item class="tm-query-actions">
            <n-button attr-type="button" @click="reload">查询</n-button>
            <n-button attr-type="button" class="ml-10px" @click="reset">重置</n-button>
          </n-form-item>
        </n-form>
      </div>

      <div class="tm-action-row">
        <n-button attr-type="button" @click="openCreate">新建</n-button>
        <n-button attr-type="button" class="ml-10px" @click="loadData">刷新</n-button>
      </div>

      <n-data-table
        remote
        :columns="columns"
        :data="records"
        :loading="loading"
        :bordered="true"
        :single-line="false"
        :single-column="false"
        :row-key="row => row.id || row.gid || row.name"
        :pagination="pagination"
        :scroll-x="config.scrollX"
        class="tm-table"
        @update:page="page => updatePage(page)"
        @update:page-size="size => updatePageSize(size)"
      />
    </div>

    <LayoutDialog
      v-model:modelValue="drawerVisible"
      :title="drawerTitle"
      :submitting="saving"
      submit-text="保存"
      @submit="submitForm"
    >
      <n-alert v-if="!editingSupported && drawerMode === 'edit'" type="warning" class="mb-12">
        LazyCraft 当前页面未暴露原地更新接口，已转为复制新增参数。
      </n-alert>

      <n-form ref="formRef" :model="formModel" label-placement="top">
        <n-grid :cols="2" :x-gap="14">
          <n-gi v-for="field in formFields" :key="field.key" :span="field.span || 1">
            <n-form-item :label="field.label" :path="field.key" :rule="field.required ? requiredRule(field) : undefined">
              <n-select
                v-if="field.type === 'select'"
                v-model:value="formModel[field.key]"
                :options="field.options"
                :multiple="field.multiple"
                :loading="field.loading"
                :filterable="field.filterable !== false"
                clearable
                :placeholder="field.placeholder || `请选择${field.label}`"
              />
              <n-input-number
                v-else-if="field.type === 'number'"
                v-model:value="formModel[field.key]"
                :min="field.min"
                :max="field.max"
                :step="field.step"
                :precision="field.precision"
                style="width: 100%"
                :placeholder="field.placeholder || `请输入${field.label}`"
              />
              <n-input
                v-else-if="field.type === 'textarea'"
                v-model:value="formModel[field.key]"
                type="textarea"
                :rows="field.rows || 4"
                :placeholder="field.placeholder || `请输入${field.label}`"
              />
              <n-input
                v-else
                v-model:value="formModel[field.key]"
                :placeholder="field.placeholder || `请输入${field.label}`"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-form-item v-if="moduleKey === 'datasets'" label="数据集文件">
          <div class="upload-field">
            <n-upload :custom-request="handleDatasetUpload" :show-file-list="true" multiple>
              <n-button secondary>上传文件</n-button>
            </n-upload>
            <n-space v-if="uploadedDatasetPaths.length > 0" size="small">
              <n-tag v-for="path in uploadedDatasetPaths" :key="path" size="small">{{ path }}</n-tag>
            </n-space>
          </div>
        </n-form-item>

        <n-form-item v-if="moduleKey === 'modelEvaluations'" label="离线评测数据集">
          <div class="upload-field">
            <n-upload :custom-request="handleEvaluationDatasetUpload" :show-file-list="true" multiple>
              <n-button secondary>上传评测数据集</n-button>
            </n-upload>
            <span class="form-help">上传成功后会自动回填数据集 ID。</span>
          </div>
        </n-form-item>

        <n-form-item v-if="moduleKey !== 'finetunes'" label="高级参数 JSON">
          <n-input
            v-model:value="advancedJson"
            type="textarea"
            :rows="8"
            placeholder="可补充 LazyCraft 原始接口需要的字段，提交时会与上方表单合并"
          />
        </n-form-item>
      </n-form>
    </LayoutDialog>

    <n-modal v-model:show="knowledgeFilesVisible" preset="card" title="知识库文件" class="workspace-modal">
      <n-space vertical size="medium">
        <n-upload :custom-request="handleKnowledgeFileUpload" :show-file-list="true" multiple>
          <n-button type="primary">上传并加入知识库</n-button>
        </n-upload>
        <n-data-table
          remote
          :columns="knowledgeFileColumns"
          :data="knowledgeFileRecords"
          :loading="knowledgeFileLoading"
          :pagination="knowledgeFilePagination"
          :row-key="row => row.id || row.file_id || row.name"
          @update:page="page => updateKnowledgeFilePage(page)"
        />
      </n-space>
    </n-modal>

    <n-modal v-model:show="datasetVersionsVisible" preset="card" title="数据集版本" class="workspace-modal">
      <n-space vertical size="medium">
        <n-space>
          <n-select v-model:value="datasetVersionType" :options="datasetVersionTypeOptions" style="width: 140px" @update:value="loadDatasetVersions" />
          <n-button secondary @click="loadDatasetVersions">刷新</n-button>
        </n-space>
        <n-data-table
          remote
          :columns="datasetVersionColumns"
          :data="datasetVersionRecords"
          :loading="datasetVersionLoading"
          :pagination="datasetVersionPagination"
          :row-key="row => row.id || row.name"
          @update:page="page => updateDatasetVersionPage(page)"
        />
      </n-space>
    </n-modal>
  </section>
</template>

<script setup lang="ts">
import { useLifecycleWorkspace } from './useLifecycleWorkspace'
import type { ModuleKey } from './types'
import LayoutDialog from '@/components/common/LayoutDialog.vue'

const props = defineProps<{ moduleKey: ModuleKey }>()

const {
  moduleKey,
  config,
  formRef,
  loading,
  saving,
  keyword,
  qtype,
  records,
  drawerVisible,
  drawerMode,
  formModel,
  advancedJson,
  uploadedDatasetPaths,
  knowledgeFilesVisible,
  knowledgeFileRecords,
  knowledgeFileLoading,
  datasetVersionsVisible,
  datasetVersionRecords,
  datasetVersionLoading,
  datasetVersionType,
  pagination,
  knowledgeFilePagination,
  datasetVersionPagination,
  evaluationScopes,
  datasetVersionTypeOptions,
  formFields,
  editingSupported,
  drawerTitle,
  columns,
  knowledgeFileColumns,
  datasetVersionColumns,
  loadData,
  reload,
  reset,
  updatePage,
  updatePageSize,
  openCreate,
  submitForm,
  requiredRule,
  handleDatasetUpload,
  handleEvaluationDatasetUpload,
  handleKnowledgeFileUpload,
  updateKnowledgeFilePage,
  loadDatasetVersions,
  updateDatasetVersionPage
} = useLifecycleWorkspace(props)
</script>
