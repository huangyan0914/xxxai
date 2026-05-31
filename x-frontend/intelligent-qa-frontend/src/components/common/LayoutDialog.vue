<template>
  <n-modal
    id="basic-modal"
    v-model:show="value"
    preset="card"
    class="custom-card"
    size="huge"
    :bordered="false"
    transform-origin="center"
    :closable="true"
    :maskClosable="false"
    @close="maskClick"
  >
    <template #header>
      <div id="basic-modal-bar" class="w-full cursor-move">{{ title }}</div>
    </template>
    <slot></slot>
    <template #footer>
      <div v-if="footer" class="w-full flex justify-center">
        <n-button class="mr-14px" @click="maskClick">
          <template #icon>
            <svg-icon name="cancle"></svg-icon>
          </template>
          取消
        </n-button>
        <n-button v-if="props.export" @click="e => emit('export', e)">
          <template #icon>
            <svg-icon name="export"></svg-icon>
          </template>
          导出
        </n-button>
        <n-button v-else @click="e => emit('submit', e)">
          <template #icon>
            <svg-icon name="save"></svg-icon>
          </template>
          确定
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<script lang="ts" setup>
import startDrag from '@/utils/drag'
import SvgIcon from './SvgIcon.vue'
import { h } from 'vue'

interface PropsOps {
  modelValue: boolean
  title: string
  footer?: boolean
  export?: boolean
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
  footer: true,
  export: false
})
const emit = defineEmits(['update:modelValue', 'submit', 'export'])
watch(
  () => props.modelValue,
  bool => {
    emit('update:modelValue', bool)
    if (bool) {
      nextTick(() => {
        const oBox = document.getElementById('basic-modal')
        const oBar = document.getElementById('basic-modal-bar')
        startDrag(oBar, oBox)
      })
    }
  }
)
const maskClick = () => emit('update:modelValue', false)
</script>

<style scoped></style>
