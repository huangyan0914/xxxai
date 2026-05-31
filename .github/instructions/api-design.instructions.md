---
description: "REST API 设计规范 — 适用于新建或修改 Controller 接口、URL 路径、请求/响应格式设计"
applyTo: "**/*Controller.java"
---

# REST API 设计规范

## 1. URL 路径规范

### 基本原则

- 全部小写，单词以连字符 `-` 分隔
- 路径使用名词（资源），不使用动词
- 业务模块路径前缀统一为 `/api/{模块标识}/`

### 路径示例

```
# ✅ 正确
GET    /api/assessment/question-banks                  # 列表/分页
GET    /api/assessment/question-banks/{id}             # 详情
POST   /api/assessment/question-banks                  # 新建
PUT    /api/assessment/question-banks/{id}             # 全量更新
PATCH  /api/assessment/question-banks/{id}             # 局部更新
DELETE /api/assessment/question-banks/{id}             # 删除

# 嵌套资源（父子关系）
GET    /api/assessment/question-banks/{bankId}/questions
POST   /api/assessment/question-banks/{bankId}/questions/import

# ❌ 错误示例（路径中含动词）
POST   /api/assessment/createQuestionBank
GET    /api/assessment/getQuestionList
```

### 模块路径前缀参考

| 模块 | 前缀 |
|---|---|
| 培训管理 | `/api/training/` |
| 数据管理 | `/api/data/` |
| AI 模型 | `/api/aimodel/` |
| 运维管理 | `/api/ops/` |
| 数据资产 | `/api/asset/` |
| 原理培训 | `/api/principle/` |
| 智能问答 | `/api/qa/` |
| 虚拟实操培训 | `/api/vpt/` |
| 原理考核 | `/api/assessment/` |
| 虚拟实操考核 | `/api/vpa/` |
| 虚拟教培场景 | `/api/vts/` |
| 综合评估 | `/api/eval/` |

---

## 2. HTTP 方法语义

| 方法 | 语义 | 幂等 | 常见场景 |
|---|---|---|---|
| `GET` | 查询，不改变资源状态 | 是 | 列表、详情、导出 |
| `POST` | 新建资源或触发操作 | 否 | 创建、导入、启动 |
| `PUT` | 全量更新（替换整个资源） | 是 | 编辑保存 |
| `PATCH` | 局部更新（仅更新部分字段） | 否 | 状态变更、字段修改 |
| `DELETE` | 删除资源 | 是 | 单个/批量删除 |

---

## 3. 统一响应格式

所有接口必须返回 `Resp<T>`，结构如下：

```json
{
  "code": "0",       // "0" 表示成功，其他值表示失败
  "msg": "成功",
  "data": { ... }    // 业务数据，失败时为 null
}
```

### 常用状态码（RespCode）

| code | msg | 场景 |
|---|---|---|
| `"0"` | 成功 | 正常响应 |
| `"1"` | 失败 | 通用业务失败 |
| `"401"` | 未授权 | 未登录 |
| `"403"` | 无权限 | 权限不足 |
| `"404"` | 资源不存在 | 查不到数据 |
| `"500"` | 服务器异常 | 系统错误 |

### 分页响应格式

```json
{
  "code": "0",
  "msg": "成功",
  "data": {
    "records": [ { ... } ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

分页数据使用 MyBatis-Plus `IPage<VO>` 即可自动满足此格式。

---

## 4. 请求参数规范

### GET 查询参数

- 分页：`pageNum`（当前页，从 1 开始）、`pageSize`（每页条数，默认 10）
- 过滤：使用 Query DTO，字段名与数据库字段对应（驼峰命名）

```java
// QueryDTO 继承 PageQuery
public class QuestionQueryDTO extends PageQuery {
    private String keyword;    // 关键词搜索
    private Integer type;      // 题型过滤
    private Integer difficulty; // 难度过滤
}
```

### POST/PUT 请求体

- 使用 JSON 格式（`@RequestBody`）
- 必填字段加 `@NotNull` / `@NotBlank`（Bean Validation）
- Controller 方法参数加 `@Valid` 触发校验

```java
@PostMapping
public Resp<Long> create(@Valid @RequestBody QuestionCreateDTO dto) { ... }
```

### 路径变量

- ID 类型统一使用 `Long`
- 路径变量名与参数名一致

```java
@GetMapping("/{id}")
public Resp<QuestionVO> getById(@PathVariable Long id) { ... }
```

---

## 5. Swagger/Knife4j 文档注解

```java
@Tag(name = "模块功能名称")         // 类级别，eg: "题目管理"
@RestController
@RequestMapping("/api/xxx")
public class XxxController {

    @Operation(summary = "接口简短描述")    // 方法级别，eg: "分页查询题目列表"
    @GetMapping
    public Resp<IPage<XxxVO>> page(...) { ... }
}
```

- `@Tag(name)` 值使用中文，简洁描述模块功能
- `@Operation(summary)` 值使用中文，简洁描述接口用途
- `@Parameter` 在需要时补充复杂参数说明

---

## 6. 典型 Controller 模板

```java
@Tag(name = "XXX管理")
@RestController
@RequestMapping("/api/{module}/xxxs")
public class XxxController {

    @Resource
    private XxxService xxxService;

    @Operation(summary = "分页查询")
    @GetMapping
    public Resp<IPage<XxxVO>> page(XxxQueryDTO query) {
        return Resp.ok(xxxService.page(query));
    }

    @Operation(summary = "查询详情")
    @GetMapping("/{id}")
    public Resp<XxxDetailVO> getById(@PathVariable Long id) {
        return Resp.ok(xxxService.getById(id));
    }

    @Operation(summary = "新建")
    @PostMapping
    public Resp<Long> create(@Valid @RequestBody XxxCreateDTO dto) {
        return Resp.ok(xxxService.create(dto));
    }

    @Operation(summary = "更新")
    @PutMapping("/{id}")
    public Resp<Void> update(@PathVariable Long id, @Valid @RequestBody XxxUpdateDTO dto) {
        xxxService.update(id, dto);
        return Resp.ok();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Resp<Void> delete(@PathVariable Long id) {
        xxxService.delete(id);
        return Resp.ok();
    }
}
```
