---
description: "人大金仓表结构命名、主键、类型与审计字段规范 — 适用于设计或修改数据库 DDL、SQL 建表脚本"
applyTo: "**/sql/**/*.sql"
---

# 表结构规范（人大金仓）

设计或修改 DDL 时遵循下列约定。完整示例可参考 `training-management/src/main/resources/sql/schema-training-full.sql`。

## 表名前缀

- **模块表**：`t_{模块缩写}_`，`xx` 为模块英文缩写（如培训管理 `tm` → `t_tm_subject`）。
- **公共表**（用户、角色、字典等跨模块复用）：`t_s_` 前缀。

## 主键与约束

- 主键列名为 **`id`**，自增：优先使用 **`BIGSERIAL PRIMARY KEY`**（与现有脚本一致）。
- **不使用** `UNIQUE` 约束；**不声明**外键约束（关联仅通过业务字段如 `project_id` 表达）。

## 数据库与类型

- 目标库为 **人大金仓**（与 PostgreSQL 语法兼容的写法即可）。
- **布尔语义**：用整型 **`1` / `0`**（如 `INT NOT NULL DEFAULT 0`），不用 `BOOLEAN`。
- **枚举 / 状态 / 类型码**：用 **`VARCHAR`** 存字符串码值（可与字典说明配合），**不用**数值枚举列。

## 必备审计与逻辑删除字段

每张业务表须包含以下列（名称、语义一致；类型可与现有库对齐）：

```sql
deleted      INT         NOT NULL DEFAULT 0,
creator      VARCHAR(64),
create_time  TIMESTAMP,
updater      VARCHAR(64),
update_time  TIMESTAMP
```

- `deleted`：`0` 未删除，`1` 已删除（逻辑删除）。

## 示例（片段）

```sql
CREATE TABLE IF NOT EXISTS t_tm_example (
    id           BIGSERIAL PRIMARY KEY,
    status       VARCHAR(32)  NOT NULL DEFAULT 'draft',
    enabled      INT          NOT NULL DEFAULT 1,
    deleted      INT          NOT NULL DEFAULT 0,
    creator      VARCHAR(64),
    create_time  TIMESTAMP,
    updater      VARCHAR(64),
    update_time  TIMESTAMP
);
```

## 反例

```sql
-- ❌ 外键 / 唯一约束
CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES t_tm_project(id),
UNIQUE (project_id, subject_id)

-- ❌ 布尔类型列
is_active BOOLEAN NOT NULL DEFAULT TRUE

-- ❌ 用数值表示枚举
status SMALLINT NOT NULL  -- 应改为 VARCHAR 存码值
```
