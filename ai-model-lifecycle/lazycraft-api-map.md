# LazyCraft 接口梳理

本文档整理 `ai-model-lifecycle` 需要封装的 LazyCraft 接口，来源包括当前后端封装代码和 LazyCraft 前端页面请求。LazyCraft 控制台接口默认前缀为 `/console/api`，当前配置见 `application.yml`：

- `lazycraft.base-url: http://localhost:30382`
- `lazycraft.console-api-prefix: /console/api`

除登录、密钥交换外，控制台接口一般需要携带 `Authorization: Bearer <token>`。接口返回结构在不同模块中不完全一致，封装时应以 LazyCraft 原始返回为准，避免自行建库表保存业务数据。

当前整体方案暂时不使用应用相关接口。应用的增删改查、工作流配置、发布和 API 启停等操作临时由人工在 LazyCraft 界面进行调整，本文档中的应用相关接口仅作为后续封装参考。

## 通用接口

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/key_exchange` | GET | 登录前密钥交换 | 无 | 服务端公钥、会话密钥相关信息 | 否 |
| `/login` | POST | 登录获取控制台 token | 加密后的用户名、密码等登录载荷 | token、用户信息 | 否 |
| `/tags` | GET | 获取标签列表 | `type`，常见值：`knowledgebase`、`model`、`dataset`、`app`、`modelAdjust`、`inference` | 标签数组 | 否 |
| `/tags/bindings/update` | POST | 更新资源标签绑定 | `type`、`tag_names`、`target_id` | 更新结果 | 否 |
| `/tags/delete` | POST | 删除标签 | `name`、`type` | 删除结果 | 否 |

## 资源库 / 知识库

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/kb/list` | POST | 查询知识库列表 | `page`、`page_size`、`search_tags`、`search_name`、`user_id` | `data` 知识库列表、`total` 总数；条目含 `id`、`name`、`user_name`、`description`、`tags`、`ref_status` | 否 |
| `/kb/create` | POST | 创建知识库 | `name`、`tags`、`description` | 创建后的知识库信息，含 `id` | 否 |
| `/kb/update` | POST | 更新知识库 | `id`、`name`、`tags`、`description`，通常合并原始行数据提交 | 更新后的知识库信息 | 否 |
| `/kb/delete` | POST | 删除知识库 | `id` | 删除结果 | 否 |
| `/kb/upload` | POST | 上传知识库文件 | multipart `file`；支持 pdf、json、html、doc、docx、xls、xlsx、txt、csv、ppt、pptx、md、tex，普通文件约 50MB，压缩包约 500MB | `files` 数组，文件条目含 `id` | 否 |
| `/kb/file/add` | POST | 将已上传文件加入知识库 | `knowledge_base_id`、`file_ids` | 添加结果 | 否 |
| `/kb/file/list` | GET | 查询知识库文件列表 | `knowledge_base_id`、分页参数 | 文件列表、处理状态 | 否 |
| `/kb/file/delete` | POST | 删除知识库文件 | 文件 id、知识库 id | 删除结果 | 否 |
| `/kb/download` | GET | 下载知识库文件 | 文件 id 或知识库文件标识 | 文件流 | 否 |
| `/kb/reference-result` | GET | 查询知识库引用情况 | `id` | 引用结果 | 否 |

创建并上传知识库文件的推荐链路：

1. `POST /kb/create` 创建知识库。
2. `POST /tags/bindings/update` 绑定 `type=knowledgebase` 标签。
3. 对每个本地文件调用 `POST /kb/upload`，收集返回的 `files[].id`。
4. `POST /kb/file/add`，把文件 id 加入知识库。
5. `GET /kb/file/list` 轮询文件处理状态。

