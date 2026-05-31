package com.xxx.aimodel.controller;

import com.xxx.aimodel.dto.LazyCraftPageQueryDTO;
import com.xxx.aimodel.service.LazyCraftService;
import com.xxx.common.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "AI知识库")
@RestController
@RequestMapping("/api/aimodel/knowledge-bases")
public class KnowledgeBaseController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "分页查询知识库")
    @GetMapping
    public Resp<Object> page(LazyCraftPageQueryDTO query) {
        return Resp.ok(lazyCraftService.pageKnowledgeBases(query));
    }

    @Operation(summary = "新建知识库")
    @PostMapping
    public Resp<Object> create(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/kb/create", body, null));
    }

    @Operation(summary = "更新知识库")
    @PutMapping
    public Resp<Object> update(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/kb/update", body, null));
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping
    public Resp<Object> delete(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/kb/delete", body, null));
    }

    @Operation(summary = "上传知识库文件")
    @PostMapping("/files/upload")
    public Resp<Object> upload(@RequestParam("file") MultipartFile file,
                               @RequestParam Map<String, Object> form) {
        form.remove("file");
        return Resp.ok(lazyCraftService.upload("/kb/upload", "file", new MultipartFile[]{file}, form));
    }

    @Operation(summary = "查询知识库文件列表")
    @GetMapping("/{id}/files")
    public Resp<Object> files(@PathVariable String id,
                              @RequestParam(defaultValue = "1") Long pageNum,
                              @RequestParam(defaultValue = "10") Long pageSize) {
        Map<String, Object> query = new HashMap<>();
        query.put("knowledge_base_id", id);
        query.put("page", pageNum);
        query.put("page_size", pageSize);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/kb/file/list", null, query));
    }

    @Operation(summary = "将已上传文件加入知识库")
    @PostMapping("/{id}/files")
    public Resp<Object> addFiles(@PathVariable String id, @RequestBody Map<String, Object> body) {
        body.put("knowledge_base_id", id);
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/kb/file/add", body, null));
    }

    @Operation(summary = "删除知识库文件")
    @DeleteMapping("/files")
    public Resp<Object> deleteFile(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/kb/file/delete", body, null));
    }

    @Operation(summary = "下载知识库文件")
    @GetMapping("/files/download")
    public ResponseEntity<byte[]> download(@RequestParam Map<String, Object> query) {
        return lazyCraftService.download("/kb/download", query);
    }

    @Operation(summary = "查询知识库引用情况")
    @GetMapping("/{id}/references")
    public Resp<Object> references(@PathVariable String id) {
        Map<String, Object> query = new HashMap<>();
        query.put("id", id);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/kb/reference-result", null, query));
    }
}

