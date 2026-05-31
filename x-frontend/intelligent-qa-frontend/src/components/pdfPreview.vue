<template>
  <vue-office-pdf
    :src="props.pdf"
    style="height: 100vh"
    @rendered="renderedHandler"
    @error="errorHandler"
  />
</template>

<script setup lang="ts">
//引入VueOfficePdf组件
import VueOfficePdf from '@vue-office/pdf'

const props = defineProps({
  pdf: {
    type: String,
    default: 'http://localhost:4000/test.pdf'
  }
})

function renderedHandler() {
  console.log('渲染完成')
}
function errorHandler() {
  console.log('渲染失败')
}
</script>
