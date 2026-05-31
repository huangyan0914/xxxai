<template>
  <div class="received-shares">
    <div class="page-header">
      <span class="page-title">收到的分享</span>
    </div>

    <n-data-table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      @update:page="handlePageChange"
      bordered
      remote
    />

    <!-- 查看详情弹窗 -->
    <n-modal
      v-model:show="detailVisible"
      preset="card"
      title="分享详情"
      style="width: 600px; max-height: 80vh"
    >
      <div class="detail-messages">
        <div
          v-for="msg in detailMessages"
          :key="msg.speakId"
          class="detail-msg"
          :class="msg.role === 'user' ? 'msg-user' : 'msg-assistant'"
        >
          <div class="msg-role">{{ msg.role === 'user' ? '用户' : 'AI' }}</div>
          <div class="msg-content">{{ msg.content }}</div>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, h, onMounted } from 'vue'
import { NButton, NSpace, useMessage } from 'naive-ui'
import { listReceivedShares, getShareDetail, deleteShare } from '@/api/share'

const naiveMessage = useMessage()

const loading = ref(false)
const tableData = ref<any[]>([])
const detailVisible = ref(false)
const detailMessages = ref<any[]>([])

const pagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: false
})

const columns = [
  { title: '对话标题', key: 'sessionTitle', ellipsis: true },
  { title: '发送人', key: 'fromUserId', width: 120 },
  { title: '收到时间', key: 'createTime', width: 170 },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    render: (row: any) =>
      h(NSpace, null, {
        default: () => [
          h(NButton, { size: 'small', onClick: () => handleView(row) }, { default: () => '查看' }),
          h(NButton, { size: 'small', type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]
      })
  }
]

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res: any = await listReceivedShares({ pageNo: pagination.value.page, pageSize: pagination.value.pageSize })
    tableData.value = res?.records || res?.list || []
    pagination.value.itemCount = res?.total || 0
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  pagination.value.page = page
  loadData()
}

async function handleView(row: any) {
  const res: any = await getShareDetail(row.id)
  detailMessages.value = res?.messages || []
  detailVisible.value = true
}

async function handleDelete(row: any) {
  await deleteShare(row.id)
  naiveMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.received-shares {
  padding: 20px;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.detail-messages {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 4px;
}

.detail-msg {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.msg-user {
  align-items: flex-end;
}

.msg-assistant {
  align-items: flex-start;
}

.msg-role {
  font-size: 12px;
  color: #999;
}

.msg-content {
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 8px;
  max-width: 80%;
  word-break: break-word;
  white-space: pre-wrap;
}

.msg-user .msg-content {
  background: #e6f4ff;
}
</style>
