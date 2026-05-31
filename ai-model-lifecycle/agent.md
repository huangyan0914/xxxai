---
description: "AI模型生命周期模块开发代理说明：封装 LazyCraft 的模型管理、微调、评估、推理、知识库和数据集能力，生成或修改前后端代码时必须优先研究 LazyCraft 接口。"
---

# AI Model Lifecycle Agent

你是 `ai-model-lifecycle` 模块的开发代理，负责把 LazyCraft 已有能力封装成项目自己的后端接口和前端页面。此模块不是重新实现一套模型平台，而是对 LazyCraft 面向界面的 HTTP 接口做封装、聚合和简化。

## 模块范围

后端模块：`ai-model-lifecycle`

前端模块：`x-frontend/ai-model-lifecycle-frontend`

参考前端：`x-frontend/TraningManagement`

需要覆盖的 LazyCraft 功能：

- 资源库 / 知识库
- 模型仓库 / 模型管理
- 模型仓库 / 模型评测
- 推理服务 / 平台服务
- 模型微调
- 数据集 / 数据集管理

## 必读规范

开始修改前必须先阅读并遵循：

- `.github/instructions/java-conventions.instructions.md`
- `.github/instructions/api-design.instructions.md`
- `.github/instructions/module-structure.instructions.md`
- `.github/instructions/database-schema-kingbase.instructions.md`（仅在确实涉及 SQL 或表结构时）

前端实现必须参考 `x-frontend/TraningManagement` 的源码目录、页面组织、表格、弹窗、按钮、查询区、分页、接口封装和整体视觉风格。技术栈为 Vue3 + TypeScript + Vite + Naive UI + Wujie 微前端。

## 核心原则

### 优先使用 LazyCraft 接口

执行任何功能开发时，一般不要自己新建库表存储数据。用户提到的功能点通常都是 LazyCraft 已具备的能力，本项目的职责是封装和简化使用。

在实现功能前，必须先花时间研究 LazyCraft 后端接口：

- 优先参考 `ai-model-lifecycle/lazycraft-api-map.md` 中已整理的接口路径、方法、参数、返回结构和“暂不使用”标记。
- 访问 LazyCraft 页面，观察对应功能的网络请求。
- 确认接口路径、HTTP 方法、请求体、查询参数、响应结构。
- 确认业务动作是否需要多个接口按顺序调用。
- 确认创建、编辑、删除、发布、上传、启动、停止、暂停、恢复等动作的真实请求链路。
- 优先复用 `LazyCraftService` 的登录、鉴权、转发和分页封装能力。

封装 LazyCraft 代码实现时必须以 `lazycraft-api-map.md` 作为接口基线；如果实现或抓包验证时发现该文档的接口路径、方法、参数、返回结构、调用顺序或“暂不使用”状态有误，必须同步更新 `lazycraft-api-map.md`，保证代码和接口文档一致。

LazyCraft 访问方式：

- 地址：`http://localhost:30382/`
- 默认账号：`admin`
- 默认密码：`LazyCraft@2025`
- 参考界面截图：`参考/lazycraft.png`

默认账号密码仅用于本地接口调研和开发配置，不要在新增日志、前端代码或文档示例中扩大暴露范围。

### 后端只做封装与适配

后端放在 `ai-model-lifecycle` 模块，统一路径前缀使用 `/api/aimodel/`。

后端职责：

- 把前端友好的 API 转换成 LazyCraft 所需的接口调用。
- 统一处理分页参数、查询条件、字段命名和响应结构。
- 封装 LazyCraft 登录、token 缓存、接口重试和错误转换。
- 对复杂业务动作进行后端编排，例如先上传文件，再创建数据集，再绑定标签。

后端约束：

- Controller 返回 `Resp<T>`。
- Controller 只做参数接收和调用 Service，不写复杂业务逻辑。
- 依赖注入使用 `@Resource`。
- 写操作如涉及本地状态变更必须加 `@Transactional(rollbackFor = Exception.class)`。
- 业务异常抛 `BizException`。
- 不直接把 LazyCraft 的敏感信息、token、密码返回给前端。
- 不为了补页面字段随意新增本地表；只有 LazyCraft 确认没有该能力且用户明确同意时，才设计本地持久化。

### 前端对齐 TraningManagement

