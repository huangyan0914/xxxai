# Git Commit 提交规范

## 格式

```
<type>(<scope>): <subject>
```

- **type**：提交类型（必填，见下表）
- **scope**：影响范围，如模块名或文件名（可选）
- **subject**：简短描述，中英文均可（必填）

## 示例

```bash
feature(SubjectManagement): 新增培训科目管理页面
fix(login): 修复登录页面跳转异常
refactor(api): 重构接口请求层
chore: 更新依赖版本
```

## Type 枚举

| type       | 说明                         |
|------------|------------------------------|
| `feature`  | 新功能                       |
| `bug`      | 标记 Bug（非修复）           |
| `fix`      | Bug 修复                     |
| `ui`       | UI / 样式调整                |
| `docs`     | 文档更新                     |
| `style`    | 代码格式调整（不影响逻辑）   |
| `perf`     | 性能优化                     |
| `release`  | 版本发布                     |
| `deploy`   | 部署相关                     |
| `refactor` | 重构（不涉及功能或 Bug 修复）|
| `test`     | 添加或修改测试               |
| `chore`    | 构建 / 依赖 / 配置变更       |
| `revert`   | 回滚提交                     |
| `merge`    | 合并分支                     |
| `build`    | 构建系统相关                 |

> ⚠️ 注意：本项目不使用标准 Conventional Commits 的 `feat`，请使用 `feature`。
