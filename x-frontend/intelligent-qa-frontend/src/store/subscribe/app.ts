/** 订阅app store */
import { useAppStore } from '../modules'
import { setLocale } from '@/utils'

export default function subscribeAppStore() {
  const app = useAppStore()
  // 监听语言切换
  const stopLocale = watch(
    () => app.lang,
    newValue => {
      setLocale(newValue)
    },
    { immediate: true }
  )

  onUnmounted(() => {
    stopLocale()
  })
}
