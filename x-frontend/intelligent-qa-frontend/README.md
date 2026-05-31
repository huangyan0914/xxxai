# 数据服务

## 前端脚手架的技术栈：
   Vue 3 + TypeScript + Vite

## 已完善
- svg压缩合并

## node>=14 
- 配置式
- 国际化
- 主题色
- 响应式
- windicss
- 骨架屏
- less

## 脚手架集成

1. setup语法糖组件名支持
2. 自动按需引入组件
3. 自动按需引入依赖
4. 自动生成路由
5. gz压缩
6. 监听配置文件改动重启
7. 构建时显示进度条
8. 可视化显示包大小
9. css原子类
10. 资源预构建, 提升首次页面加载速度


## 提交代码规范

1. npm install -g commitizen cz-conventional-changelog

2. 配置文件
> mac:      `echo '{ "path": "cz-conventional-changelog" }' > ~/.czrc ` 
> windows:  `echo { "path": "cz-conventional-changelog" } > C:\Users\Administrator`

3. 使用
先暂存本地，之后 git commit 替换 git cz


## 代码生成
vscode插件 `EasyCode`

## 微应用
配置cetc-micro-app插件
demo：src/micro-app
  tips1: 微应用开发过程中，为避免storage冲突-建议使用脚手架封装的storage。
  tips2: 在使用$bus进行数据通信，注意先后顺序。先on再emit。

## 配置5A-SDK，vite-auth