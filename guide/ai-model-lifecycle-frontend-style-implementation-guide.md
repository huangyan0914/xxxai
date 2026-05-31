# ai-model-lifecycle-frontend 风格对齐实施说明

## 结论
已完成 `x-frontend/ai-model-lifecycle-frontend` 的首轮实施，功能与交互保持不变，完成了：

- 页面与源码目录按业务模块拆分
- 大页面脚本拆出到 TypeScript 文件
- API 封装按业务域拆分
- 全局视觉风格向 `TraningManagement` 对齐
- Vite/TypeScript 依赖冲突修复并通过构建验证

## 已实施内容

### 1. 页面与目录拆分
- 新增模块目录：
  - `src/views/modules/knowledge-base`
  - `src/views/modules/model-management`
  - `src/views/modules/model-evaluation`
  - `src/views/modules/inference-service`
  - `src/views/modules/finetune`
  - `src/views/modules/dataset-management`
- 每个模块均包含：
  - `index.vue`
  - `index.ts`
  - `columns.ts`

### 2. 共享面板与脚本分离
- 新增：
  - `src/views/modules/shared/LifecycleWorkspacePanel.vue`
  - `src/views/modules/shared/useLifecycleWorkspace.ts`
  - `src/views/modules/shared/types.ts`
- 旧文件 `src/views/LifecycleWorkspace.vue` 改为轻量包装器。

### 3. 微调详情页拆分
- 新增：
  - `src/views/modules/finetune/detail/index.vue`
  - `src/views/modules/finetune/detail/index.ts`
- 旧文件 `src/views/FinetuneDetail.vue` 改为包装器。

### 4. 路由切换到模块页
- 修改 `src/router/index.ts`，保留原 URL：
  - `/knowledge-bases`
  - `/models`
  - `/model-evaluations`
  - `/inference-services`
  - `/finetunes`
  - `/finetunes/:id`
  - `/datasets`

### 5. API 按域拆分
- 新增目录 `src/api/modules`：
  - `knowledge-base.ts`
  - `model.ts`
  - `model-evaluation.ts`
  - `inference-service.ts`
  - `finetune.ts`
  - `dataset.ts`
  - `types.ts`
- `src/api/lifecycle.ts` 改为统一 re-export 入口，原函数名保持兼容。

### 6. 视觉风格对齐
- 新增：
  - `src/styles/index.less`
  - `src/styles/public.less`
- 修改：
  - `src/main.ts` 改为引入 `styles/index.less`
  - `src/App.vue` 调整主题色、标题描述和路径匹配逻辑

### 7. Vite 与依赖版本调整
- `package.json` 调整为构建工具放入 `devDependencies`
- 核心版本：
  - `vite` `^4.5.5`
  - `@vitejs/plugin-vue` `^4.6.2`
  - `typescript` `5.4.5`（固定）
  - `vue-tsc` `^1.8.27`
  - 新增 `less`

## 验证结果
已在目录 `x-frontend/ai-model-lifecycle-frontend` 执行：

- `npm install`
- `npm run build`

构建成功（vite build 通过）。

## 上手指南

### 本地开发
1. 进入目录：`x-frontend/ai-model-lifecycle-frontend`
2. 安装依赖：`npm install`
3. 启动开发：`npm run dev`
4. 生产构建：`npm run build`

### 新功能开发建议
1. 按业务模块放到 `src/views/modules/<module>/`
2. 页面维持 `index.vue + index.ts + columns.ts`
3. API 优先放到 `src/api/modules/<domain>.ts`
4. 全局风格统一改 `src/styles/public.less`

### 重点注意
- 不要将 `typescript` 升到 `5.9+`（当前 `vue-tsc@1.8.27` 会报错）
- 如升级 TypeScript，请同步验证 `vue-tsc` 兼容性

---

## 第二轮深度对齐（2026-05-27）

本轮针对“与 TraningManagement 保持相同风格与组织方式”继续实施，保持业务功能和接口行为不变，重点完成路由与布局形态、菜单层级和公共组件对齐。