## 模型仓库 / 模型管理

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/mh/list` | POST | 查询模型列表 | `page`、`page_size`、`model_type`、`model_kind`、`status`、`search_tags`、`available`、`search_name` | `data` 模型列表、`total` 总数；条目含 `id`、`model_name`、`model_type`、`model_kind`、`model_list`、`tags`、`status`、`available` | 否 |
| `/mh/create` | POST | 创建 / 纳管模型 | `model_name`、`model_type`、`model_kind`、`model_from`、`model_url`、`model_list`、`icon`、`tag_names` 等，按模型来源变化 | 创建后的模型信息，含 `id` | 否 |
| `/mh/update` | POST | 更新模型 | 模型原始字段加编辑字段 | 更新后的模型信息 | 否 |
| `/mh/delete` | POST | 删除模型 | `model_id`、`qtype`、`namespace` | 删除结果 | 否 |
| `/mh/check/model_name` | GET | 校验模型名称 | `model_name` | 是否可用 | 否 |
| `/mh/exist_model_list` | GET | 获取已存在模型 | 无或查询条件 | 模型名称列表 | 否 |
| `/mh/online_model_support_list` | GET | 获取支持的在线模型 | 无或模型类型 | 在线模型清单 | 是 |
| `/mh/default_icon_list` | GET | 获取默认模型图标 | 无 | 图标列表 | 否 |
| `/mh/upload/chunk` | POST | 上传模型文件分片 | multipart 分片、文件标识、分片序号 | 分片上传结果 | 是 |
| `/mh/upload/merge` | POST | 合并模型文件分片 | 文件标识、分片信息 | 合并结果 | 是 |
| `/mh/delete_uploaded_file` | POST | 删除已上传模型文件 | 文件标识 | 删除结果 | 是 |
| `/mh/upload/icon` | POST | 上传模型图标 | multipart `file` | 图标地址或文件信息 | 是 |

当前后端 `LazyCraftServiceImpl` 查询模型时固定传了 `model_type=local`，如果前端需要展示全部模型，需要调整封装参数。

## 模型仓库 / 模型评测

LazyCraft 路由为 `/modelWarehouse/modelTest`，接口路径中保留了原拼写 `/model_evalution`。

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/model_evalution/list` | GET | 查询评测任务列表 | `page`、`per_page`、`keyword`、`qtype` | `result.total`、`result.tasks`；任务含 `id`、`name`、`model_name`、`evaluation_method`、`process`、`status`、`status_zh`、`created_time` | 否 |
| `/model_evalution/create_task` | POST | 创建评测任务 | `task_name`、`model_name`、`model_type`、`evaluation_type`、`dataset_id`、`evaluation_method`、`dimensions`；AI 评测还需 `ai_evaluator_name`、`ai_evaluator_type`、`prompt`、`scene`、`scene_descrp` | 创建结果、任务 id | 否 |
| `/infer-service/list/draw` | GET | 获取可用于评测的推理模型 | `qtype=already`、`available=1`、`model_kind=localLLM` | 模型服务选项 | 否 |
| `/model_evalution/all_online_datasets` | GET | 获取在线评测数据集 | 无 | `result` 数据集列表 | 是 |
| `/model_evalution/upload_dataset` | POST | 上传离线评测数据集 | multipart `files`；支持 json、csv、xlsx、xls、zip、gz、tar，约 1GB 内 | 上传结果；成功项含 `result.dataset_id` | 否 |
| `/model_evalution/delete_task/{id}` | POST | 删除评测任务 | 路径 `id` | 删除结果 | 否 |
| `/model_evalution/evaluation_summary_download/{id}` | GET | 下载评测汇总 | 路径 `id`、`token` | xlsx 文件流 | 否 |

注意：LazyCraft 前端删除评测任务调用为 `POST /model_evalution/delete_task/{id}`，本项目后端封装应保持对齐。

