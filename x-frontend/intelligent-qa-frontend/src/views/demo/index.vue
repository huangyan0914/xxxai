<template>
  <div class="wh-full bg-[#f1f1f1] flex flex-col">
    <div class="w-full mb-15px">
      <h1>tag</h1>
      <n-tag> 爱在西元前 </n-tag>
      <n-tag type="success"> 不该 </n-tag>
      <n-tag type="warning"> 超人不会飞 </n-tag>
      <n-tag type="error"> 手写的从前 </n-tag>
      <n-tag type="info"> 哪里都是你 </n-tag>
      <br />

      <h1>form表单</h1>
      <n-form
        ref="formRef"
        inline
        :label-width="'auto'"
        label-placement="left"
        :model="searchForm"
        class="flex justify-start"
      >
        <n-form-item label="任务名称">
          <n-input
            v-model:value="searchForm.name"
            placeholder="任务名称"
            clearable
          />
        </n-form-item>
        <n-form-item label="任务类型">
          <n-select
            class="w-179px"
            v-model:value="searchForm.type"
            placeholder="请选择"
            :options="selectOption"
            :label-field="'label'"
            :value-field="'value'"
            clearable
          ></n-select>
        </n-form-item>

        <n-form-item class="inline-block">
          <n-button attr-type="button">
            <template #icon>
              <svg-icon name="query"></svg-icon>
              <!-- <n-icon>
                <cash-icon />
              </n-icon> -->
            </template>
            查询
          </n-button>
          <n-button attr-type="button" class="ml-10px">
            <template #icon>
              <svg-icon name="reset"></svg-icon>
            </template>
            重置
          </n-button>
        </n-form-item>
      </n-form>

      <br />
      <h1>按钮</h1>
      <div>
        <n-button attr-type="button" class="ml-10px">
          <template #icon>
            <svg-icon name="add"></svg-icon>
          </template>
          新建
        </n-button>
        <n-button attr-type="button" class="ml-10px">
          <template #icon>
            <svg-icon name="import"></svg-icon>
          </template>
          导入
        </n-button>
        <n-button attr-type="button" class="ml-10px">
          <template #icon>
            <svg-icon name="export"></svg-icon>
          </template>
          导出
        </n-button>
        <n-button attr-type="button" class="ml-10px" type="error" secondary>
          <template #icon>
            <svg-icon name="delete"></svg-icon>
          </template>
          删除
        </n-button>
      </div>

      <h1>
        对话框
        <n-button
          attr-type="button"
          class="ml-10px"
          @click="
            layoutDialogOptions.showModel = !layoutDialogOptions.showModel
          "
        >
          <template #icon>
            <svg-icon name="add"></svg-icon>
          </template>
          打开对话框
        </n-button>
      </h1>
      <layoutDialog
        class="w-2/3"
        @submit="submit"
        v-model:modelValue="layoutDialogOptions.showModel"
        :title="layoutDialogOptions.title"
        :footer="layoutDialogOptions.footer"
      >
        <n-form
          ref="formRef"
          :label-width="'auto'"
          label-placement="left"
          :model="searchForm"
        >
          <n-form-item label="任务名称">
            <n-input
              v-model:value="searchForm.name"
              placeholder="任务名称"
              clearable
            />
          </n-form-item>
          <n-form-item label="任务类型">
            <n-select
              v-model:value="searchForm.type"
              placeholder="请选择"
              :options="selectOption"
              :label-field="'label'"
              :value-field="'value'"
              clearable
            ></n-select>
          </n-form-item>
        </n-form>
      </layoutDialog>

      <div class="w-200px h-200px inline-block mr-20px" ref="lineRef"></div>
      <div class="w-200px h-200px inline-block mr-20px" ref="barRef"></div>
      <div class="w-200px h-200px inline-block mr-20px" ref="pieRef"></div>
      <div class="w-200px h-200px inline-block mr-20px" ref="raderRef"></div>
      <!-- 123 -->
    </div>
    <n-data-table
      :columns="columns"
      :data="list"
      :pagination="pagination"
      :bordered="true"
      :loading="loading"
      :single-line="false"
      :single-column="false"
      class="flex-1 border-[#bebebe]"
      flex-height
    />
  </div>
</template>

<script setup lang="ts">
import { createColumns, Song } from './index'
import usePage from '@/hooks/basic-page/index'
import { ref } from 'vue'
import { type ECOption, useEcharts } from '@/composables'

