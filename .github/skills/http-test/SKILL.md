---
name: http-test
description: "生成用于 JetBrains HTTP Client 插件的接口测试 .http 文件。Use when: 需要为 Controller/接口生成 HTTP 测试文件、需要创建可顺序执行的接口测试、生成 xxtest.http 文件、接口测试、http client test、generate http test file。生成的文件支持链式调用（从响应中提取 ID 供后续接口使用）、包含断言、末尾有清理环境请求。"
argument-hint: "要测试的模块名或 Controller 名，例如：用户管理、QuestionBankController"
---

# HTTP 接口测试文件生成技能

## 适用场景

- 为某个业务模块的 REST 接口生成完整 `.http` 测试文件
- 文件需可在 JetBrains IDEA HTTP Client 插件中**一次性顺序执行**
- 测试覆盖：正常 CRUD 流程 + 典型异常用例 + 末尾清理环境

## 生成前必做

1. **读取相关源码**：读取目标模块的 Controller、Service 接口、DTO/VO 类，弄清楚：
   - 所有接口的 HTTP 方法、URL 路径、请求参数（Query/PathVariable/RequestBody）
   - 返回值结构（`data` 字段的类型：Long ID、对象、分页列表等）
   - 业务状态码约定（通常 `code === "0"` 表示成功）
2. **分析实体依赖**：哪些实体被其他实体引用（例如题库→题目→试卷），删除顺序需**倒序**（先删引用方，再删被引用方）。

---

## 文件结构模板

```
###############################################################
#  <模块名> — HTTP 接口测试
#  兼容 JetBrains IntelliJ IDEA HTTP Client 插件
#  服务地址: http://localhost:<port>
#  接口前缀: /api/<prefix>
#  认证方式: X-User-Id 请求头
#
#  【全局变量说明】
#    <请求名>  → {{varName}}
#    ...
#
#  执行顺序：
#    一、<实体A>管理
#    二、<实体B>管理
#    ...
#    末. 清理环境
###############################################################

# ─── 原地变量（In-place Variables）───────────────────────────
@host    = http://localhost:<port>
@prefix  = /api/<prefix>
@userId  = 1001
```

---

## 编写规则

### 规则 1：请求命名（必须使用中文）

每个请求前必须以 `# @name` 指定中文名称，`###` 作为分隔符：

```http
### 1-1  <描述>
# @name <中文名称>
GET {{host}}{{prefix}}/path
X-User-Id: {{userId}}

###
```

### 规则 2：提取响应值到全局变量

当后续请求需要使用本次响应中的 ID 或其他字段，在请求末尾添加响应处理脚本：

```http
### 1-3  新建<实体>
# @name 新建<实体>
# 响应 data 字段存入全局变量 entityId，后续请求以 {{entityId}} 引用
POST {{host}}{{prefix}}/entities
X-User-Id: {{userId}}
Content-Type: application/json

{
  "name": "测试<实体>-HTTP",
  ...
}

> {%
    client.global.set("entityId", response.body.data);
%}

###
```

- 若 `data` 是嵌套对象中的 ID：`response.body.data.id`
- 若 `data` 是数组第一项的 ID：`response.body.data.list[0].id`

### 规则 3：断言

**关键状态变更接口**（创建、修改、删除、业务操作）必须带断言；查询接口视情况添加。

```http
> {%
    client.global.set("entityId", response.body.data);
    client.test("新建<实体>: HTTP 状态为 200", function() {
        client.assert(response.status === 200, "期望 200，实际 " + response.status);
    });
    client.test("新建<实体>: 业务码为成功", function() {
        client.assert(response.body.code === "0",
            "业务码非预期，实际响应: " + JSON.stringify(response.body));
    });
%}
```

同一个 `> {% %}` 块中可同时提取变量并断言。

### 规则 4：每个实体的接口顺序

```
查询（分页/列表）→ 新建 → 查询详情 → 修改/编辑 → 其他业务操作 → [异常用例]
↑ 不在循环末尾删除；删除统一放到末尾"清理环境"章节
```

**异常用例**单独用分隔注释标出：

```http
# ─── 异常: <场景描述> ─────────────────────────────────

### X-E1  <描述>（期望业务错误）
# @name <中文名>
...

###
```

### 规则 5：清理环境章节（末尾）

- 按**依赖倒序**删除：先删引用方实体，再删被引用方实体
- 若某实体删除前需要先改变状态（如撤销发布），先调撤销接口，再调删除
- 无法清理的数据（操作记录、审计日志等）写注释说明

```http
###############################################################
#  末. 清理测试环境
#  顺序：<依赖倒序说明>
###############################################################

### CLEAN-1  <描述>
# @name 清理-<中文名>
DELETE {{host}}{{prefix}}/entities/{{entityId}}
X-User-Id: {{userId}}

###

### CLEAN-N  验证<实体>已删除（期望 404 或业务错误）
# @name 清理-验证<实体>已删除
GET {{host}}{{prefix}}/entities/{{entityId}}
X-User-Id: {{userId}}

###
```

---

## 完整结构示例

参考项目中已有的完整测试文件：
[principle-assessment.http](../../principle-assessment/src/test/http/principle-assessment.http)

该文件展示了：
- 头部变量声明与全局变量映射表
- 多实体分章节组织（题库→题目→试卷→卷题→考试计划→考生流程）
- ID 链式传递（bankId → singleId/judgeId → paperId → sessionId）
- 断言模式（HTTP 状态 + 业务码双重断言）
- 异常用例格式
- 清理章节（倒序：计划→试卷→题目→题库）

---

## 输出文件路径约定

```
<module>/src/test/http/<module-name>.http
```

例如：`ops-management/src/test/http/ops-management.http`

---

## 完成检查清单

- [ ] 文件顶部有说明注释（模块名、服务地址、前缀、全局变量映射表、执行顺序）
- [ ] `@host`、`@prefix`、`@userId` 原地变量已声明
- [ ] 所有请求均有 `# @name` 中文命名
- [ ] 关键变更接口有 `client.global.set()` 提取 ID
- [ ] 关键变更接口有 `client.test` + `client.assert` 双重断言
- [ ] 包含典型异常用例（不存在的 ID、非法状态等）
- [ ] 末尾有"清理测试环境"章节，按依赖倒序删除
- [ ] 清理末尾有"验证已删除"确认请求
