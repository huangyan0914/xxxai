## 代码架构（整体约定）

- **多模块结构**：采用 Maven 多模块，`x-wxwh` 作为聚合工程，业务模块（如 `training-management` 等）与基础公共模块（如 `common`）以兄弟模块形式存在，统一由父 POM 管理依赖与版本。
- **分层风格**：各业务模块内普遍采用「Controller - Service - Mapper - Entity/VO」分层，尽量保持：
  - **Controller 层**：接收 HTTP 请求、做参数校验与简单组装，仅调用 Service，不写业务逻辑。
  - **Service 层**：承载核心业务逻辑、事务控制，协调多个 Mapper / 外部服务。
  - **Mapper 层**：MyBatis 映射接口，专注数据库 CRUD，SQL 定义在 `src/main/resources/mapper/*.xml` 中。
  - **Entity / VO 层**：`entity` 为持久化实体，`vo` 为对前端展示友好的视图对象或组合对象。
- **统一返回与异常处理**：鼓励统一使用公共模块中的响应包装与异常体系（见下文 `common` 模块），保证接口风格一致，便于前后端协作与后续自动生成接口文档。

## 模块代码架构（可供生成参考）

### `common` 公共基础模块

`common` 为所有业务模块的公共基础库，主要包含以下包（`com.cetc.common.*`）：

- **`resp` 包**
  - `Resp`：统一返回结果包装类，一般包含 `code`、`msg`、`data` 等字段。
  - `RespCode`：约定好的业务状态码/错误码枚举，所有业务模块应优先复用。
- **`exception` 包**
  - `BizException`：业务异常类型，用于显式抛出可预期的业务错误。
  - `GlobalExceptionHandler`：全局异常处理（基于 Spring），统一将异常转换为 `Resp` 返回。
- **`entity` 包**
  - `BaseEntity`：实体基类，约定通用字段（如主键、创建/更新时间等），业务实体可继承。
- **`page` 包**
  - `PageQuery`：分页查询基础参数（页码、页大小等）。
  - `PageResult`：分页返回结构（总数、列表数据等）。
- **`web` 包**
  - `WebMvcConfig` / `WebMvcInterceptorConfig` / `LogInterceptor`：统一 Web MVC 配置与日志拦截，供各业务模块复用。
- **`mybatis` 包**
  - `MpConfig`：MyBatis / MyBatis-Plus 相关的全局配置。
- **`config` 包**
  - `Knife4jConfig`：Swagger/Knife4j 文档配置，为后续自动生成接口文档提供基础能力。

> 后续代码生成/新模块开发时，建议：
>
> - 统一使用 `Resp` / `RespCode` 作为接口返回与状态码标准。
> - Controller 不直接返回实体，而是通过 `Resp<T>` 包装。
> - 分页接口统一使用 `PageQuery` + `PageResult<T>` 组合，便于前后端约定。

### `training-management` 培训管理模块

`training-management` 模块负责培训计划、课程与培训过程等业务，目前核心代码结构如下（包名均位于 `com.cetc.training` 之下）：

- **入口类**
  - `TrainingManagementApplication`：模块启动入口类。
- **`config` 包**
  - `Knife4jConfig`：培训管理模块下的接口文档配置（如需要可覆盖或补充 `common` 中的默认配置）。
- **`entity` 包**
  - `Subject`：培训科目实体，对应数据库中培训科目表（结构见 `schema-training-subject.sql`）。
- **`vo` 包**
  - `SubjectVO`：面向前端的培训科目视图对象（可在 `Subject` 基础上添加展示字段、字典名等）。
- **`mapper` 包 + `resources/mapper` 目录**
  - `SubjectMapper`：MyBatis 映射接口。
  - `mapper/SubjectMapper.xml`：`Subject` 相关的 SQL 映射文件。
- **`service` / `service.impl` 包**
  - `SubjectService`：培训科目相关业务接口。
  - `SubjectServiceImpl`：培训科目业务实现，承载培训科目的增删改查与业务校验。
- **`controller` 包**
  - `SubjectController`：培训科目相关 HTTP 接口入口，按 REST 风格划分路径与方法。

> 在该模块中扩展其他培训相关实体（如培训计划、课程、班次、学员等）时，建议：
>
> - 按照 `Subject` 的模式新增 `Entity/VO/Mapper/Service/Controller` 五层结构。
> - 所有 Controller 的入参与返回统一使用 `Resp`、`PageQuery`、`PageResult` 等公共模型。
> - 业务异常统一抛出 `BizException`，并通过 `GlobalExceptionHandler` 处理。

