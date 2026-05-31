<template>
  <div :val="value_">
    <div>
      <el-radio v-model="data.type" label="1" size="mini" border>每日</el-radio>
    </div>
    <div>
      <el-radio v-model="data.type" label="5" size="mini" border
        >不指定</el-radio
      >
    </div>
    <div>
      <el-radio v-model="data.type" label="2" size="mini" border>周期</el-radio>
      <span style="margin-left: 10px; margin-right: 5px">从</span>
      <el-input-number
        @change="data.type = '2'"
        v-model="data.cycle.start"
        :min="1"
        :max="31"
        size="mini"
        style="width: 100px"
      ></el-input-number>
      <span style="margin-left: 5px; margin-right: 5px">至</span>
      <el-input-number
        @change="data.type = '2'"
        v-model="data.cycle.end"
        :min="2"
        :max="31"
        size="mini"
        style="width: 100px"
      ></el-input-number>
      日
    </div>
    <div>
      <el-radio v-model="data.type" label="3" size="mini" border>循环</el-radio>
      <span style="margin-left: 10px; margin-right: 5px">从</span>
      <el-input-number
        @change="data.type = '3'"
        v-model="data.loop.start"
        :min="1"
        :max="31"
        size="mini"
        style="width: 100px"
      ></el-input-number>
      <span style="margin-left: 5px; margin-right: 5px">日开始，每</span>
      <el-input-number
        @change="data.type = '3'"
        v-model="data.loop.end"
        :min="1"
        :max="31"
        size="mini"
        style="width: 100px"
      ></el-input-number>
      日执行一次
    </div>
    <div>
      <el-radio v-model="data.type" label="8" size="mini" border
        >工作日</el-radio
      >
      <span style="margin-left: 10px; margin-right: 5px">本月</span>
      <el-input-number
        @change="data.type = '8'"
        v-model="data.work"
        :min="1"
        :max="7"
        size="mini"
        style="width: 100px"
      ></el-input-number>
      号，最近的工作日
    </div>
    <div>
      <el-radio v-model="data.type" label="6" size="mini" border
        >本月最后一天</el-radio
      >
    </div>
    <div>
      <el-radio v-model="data.type" label="4" size="mini" border>指定</el-radio>
      <el-checkbox-group v-model="data.appoint">
        <div v-for="i in 4" :key="i" style="line-height: 25px">
          <!-- <span v-for="j in 10" class="px-10px"> -->
          <el-checkbox
            v-for="j in 10"
            @change="data.type = '4'"
            v-show="
              parseInt(i - 1 + '' + (j - 1)) < 32 && !(i === 1 && j === 1)
            "
            :key="j"
            :label="i - 1 + '' + (j - 1)"
            >{{ i - 1 + '' + (j - 1) }}</el-checkbox
          >
          <!-- </span> -->
        </div>
      </el-checkbox-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ElRadio,
  ElInputNumber,
  ElCheckboxGroup,
  ElCheckbox
} from 'element-plus'

const data = reactive({
  type: '1', // 类型
  cycle: {
    // 周期
    start: 0,
    end: 0
  },
  loop: {
    // 循环
    start: 0,
    end: 0
  },
  week: {
    // 指定周
    start: 0,
    end: 0
  },
  work: 0,
  last: 0,
  appoint: [] // 指定
})
interface PropValue {
  modelValue: string
}
const props = withDefaults(defineProps<PropValue>(), {
  modelValue: '*'
})
watch(
  () => props.modelValue,
  value => {
    updateVal(value)
  }
)

function updateVal(val) {
  if (!val) {
    return
  }
  if (val === '?') {
    data.type = '5'
  } else if (val.indexOf('-') !== -1) {
    // 2周期
    if (val.split('-').length === 2) {
      data.type = '2'
      data.cycle.start = val.split('-')[0]
      data.cycle.end = val.split('-')[1]
    }
  } else if (val.indexOf('/') !== -1) {
    // 3循环
    if (val.split('/').length === 2) {
      data.type = '3'
      data.loop.start = val.split('/')[0]
      data.loop.end = val.split('/')[1]
    }
  } else if (val.indexOf('*') !== -1) {
    // 1每
    data.type = '1'
  } else if (val.indexOf('L') !== -1) {
    // 6最后
    data.type = '6'
    data.last = val.replace('L', '')
  } else if (val.indexOf('#') !== -1) {
    // 7指定周
    if (val.split('#').length === 2) {
      data.type = '7'
      data.week.start = val.split('#')[0]
      data.week.end = val.split('#')[1]
    }
  } else if (val.indexOf('W') !== -1) {
    // 8工作日
    data.type = '8'
    data.work = val.replace('W', '')
  } else {
    // *
    data.type = '4'
    data.appoint = val.split(',')
  }
}
updateVal(props.modelValue)
const emit = defineEmits(['update:modelValue'])
const value_ = computed({
  get() {
    let result = []
    switch (data.type) {
      case '1': // 每秒
        result.push('*')
        break
      case '2': // 周期
        result.push(`${data.cycle.start}-${data.cycle.end}`)
        break
      case '3': // 循环
        result.push(`${data.loop.start}/${data.loop.end}`)
        break
      case '4': // 指定
        result.push(data.appoint.join(','))
        break
      case '6': // 最后
        result.push(`${data.last === 0 ? '' : data.last}L`)
        break
      case '7': // 指定周
        result.push(`${data.week.start}#${data.week.end}`)
        break
      case '8': // 工作日
        result.push(`${data.work}W`)
        break
      default: // 不指定
        result.push('?')
        break
    }
    emit('update:modelValue', result.join(''))
    return result.join('')
  },
  set() {}
})
</script>

<style lang="css">
@import url('element-plus/dist/index.css');

/* .el-checkbox+.el-checkbox {
  margin-left: 10px;
} */
</style>
