package com.xxx.system.controller;

import com.xxx.common.resp.Resp;
import com.xxx.system.service.FileStorageService;
import com.xxx.system.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Tag(name = "文件", description = "通用文件上传接口")
@RestController
@RequestMapping("/api/system/files")
public class FileController {

    @Resource
    private FileStorageService fileStorageService;

    @Operation(summary = "上传文件", description = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resp<FileUploadVO> upload(
            @Parameter(description = "上传文件", required = true)
            @RequestPart("file") MultipartFile file) {
        return Resp.ok(fileStorageService.upload(file));
    }

    @Operation(summary = "下载文件", description = "按 fileId 下载文件")
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @Parameter(description = "文件ID", required = true) @PathVariable String fileId,
            @Parameter(description = "下载文件名（可选）") @RequestParam(required = false) String fileName) {
        byte[] bytes = fileStorageService.download(fileId);
        String downloadName = (fileName == null || fileName.trim().isEmpty()) ? (fileId) : fileName;
        String encoded;
        try {
            encoded = URLEncoder.encode(downloadName, "UTF-8");
        } catch (Exception ex) {
            encoded = downloadName;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encoded)
                .body(bytes);
    }

    @Operation(summary = "删除文件", description = "按 fileId 删除文件")
    @DeleteMapping("/{fileId}")
    public Resp<Boolean> delete(
            @Parameter(description = "文件ID", required = true) @PathVariable String fileId) {
        fileStorageService.delete(fileId);
        return Resp.ok(true);
    }
}

