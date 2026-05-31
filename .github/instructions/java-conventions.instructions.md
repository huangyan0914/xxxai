---
description: "Java 编码规范 — 适用于所有 Java 源文件的命名、注解、分层、异常处理约定"
applyTo: "**/*.java"
---

# Java 编码规范

## 1. 命名规范

| 元素 | 风格 | 示例 |
|---|---|---|
| 类 / 接口 | UpperCamelCase | `QuestionService`, `ServerVO` |
| 方法 / 变量 | lowerCamelCase | `pageQuestions`, `bankId` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 包名 | 全小写，点分隔 | `com.cetc.ops.controller` |
| 数据库表字段映射 | lowerCamelCase（MyBatis-Plus 自动转换） | `createTime` ↔ `create_time` |

### 类名后缀约定

| 后缀 | 用途 |
|---|---|
| `Controller` | HTTP 接口入口 |
| `Service` / `ServiceImpl` | 业务逻辑接口及实现 |
| `Mapper` | MyBatis 数据访问接口 |
| `Entity` / 无后缀 | 数据库持久化实体 |
| `DTO` | Controller 入参数据传输对象 |
| `VO` | 对前端的视图对象 |
| `QueryDTO` | 分页/查询专用入参 DTO |
| `Config` | Spring 配置类 |
| `Task` | 定时任务 |
| `Util` / `Utils` | 工具类（无状态静态方法） |
| `Constants` | 常量类 |

---

## 2. 注解规范

### 依赖注入

```java
// ✅ 使用 @Resource（JSR-250）
@Resource
private QuestionService questionService;

// ❌ 禁止使用 @Autowired
@Autowired  // 禁止
private QuestionService questionService;
```

### Controller

```java
@Tag(name = "题目管理")                    // io.swagger.v3.oas.annotations.tags.Tag
@RestController
@RequestMapping("/api/assessment/questions")
public class QuestionController {

    @Operation(summary = "分页查询题目列表")  // io.swagger.v3.oas.annotations.Operation
    @GetMapping
    public Resp<IPage<QuestionVO>> page(QuestionQueryDTO query) { ... }

    @Operation(summary = "新建题目")
    @PostMapping
    public Resp<Long> create(@Valid @RequestBody QuestionCreateDTO dto) { ... }
}
```

### Service 实现

```java
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    // 写操作必须加事务
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(QuestionCreateDTO dto) { ... }
}
```

---

## 3. 分层职责

### Controller 层

- 仅做参数接收、校验（`@Valid`）、调用 Service、包装 `Resp` 返回
- **禁止**写业务逻辑或直接操作 Mapper
- **禁止**直接返回 Entity；必须返回 `Resp<T>`

```java
@PostMapping
public Resp<Long> create(@Valid @RequestBody XxxCreateDTO dto) {
    return Resp.ok(xxxService.create(dto));
}
```

### Service 层

- 承载核心业务逻辑、事务控制
- 继承 `ServiceImpl<Mapper, Entity>`（MyBatis-Plus）
- 业务校验失败时抛出 `BizException`

```java
@Override
@Transactional(rollbackFor = Exception.class)
public Long create(XxxCreateDTO dto) {
    // 业务校验
    if (condition) {
        throw new BizException("错误原因");
    }
    Xxx entity = new Xxx();
    // ... 赋值
    save(entity);
    return entity.getId();
}
```

### Mapper 层

- 继承 `BaseMapper<Entity>`（MyBatis-Plus）
- 简单 CRUD 直接使用 Lambda 条件构造器（`lambdaQuery()` / `lambdaUpdate()`）
- 复杂 SQL（多表关联、子查询）在 `resources/mapper/XxxMapper.xml` 中定义

---

## 4. 统一返回 Resp

```java
// 成功，带数据
return Resp.ok(data);

// 成功，无数据
return Resp.ok();

// 失败
return Resp.fail("错误信息");
return Resp.fail(RespCode.NOT_FOUND);
```

`Resp<T>` 字段：`code`（String）、`msg`（String）、`data`（T）

---

## 5. 异常处理

```java
// 业务异常：在 Service 中抛出
throw new BizException("题目不存在");
throw new BizException("题库名称已存在，请更换");

// GlobalExceptionHandler 会自动捕获 BizException 并转为 Resp 返回
// 不需要在 Controller 手动 try-catch BizException
```

---

## 6. 分页

```java
// QueryDTO 继承 PageQuery（含 pageNum、pageSize）
public class XxxQueryDTO extends PageQuery {
    private String name;
    // ...
}

// Service
public IPage<XxxVO> page(XxxQueryDTO query) {
    Page<XxxVO> page = new Page<>(query.getPageNum(), query.getPageSize());
    return baseMapper.pageXxx(page, query);
}

// Controller
public Resp<IPage<XxxVO>> page(XxxQueryDTO query) {
    return Resp.ok(xxxService.page(query));
}
```

---

## 7. 实体 Entity

- 继承 `BaseEntity`（含 `id`、`createTime`、`updateTime` 等公共字段）
- 使用 MyBatis-Plus 注解：`@TableName`、`@TableId`、`@TableField`
- 字段命名使用 lowerCamelCase，数据库列名使用 snake_case（框架自动映射）

```java
@TableName("t_question")
public class Question extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long bankId;
    private String stem;
    private Integer type;
    // ...
}
```

---

## 8. DTO / VO

```java
// DTO：Controller 入参，按需加 @NotNull / @NotBlank 等校验注解
public class QuestionCreateDTO {
    @NotBlank(message = "题目内容不能为空")
    private String stem;

    @NotNull(message = "题型不能为空")
    private Integer type;
}

// VO：面向前端，可添加额外展示字段，不暴露敏感字段
public class QuestionVO {
    private Long id;
    private String stem;
    private Integer type;
    private String typeName;   // 字典翻译后的展示名
    // ...
}
```
