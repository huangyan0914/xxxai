package com.xxx.aimodel.controller;

import com.xxx.aimodel.service.LazyCraftService;
import com.xxx.common.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Tag(name = "AI模型LazyCraft代理")
@RestController
@RequestMapping("/api/aimodel/lazycraft")
public class LazyCraftProxyController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "代理GET接口")
    @GetMapping
    public Resp<Object> get(@RequestParam String path, @RequestParam Map<String, Object> params) {
        params.remove("path");
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, decode(path), null, params));
    }

    @Operation(summary = "代理POST接口")
    @PostMapping
    public Resp<Object> post(@RequestParam String path, @RequestBody(required = false) Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, decode(path), body, null));
    }

    @Operation(summary = "代理PUT接口")
    @PutMapping
    public Resp<Object> put(@RequestParam String path, @RequestBody(required = false) Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.PUT, decode(path), body, null));
    }

    @Operation(summary = "代理PATCH接口")
    @PatchMapping
    public Resp<Object> patch(@RequestParam String path, @RequestBody(required = false) Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.PATCH, decode(path), body, null));
    }

    @Operation(summary = "代理DELETE接口")
    @DeleteMapping
    public Resp<Object> delete(@RequestParam String path, @RequestBody(required = false) Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.DELETE, decode(path), body, null));
    }

    private String decode(String path) {
        return UriUtils.decode(path, StandardCharsets.UTF_8);
    }
}