## 推理服务 / 平台服务

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/infer-service/list` | POST | 查询推理服务列表 | `page`、`per_page`、`search_name`、`user_id`、`status` | `result.result` 服务组列表、`result.total`；组内含 `services` | 否 |
| `/infer-service/model/list` | GET | 查询可部署模型 | `model_type=local`、`model_kind`、`qtype=already` | 可部署模型列表 | 否 |
| `/infer-service/group/create` | POST | 创建服务组并部署服务 | `model_type`、`model_id`、`services`，以及服务名称、资源配置等表单字段 | 服务组创建结果 | 否 |
| `/infer-service/service/create` | POST | 在已有服务组下新增服务 | `group_id`、`model_type`、`model_id`、服务名称、资源配置等 | 服务创建结果 | 否 |
| `/infer-service/group/start` | POST | 启动服务组 | `group_id` | 启动结果 | 否 |
| `/infer-service/group/close` | POST | 关闭服务组 | `group_id` | 关闭结果 | 否 |
| `/infer-service/service/start` | POST | 启动单个服务 | `service_id` | 启动结果，前端以 `status===0` 判断成功 | 否 |
| `/infer-service/service/stop` | POST | 停止单个服务 | `service_id` | 停止结果 | 否 |
| `/infer-service/service/delete` | POST | 删除单个服务 | `service_id` | 删除结果 | 否 |

服务状态常见值包括 `InQueue`、`Pending`、`Running`、`Done`、`Cancelled`。前端列表会展示模型名称、模型类型、在线服务数、总服务数和每个服务的状态。

## 模型微调

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/finetune/list/page` | POST | 查询微调任务列表 | `page`、`limit`、`search_name`、`user_id`、`status` | `total`、`data`；任务含 `id`、`name`、`base_model_key`、`target_model_name`、`status`、`train_runtime`、`created_by_account`、`created_at`、`train_end_time` | 否 |
| `/finetune/ft/models` | GET | 查询可微调基础模型 | 无或筛选条件 | `data` 基础模型列表；条目含 `model`、`source`、`available` | 否 |
| `/finetune/datasets` | GET | 查询可用于微调的数据集 | `qtype=mine` 或 `qtype=already` | 数据集树或列表；条目含 `val_key`、`label`、`child`、数据格式 | 否 |
| `/finetune_param` | GET | 查询微调参数模板 | 无 | 参数模板列表 | 是 |
| `/finetune_param` | DELETE | 删除微调参数模板 | `record_id` | 删除结果 | 是 |
| `/finetune` | POST | 创建微调任务 | `base`、`finetune_config` | 创建结果、任务 id | 否 |
| `/finetune/detail/{id}` | GET | 查询微调任务详情 | 路径 `id` | 任务完整详情；含 `name`、`created_from_info`、`status`、`train_runtime`、`base_model_name`、`dataset_list`、`finetuning_type`、`finetune_config` | 否 |
| `/finetune/log/{id}` | GET | 查询微调任务训练日志 | 路径 `id` | 日志文本或字节流，LazyCraft 前端按 UTF-8 解码并处理转义字符 | 否 |
| `/finetune/resume/{id}` | GET | 继续微调任务 | 路径 `id` | 操作结果 | 否 |
| `/finetune/pause/{id}` | GET | 暂停微调任务 | 路径 `id` | 操作结果 | 否 |
| `/finetune/cancel/{id}` | DELETE | 取消微调任务 | 路径 `id` | 操作结果 | 否 |
| `/finetune/delete/{id}` | DELETE | 删除微调任务 | 路径 `id` | 删除结果 | 否 |

创建微调任务的主体结构：

```json
{
  "base": {
    "name": "任务名称",
    "base_model": 0,
    "base_model_key": "基础模型:来源文件",
    "target_model_name": "输出模型名称",
    "datasets": [30001],
    "datasets_type": ["DATASET_FORMAT_ALPACA"],
    "finetuning_type": "LoRA",
    "created_from": 1,
    "created_from_info": "模型微调"
  },
  "finetune_config": {
    "training_type": "SFT",
    "num_epochs": 2,
    "learning_rate": "0.00005",
    "lr_scheduler_type": "cosine",
    "batch_size": 2,
    "cutoff_len": 1024,
    "lora_r": 8,
    "lora_alpha": 8,
    "val_size": 0.01
  }
}
```

