/**
 * @name AutoImportDeps
 * @description 按需加载，自动引入
 *
 *  import { ref } from 'vue'
 *  import { useRouter } from 'vue-router'
 *  不用手动引入库文件的方法或变量
 */

import AutoImport from 'unplugin-auto-import/vite'
import { NaiveUiResolver } from 'unplugin-vue-components/resolvers'

export const AutoImportDeps = () => {
  return AutoImport({
    dts: 'types/auto-imports.d.ts', // 生成 `auto-import.d.ts` 全局声明
    imports: [
      // 自动导入vue、pinia和vue-router相关函数
      'vue',
      'pinia',
      'vue-router',
      {
        '@vueuse/core': [],
        '@/hooks/useDict': ['useDict']
      }
    ],
    resolvers: [NaiveUiResolver()]
  })
}
