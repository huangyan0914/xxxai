# AI 生命周期分页提取适配说明

## 结论
已在 `x-frontend/ai-model-lifecycle-frontend/src/views/modules/shared/useLifecycleWorkspace.ts` 中调整分页提取逻辑，仅支持后端当前返回结构：

```json
{
  "code": "0",
  "msg": "成功",
  "data": {
    "page": 1,
    "page_size": 10,
    "total": 1,
    "has_more": false,
    "data": []
  }
}
```

现在列表仅从 `data.data` 提取，总数仅从 `data.total` 提取。

## 修改点
- `normalizePage`：直接基于当前分页容器提取 `records` 与 `total`。
- `extractRows`：仅返回当前分页容器内的 `data` 数组。
- `extractTotal`：仅读取当前分页容器内的 `total`。
- `extractPageContainer`：仅识别 `page + page_size + total + data[]` 的结构。

## 上手指南
1. 页面列表接口需返回 `data: { page, page_size, total, data: [] }`，否则不会被当前逻辑识别。
2. 若后端字段名发生变化，需要同步修改 `extractPageContainer` 判定条件与 `extractRows`/`extractTotal` 的读取字段。
3. 若出现“总数正确但列表为空”，先确认返回体中的列表是否位于 `data.data`，且其类型为数组。
