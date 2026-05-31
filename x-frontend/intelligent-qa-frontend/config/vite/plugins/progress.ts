/**
 * @name ConfigProgressPlugin
 * @description 构建显示进度条, 在打包时展示进度条的插件
 */

import progress from 'vite-plugin-progress'
export const ConfigProgressPlugin = () => {
  return progress()
}
