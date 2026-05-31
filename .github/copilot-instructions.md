# X-WXWH 教培综合平台 — Copilot 工作空间规范

> 此文件作为整个工作区的基础约定，对所有文件始终生效。

## 项目概述

- **项目名称**：X-WXWH 教培综合平台
- **技术栈**：Java 8 + Spring Boot 2.7.18 + MyBatis-Plus 3.5.6 + Knife4j 4.4.0
- **数据库**：人大金仓（PostgreSQL 兼容，驱动使用 `postgresql:42.7.3`）
- **构建工具**：Maven 多模块（聚合工程 `x-wxwh`）
- **根包名**：`com.cetc`，各业务模块包名为 `com.cetc.{moduleName}`

## 模块列表

| 模块目录 | 包名 | 职责 |
|---|---|---|
| `common` | `com.cetc.common` | 公共基础库（Resp、异常、分页、拦截器） |
| `system` | `com.cetc.system` | 系统通用（字典等） |
| `host-metrics` | `com.cetc.metrics` | 主机指标采集 |
| `training-management` | `com.cetc.training` | 培训管理 |
| `data-management` | `com.cetc.data` | 数据管理 |
| `ai-model-lifecycle` | `com.cetc.aimodel` | AI 模型全生命周期 |
| `ops-management` | `com.cetc.ops` | 运维管理 |
| `data-asset-management` | `com.cetc.asset` | 数据资产管理 |
| `principle-training` | `com.cetc.principle` | 原理培训 |
| `intelligent-qa` | `com.cetc.qa` | 智能问答 |
| `virtual-practical-training` | `com.cetc.vpt` | 虚拟实操培训 |
| `principle-assessment` | `com.cetc.assessment` | 原理考核 |
| `virtual-practical-assessment` | `com.cetc.vpa` | 虚拟实操考核 |
| `virtual-training-scenario` | `com.cetc.vts` | 虚拟教培场景 |
| `comprehensive-evaluation` | `com.cetc.eval` | 综合评估 |

## 核心约定（全局）

1. **统一返回**：所有 Controller 方法必须返回 `Resp<T>`，禁止直接返回 Entity 或裸数据。
2. **业务异常**：可预期的业务错误统一抛出 `BizException`，由 `GlobalExceptionHandler` 处理。
3. **分页**：分页接口入参用 `PageQuery`，返回 `Resp<PageResult<T>>` 或 `Resp<IPage<T>>`。
4. **DTO / VO 分离**：Controller 入参用 `DTO`，前端展示用 `VO`，禁止直接暴露 `Entity`。
5. **事务**：Service 写操作加 `@Transactional(rollbackFor = Exception.class)`。
6. **接口文档**：Controller 类加 `@Tag(name="...")` ，方法加 `@Operation(summary="...")`。
7. **依赖注入**：使用 `@Resource`（JSR-250），不使用 `@Autowired`。
8. **SQL**：简单查询用 MyBatis-Plus Lambda，复杂 SQL 写在 `resources/mapper/*.xml`。
