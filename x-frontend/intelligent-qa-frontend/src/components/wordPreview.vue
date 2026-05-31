<template>
  <vue-office-docx
    :src="props.docx"
    style="height: 100vh"
    @rendered="rendered"
    :zoom="1.5"
  />
</template>

<script setup lang="ts">
//引入VueOfficeDocx组件
import VueOfficeDocx from '@vue-office/docx'
//引入相关样式
import '@vue-office/docx/lib/index.css'

const props = defineProps({
  docx: {
    type: String,
    default: 'http://localhost:4000/test.docx'
  }
})

function rendered() {
  console.log('渲染完成')
}
</script>
