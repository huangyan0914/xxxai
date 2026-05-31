# X-WXWH 教培综合平台

多模块 Maven 项目，包含 **12 个业务模块**

## 模块一览

| 模块目录 | 模块名称 | 说明 |
|----------|----------|------|
| `training-management` | 培训管理 | 培训计划、课程与培训过程管理 |
| `data-management` | 数据管理 | 数据采集、存储与数据治理 |
| `ai-model-lifecycle` | AI模型全周期生命周期管理 | 模型开发、训练、部署与迭代 |
| `ops-management` | 运维管理 | 系统运维、监控与告警 |
| `data-asset-management` | 数据资产管理 | 数据资产目录、血缘与价值管理 |
| `principle-training` | 原理培训 | 原理与理论知识培训 |
| `intelligent-qa` | 智能问答 | 基于AI的智能问答与知识检索 |
| `virtual-practical-training` | 虚拟实操培训 | 虚拟环境下实操技能培训 |
| `principle-assessment` | 原理考核 | 原理与理论考核与阅卷 |
| `virtual-practical-assessment` | 虚拟实操考核 | 虚拟环境下实操考核与评分 |
| `virtual-training-scenario` | 虚拟教培场景 | 虚拟教培场景编排与仿真 |
| `comprehensive-evaluation` | 综合评估 | 培训效果与能力综合评估 |

## 需求文档

- [培训管理模块 - 精简版需求](docs/training-management-requirements.md)：基于 UI 的实体、字段、字典及功能点说明

## 构建与运行

```bash
# 编译全部模块
mvn clean compile

# 打包全部模块
mvn clean package

# 仅编译/打包指定模块（示例：认证模块）
mvn -pl auth clean compile
```

各模块入口类为 `com.cetc.*` 包下的 `*Application`，可在 IDE 中直接运行或通过 `mvn exec:java` 指定 main 类运行。

## 代码架构

详细的代码架构与分层约定见：[`docs/code-architecture.md`](docs/code-architecture.md)。

## 技术栈

- Java 8
- Maven 多模块（父 POM + 12 个子模块）

后续可在此基础上为各模块引入 Spring Boot、数据库、API 等实现具体功能。
