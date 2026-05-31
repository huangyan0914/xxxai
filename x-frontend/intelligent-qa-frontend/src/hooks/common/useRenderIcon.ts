import { h, Component } from 'vue'
import { NIcon } from 'naive-ui'
export default function useRenderIcon() {
  function renderIcon(icon: Component) {
    return () => h(NIcon, null, { default: () => h(icon) })
  }
  return {
    renderIcon
  }
}
