# AI 生命周期微调页与培训管理样式对齐指南

## 结论

已将 AI 模型生命周期管理中的微调页面，按培训管理“资源编目”页面的结构与视觉规范完成对齐，核心变化如下：

- 统一页面结构为：标题条 -> 查询区 -> 操作区 -> 列表区
- 移除 AI 页面中的统计卡片区域，避免与培训管理页面结构不一致
- 标题条增加培训管理同款左侧蓝色竖线
- 表格启用与培训管理一致的边框/网格参数（包含纵向分隔线）
- 保留 AI 页面已有业务能力（新建、刷新、查询、重置、分页、操作列）

## 具体改动文件

- x-frontend/ai-model-lifecycle-frontend/src/views/modules/shared/LifecycleWorkspacePanel.vue
- x-frontend/ai-model-lifecycle-frontend/src/styles/public.less

## 改动说明

### 1. 页面结构对齐

在 `LifecycleWorkspacePanel.vue` 中：

- 删除统计卡片（summary cards）渲染块
- 保留查询与操作区域
- 将 `n-data-table` 调整为培训管理常用视觉参数：
  - `:bordered="true"`
  - `:single-line="false"`
  - `:single-column="false"`
  - `flex-height`

### 2. 标题样式对齐

在 `public.less` 中：

- 为 `.tm-title-bar` 增加 `position: relative`
- 新增 `.tm-title-bar::before`，实现左侧蓝色竖线
- `.tm-title-bar span` 增加左间距，使标题文字与竖线对齐

### 3. 列表与表单视觉细节

在 `public.less` 中：

- 为 `.tm-toolbar .n-form` 增加换行与间距规则，匹配培训管理查询区布局习惯
- 为 `.tm-table .n-data-table-th/.n-data-table-td` 增加纵向分隔线
- 增加移动端规则，保证窄屏下表单项可折行显示

## 验证结果

- 构建命令：

```bash
cd "e:/work/cetcwork/workspace/x-wxwh/x-frontend/ai-model-lifecycle-frontend"
npm run build
```

- 验证结论：构建通过（`vue-tsc` 与 `vite build` 均成功）
- 页面验证：已在浏览器刷新 `model-management/finetune/index`，标题条和页面结构已与培训管理风格靠拢

## 上手指南（后续若要继续统一其它页面）

1. 复用相同共享组件
- 统一使用 `src/views/modules/shared/LifecycleWorkspacePanel.vue`
- 模块差异尽量通过 `moduleKey` 和 `useLifecycleWorkspace.ts` 配置驱动，而不是分散写样式

2. 样式统一只改一处
- 统一在 `src/styles/public.less` 管理 `tm-*` 样式
- 避免在单页组件内新增局部风格，防止再次偏离培训管理视觉规范

3. 每次改动后固定执行
- `npm run build`
- 打开目标页面核对：标题条、查询区、操作区、表格区、分页文本

4. 若要进一步 1:1 对齐培训管理
- 可增加按钮图标（查询/重置/新建/删除）
- 可将分页文案本地化为“页/跳至”
- 可补充批量删除能力（需要引入勾选行状态与批量删除 API 封装）