注意：

- LazyCraft 前端查询列表参数使用 `limit`，当前后端封装使用 `page_size`，建议后续对齐。
- LazyCraft 前端暂停、继续使用 `GET`，取消、删除使用 `DELETE`；本项目后端封装应保持对齐。
- `val_size` 在前端从百分比转换为小数，例如 10 转为 `0.1`。
- LazyCraft 实测创建请求使用 `DATASET_FORMAT_ALPACA` 这类数据集格式枚举；`learning_rate` 为字符串；LoRA 配置需传 `lora_r` 和 `lora_alpha`。
- `base_model_key` 必须是 `模型名:来源` 两段格式；来源缺失时按 LazyCraft 前端实测补为 `模型名:模型名`，否则后端拆分时会报 `not enough values to unpack`。
- 创建任务时应传 `batch_size`，不要传 `per_device_train_batch_size`。
- LazyCraft 微调创建参数中，`base` 必填 `finetuning_type`，`finetune_config` 必填 `training_type`、`lr_scheduler_type`、`cutoff_len`；`training_type` 不应放在 `base` 中，`finetuning_type` 和 `gradient_accumulation_steps` 不应放在 `finetune_config` 中。
- LazyCraft 微调方式枚举为 `LoRA`、`QLoRA`、`Full`。选择 `LoRA` 或 `QLoRA` 时，`finetune_config` 还需传 `lora_r`，可选值包括 `2`、`4`、`8`、`16`、`32`、`64`。

