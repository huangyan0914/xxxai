<template>
  <n-modal v-model:show="visible" preset="card" title="转发对话" style="width: 480px" @after-leave="$emit('close')">
    <n-form :model="formData" label-placement="left" label-width="80">
      <n-form-item label="接收人">
        <n-select
          v-model:value="formData.toUserId"
          filterable
          placeholder="请输入姓名搜索"
          :options="userOptions"
          :loading="userLoading"
          remote
          clearable
          @search="handleUserSearch"
        />
      </n-form-item>
    </n-form>

    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 8px">
        <n-button @click="visible = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleConfirm">确认转发</n-button>
      </div>
    </template>
  </n-modal>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { createShare } from '@/api/share'
import { listUsers } from '@/api/system'

const props = defineProps<{
  sessionId: string
  sessionTitle?: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const naiveMessage = useMessage()

const visible = ref(true)
const submitting = ref(false)
const userLoading = ref(false)
const userOptions = ref<{ label: string; value: string }[]>([])

const formData = ref({
  toUserId: null as string | null
})

onMounted(() => {
  loadUsers('')
})

async function loadUsers(keyword: string) {
  userLoading.value = true
  try {
    const res: any = await listUsers({ pageNum: 1, pageSize: 20, nickName: keyword })
    const list = res?.records || res?.list || []
    userOptions.value = list.map((u: any) => ({
      label: u.realName || u.nickName || u.username || u.userName,
      value: String(u.id || u.userId)
    }))
  } finally {
    userLoading.value = false
  }
}

function handleUserSearch(keyword: string) {
  loadUsers(keyword)
}

async function handleConfirm() {
  if (!formData.value.toUserId) {
    naiveMessage.warning('请选择接收人')
    return
  }
  submitting.value = true
  try {
    await createShare({
      sessionId: props.sessionId,
      sessionTitle: props.sessionTitle,
      toUserId: formData.value.toUserId!
    })
    naiveMessage.success('转发成功')
    visible.value = false
  } finally {
    submitting.value = false
  }
}
</script>
