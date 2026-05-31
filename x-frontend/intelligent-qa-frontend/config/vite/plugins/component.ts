/**
 * @name  AutoRegistryComponents
 * @description 按需加载，自动引入组件
 */
import Components from 'unplugin-vue-components/vite'
import {
  NaiveUiResolver,
  VueUseComponentsResolver
} from 'unplugin-vue-components/resolvers'

export const AutoRegistryComponents = () => {
  return Components({
    // dirs: ['src/components'],
    extensions: ['vue', 'md'],
    deep: true,
    dts: 'types/components.d.ts', // 配置需要默认导入的自定义组件文件夹，该文件夹下的所有组件都会自动 import
    directoryAsNamespace: false,
    globalNamespaces: [],
    directives: true,
    include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
    exclude: [/[\\/]node_modules[\\/]/, /[\\/]\.git[\\/]/, /[\\/]\.nuxt[\\/]/],
    resolvers: [NaiveUiResolver(), VueUseComponentsResolver()]
  })
}
