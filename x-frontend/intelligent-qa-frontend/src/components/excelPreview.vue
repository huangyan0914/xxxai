<template>
  <vue-office-excel
    :src="excel"
    style="height: 100vh"
    @rendered="renderedHandler"
    @error="errorHandler"
  />
</template>

<script setup lang="ts">
//引入VueOfficeExcel组件
import VueOfficeExcel from '@vue-office/excel'
//引入相关样式
import '@vue-office/excel/lib/index.css'

const props = defineProps({
  excel: {
    type: String,
    default: 'http://localhost:4000/test.xlsx'
  }
})

function renderedHandler() {
  console.log('渲染完成')
}
function errorHandler() {
  console.log('渲染失败')
}
// export default {
//   components: {
//     VueOfficeExcel
//   },
//   data() {
//     return {
//       excel: 'http://localhost:4000/test.xlsx' //设置文档地址
//     }
//   },
//   methods: {
//     renderedHandler() {
//       console.log('渲染完成')
//     },
//     errorHandler() {
//       console.log('渲染失败')
//     }
//   }
// }
</script>
