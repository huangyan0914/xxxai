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
            <span class="leading-40px ml-8px">教培项目总览</span>
          </div>
          <div class="wh-full" ref="subjectBarRef"></div>
        </n-gi>
        <n-gi
          class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)] bg-[#F1F1F1]"
        >
          <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
            <span class="leading-40px ml-8px">课件总览</span>
          </div>
          <div class="flex-1 p-24px">
            <n-grid x-gap="16" :cols="3" class="h-full">
              <n-gi
                class="border border-[#bebebe] flex flex-col"
                v-for="(item, index) in kejianTotal"
                :key="index"
              >
                <div class="flex-1 bg-[#BEBEBE] flex items-center">
                  <img
                    class="m-auto"
                    src="@/assets/imgs/kejianIcon.png"
                    alt=""
                  />
                </div>
                <div class="h-96px bg-[#ebebeb] p-16px">
                  <div class="leading-32px font-bold">{{ item.name }}</div>
                  <div class="flex flex-row text-14px mt-16px">
                    <div class="w-1/2 flex justify-start">
                      <svg-icon
                        name="learners"
                        class="wh-16px mr-5px"
                      ></svg-icon>
                      {{ item.numberOfLearners }}人已学
                    </div>
                    <div class="w-1/2 flex justify-end">
                      <svg-icon
                        name="likeIcon"
                        class="wh-16px mr-5px"
                      ></svg-icon>
                      {{ item.numberOfLikes }}
                    </div>
                  </div>
                </div>
              </n-gi>
            </n-grid>
          </div>
        </n-gi>
      </n-grid>
      <n-grid x-gap="24" :cols="2" class="h-1/2 pt-24px">
        <n-gi
          class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)] bg-[#F1F1F1]"
        >
          <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
            <span class="leading-40px ml-8px">ZB数据统计</span>
          </div>
          <div class="wh-full" ref="zbDataBarRef"></div>
        </n-gi>
        <n-gi
          class="wh-full flex flex-col border border-[#bebebe] shadow-md shadow-[#bebebe)] bg-[#F1F1F1]"
        >
          <div class="title pl-24px bg-[#2F4050] h-40px text-[#e5e5e5]">
            <span class="leading-40px ml-8px">视频总览</span>
          </div>
          <div class="flex-1 p-24px">
            <n-grid x-gap="16" :cols="3" class="h-full">
              <n-gi
                class="border border-[#bebebe] flex flex-col"
                v-for="(item, index) in videoTotal"
                :key="index"
              >
                <div class="flex-1 bg-[#BEBEBE] flex items-center">
                  <img
                    class="m-auto"
                    src="@/assets/imgs/kejianIcon.png"
                    alt=""
                  />
                </div>
                <div class="h-96px bg-[#ebebeb] p-16px">
                  <div class="leading-32px font-bold">{{ item.name }}</div>
                  <div class="flex flex-row text-14px mt-16px">
                    <div class="w-1/2 flex justify-start">
                      <svg-icon
                        name="learners"
                        class="wh-16px mr-5px"
                      ></svg-icon>
                      {{ item.numberOfLearners }}人已学
                    </div>
                    <div class="w-1/2 flex justify-end">
                      <svg-icon
                        name="likeIcon"
                        class="wh-16px mr-5px"
                      ></svg-icon>
                      {{ item.numberOfLikes }}
                    </div>
                  </div>
                </div>
              </n-gi>
            </n-grid>
          </div>
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
import { getOverview } from '@/api/overview'

const topTotalInfo = ref([
  {
    week: 0,
    day: 0,
    total: 0,
    name: '项目总数'
  },
  {
    week: 0,
    day: 0,
    total: 0,
    name: '进行中项目数'
  },
  {
    week: 0,
    day: 0,
    total: 0,
    name: '科目总数'
  },
  {
    week: 0,
    day: 0,
    total: 0,
    name: '资源总数'
  },
  {
    week: 0,
    day: 0,
    total: 0,
    name: '已学习人数'
  }
])

onMounted(async () => {
  try {
    const res: any = await getOverview()
    const data = res?.data || res || {}
    topTotalInfo.value[0].total = data.projectCount || 0
    topTotalInfo.value[1].total = data.ongoingProjectCount || 0
    topTotalInfo.value[2].total = data.subjectCount || 0
    topTotalInfo.value[3].total = data.resourceCount || 0
    topTotalInfo.value[4].total = data.learnedUserCountTotal || 0
  } catch (e) {
    console.error('Failed to load overview data', e)
  }
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
    data: ['教培项目']
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
    name: '教培项目',
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
const zbDataBaOptions = ref<ECOption>({
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
    data: ['ZB数据统计']
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
    name: 'ZB数据统计',
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
const { domRef: subjectBarRef, update: updateSubjectBar } =
  useEcharts(subjectBarOptions)
const { domRef: zbDataBarRef, update: updateZbDataBarBar } =
  useEcharts(zbDataBaOptions)

const kejianTotal = ref([
  {
    name: '课件名称',
    numberOfLearners: 100,
    numberOfLikes: 10
  },
  {
    name: '课件名称',
    numberOfLearners: 100,
    numberOfLikes: 10
  },
  {
    name: '课件名称',
    numberOfLearners: 100,
    numberOfLikes: 10
  }
])
const videoTotal = ref([
  {
    name: '视频名称',
    numberOfLearners: 100,
    numberOfLikes: 10
  },
  {
    name: '视频名称',
    numberOfLearners: 100,
    numberOfLikes: 10
  },
  {
    name: '视频名称',
    numberOfLearners: 100,
    numberOfLikes: 10
  }
])
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
