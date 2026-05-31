<template>
  <n-modal
    v-model:show="value"
    preset="card"
    class="tm-layout-dialog"
    size="huge"
    :bordered="false"
    transform-origin="center"
    :closable="true"
    :mask-closable="false"
  >
    <template #header>
      <div class="tm-layout-dialog-title">{{ title }}</div>
    </template>

    <slot />

    <template #footer>
      <div v-if="footer" class="tm-layout-dialog-footer">
        <n-button @click="value = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="emit('submit')">{{ submitText }}</n-button>
      </div>
    </template>
  </n-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: boolean
  title: string
  footer?: boolean
  submitText?: string
  submitting?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  title: '标题',
  footer: true,
  submitText: '确定',
  submitting: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit'): void
}>()

const value = computed({
  get() {
    return props.modelValue
  },
  set(next: boolean) {
    emit('update:modelValue', next)
  }
})
</script>

<style scoped>
.tm-layout-dialog-title {
  font-size: 15px;
  font-weight: 600;
}

.tm-layout-dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
