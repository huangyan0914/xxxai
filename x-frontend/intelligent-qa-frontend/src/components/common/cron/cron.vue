<template>
  <div class="cron" :val="value_">
    <el-tabs v-model="data.activeName">
      <el-tab-pane label="秒" name="s">
        <second-and-minute
          v-model:modelValue="data.sVal"
          :label="'秒'"
        ></second-and-minute>
      </el-tab-pane>
      <el-tab-pane label="分" name="m">
        <second-and-minute
          v-model:modelValue="data.mVal"
          :label="'分'"
        ></second-and-minute>
      </el-tab-pane>
      <el-tab-pane label="时" name="h">
        <hour v-model:modelValue="data.hVal" lable="时"></hour>
      </el-tab-pane>
      <el-tab-pane label="日" name="d">
        <day v-model:modelValue="data.dVal" lable="日"></day>
      </el-tab-pane>
      <el-tab-pane label="月" name="month">
        <month v-model:modelValue="data.monthVal" lable="月"></month>
      </el-tab-pane>
      <el-tab-pane label="周" name="week">
        <week v-model:modelValue="data.weekVal" lable="周"></week>
      </el-tab-pane>
      <el-tab-pane label="年" name="year">
        <year v-model:modelValue="data.yearVal" lable="年"></year>
      </el-tab-pane>
    </el-tabs>
    <!-- table -->
    <el-table :data="tableData" size="small" border style="width: 100%">
      <el-table-column prop="sVal" label="秒" width="70"> </el-table-column>
      <el-table-column prop="mVal" label="分" width="70"> </el-table-column>
      <el-table-column prop="hVal" label="时" width="70"> </el-table-column>
      <el-table-column prop="dVal" label="日" width="70"> </el-table-column>
      <el-table-column prop="monthVal" label="月" width="70"> </el-table-column>
      <el-table-column prop="weekVal" label="周" width="70"> </el-table-column>
      <el-table-column prop="yearVal" label="年"> </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ElTabs, ElTabPane, ElTable, ElTableColumn } from 'element-plus'
import SecondAndMinute from './secondAndMinute.vue'
import hour from './hour.vue'
import day from './day.vue'
import month from './month.vue'
import week from './week.vue'
import year from './year.vue'

const data = reactive({
  activeName: 'd',
  sVal: '59',
  mVal: '59',
  hVal: '23',
  dVal: '01',
  monthVal: '*',
  weekVal: '?',
  yearVal: '*'
})

interface PropValue {
  modelValue: string
}

const props = withDefaults(defineProps<PropValue>(), {
  modelValue: '59 59 23 * * ? *'
})
updateVal(props.modelValue)
watch(
  () => props.modelValue,
  value => {
    updateVal(value)
  }
)
function updateVal(val: string) {
  let array = val.split(' ')
  data.sVal = array[0]
  data.mVal = array[1]
  data.hVal = array[2]
  data.dVal = array[3]
  data.monthVal = array[4]
  data.weekVal = array[5]
  data.yearVal = array[6]
}
const emit = defineEmits(['update:modelValue'])
const value_ = computed({
  get() {
    if (!data && !data.weekVal) {
      return ''
    }
    if (data.dVal === '?' && data.weekVal === '?') {
      window?.$message?.error('日期与星期不可以同时为“不指定”')
    }
    if (data.dVal !== '?' && data.weekVal !== '?') {
      window?.$message?.error('日期与星期必须有一个为“不指定”')
    }
    let v = `${data.sVal} ${data.mVal} ${data.hVal} ${data.dVal} ${data.monthVal} ${data.weekVal} ${data.yearVal}`
    if (v !== props.modelValue) {
      emit('update:modelValue', v)
    }
    return v
  },
  set() {}
})
const tableData = computed({
  get() {
    return [
      {
        sVal: data.sVal,
        mVal: data.mVal,
        hVal: data.hVal,
        dVal: data.dVal,
        monthVal: data.monthVal,
        weekVal: data.weekVal,
        yearVal: data.yearVal
      }
    ]
  },
  set() {}
})
</script>

<style lang="css">
@import url('element-plus/dist/index.css');

.cron {
  text-align: left;
  padding: 10px;
  background: #fff;
  border: 1px solid #dcdfe6;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.12), 0 0 6px 0 rgba(0, 0, 0, 0.04);
}
</style>
