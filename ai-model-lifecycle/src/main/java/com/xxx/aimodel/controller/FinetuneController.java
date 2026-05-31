package com.xxx.aimodel.controller;

import com.xxx.aimodel.dto.LazyCraftPageQueryDTO;
import com.xxx.aimodel.service.LazyCraftService;
import com.xxx.common.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "AI模型微调")
@RestController
@RequestMapping("/api/aimodel/finetunes")
public class FinetuneController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "分页查询微调任务")
    @GetMapping
    public Resp<Object> page(LazyCraftPageQueryDTO query) {
        return Resp.ok(lazyCraftService.pageFinetunes(query));
    }

    @Operation(summary = "新建微调任务")
    @PostMapping
    public Resp<Object> create(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/finetune", normalizeCreateBody(body), null));
    }

    @Operation(summary = "暂停微调任务")
    @PatchMapping("/{id}/pause")
    public Resp<Object> pause(@PathVariable Long id) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/finetune/pause/" + id, null, null));
    }

    @Operation(summary = "恢复微调任务")
    @PatchMapping("/{id}/resume")
    public Resp<Object> resume(@PathVariable Long id) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/finetune/resume/" + id, null, null));
    }

    @Operation(summary = "取消微调任务")
    @PatchMapping("/{id}/cancel")
    public Resp<Object> cancel(@PathVariable Long id) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.DELETE, "/finetune/cancel/" + id, null, null));
    }

    @Operation(summary = "删除微调任务")
    @DeleteMapping("/{id}")
    public Resp<Object> delete(@PathVariable Long id) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.DELETE, "/finetune/delete/" + id, null, null));
    }

    @Operation(summary = "查询微调任务详情")
    @GetMapping("/{id}/detail")
    public Resp<Object> detail(@PathVariable Long id) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/finetune/detail/" + id, null, null));
    }

    @Operation(summary = "查询微调任务日志")
    @GetMapping("/{id}/log")
    public ResponseEntity<byte[]> log(@PathVariable Long id) {
        return lazyCraftService.download("/finetune/log/" + id, null);
    }

    @Operation(summary = "查询可微调基础模型")
    @GetMapping("/models")
    public Resp<Object> models(@RequestParam Map<String, Object> query) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/finetune/ft/models", null, query));
    }

    @Operation(summary = "查询可用于微调的数据集")
    @GetMapping("/datasets")
    public Resp<Object> datasets(@RequestParam(defaultValue = "mine") String qtype) {
        Map<String, Object> query = new HashMap<>();
        query.put("qtype", qtype);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/finetune/datasets", null, query));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeCreateBody(Map<String, Object> body) {
        Map<String, Object> normalized = new HashMap<>(body);
        Object baseValue = normalized.get("base");
        if (!(baseValue instanceof Map)) {
            return normalized;
        }

        Map<String, Object> base = new HashMap<>((Map<String, Object>) baseValue);
        base.put("base_model", normalizeInteger(base.get("base_model")));
        base.put("base_model_key", normalizeBaseModelKey(base.get("base_model_key")));
        if (base.containsKey("datasets_type")) {
            base.put("datasets_type", normalizeDatasetTypes(base.get("datasets_type")));
        }
        normalized.put("base", base);

        Object configValue = normalized.get("finetune_config");
        if (configValue instanceof Map) {
            Map<String, Object> config = new HashMap<>((Map<String, Object>) configValue);
            if (config.containsKey("learning_rate") && config.get("learning_rate") != null) {
                config.put("learning_rate", String.valueOf(config.get("learning_rate")));
            }
            config.remove("num_gpus");
            normalized.put("finetune_config", config);
        }
        return normalized;
    }

    private Object normalizeInteger(Object value) {
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ignored) {
                return value;
            }
        }
        return value;
    }

    private Object normalizeBaseModelKey(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        if (text.contains(":") || text.isEmpty()) {
            return value;
        }
        return text + ":" + text;
    }

    private List<Object> normalizeDatasetTypes(Object value) {
        List<Object> result = new ArrayList<>();
        if (value instanceof Iterable) {
            for (Object item : (Iterable<?>) value) {
                result.add(normalizeDatasetType(item));
            }
        } else if (value != null) {
            result.add(normalizeDatasetType(value));
        }
        return result;
    }

    private Object normalizeDatasetType(Object value) {
        if (value == null) {
            return "DATASET_FORMAT_ALPACA";
        }
        String text = String.valueOf(value);
        if ("Alpaca_fine_tuning".equals(text) || "Alpaca_pre_train".equals(text)) {
            return "DATASET_FORMAT_ALPACA";
        }
        if ("Openai_fine_tuning".equals(text)) {
            return "DATASET_FORMAT_OPENAI";
        }
        if ("Sharegpt_fine_tuning".equals(text)) {
            return "DATASET_FORMAT_SHAREGPT";
        }
        return value;
    }
}

