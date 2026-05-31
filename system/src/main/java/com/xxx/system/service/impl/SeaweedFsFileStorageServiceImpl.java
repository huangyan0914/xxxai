package com.xxx.system.service.impl;

import com.xxx.common.exception.BizException;
import com.xxx.system.service.FileStorageService;
import com.xxx.system.vo.FileUploadVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "seaweed", matchIfMissing = true)
public class SeaweedFsFileStorageServiceImpl implements FileStorageService {

    @Value("${seaweedfs.master-url:http://127.0.0.1:9333}")
    private String seaweedMasterUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SeaweedFsFileStorageServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public FileUploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        String originalFileName = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "file.bin";

        AssignResult assignResult = requestAssign();
        String uploadUrl = buildUploadUrl(assignResult.getUploadHost(), assignResult.getFid());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return originalFileName;
                }
            };
            body.add("file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BizException("上传到SeaweedFS失败");
            }
        } catch (IOException ex) {
            throw new BizException("读取上传文件失败: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BizException("上传到SeaweedFS失败: " + ex.getMessage());
        }

        String fileUrl = buildPublicFileUrl(assignResult.getUploadHost(), assignResult.getFid());
        String type = resolveType(file, originalFileName);
        return new FileUploadVO(assignResult.getFid(), originalFileName, file.getSize(), type, fileUrl);
    }

    @Override
    public byte[] download(String fileId) {
        if (!StringUtils.hasText(fileId)) {
            throw new BizException("文件ID不能为空");
        }
        String host = lookupFileHost(fileId);
        String downloadUrl = buildPublicFileUrl(host, fileId);
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(downloadUrl, byte[].class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BizException("下载文件失败");
            }
            return response.getBody();
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("下载文件失败: " + ex.getMessage());
        }
    }

    @Override
    public void delete(String fileId) {
        if (!StringUtils.hasText(fileId)) {
            throw new BizException("文件ID不能为空");
        }
        String host = lookupFileHost(fileId);
        String deleteUrl = buildPublicFileUrl(host, fileId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BizException("删除文件失败");
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("删除文件失败: " + ex.getMessage());
        }
    }

    private AssignResult requestAssign() {
        String assignUrl = trimTrailingSlash(seaweedMasterUrl) + "/dir/assign";
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(assignUrl, String.class);
        } catch (Exception ex) {
            throw new BizException("调用SeaweedFS分配接口失败: " + ex.getMessage());
        }

        if (!response.getStatusCode().is2xxSuccessful() || !StringUtils.hasText(response.getBody())) {
            throw new BizException("SeaweedFS分配文件ID失败");
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String fid = textValue(jsonNode, "fid");
            String publicUrl = textValue(jsonNode, "publicUrl");
            String url = textValue(jsonNode, "url");
            String uploadHost = StringUtils.hasText(publicUrl) ? publicUrl : url;

            if (!StringUtils.hasText(fid) || !StringUtils.hasText(uploadHost)) {
                throw new BizException("SeaweedFS分配结果缺少必要字段");
            }
            return new AssignResult(fid, uploadHost);
        } catch (IOException ex) {
            throw new BizException("解析SeaweedFS分配结果失败: " + ex.getMessage());
        }
    }

    private String textValue(JsonNode node, String fieldName) {
        JsonNode child = node.get(fieldName);
        if (child == null || child.isNull()) {
            return null;
        }
        return child.asText();
    }

    private String buildUploadUrl(String uploadHost, String fid) {
        String host = normalizeHost(uploadHost);
        return trimTrailingSlash(host) + "/" + fid;
    }

    private String buildPublicFileUrl(String uploadHost, String fid) {
        String host = normalizeHost(uploadHost);
        return trimTrailingSlash(host) + "/" + fid;
    }

    private String normalizeHost(String host) {
        if (host.startsWith("http://") || host.startsWith("https://")) {
            return host;
        }
        return "http://" + host;
    }

    private String trimTrailingSlash(String value) {
        String trimmed = value;
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static class AssignResult {
        private final String fid;
        private final String uploadHost;

        private AssignResult(String fid, String uploadHost) {
            this.fid = fid;
            this.uploadHost = uploadHost;
        }

        public String getFid() {
            return fid;
        }

        public String getUploadHost() {
            return uploadHost;
        }
    }

    private String lookupFileHost(String fileId) {
        String volumeId = parseVolumeId(fileId);
        String lookupUrl = trimTrailingSlash(seaweedMasterUrl) + "/dir/lookup?volumeId=" + volumeId;
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(lookupUrl, String.class);
        } catch (Exception ex) {
            throw new BizException("查询文件位置失败: " + ex.getMessage());
        }
        if (!response.getStatusCode().is2xxSuccessful() || !StringUtils.hasText(response.getBody())) {
            throw new BizException("查询文件位置失败");
        }
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode locations = root.get("locations");
            if (locations == null || !locations.isArray() || locations.size() == 0) {
                throw new BizException("文件位置不存在");
            }
            JsonNode first = locations.get(0);
            String publicUrl = textValue(first, "publicUrl");
            String url = textValue(first, "url");
            String host = StringUtils.hasText(publicUrl) ? publicUrl : url;
            if (!StringUtils.hasText(host)) {
                throw new BizException("文件位置不存在");
            }
            return host;
        } catch (IOException ex) {
            throw new BizException("解析文件位置失败: " + ex.getMessage());
        }
    }

    private String parseVolumeId(String fileId) {
        int idx = fileId.indexOf(',');
        if (idx <= 0) {
            throw new BizException("文件ID格式不正确");
        }
        return fileId.substring(0, idx);
    }

    private String resolveType(MultipartFile file, String originalFileName) {
        if (StringUtils.hasText(file.getContentType())) {
            return file.getContentType();
        }
        int dot = originalFileName.lastIndexOf('.');
        if (dot > -1 && dot < originalFileName.length() - 1) {
            return originalFileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        }
        return "unknown";
    }
}

