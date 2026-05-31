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

@Tag(name = "AI数据集")
@RestController
@RequestMapping("/api/aimodel/datasets")
public class DatasetController {

    @Resource
    private LazyCraftService lazyCraftService;

    @Operation(summary = "分页查询数据集")
    @GetMapping
    public Resp<Object> page(LazyCraftPageQueryDTO query) {
        return Resp.ok(lazyCraftService.pageDatasets(query));
    }

    @Operation(summary = "新建数据集")
    @PostMapping
    public Resp<Object> create(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/data/create_date_set", body, null));
    }

    @Operation(summary = "更新数据集标签")
    @PutMapping
    public Resp<Object> update(@RequestBody Map<String, Object> body) {
        body.put("type", "dataset");
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/tags/bindings/update", body, null));
    }

    @Operation(summary = "删除数据集")
    @DeleteMapping
    public Resp<Object> delete(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/data/delete", body, null));
    }

    @Operation(summary = "上传数据集文件")
    @PostMapping("/files/upload")
    public Resp<Object> upload(@RequestParam("file") MultipartFile file,
                               @RequestParam(defaultValue = "doc") String fileType,
                               @RequestParam Map<String, Object> form) {
        form.remove("file");
        form.put("file_type", fileType);
        return Resp.ok(lazyCraftService.upload("/data/upload", "file", new MultipartFile[]{file}, form));
    }

    @Operation(summary = "查询数据集详情")
    @GetMapping("/{id}")
    public Resp<Object> detail(@PathVariable Long id) {
        Map<String, Object> query = new HashMap<>();
        query.put("data_set_id", id);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/data", null, query));
    }

    @Operation(summary = "查询数据集版本列表")
    @GetMapping("/{id}/versions")
    public Resp<Object> versions(@PathVariable Long id,
                                 @RequestParam(defaultValue = "1") Long pageNum,
                                 @RequestParam(defaultValue = "10") Long pageSize,
                                 @RequestParam(defaultValue = "branch") String versionType) {
        Map<String, Object> query = new HashMap<>();
        query.put("data_set_id", id);
        query.put("page", pageNum);
        query.put("page_size", pageSize);
        query.put("version_type", versionType);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/data/version/list", null, query));
    }

    @Operation(summary = "查询数据集标签版本")
    @GetMapping("/{id}/tag-versions")
    public Resp<Object> tagVersions(@PathVariable Long id) {
        Map<String, Object> query = new HashMap<>();
        query.put("data_set_id", id);
        return Resp.ok(lazyCraftService.forward(HttpMethod.GET, "/data/tag/list", null, query));
    }

    @Operation(summary = "基于标签版本创建分支")
    @PostMapping("/versions/from-tag")
    public Resp<Object> createVersionFromTag(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/data/version/create_by_tag", body, null));
    }

    @Operation(summary = "发布数据集版本")
    @PatchMapping("/versions/{versionId}/publish")
    public Resp<Object> publishVersion(@PathVariable Long versionId) {
        Map<String, Object> body = new HashMap<>();
        body.put("data_set_version_id", versionId);
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/data/version/publish", body, null));
    }

    @Operation(summary = "删除数据集版本")
    @DeleteMapping("/versions")
    public Resp<Object> deleteVersion(@RequestBody Map<String, Object> body) {
        return Resp.ok(lazyCraftService.forward(HttpMethod.POST, "/data/version/delete", body, null));
    }

}

