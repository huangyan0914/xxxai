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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "AI模型评测")
@RestController
@RequestMapping("/api/aimodel/model-evaluations")
public class ModelEvaluationController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "分页查询模型评测任务")
    @GetMapping
    public Resp<Object> page(LazyCraftPageQueryDTO query) {
        return Resp.ok(lazyCraftService.pageModelEvaluations(query));
    }

    @Operation(summary = "新建模型评测任务")
    @PostMapping
    public Resp<Object> create(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/model_evalution/create_task", body, null));
    }

    @Operation(summary = "删除模型评测任务")
    @DeleteMapping("/{id}")
    public Resp<Object> delete(@PathVariable Long id) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/model_evalution/delete_task/" + id, null, null));
    }

    @Operation(summary = "获取可用于评测的推理模型")
    @GetMapping("/inference-models")
    public Resp<Object> inferenceModels(@RequestParam(defaultValue = "already") String qtype,
                                        @RequestParam(defaultValue = "1") Integer available,
                                        @RequestParam(defaultValue = "localLLM") String modelKind) {
        Map<String, Object> query = new HashMap<>();
        query.put("qtype", qtype);
        query.put("available", available);
        query.put("model_kind", modelKind);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/infer-service/list/draw", null, query));
    }

    @Operation(summary = "上传离线评测数据集")
    @PostMapping("/datasets/upload")
    public Resp<Object> uploadDataset(@RequestParam("files") MultipartFile[] files,
                                      @RequestParam Map<String, Object> form) {
        form.remove("files");
        return Resp.ok(lazyCraftService.upload("/model_evalution/upload_dataset", "files", files, form));
    }

    @Operation(summary = "下载评测汇总")
    @GetMapping("/{id}/summary-download")
    public ResponseEntity<byte[]> downloadSummary(@PathVariable Long id, @RequestParam Map<String, Object> query) {
        return lazyCraftService.download("/model_evalution/evaluation_summary_download/" + id, query);
    }
}

