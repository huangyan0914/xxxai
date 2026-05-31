# FineTune 创建页基础模型默认值说明

## 结论
已将新建微调训练任务页面的基础模型默认值设置为 `Qwen3-0.6B-Thinking`。

## 变更位置
- `x-frontend/ai-model-lifecycle-frontend/src/views/FineTune/Create.vue`
- 表单初始值字段：`baseModel`

## 上手检查步骤
1. 打开“新建微调训练任务”页面。
2. 检查“基础模型”下拉框，页面首次加载时应默认显示 `Qwen3-0.6B-Thinking`。
3. 若后端返回模型列表中不包含该项，因组件启用了 `tag`，默认值仍可保留并提交。
