<template>
  <LifecycleWorkspacePanel :module-key="moduleKey" />
</template>

<script setup lang="ts">
import LifecycleWorkspacePanel from '@/views/modules/shared/LifecycleWorkspacePanel.vue'
import type { ModuleKey } from '@/views/modules/shared/types'

withDefaults(defineProps<{ moduleKey?: ModuleKey }>(), {
  moduleKey: 'models'
})
</script>
