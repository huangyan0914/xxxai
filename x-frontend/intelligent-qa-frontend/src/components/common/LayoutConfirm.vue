<template>
  <n-modal
    v-model:show="value"
    preset="dialog"
    :title="title"
    :content="content"
    :mask-closable="true"
    positive-text="确认"
    negative-text="取消"
    @positive-click="emit('submit')"
    @negative-click="cancelCallback"
    @mask-click="maskClick"
    @close="maskClick"
  />
</template>

<script lang="ts" setup>
interface PropsOps {
  modelValue: boolean
  title: string
  content: string
}
const value = computed({
  get() {
    return props.modelValue
  },
  set(bool) {
    emit('update:modelValue', bool)
  }
})
const props = withDefaults(defineProps<PropsOps>(), {
  modelValue: false,
  title: '标题',
  content: '内容占位'
})
const emit = defineEmits(['update:modelValue', 'submit', 'cancel'])
watch(
  () => props.modelValue,
  bool => emit('update:modelValue', bool)
)

const cancelCallback = () => {
  emit('update:modelValue', false)
}
const maskClick = () => emit('update:modelValue', false)
</script>