## 数据集 / 数据集管理

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/data/list` | POST | 查询数据集列表 | `page`、`page_size`、`data_type`、`search_tags`、`user_id`、`search_name` | `total`、`data`；数据集含 `id`、`name`、`tags`、`from_type`、`branches_num`、`user_name`、`created_at`、`data_type` | 否 |
| `/data/upload` | POST | 上传数据集文件 | multipart `file`、`file_type=doc\|pic`；文档支持 json、xls、csv、jsonl、txt、parquet、zip、gz、tar，约 1GB；图片支持图片文件和压缩包，约 2GB | `file_path` | 否 |
| `/data/create_date_set` | POST | 创建数据集 | `name`、`tag_names`、`description`、`data_type`、`upload_type`、`data_format`、`file_paths` 或 `file_urls`、`from_type=upload` | 创建后的数据集信息，含 `id` | 否 |
| `/data/delete` | POST | 删除数据集 | `data_set_id` | 删除结果 | 否 |
| `/data` | GET | 查询数据集详情 | `data_set_id` | 数据集详情 | 否 |
| `/data/version/list` | GET | 查询数据集版本列表 | `page`、`page_size`、`data_set_id`、`version_type=branch\|tag` | `total`、`data`；版本含 `id`、`name`、`status`、`updated_at`、`version_doing` | 否 |
| `/data/tag/list` | GET | 查询数据集标签版本 | `data_set_id` | `data` 标签版本列表 | 否 |
| `/data/version/create_by_tag` | POST | 基于标签版本创建分支 | `name`、`data_set_version_id` | 创建结果 | 否 |
| `/data/version/publish` | POST | 发布数据集版本 | `data_set_version_id` | 发布结果 | 否 |
| `/data/version/clean_or_augment` | POST | 对版本执行清洗或增强 | `data_set_version_id`、`data_set_version_name`、`script_agent`、`script_type`、`data_set_script_id` | 处理任务结果 | 是 |
| `/data/version/delete` | POST | 删除数据集版本 | `data_set_version_id` | 删除结果 | 否 |
| `/script/list_by_type` | GET | 查询可用数据处理脚本 | 脚本类型 | 脚本列表 | 是 |
| `/apps/list/page` | POST | 查询可作为数据处理 Agent 的应用 | `page`、`limit`、`qtype=already`、`search_tags=["数据处理"]`、`enable_api=true`、`is_published=true` | 应用列表 | 是 |
| `/data/version/export` | GET | 导出数据集版本 | 版本 id 等 | 文件流 | 是 |
| `/data/reflux/version/export` | GET | 导出回流数据版本 | 版本 id 等 | 文件流 | 是 |

创建数据集的主体结构：

```json
{
  "name": "数据集名称",
  "tag_names": ["标签"],
  "description": "描述",
  "data_type": "doc",
  "upload_type": "local",
  "data_format": "Alpaca_fine_tuning",
  "file_paths": ["上传接口返回的 file_path"],
  "file_urls": [],
  "from_type": "upload"
}
```

版本状态：

- `1`：正在处理
- `2`：已完成
- `3`：处理失败

数据集管理应以文件上传为主，不要求用户在页面直接输入文件 URL。URL 导入能力可保留为可选能力。数据集版本发布链路为：

1. `POST /data/upload` 上传文件，收集 `file_path`。
2. `POST /data/create_date_set` 创建数据集。
3. `POST /tags/bindings/update` 绑定 `type=dataset` 标签。
4. `GET /data/version/list` 查询初始分支版本和处理状态。
5. 可选调用 `POST /data/version/clean_or_augment` 进行清洗或增强。
6. `GET /data/version/list` 轮询版本状态，状态为 `2` 后调用 `POST /data/version/publish` 发布版本。

## 模型微调接口调用顺序链

1. 准备训练数据。
   - 已有数据集：`POST /data/list` 或 `GET /finetune/datasets?qtype=mine|already` 查询。
   - 新数据集：`POST /data/upload` 上传文件，`POST /data/create_date_set` 创建数据集，必要时 `POST /data/version/publish` 发布版本。
2. 查询基础模型：`GET /finetune/ft/models`。
3. 查询可用微调数据集：`GET /finetune/datasets?qtype=mine|already`。
4. 可选查询参数模板：`GET /finetune_param`。
5. 创建微调任务：`POST /finetune`，提交 `base` 和 `finetune_config`。
6. 轮询任务状态：`POST /finetune/list/page`。状态处于 `InQueue`、`Pending`、`InProgress`、`Download` 等进行中状态时继续轮询。
7. 查看任务详情：
   - 详情：`GET /finetune/detail/{id}`
   - 日志：`GET /finetune/log/{id}`
8. 任务控制：
   - 暂停：`GET /finetune/pause/{id}`
   - 继续：`GET /finetune/resume/{id}`
   - 取消：`DELETE /finetune/cancel/{id}`
   - 删除：`DELETE /finetune/delete/{id}`
9. 微调完成后，目标模型进入模型仓库或可被推理服务使用，再通过 `/infer-service/model/list`、`/infer-service/group/create` 部署。

## 提供问答服务接口调用顺序链（含 RAG）

LazyCraft 的问答服务主要通过“应用 / Workflow / 推理服务 / 知识库”组合完成。RAG 没有单一的创建接口，知识库检索节点和模型节点保存在应用工作流 `graph.nodes` 中，封装时应复用 LazyCraft 前端保存工作流时的 DSL 结构。

### 应用 API Key 调用接口

LazyCraft 界面中“发布应用并开启 API 访问”后，对外调用入口由后端源码 `parts/apikey/apikey_api.py` 中的 `ApikeyChat` 注册：

| 接口 | 方法 | 用途 | 鉴权 | 请求体 | 返回 |
| --- | --- | --- | --- | --- | --- |
| `/apikey/chat/{app_id}` | POST | 使用 API Key 调用已发布应用进行问答 | `Authorization: Bearer <API_KEY>` | `inputs` 必填，`mode` 可选，`files` 可选 | `{"result": ...}` |

完整访问地址形如：

```text
http://localhost:30382/console/api/apikey/chat/{app_id}
```

请求头：

```http
Content-Type: application/json
Authorization: Bearer <API_KEY>
```

请求体：

```json
{
  "inputs": ["问题内容"],
  "mode": "publish",
  "files": []
}
```

字段说明：

- `inputs`：必填数组，当前源码取 `inputs[0]` 作为用户输入。
- `mode`：可选，默认 `publish`。通常调用已发布应用时使用 `publish`。
- `files`：可选数组，未上传附件时传 `[]` 或省略。

示例：

```bash
curl -X POST "http://localhost:30382/console/api/apikey/chat/{app_id}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <API_KEY>" \
  -d '{"inputs":["你好，请介绍一下这个应用"],"mode":"publish","files":[]}'