### 本轮关键变更

1. 路由组织改为 `routes/index.ts` 写法
- 新增：`src/router/routes/index.ts`
- 一级菜单路由：
  - `数据集管理` -> `/dataset-management/index`
  - `模型管理` -> `/model-management/*`
  - `知识库管理` -> `/knowledge-base/index`
- 模型管理二级路由：
  - `/model-management/model-list/index`
  - `/model-management/finetune/index`
  - `/model-management/model-evaluation/index`
  - `/model-management/inference-service/index`
- 微调详情路由：`/model-management/finetune/detail/:id`
- 旧路径兼容重定向保留：`/models`、`/finetunes`、`/knowledge-bases` 等。

2. 路由模式切换为 history
- 修改：`src/router/index.ts`
- 使用：`createWebHistory('/child/ai-model-lifecycle-frontend')`

3. 菜单布局改为“顶部一级 + 左侧二级”
- 新增：`src/layouts/default/index.vue`
- 顶部一级菜单：数据集管理、模型管理、知识库管理
- 左侧二级菜单：仅在模型管理下展示（模型列表、模型微调、模型评测、推理服务）

4. App 壳层职责收敛
- 修改：`src/App.vue`
- 仅保留 Provider（`n-config-provider`、`n-message-provider`、`n-dialog-provider`）与 `router-view`
- 菜单与布局职责迁移到 `src/layouts/default/index.vue`

5. 公共弹窗组件对齐
- 新增：`src/components/common/LayoutDialog.vue`
- 修改：`src/views/modules/shared/LifecycleWorkspacePanel.vue`
- 将编辑表单容器从原生 `n-drawer` 替换为 `LayoutDialog`（内部仍使用 `n-form` / `n-form-item`）

6. 模块文案与详情跳转路径对齐
- 修改：`src/views/modules/shared/useLifecycleWorkspace.ts`
  - 模块名更新：数据集管理、模型列表、模型微调、模型评测、推理服务、知识库管理
  - 微调详情跳转改为：`/model-management/finetune/detail/:id`
- 修改：`src/views/modules/finetune/detail/index.vue`
  - 面包屑与返回路径改为：`/model-management/finetune/index`

7. 全局样式进一步靠拢 TraningManagement
- 修改：`src/styles/public.less`
  - 新增布局样式：`tm-layout-*`
  - 保留并强化页面样式：`tm-page`、`tm-title-bar`、`tm-table` 等
  - 增补常用间距类：`ml-10px`、`mb-12`

8. 遗留大文件清理
- 修改：`src/views/LifecycleWorkspace.vue`
- 改为轻量包装器，避免与模块化页面组织冲突。

### 本轮验证口径

- 已执行静态错误检查（针对新增/修改文件），无错误。
- 本轮按需求 **未执行**：`npm install`、`npm run build`。
- 建议由使用方手测以下路径：
  1. 顶部一级菜单切换：数据集管理 / 模型管理 / 知识库管理
  2. 模型管理左侧二级菜单切换：模型列表 / 模型微调 / 模型评测 / 推理服务
  3. 微调详情页进入与返回链路
  4. 查询、分页、新建、编辑、上传、状态操作

## 运行期问题修复记录（2026-05-27）

### 问题 1：访问页面白屏
- 现象：`http://localhost:9906/child/ai-model-lifecycle-frontend` 页面空白。
- 根因：`src/styles/public.less` 存在 Less 语法断裂，导致 `src/styles/index.less` 请求返回 500。
- 修复：恢复滚动条样式块结构，移除错误插入位置的根级属性，页面恢复渲染。

### 问题 2：顶部菜单看不见（实际已渲染但对比度过低）
- 现象：在 `dataset-management/index` 页面顶部菜单文字极暗，误判为未展示。
- 根因：顶部菜单在深色背景下未使用足够明确的亮色主题覆盖。
- 修复：在布局文件 `src/layouts/default/index.vue` 的顶部 `n-menu` 增加 `inverted` 和 `theme-overrides`，强化横向菜单文本与激活态对比度。
