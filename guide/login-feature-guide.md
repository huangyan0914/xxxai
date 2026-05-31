# 登录认证功能上手指南

> 模块：`ops-management`（端口 9902）  
> 功能：用户名密码 + 图形验证码登录、JWT Token 鉴权、获取当前用户信息、按模块获取菜单权限

---

## 一、功能概览

| 接口 | 方法 | 路径 | 是否需要 Token |
|---|---|---|---|
| 获取图形验证码 | GET | `/api/auth/captcha` | ❌ |
| 登录 | POST | `/api/auth/login` | ❌ |
| 注销 | POST | `/api/auth/logout` | ✅ |
| 获取当前用户信息 | GET | `/api/auth/me` | ✅ |
| 获取当前用户菜单 | GET | `/api/auth/menus?module=xxx` | ✅ |

---

## 二、登录流程

```
1. GET /api/auth/captcha          → 获得 captchaKey + captchaImage（base64 PNG）
2. 前端展示验证码图片
3. POST /api/auth/login            → 提交 username/password/captchaKey/captchaCode
4. 响应：{ token, userId, username, realName, avatar }
5. 后续请求 Header 加：Authorization: Bearer {token}
```

### 登录请求示例

```json
POST /api/auth/login
{
  "username": "admin",
  "password": "123456",
  "captchaKey": "a1b2c3d4...",
  "captchaCode": "8024"
}
```

### 登录响应示例

```json
{
  "code": "0",
  "msg": "成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "avatar": null
  }
}
```

---

## 三、Token 使用方式

所有需要登录的接口，请求头中携带：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Token 默认有效期 **24 小时**（可通过 `jwt.expire-seconds` 配置调整）。

---

## 四、获取当前用户菜单

```
GET /api/auth/menus              ← 全部模块菜单
GET /api/auth/menus?module=ops   ← 仅 ops 模块菜单
```

- 菜单以**树形结构**返回，`children` 为子菜单/按钮
- `permType` 字段区分 `MENU`（菜单）和 `BUTTON`（功能按钮）
- 仅返回当前用户通过角色（直接绑定 + 用户组继承）有权访问的菜单

---

## 五、数据库初始化

登录功能需要在 `t_s_permission` 表中添加 `module` 字段：

```sql
-- 执行 ops-management/src/main/resources/sql/alter-permission-add-module.sql
ALTER TABLE t_s_permission ADD COLUMN IF NOT EXISTS module VARCHAR(64);
```

执行后可为权限节点设置模块标识，如 `training`、`ops`、`assessment` 等。

---

## 六、配置说明

`ops-management/src/main/resources/application.yml`：

```yaml
jwt:
  secret: x-wxwh-jwt-secret-key-please-change-in-production-min32chars  # 生产环境务必修改，≥32字节
  expire-seconds: 86400  # Token 有效期（秒），默认 24 小时
```

---

## 七、架构说明

### JWT 鉴权流程

```
请求 → AuthInterceptor（common 模块）
         ├── 读取 Authorization: Bearer {token}
         ├── JwtUtil.parseToken(token) → LoginUser
         ├── UserContext.set(loginUser)  ← ThreadLocal 存储
         └── 放行 → Controller

Controller/Service 中获取当前用户：
  UserContext.getUserId()    → Long
  UserContext.getUsername()  → String
  UserContext.get()          → LoginUser
```

### 白名单（无需 Token 的路径）

- `/api/auth/captcha`
- `/api/auth/login`
- `/doc.html`、`/webjars/**`、`/swagger-resources/**`、`/v3/api-docs/**`

### 关键文件

| 文件 | 说明 |
|---|---|
| `common/.../auth/JwtUtil.java` | JWT 生成/解析 |
| `common/.../auth/UserContext.java` | ThreadLocal 用户上下文 |
| `common/.../web/AuthInterceptor.java` | 全局鉴权拦截器 |
| `ops-management/.../auth/CaptchaCache.java` | 内存验证码缓存（5分钟TTL） |
| `ops-management/.../service/AuthService.java` | 登录/获取用户/获取菜单 |
| `ops-management/.../controller/AuthController.java` | 认证接口入口 |
| `ops-management/.../mapper/SysPermissionMapper.xml` | 用户权限菜单 SQL |

---

## 八、RBAC 权限链

```
用户 → 直接角色（t_s_user_role）
     └─┬─→ 角色权限（t_s_role_permission）→ 权限（t_s_permission）
用户 → 用户组（t_s_user_group_member）
       └──→ 用户组角色（t_s_user_group_role）→ 角色权限 → 权限
```

两路来源取**并集去重**后构建菜单树。

---

## 九、注意事项

1. **验证码一次性**：`validate()` 成功后立即删除，不可重复使用
2. **账号冻结**：`status != "normal"` 的用户登录时会被拒绝
3. **密码加密**：使用 BCrypt，创建用户时需先 `passwordEncoder.encode(password)` 存储
4. **生产环境**：请修改 `jwt.secret` 为随机强密码，且长度 ≥ 32 字节
5. **多模块部署**：`AuthInterceptor` 已在 `common` 中，所有依赖 `common` 的模块自动生效