```

源码校验逻辑：

- `Authorization` 必须是 `Bearer <api-key>` 格式。
- API Key 必须存在、启用且未过期。
- API Key 所属空间必须包含当前应用空间。
- 应用必须已开启服务，并且 `enable_api_call == "1"`。
- 接口内部以流式方式运行应用，但最终只返回 `event=result` 中的 `data`，包装为 `{"result": ...}`。

### 会话式应用对话接口

LazyCraft 还提供一组会话式应用对话接口，源码位于 `parts/conversation/speak_api.py`，用于应用对话页面。它和 `/apikey/chat/{app_id}` 的区别是：

- `/apikey/chat/{app_id}`：面向外部系统集成，使用 API Key，单次调用返回最终结果。
- `/conversation/{app_id}/...`：面向 Web 对话页，先换取临时 token，支持会话列表、历史记录、流式输出和反馈。

会话式接口统一前缀：

```text
http://localhost:30382/console/api/conversation/{app_id}
```

| 接口 | 方法 | 用途 | 鉴权 | 请求 / 查询参数 | 返回 |
| --- | --- | --- | --- | --- | --- |
| `/conversation/{app_id}/init` | GET | 初始化对话用户并获取临时 token | 无；也可传已有 `TempToken` 或 `_token` | 可选查询参数 `_token`；可选请求头 `TempToken` | `{"token": "<temp_token>"}` |
| `/conversation/{app_id}/sessions` | GET | 查询当前用户在该应用下的会话列表 | `TempToken: <temp_token>` | 无 | `{"data":[{"sessionid":"...","title":"...","order":1}]}` |
| `/conversation/{app_id}/history` | GET | 查询某个会话的历史消息 | `TempToken: <temp_token>` | `sessionid` 必填，`start_id` 可选 | `{"data":[消息...]}` |
| `/conversation/{app_id}/run` | POST | 发送消息并获取 SSE 流式响应 | `TempToken: <temp_token>` | JSON：`sessionid`、`inputs` 必填，`files`、`mode` 可选 | `text/event-stream` |
| `/conversation/{app_id}/feedback` | POST | 对历史消息提交满意度反馈 | `TempToken: <temp_token>` | JSON：`sessionid`、`speak_id`、`is_satisfied`、`user_feedback` | `{"result":"success"}` |

鉴权说明：

- `init` 不需要 API Key；没有传 token 时，服务端会生成一个访客用户 token。
- 后续接口通过 `TempToken: <temp_token>` 请求头识别用户。
- 源码里也会读取 `Authorization` 请求头作为兜底，但这里不是 `Bearer API_KEY` 语义，推荐统一使用 `TempToken`。

推荐调用链：

1. `GET /conversation/{app_id}/init` 获取 `token`。
2. 前端或调用方自行生成一个 `sessionid`，通常使用 UUID。
3. `POST /conversation/{app_id}/run`，请求头带 `TempToken`，请求体传 `sessionid` 和 `inputs`。
4. `GET /conversation/{app_id}/sessions` 查询会话列表。
5. `GET /conversation/{app_id}/history?sessionid={sessionid}` 查询历史记录。
6. 可选 `POST /conversation/{app_id}/feedback` 提交反馈。

初始化示例：

```bash
curl "http://localhost:30382/console/api/conversation/{app_id}/init"
```

会话列表示例：

```bash
curl "http://localhost:30382/console/api/conversation/{app_id}/sessions" \
  -H "TempToken: <temp_token>"
