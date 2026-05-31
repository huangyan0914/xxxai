/**
 * @name ConfigPagesPlugin
 * @description 动态生成路由
 * 优先是自己手写的路由
 */
import Pages from 'vite-plugin-pages'
export const ConfigPagesPlugin = () => {
  return Pages({
    pagesDir: [{ dir: 'src/views', baseRoute: '' }], // 需要生成路由的文件目录
    extensions: ['vue', 'md'],
    exclude: ['**/components/*.vue'], // 排除在外的目录，即不将所有 components 目录下的 .vue 文件生成路由
    nuxtStyle: true
  })
}
