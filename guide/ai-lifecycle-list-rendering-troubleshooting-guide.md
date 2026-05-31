# AI 生命周期列表页“后端有数据但前端不显示”修复指南

## 结论

本次问题根因是前端公共列表解析逻辑过窄：只识别了少数字段，未覆盖 `records/rows/list` 等常见返回结构，导致出现“分页总数有值，但表格行为空”的现象。

已在共享文件中完成统一修复，推理服务页已验证恢复显示。

## 根因说明

原先在 `useLifecycleWorkspace.ts` 的 `normalizePage` 中，行数据提取主要依赖：

- `tasks`
- `files`
- `versions`
- `result`
- `data`

当后端返回结构是如下形态时，容易丢行：

- `data.records`
- `data.rows`
- `data.list`
- `result.result`（嵌套对象后才是数组）

这会导致：

- `total` 能正常显示
- `records` 被解析为 `[]`
- 页面表现为 “No Data”

## 修复点

已修改文件：

- `x-frontend/ai-model-lifecycle-frontend/src/views/modules/shared/useLifecycleWorkspace.ts`

关键改动：

1. `normalizePage` 改为调用统一提取函数
- `extractRows(payload)`：统一提取数组行
- `extractTotal(payload, records)`：统一提取总数，缺失时回退为 `records.length`

2. `unwrapRows` 复用同一逻辑
- 下拉选项/树结构等也能复用同一套解析能力，减少模块间差异

3. 新增容器候选层级
- `payload`
- `payload.result`
- `payload.data`
- `payload.result.result`
- `payload.result.data`
- `payload.data.result`
- `payload.data.data`
- `payload.data.page`
- `payload.result.page`

4. 新增优先键集合
- `records`、`rows`、`list`、`items`
- `tasks`、`files`、`versions`
- `services`、`children`
- `result`、`data`

## 验证结果

### 1) 接口返回确认

推理服务接口返回（示例）中包含：

- `data.result.total = 3`
- `data.result.result = [ ...3条服务组数据... ]`

### 2) 页面结果确认

`model-management/inference-service/index` 已显示 3 条记录（此前为 No Data）。

### 3) 构建验证

已在前端工程目录执行：

```bash
npm run build
```

构建通过。

## 上手指南（后续排查同类问题）

1. 先看接口真实结构
- 浏览器控制台或直接请求 `/api/aimodel/**`，确认数组字段实际在 `records`、`list`、`result.result` 还是其他层级。

2. 优先修改共享解析
- 本项目列表页统一走 `useLifecycleWorkspace.ts`，同类问题优先在这里修一次覆盖多页面。

3. 排查“有总数无行”
- 若 `total > 0` 且页面空白，第一怀疑点就是 rows 提取失败。

4. 快速验证
- 用接口返回样本在本地走一遍 `extractRows`/`extractTotal` 逻辑。
- 刷新至少两个模块（例如推理服务、数据集管理）确认修复范围。

5. 模型评测页注意事项
- 当前环境下 `model-evaluations` 接口返回 `tasks: []`、`total: 0`，该页面空表是数据本身为空，不是渲染缺陷。