```

历史记录示例：

```bash
curl "http://localhost:30382/console/api/conversation/{app_id}/history?sessionid={sessionid}" \
  -H "TempToken: <temp_token>"
```

发送消息示例：

```bash
curl -N -X POST "http://localhost:30382/console/api/conversation/{app_id}/run" \
  -H "Content-Type: application/json" \
  -H "TempToken: <temp_token>" \
  -d '{"sessionid":"{sessionid}","inputs":["你好"],"mode":"publish","files":[]}'
```

`run` 请求体字段：

```json
{
  "sessionid": "会话ID，调用方生成并复用",
  "inputs": ["问题内容"],
  "mode": "publish",
  "files": []
}
```

`run` 返回为 SSE，事件格式为：

```text
data: {"flow_type":"app_run","event":"chunk","timestamp":...,"data":"..."}
data: {"flow_type":"app_run","event":"result","timestamp":...,"data":...}
data: {"flow_type":"app_run","event":"finish","timestamp":...,"data":{"status":"succeeded",...}}
data: {"flow_type":"app_run","event":"stop","timestamp":...,"data":null}
```

常见事件：

- `start`：开始执行。
- `chunk`：流式文本片段。
- `result`：最终结果。
- `finish`：执行成功或失败状态，成功时 `status=succeeded`，失败时 `status=failed` 且包含错误信息。
- `stop`：执行结束。

历史消息字段来自 `parts/conversation/fields.py`：

```json
{
  "id": 1,
  "from_who": "用户ID或lazyllm",
  "content": "消息内容",
  "turn_number": 1,
  "files": [],
  "created_at": "时间",
  "is_satisfied": true,
  "user_feedback": "反馈内容"
}
```

反馈示例：

```bash
curl -X POST "http://localhost:30382/console/api/conversation/{app_id}/feedback" \
  -H "Content-Type: application/json" \
  -H "TempToken: <temp_token>" \
  -d '{"sessionid":"{sessionid}","speak_id":1,"is_satisfied":true,"user_feedback":"回答有帮助"}'
