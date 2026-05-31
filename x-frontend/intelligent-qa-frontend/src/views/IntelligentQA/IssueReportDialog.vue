<template>
  <n-modal v-model:show="visible" preset="card" title="问题反馈" style="width: 520px" @after-leave="$emit('close')">
    <n-form ref="formRef" :model="formData" label-placement="left" label-width="80">
      <!-- 反馈存疑 -->
      <n-divider title-placement="left">反馈存疑</n-divider>
      <n-form-item label="存疑问题" path="doubtIssue">
        <n-input
          v-model:value="formData.doubtIssue"
          type="textarea"
          placeholder="请输入相关内容"
          :autosize="{ minRows: 4, maxRows: 6 }"
          clearable
        />
      </n-form-item>

      <!-- 反馈错误 -->
      <n-divider title-placement="left">反馈错误</n-divider>
      <n-form-item label="错误答案" path="wrongAnswer">
        <n-input
          v-model:value="formData.wrongAnswer"
          type="textarea"
          placeholder="请输入相关内容"
          :autosize="{ minRows: 4, maxRows: 6 }"
          clearable
        />
      </n-form-item>
      <n-form-item label="正确答案" path="correctAnswer">
        <n-input
          v-model:value="formData.correctAnswer"
          type="textarea"
          placeholder="请输入相关内容"
          :autosize="{ minRows: 4, maxRows: 6 }"
          clearable
        />
      </n-form-item>
    </n-form>

    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 8px">
        <n-button @click="visible = false">取消</n-button>
        <n-button @click="handleReset">重置</n-button>
        <n-button type="primary" :loading="submitting" @click="handleConfirm">确定</n-button>
      </div>
    </template>
  </n-modal>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useDialog, useMessage } from 'naive-ui'
import { submitIssueReport } from '@/api/intelligentQa'

const props = defineProps<{
  sessionId: string
  speakId: number
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const naiveMessage = useMessage()
const naiveDialog = useDialog()

const visible = ref(true)
const submitting = ref(false)
const formRef = ref()

const emptyForm = () => ({
  doubtIssue: '',
  wrongAnswer: '',
  correctAnswer: ''
})

const formData = ref(emptyForm())

function handleReset() {
  formData.value = emptyForm()
}

async function handleConfirm() {
  const { doubtIssue, wrongAnswer, correctAnswer } = formData.value
  if (!doubtIssue?.trim() && !wrongAnswer?.trim() && !correctAnswer?.trim()) {
    naiveMessage.warning('请至少填写一项反馈内容')
    return
  }

  submitting.value = true
  try {
    await submitIssueReport({
      sessionId: props.sessionId,
      speakId: props.speakId,
      doubtIssue: doubtIssue?.trim() || undefined,
      wrongAnswer: wrongAnswer?.trim() || undefined,
      correctAnswer: correctAnswer?.trim() || undefined
    })
    visible.value = false
    naiveDialog.success({
      title: '提示',
      content: '反馈成功！',
      positiveText: '确定',
      negativeText: '取消'
    })
  } finally {
    submitting.value = false
  }
}
</script>
