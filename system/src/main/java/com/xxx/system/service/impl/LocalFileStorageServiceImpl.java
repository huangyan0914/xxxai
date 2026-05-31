package com.xxx.system.service.impl;

import com.xxx.common.exception.BizException;
import com.xxx.system.service.FileStorageService;
import com.xxx.system.vo.FileUploadVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local")
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.storage.local.path:./uploads}")
    private String storagePath;

    /**
     * 可选：用于拼接文件访问 URL 的前缀（如 http://host/api/system/files/{fileId}/download）。
     * 留空则不填充 url 字段。
     */
    @Value("${file.storage.local.base-url:}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        try {
            Path dir = Paths.get(storagePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("无法创建本地文件存储目录: " + storagePath, ex);
        }
    }

    @Override
    public FileUploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        String originalFileName = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "file.bin";

        String ext = extractExtension(originalFileName);
        String fileId = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);

        Path target = Paths.get(storagePath, fileId);
        try (InputStream in = file.getInputStream();
             OutputStream out = Files.newOutputStream(target)) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = in.read(buf)) != -1) {
                out.write(buf, 0, read);
            }
        } catch (IOException ex) {
            throw new BizException("保存文件到本地存储失败: " + ex.getMessage());
        }

        String type = resolveType(file, originalFileName);
        String url = buildUrl(fileId);
        return new FileUploadVO(fileId, originalFileName, file.getSize(), type, url);
    }

    @Override
    public byte[] download(String fileId) {
        if (!StringUtils.hasText(fileId)) {
            throw new BizException("文件ID不能为空");
        }
        Path filePath = resolveAndValidatePath(fileId);
        if (!Files.exists(filePath)) {
            throw new BizException("文件不存在");
        }
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new BizException("读取文件失败: " + ex.getMessage());
        }
    }

    @Override
    public void delete(String fileId) {
        if (!StringUtils.hasText(fileId)) {
            throw new BizException("文件ID不能为空");
        }
        Path filePath = resolveAndValidatePath(fileId);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new BizException("删除文件失败: " + ex.getMessage());
        }
    }

    /**
     * 解析文件路径并防止路径穿越攻击（Path Traversal）。
     */
    private Path resolveAndValidatePath(String fileId) {
        Path base = Paths.get(storagePath).toAbsolutePath().normalize();
        Path resolved = base.resolve(fileId).normalize();
        if (!resolved.startsWith(base)) {
            throw new BizException("非法的文件ID");
        }
        return resolved;
    }

    private String extractExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot > 0 && dot < fileName.length() - 1) {
            return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        }
        return "";
    }

    private String resolveType(MultipartFile file, String originalFileName) {
        if (StringUtils.hasText(file.getContentType())) {
            return file.getContentType();
        }
        String ext = extractExtension(originalFileName);
        return ext.isEmpty() ? "unknown" : ext;
    }

    private String buildUrl(String fileId) {
        if (!StringUtils.hasText(baseUrl)) {
            return "/api/system/files/" + fileId + "/download";
        }
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + "/" + fileId + "/download";
    }
}

