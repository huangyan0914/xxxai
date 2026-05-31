package com.xxx.aimodel.controller;

import com.xxx.aimodel.dto.LazyCraftPageQueryDTO;
import com.xxx.aimodel.service.LazyCraftService;
import com.xxx.common.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "AI模型仓库")
@RestController
@RequestMapping("/api/aimodel/models")
public class ModelRepositoryController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "分页查询模型")
    @GetMapping
    public Resp<Object> page(LazyCraftPageQueryDTO query) {
        return Resp.ok(lazyCraftService.pageModels(query));
    }

    @Operation(summary = "新建模型")
    @PostMapping
    public Resp<Object> create(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/mh/create", body, null));
    }

    @Operation(summary = "更新模型")
    @PutMapping
    public Resp<Object> update(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/mh/update", body, null));
    }

    @Operation(summary = "删除模型")
    @DeleteMapping
    public Resp<Object> delete(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/mh/delete", body, null));
    }

    @Operation(summary = "校验模型名称")
    @GetMapping("/check-name")
    public Resp<Object> checkName(@RequestParam String modelName) {
        Map<String, Object> query = new HashMap<>();
        query.put("model_name", modelName);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/mh/check/model_name", null, query));
    }

    @Operation(summary = "查询已存在模型")
    @GetMapping("/existing")
    public Resp<Object> existing(@RequestParam Map<String, Object> query) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/mh/exist_model_list", null, query));
    }

    @Operation(summary = "查询默认模型图标")
    @GetMapping("/default-icons")
    public Resp<Object> defaultIcons() {
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/mh/default_icon_list", null, null));
    }
}

