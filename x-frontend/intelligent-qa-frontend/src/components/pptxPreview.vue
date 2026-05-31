<template>
  <vue-office-pptx
    :src="ppt"
    style="height: 100vh"
    @rendered="renderedHandler"
    @error="errorHandler"
  />
</template>

<script setup lang="ts">
import VueOfficePptx from '@vue-office/pptx'

const props = defineProps({
  ppt: {
    type: String,
    default: 'http://localhost:4000/test.pptx'
  }
})

function renderedHandler() {
  console.log('渲染完成')
}
function errorHandler() {
  console.log('渲染失败')
}
</script>
