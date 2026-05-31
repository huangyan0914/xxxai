package com.xxx.aimodel.controller;

import com.xxx.aimodel.dto.LazyCraftPageQueryDTO;
import com.xxx.aimodel.service.LazyCraftService;
import com.xxx.common.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "AI推理服务")
@RestController
@RequestMapping("/api/aimodel/inference-services")
public class InferenceServiceController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "分页查询平台推理服务")
    @GetMapping
    public Resp<Object> page(LazyCraftPageQueryDTO query) {
        return Resp.ok(lazyCraftService.pageInferenceServices(query));
    }

    @Operation(summary = "查询可部署模型")
    @GetMapping("/deployable-models")
    public Resp<Object> models(@RequestParam(defaultValue = "local") String modelType,
                               @RequestParam(defaultValue = "localLLM") String modelKind,
                               @RequestParam(defaultValue = "already") String qtype) {
        Map<String, Object> query = new HashMap<>();
        query.put("model_type", modelType);
        query.put("model_kind", modelKind);
        query.put("qtype", qtype);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/infer-service/model/list", null, query));
    }

    @Operation(summary = "新建推理服务")
    @PostMapping
    public Resp<Object> create(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/group/create", body, null));
    }

    @Operation(summary = "为模型组添加推理服务")
    @PostMapping("/services")
    public Resp<Object> createService(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/service/create", body, null));
    }

    @Operation(summary = "启动推理服务")
    @PatchMapping("/start")
    public Resp<Object> start(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/service/start", body, null));
    }

    @Operation(summary = "停止推理服务")
    @PatchMapping("/stop")
    public Resp<Object> stop(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/service/stop", body, null));
    }

    @Operation(summary = "启动推理服务组")
    @PatchMapping("/groups/start")
    public Resp<Object> startGroup(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/group/start", body, null));
    }

    @Operation(summary = "关闭推理服务组")
    @PatchMapping("/groups/close")
    public Resp<Object> closeGroup(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/group/close", body, null));
    }

    @Operation(summary = "删除单个推理服务")
    @DeleteMapping("/services")
    public Resp<Object> deleteService(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/infer-service/service/delete", body, null));
    }
}