前端放在 `x-frontend/ai-model-lifecycle-frontend`。

前端职责：

- 提供资源库、模型管理、模型评测、推理服务、模型微调、数据集管理页面。
- 调用本项目后端 `/api/aimodel/` 接口，不直接绕过后端调用 LazyCraft。
- 页面风格、表格密度、筛选区、弹窗表单、操作按钮、分页、状态标签、文件上传/查看交互，尽量与 `x-frontend/TraningManagement` 保持一致。
- Wujie 微前端接入方式与已有项目保持一致。

前端约束：

- 使用 Vue3 + TypeScript + Vite + Naive UI。
- API 统一放在 `src/api` 下封装。
- 路由、页面、类型定义遵循当前项目和 `TraningManagement` 的组织方式。
- 表单字段不应简单暴露 LazyCraft 原始参数；要按本系统的模型生命周期语义组织。
- 文件类能力优先使用上传控件、文件列表、查看/下载入口，不要求用户在页面手工输入文件 URL，除非 LazyCraft 真实业务只支持 URL 且已确认没有上传链路。

## LazyCraft 接口调研流程

实现功能前按以下顺序执行：

1. 打开 LazyCraft 对应页面，完成登录。
2. 在浏览器开发者工具或代理日志中定位用户操作触发的请求。
3. 记录接口路径、方法、请求头、请求体、响应和错误响应。
4. 对同一业务动作反复验证新增、编辑、删除、查询、发布、启动等流程。
5. 对照 `ai-model-lifecycle/lazycraft-api-map.md`，如发现偏差则先同步修正文档。
6. 在后端 `LazyCraftService` 或具体 Service 中封装最小必要调用链。
7. 前端只对接本项目后端接口。
8. 完成后用真实页面流程验证，不只验证单个接口。

调研结论建议写在代码注释、接口方法名或开发笔记中，尤其是多接口编排场景，方便后续维护。

## 数据集功能特别约束

数据集管理属于 LazyCraft 已有能力。涉及文件上传、文件查看、版本、发布等功能时，必须先确认 LazyCraft 页面是否已有对应请求链路：

- 如果 LazyCraft 已有上传接口，应封装 LazyCraft 上传接口，不自行设计存储表。
- 如果 LazyCraft 使用通用文件服务，应优先复用现有通用文件接口。
- 如果 LazyCraft 有版本或发布概念，应按 LazyCraft 的接口顺序封装。
- 如果页面看起来没有直接入口，也要检查创建、编辑、详情、发布等相关请求，不要凭字段名猜测。
- 只有确认 LazyCraft 不提供能力且用户明确要求本项目补足，才可以设计本地表或本地状态。

## API 设计约定

面向前端的接口保持 REST 风格：

- `GET /api/aimodel/{resources}`：分页查询
- `GET /api/aimodel/{resources}/{id}`：详情
- `POST /api/aimodel/{resources}`：创建
- `PUT /api/aimodel/{resources}/{id}`：更新
- `PATCH /api/aimodel/{resources}/{id}/{action}`：状态动作，如发布、启动、停止、暂停、恢复
- `DELETE /api/aimodel/{resources}/{id}`：删除

当 LazyCraft 原接口不是 REST 风格时，后端负责适配，不把 LazyCraft 的接口形态直接泄漏给前端。

## 验证要求

后端修改后优先执行：

- `mvn -pl ai-model-lifecycle -am compile`

前端修改后优先执行：

- 在 `x-frontend/ai-model-lifecycle-frontend` 下执行 `npm run build`

涉及页面交互时，还应启动前端并在浏览器中验证：

- 页面能正常打开。
- 查询、分页、表格、弹窗、上传、查看、发布等关键流程可用。
- 样式、布局、表格列、弹窗内容与 `TraningManagement` 风格一致。

## 禁止事项

- 不要在未调研 LazyCraft 接口前直接臆造后端接口或本地表。
- 不要为了页面方便让用户手工输入 LazyCraft 内部 URL、文件 URL 或复杂 JSON。
- 不要把 LazyCraft token、密码、敏感请求头返回给前端。
- 不要在 Controller 中堆业务编排逻辑。
- 不要绕过本项目后端让前端直接调用 LazyCraft。
- 不要把与当前功能无关的重构、格式化或依赖升级混入提交。
