<template>
  <section class="tm-page tm-page-detail">
    <div class="tm-title-bar">
      <span>模型微调详情</span>
    </div>

    <div class="tm-page-body">
      <div class="detail-page-header">
        <n-breadcrumb>
          <n-breadcrumb-item @click="router.push('/model-management/finetune/index')">模型微调</n-breadcrumb-item>
          <n-breadcrumb-item>任务详情</n-breadcrumb-item>
        </n-breadcrumb>
        <n-button secondary @click="router.push('/model-management/finetune/index')">返回</n-button>
      </div>

      <n-spin :show="loading">
        <div class="detail-surface">
          <div class="detail-header">
            <div>
              <div class="detail-title">{{ detail.name || '-' }}</div>
              <div class="detail-subtitle">{{ detail.target_model_name || '-' }}</div>
            </div>
            <n-button type="primary" ghost :disabled="!detail.id" @click="fullConfigVisible = true">查看完整配置</n-button>
          </div>

          <div class="detail-section-title">基本信息</div>
          <div class="detail-grid">
            <div class="detail-item"><span>来源</span><strong>{{ detail.created_from_info || '-' }}</strong></div>
            <div class="detail-item"><span>训练状态</span><strong>{{ finetuneStatusText(detail.status) }}</strong></div>
            <div class="detail-item"><span>耗时</span><strong>{{ formatRuntime(detail.train_runtime) }}</strong></div>
            <div class="detail-item"><span>基础模型</span><strong>{{ detail.base_model_name || detail.base_model_key || '-' }}</strong></div>
            <div class="detail-item detail-wide"><span>训练数据集</span><strong>{{ formatDatasetList(detail.dataset_list || detail.datasets) }}</strong></div>
          </div>
        </div>

        <div class="detail-surface">
          <div class="detail-section-title detail-section-title-top">训练日志</div>
          <div class="log-panel">
            <div v-if="logs.length === 0" class="log-empty">暂无日志</div>
            <div v-for="(line, index) in logs" :key="index" class="log-line">{{ line }}</div>
          </div>
        </div>
      </n-spin>

      <n-modal v-model:show="fullConfigVisible" preset="card" title="完整配置" class="workspace-modal">
        <div class="detail-section-title">基本信息</div>
        <div class="detail-grid">
          <div v-for="item in fullBaseItems" :key="item.label" class="detail-item" :class="{ 'detail-wide': item.wide }">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>

        <div class="detail-section-title">超参配置</div>
        <div class="detail-grid">
          <div v-for="item in hyperParamItems" :key="item.label" class="detail-item">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </n-modal>
    </div>
  </section>
</template>

<script setup lang="ts">
import { useFinetuneDetail } from './index'

const {
  router,
  loading,
  fullConfigVisible,
  detail,
  logs,
  fullBaseItems,
  hyperParamItems,
  finetuneStatusText,
  formatRuntime,
  formatDatasetList
} = useFinetuneDetail()
</script>