```

附件上传接口位于 `parts/files/file_api.py`：

| 接口 | 方法 | 用途 | 请求 | 返回 |
| --- | --- | --- | --- | --- |
| `/files/upload` | POST | 上传本地文件供应用对话使用 | multipart `file` | `{"file_path":"..."}` |

上传后可将返回的 `file_path` 放入 `run` 的 `files` 数组中。

### 1. 准备知识库

1. `POST /kb/create` 创建知识库。
2. `POST /tags/bindings/update` 绑定知识库标签。
3. `POST /kb/upload` 上传文档文件。
4. `POST /kb/file/add` 加入知识库。
5. `GET /kb/file/list` 轮询文件处理状态。

### 2. 准备推理模型服务

1. `POST /mh/list` 或 `GET /infer-service/model/list` 查询可用模型。
2. `POST /infer-service/group/create` 创建服务组，或 `POST /infer-service/service/create` 在已有组新增服务。
3. `POST /infer-service/group/start` 或 `POST /infer-service/service/start` 启动服务。
4. `POST /infer-service/list` 轮询，直到服务状态可用。

### 3. 创建并配置应用工作流

当前阶段应用相关接口暂不封装调用。应用增删改查、工作流配置、发布、API 启停临时由人工在 LazyCraft 界面完成，下表仅保留为后续对接参考。

| 接口 | 方法 | 用途 | 关键参数 | 返回内容 | 暂不使用 |
| --- | --- | --- | --- | --- | --- |
| `/apps` | POST | 创建应用 | `name`、`description`、`icon`、`tag_names` | 应用信息，含 `id` | 是 |
| `/apps/list/page` | POST | 查询应用列表 | `page`、`limit`、`qtype`、`search_name`、`is_published`、`enable_api`、`search_tags` | 应用列表 | 是 |
| `/apps/{id}` | PUT | 更新应用基础信息 | 应用名称、描述、图标、标签等 | 更新结果 | 是 |
| `/apps/{id}` | DELETE | 删除应用 | 路径 `id` | 删除结果 | 是 |
| `/apps/{id}/workflows/draft` | GET | 获取应用草稿工作流 | 路径 `id` | 草稿工作流 DSL | 是 |
| `/apps/{id}/workflows/draft` | POST | 保存应用草稿工作流 | `graph`、`features`、`environment_variables`、`hash` | 保存结果 | 是 |
| `/apps/{id}/workflows/draft/run` | POST | 运行草稿工作流 | 输入变量、会话参数 | 运行结果或流式事件 | 是 |
| `/advanced-chat/workflows/draft/run` | POST | 调试高级聊天工作流 | 应用 id、输入、会话参数 | 调试结果或流式事件 | 是 |
| `/apps/{id}/workflows/draft/status` | GET | 查询草稿运行状态 | 路径 `id` | 运行状态 | 是 |
| `/apps/{id}/workflows/draft/debug-detail` | GET | 查询草稿调试详情 | 路径 `id`、运行 id | 调试详情 | 是 |
| `/apps/{id}/workflows/publish` | GET | 查询已发布工作流 | 路径 `id` | 已发布工作流 DSL | 是 |
| `/apps/{id}/workflows/cancel_publish` | POST | 取消发布 | 路径 `id` | 取消发布结果 | 是 |
| `/apps/{id}/enable_api` | POST | 启用或关闭应用 API | `enable_api`、`response_mode=streaming` 等 | 流式启用结果、API 状态 | 是 |
| `/apps/{id}/export` | GET | 导出应用 DSL | `format=json` | DSL 文件 | 是 |
| `/apps/import` | POST | 导入应用 DSL | multipart 文件 | 导入结果 | 是 |

保存 RAG 工作流时，`POST /apps/{id}/workflows/draft` 的关键结构如下：

```json
{
  "graph": {
    "nodes": [],
    "edges": [],
    "edgeMode": "bezier"
  },
  "features": {
    "retriever_resource": {
      "enabled": true
    }
  },
  "environment_variables": []
}
```

其中：

- `graph.nodes` 至少应包含输入节点、知识库检索节点、LLM / Chat 模型节点、回答节点。
- 知识库 id、检索参数、TopK、相似度阈值等位于知识库检索节点的 `data` 中。
- 模型服务 id、模型名称、温度、最大输出长度、提示词等位于 LLM / Chat 节点的 `data` 中。
- 精确节点 DSL 建议从 LazyCraft 前端配置完成后调用 `GET /apps/{id}/workflows/draft` 获取，再在本项目封装为模板。

### 4. 发布并对外提供问答服务

当前阶段该链路由人工在 LazyCraft 界面完成，后端暂不调用应用相关接口。后续需要自动化时，可参考以下接口顺序：

1. `POST /apps/{id}/workflows/draft` 保存带 RAG 的草稿工作流。
2. `POST /advanced-chat/workflows/draft/run` 或 `POST /apps/{id}/workflows/draft/run` 调试问答效果。
3. `GET /apps/{id}/workflows/draft/status`、`GET /apps/{id}/workflows/draft/debug-detail` 查看运行和节点详情。
4. 发布工作流。LazyCraft 前端已存在 `/apps/{id}/workflows/publish` 相关接口；发布动作的方法和请求体需在界面实际点击发布时再次抓包确认。
5. `POST /apps/{id}/enable_api` 启用应用 API，建议使用 `response_mode=streaming`。
6. 从应用详情、应用列表或启用 API 返回中获取访问地址。LazyCraft 前端会提供 `/agent/{id}` 页面链接，API 地址由应用服务信息返回。
7. 业务系统调用应用 API 或打开 `/agent/{id}`，即可提供含 RAG 检索的问答服务。
