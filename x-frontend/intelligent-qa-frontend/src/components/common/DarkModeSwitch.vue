<template>
  <div class="flex-center text-18px cursor-pointer" @click="handleSwitch">
    <span v-if="darkMode" class="h-25px">
      <n-icon size="25px">
        <Sunny />
      </n-icon>
    </span>
    <span v-else class="h-25px">
      <n-icon size="25px">
        <SunnyOutline />
      </n-icon>
    </span>
    <!-- <icon-mdi-moon-waning-crescent v-if="darkMode" />
    <icon-mdi-white-balance-sunny v-else /> -->
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Sunny, SunnyOutline } from '@vicons/ionicons5'
interface Props {
  /** 暗黑模式 */
  dark?: boolean
}

interface Emits {
  (e: 'update:dark', darkMode: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  dark: false
})

const emit = defineEmits<Emits>()

const darkMode = computed({
  get() {
    return props.dark
  },
  set(newValue: boolean) {
    emit('update:dark', newValue)
  }
})

function handleSwitch() {
  darkMode.value = !darkMode.value
}
</script>

<style scoped></style>
