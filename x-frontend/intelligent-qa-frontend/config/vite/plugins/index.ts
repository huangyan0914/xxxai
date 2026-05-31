/**
 * @name createVitePlugins
 * @description 封装plugins数组统一调用
 */
import { PluginOption, Plugin } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueSetupExtend from 'vite-plugin-vue-setup-extend'
import WindiCSS from 'vite-plugin-windicss'
import { AutoRegistryComponents } from './component'
import { AutoImportDeps } from './autoImport'
import { ConfigVisualizerConfig } from './visualizer'
import { ConfigCompressPlugin } from './compress'
import { ConfigPagesPlugin } from './pages'
import { ConfigRestartPlugin } from './restart'
import { ConfigProgressPlugin } from './progress'
import { ConfigImageminPlugin } from './imagemin'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import OptimizationPersist from 'vite-plugin-optimize-persist'
import PkgConfig from 'vite-plugin-package-config'
import legacy from '@vitejs/plugin-legacy'
import path from 'path'
export function createVitePlugins() {
  const vitePlugins: (Plugin | PluginOption | Plugin[])[] = [
    // vue支持
    vue(),
    // JSX支持
    vueJsx(),
    // 递归组件setup语法糖组件名支持，vite 会根据组件的文件名自动生成组件名，但是需要自定义的组件名时，就很不方便
    vueSetupExtend(),
    WindiCSS(),
    // 配置SvgIcon
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/icons')],
      symbolId: 'icon-[dir]-[name]'
    }),
    PkgConfig(),
    OptimizationPersist(), // 资源预构建
    legacy({
      // 低版本浏览器兼容
      targets: ['chrome 69'],
      additionalLegacyPolyfills: ['regenerator-runtime/runtime'],
      modernPolyfills: true
    })
  ]

  // 自动按需引入组件
  vitePlugins.push(AutoRegistryComponents())

  // 自动按需引入依赖
  vitePlugins.push(AutoImportDeps())

  // rollup-plugin-visualizer可视化显示包大小/node_modules/.cache/visualizer/stats.html
  vitePlugins.push(ConfigVisualizerConfig() as Plugin)

  // 开启.gz压缩  rollup-plugin-gzip
  vitePlugins.push(ConfigCompressPlugin())

  // 自动生成路由
  // vitePlugins.push(ConfigPagesPlugin())

  // 监听配置文件改动重启
  vitePlugins.push(ConfigRestartPlugin())

  // 图片压缩 https://github.com/2537178246/Vue3-Template-Imagemin.git
  vitePlugins.push(ConfigImageminPlugin())

  // 构建时显示进度条
  vitePlugins.push(ConfigProgressPlugin())

  // windCSS
  vitePlugins.push(WindiCSS())

  return vitePlugins
}
