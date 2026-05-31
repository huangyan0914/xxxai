<template>
  <div class="wh-full flex flex-col">
    <div class="h-178px">
      <n-grid x-gap="24" :cols="5" class="h-full">
        <n-gi v-for="(item, index) in topTotalInfo" :key="index">
          <div class="totalInfo wh-full pt-40px px-16px">
            <div class="text-40px tracking-6px truncate">
              {{ numberFormatter(item.total) }}
            </div>
            <div class="mt-16px text-16px">
              <span>周环比</span>
              <n-icon
                size="16"
                :color="item.week >= 0 ? '#fd2e2e' : '#00da8a'"
                class="mx-10px"
              >
                <CaretUp v-if="item.week >= 0" /> <CaretDown v-else />
              </n-icon>
              <span class="mr-40px">{{ Math.abs(item.week) }}</span>
              <span>日环比</span>
              <n-icon
                size="16"
                :color="item.day >= 0 ? '#fd2e2e' : '#00da8a'"
                class="mx-10px"
              >
                <CaretUp v-if="item.day >= 0" /> <CaretDown v-else />
              </n-icon>
              <span class="mr-40px">{{ Math.abs(item.day) }}</span>
            </div>
            <div class="mt-16px text-16px font-bold">{{ item.name }}</div>
          </div>
        </n-gi>
      </n-grid>
    </div>

    <div class="flex-1">
      <n-grid x-gap="24" :cols="2" class="h-1/2 pt-24px">
        <n-gi
          class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)] bg-[#F1F1F1]"
        >
          <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
            <span class="leading-40px ml-8px">按资源类型数量分布</span>
          </div>
          <div class="wh-full" ref="bookBarRef"></div>
        </n-gi>
        <n-gi
          class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)] bg-[#F1F1F1]"
        >
          <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
            <span class="leading-40px ml-8px">按标签资源数分布</span>
          </div>
          <div class="wh-full" ref="ccBookBarRef"></div>
        </n-gi>
      </n-grid>
      <n-grid x-gap="24" :cols="2" class="h-1/2 pt-24px">
        <n-gi
          class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)] bg-[#F1F1F1]"
        >
          <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
            <span class="leading-40px ml-8px">按已学习人数排行</span>
          </div>
          <div class="wh-full" ref="PPTlecture"></div>
        </n-gi>
      </n-grid>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { numberFormatter } from '@/utils'
import { CaretDown, CaretUp } from '@vicons/ionicons5'
import { type ECOption, useEcharts } from '@/composables'
import { getResourceStatistics } from '@/api/overview'

const topTotalInfo = ref<any[]>([
  { week: 0, day: 0, total: 0, name: '-' },
  { week: 0, day: 0, total: 0, name: '-' },
  { week: 0, day: 0, total: 0, name: '-' },
  { week: 0, day: 0, total: 0, name: '-' },
  { week: 0, day: 0, total: 0, name: '-' }
])

onMounted(async () => {
  try {
    const res: any = await getResourceStatistics()
    const data = res?.data || res || {}

    if (data.byFileType && Array.isArray(data.byFileType)) {
      const infos = data.byFileType.slice(0, 5).map((item: any) => ({
        week: 0,
        day: 0,
        total: item.count || 0,
        name: (item.fileTypeName || '未知') + '总数'
      }))
      // Pad array if less than 5
      while (infos.length < 5) {
        infos.push({ week: 0, day: 0, total: 0, name: '-' })
      }
      topTotalInfo.value = infos

      subjectBarOptions.value.xAxis[0].data = data.byFileType.map(
        (item: any) => item.fileTypeName || '未知'
      )
      subjectBarOptions.value.series.data = data.byFileType.map(
        (item: any) => item.count || 0
      )
      subjectBarOptions.value.title.text = ''
      subjectBarOptions.value.legend.data = ['资源数量']
      subjectBarOptions.value.series.name = '资源数量'
      updateSubjectBar()
    }

    if (data.byTag && Array.isArray(data.byTag)) {
      ccBookBarOptions.value.xAxis[0].data = data.byTag.map(
        (item: any) => item.tagName || '未知'
      )
      ccBookBarOptions.value.series.data = data.byTag.map(
        (item: any) => item.count || 0
      )
      ccBookBarOptions.value.title.text = ''
      ccBookBarOptions.value.legend.data = ['资源数']
      ccBookBarOptions.value.series.name = '资源数'
      updateCcBookBarBar()
    }
    if (data.byLearnedUserCount && Array.isArray(data.byLearnedUserCount)) {
      PPTlectureBarOptions.value.xAxis[0].data = data.byLearnedUserCount.map(
        (item: any) => item.fileName || '未知'
      )
      PPTlectureBarOptions.value.series.data = data.byLearnedUserCount.map(
        (item: any) => item.learnedUserCount || 0
      )
      PPTlectureBarOptions.value.title.text = ''
      PPTlectureBarOptions.value.legend.data = ['学习人数']
      PPTlectureBarOptions.value.series.name = '学习人数'
      updatePPTlectureBarBar()
    }
  } catch (e) {}
})

// 柱状图option
const subjectBarOptions = ref<ECOption>({
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
  legend: {
    data: ['电子书籍']
  },
  grid: {
    left: '5%',
    right: '5%',
    bottom: '10%',
    top: '10%'
    // containLabel: true
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
    name: '电子书籍',
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

const ccBookBarOptions = ref<ECOption>({
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
  legend: {
    data: ['cc书籍']
  },
  grid: {
    left: '5%',
    right: '5%',
    bottom: '10%',
    top: '10%'
    // containLabel: true
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
    name: 'cc书籍',
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

const PPTlectureBarOptions = ref<ECOption>({
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
  legend: {
    data: ['电子课件']
  },
  grid: {
    left: '5%',
    right: '5%',
    bottom: '10%',
    top: '10%'
    // containLabel: true
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
    name: '电子课件',
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

// 柱状图初始化
const { domRef: bookBarRef, update: updateSubjectBar } =
  useEcharts(subjectBarOptions)
const { domRef: ccBookBarRef, update: updateCcBookBarBar } =
  useEcharts(ccBookBarOptions)
const { domRef: PPTlecture, update: updatePPTlectureBarBar } =
  useEcharts(PPTlectureBarOptions)
</script>

<style lang="less" scoped>
.totalInfo {
  background-image: url('@/assets/imgs/bgTopTotalInfo.png');
  background-size: cover;
  background-position: center; /* 确保图片居中 */
  background-repeat: no-repeat; /* 防止图片重复 */
}
.title {
  position: relative;
}
.title::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: #007eff;
  display: inline-block;
  top: 12px;
  position: absolute;
}
</style>