const lineOptions = ref<ECOption>({
  title: {
    text: ''
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#42BBF6'
      }
    }
  },
  grid: {
    left: '3%',
    right: '3%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [
    {
      type: 'category',
      //坐标轴两边留白,影响坐标轴标签和数据显示位置
      // boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    }
  ],
  yAxis: [
    {
      type: 'value'
    }
  ],
  series: {
    color: '#42BBF6',
    type: 'line',
    // barWidth: 20,
    // 平滑曲线
    smooth: true,
    // 数据堆叠
    stack: false,
    // 折线图数据阴影
    // areaStyle: {
    //   color: {
    //     type: 'linear',
    //     x: 0,
    //     y: 0,
    //     x2: 0,
    //     y2: 1,
    //     colorStops: [
    //       {
    //         offset: 0.25,
    //         color: '#42BBF6'
    //       },
    //       {
    //         offset: 1,
    //         color: '#fff'
    //       }
    //     ]
    //   }
    // },
    // emphasis: {
    //   focus: 'series'
    // },
    data: [150, 230, 224, 218, 135, 147, 260]
  }
}) as Ref<ECOption>
const barOptions = ref<ECOption>({
  title: {
    text: ''
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#42BBF6'
      }
    }
  },
  grid: {
    left: '3%',
    right: '3%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [
    {
      type: 'category',
      //坐标轴两边留白,影响坐标轴标签和数据显示位置
      // boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    }
  ],
  yAxis: [
    {
      type: 'value'
    }
  ],
  series: {
    color: '#42BBF6',
    type: 'bar',
    // 柱宽
    barWidth: 20,
    // 数据堆叠
    stack: false,
    // emphasis: {
    //   focus: 'series'
    // },
    data: [150, 230, 224, 218, 135, 147, 260]
  }
}) as Ref<ECOption>
const pieOptions = ref<ECOption>({
  title: {
    text: ''
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#42BBF6'
      }
    }
  },
  grid: {
    left: '3%',
    right: '3%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [
    {
      type: 'category',
      //坐标轴两边留白,影响坐标轴标签和数据显示位置
      // boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    }
  ],
  yAxis: [
    {
      type: 'value'
    }
  ],
  series: {
    color: '#42BBF6',
    type: 'bar',
    // 柱宽
    barWidth: 20,
    // 数据堆叠
    stack: false,
    // emphasis: {
    //   focus: 'series'
    // },
    data: [150, 230, 224, 218, 135, 147, 260]
  }
}) as Ref<ECOption>

const { domRef: lineRef, update: updateLine } = useEcharts(lineOptions)
const { domRef: barRef, update: updateBar } = useEcharts(barOptions)
const { domRef: pieRef, update: updatePie } = useEcharts(pieOptions)
// 更新echarts图
// updateLine(lineOptions.value)
// updateBar(barOptions.value)

// 查询表单
type SearchForm = {
  name: string
  type: string
}
const searchForm = ref<SearchForm>({
  name: '',
  type: ''
})
// 下拉选项
const selectOption = ref([
  {
    label: '张三',
    value: 'zhangsan'
  },
  {
    label: '李四',
    value: 'lisi'
  }
])

const layoutDialogOptions = ref({
  showModel: false,
  title: '详情',
  footer: true
})
function submit() {
  window.$message?.info('submit')
}

// 表格样例
function getSourceList() {
  return {
    list: [
      { no: 3, title: 'Wonderwall', length: '4:18' },
      { no: 4, title: "Don't Look Back in Anger", length: '4:48' },
      { no: 12, title: 'Champagne Supernova', length: '7:27' },
      { no: 3, title: 'Wonderwall', length: '4:18' },
      { no: 4, title: "Don't Look Back in Anger", length: '4:48' },
      { no: 12, title: 'Champagne Supernova', length: '7:27' },
      { no: 3, title: 'Wonderwall', length: '4:18' },
      { no: 4, title: "Don't Look Back in Anger", length: '4:48' },
      { no: 12, title: 'Champagne Supernova', length: '7:27' },
      { no: 3, title: 'Wonderwall', length: '4:18' },
      { no: 4, title: "Don't Look Back in Anger", length: '4:48' },
      { no: 12, title: 'Champagne Supernova', length: '7:27' }
    ],
    total: 300,
    page: 1,
    size: 10
  }
}
const { loading, list, reset, loadData, pagination } = usePage(getSourceList, {
  filterOption: searchForm
})

const columns = createColumns({
  pagination,
  play(row: Song) {
    window?.$message?.info(`Play ${row.title}`)
  }
})
</script>

<style></style>
