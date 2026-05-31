package com.xxx.aimodel.service.impl;

import com.xxx.aimodel.config.LazyCraftProperties;
import com.xxx.aimodel.dto.LazyCraftPageQueryDTO;
import com.xxx.aimodel.service.LazyCraftService;
import com.xxx.common.exception.BizException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LazyCraftServiceImpl implements LazyCraftService {

    private static final Logger log = LoggerFactory.getLogger(LazyCraftServiceImpl.class);

    private static final String HKDF_INFO = "ecdh-aes-key-exchange";

    private static final int LOG_BODY_MAX_LENGTH = 10000;

    @Resource
    private LazyCraftProperties lazyCraftProperties;

    @Resource
    private RestTemplate lazyCraftRestTemplate;

    @Resource
    private ObjectMapper objectMapper;

    private final SecureRandom secureRandom = new SecureRandom();

    private volatile String cachedToken;

    private volatile Instant tokenExpireAt = Instant.EPOCH;

    @Override
    public Object pageKnowledgeBases(LazyCraftPageQueryDTO query) {
        Map<String, Object> body = pageBody(query);
        body.put("search_tags", defaultList(query.getTags()));
        body.put("search_name", defaultString(query.getKeyword()));
        body.put("user_id", defaultList(query.getUserId()));
        return forward(HttpMethod.POST, "/kb/list", body, null);
    }

    @Override
    public Object pageModels(LazyCraftPageQueryDTO query) {
        Map<String, Object> body = pageBody(query);
        body.put("search_name", defaultString(query.getKeyword()));
//        body.put("model_type", defaultList(query.getModelType()));
        body.put("model_type", "local");
        body.put("model_kind", defaultString(query.getModelKind()));
        body.put("search_tags", defaultList(query.getTags()));
        body.put("user_id", defaultList(query.getUserId()));
        return forward(HttpMethod.POST, "/mh/list", body, null);
    }

    @Override
    public Object pageModelEvaluations(LazyCraftPageQueryDTO query) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", query.getPageNo());
        params.put("per_page", query.getPageSize());
        params.put("keyword", defaultString(query.getKeyword()));
        params.put("qtype", defaultString(query.getQtype(), "mine"));
        return forward(HttpMethod.GET, "/model_evalution/list", null, params);
    }

    @Override
    public Object pageInferenceServices(LazyCraftPageQueryDTO query) {
        Map<String, Object> body = new HashMap<>();
        body.put("page", query.getPageNo());
        body.put("per_page", query.getPageSize());
        body.put("search_name", defaultString(query.getKeyword()));
        body.put("user_id", defaultList(query.getUserId()));
        body.put("status", defaultList(query.getStatus()));
        return forward(HttpMethod.POST, "/infer-service/list", body, null);
    }

    @Override
    public Object pageFinetunes(LazyCraftPageQueryDTO query) {
        Map<String, Object> body = new HashMap<>();
        body.put("page", query.getPageNo());
        body.put("limit", query.getPageSize());
        body.put("search_name", defaultString(query.getKeyword()));
        body.put("user_id", defaultList(query.getUserId()));
        body.put("status", defaultList(query.getStatus()));
        return forward(HttpMethod.POST, "/finetune/list/page", body, null);
    }

    @Override
    public Object pageDatasets(LazyCraftPageQueryDTO query) {
        Map<String, Object> body = pageBody(query);
        body.put("data_type", defaultList(query.getDataType()));
        body.put("search_tags", defaultList(query.getTags()));
        body.put("user_id", defaultList(query.getUserId()));
        body.put("search_name", defaultString(query.getKeyword()));
        return forward(HttpMethod.POST, "/data/list", body, null);
    }

    @Override
    public Object forward(HttpMethod method, String lazyCraftPath, Map<String, Object> body, Map<String, ?> query) {
        return exchange(method, lazyCraftPath, body, query, true);
    }

    @Override
    public Object upload(String lazyCraftPath, String fileFieldName, MultipartFile[] files, Map<String, Object> form) {
        return multipartExchange(lazyCraftPath, fileFieldName, files, form, true);
    }

    @Override
    public ResponseEntity<byte[]> download(String lazyCraftPath, Map<String, ?> query) {
        return downloadExchange(lazyCraftPath, query, true);
    }

    private Object multipartExchange(String path, String fileFieldName, MultipartFile[] files, Map<String, Object> form,
                                     boolean retryOnUnauthorized) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (form != null) {
            for (Map.Entry<String, Object> entry : form.entrySet()) {
                if (entry.getValue() != null) {
                    body.add(entry.getKey(), entry.getValue());
                }
            }
        }
        if (files != null) {
            for (MultipartFile file : files) {
                body.add(fileFieldName, new MultipartInputStreamFileResource(file));
            }
        }
        URI uri = buildUri(path, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(getToken());
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            log.info("调用LazyCraft接口 method={}, url={}, form={}, files={}",
                    HttpMethod.POST, uri, toLogText(form), toLogText(fileSummaries(files)));
            ResponseEntity<Object> response = lazyCraftRestTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);
            log.info("LazyCraft接口返回 method={}, url={}, status={}, body={}",
                    HttpMethod.POST, uri, response.getStatusCodeValue(), toLogText(response.getBody()));
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            log.warn("LazyCraft接口异常 method={}, url={}, status={}, body={}",
                    HttpMethod.POST, uri, ex.getStatusCode().value(), limitLogText(ex.getResponseBodyAsString()));
            if (retryOnUnauthorized && ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                clearToken();
                return multipartExchange(path, fileFieldName, files, form, false);
            }
            throw new BizException("LazyCraft调用失败：" + ex.getResponseBodyAsString());
        }
    }

    private Object exchange(HttpMethod method, String path, Map<String, Object> body, Map<String, ?> query, boolean retryOnUnauthorized) {
        URI uri = buildUri(path, query);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getToken());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body == null ? new HashMap<>() : body, headers);
        try {
            log.info("调用LazyCraft接口 method={}, url={}, query={}, body={}",
                    method, uri, toLogText(query), toLogText(body));
            ResponseEntity<Object> response = lazyCraftRestTemplate.exchange(uri, method, entity, Object.class);
            log.info("LazyCraft接口返回 method={}, url={}, status={}, body={}",
                    method, uri, response.getStatusCodeValue(), toLogText(response.getBody()));
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            log.warn("LazyCraft接口异常 method={}, url={}, status={}, body={}",
                    method, uri, ex.getStatusCode().value(), limitLogText(ex.getResponseBodyAsString()));
            if (retryOnUnauthorized && ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                clearToken();
                return exchange(method, path, body, query, false);
            }
            throw new BizException("LazyCraft调用失败：" + ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<byte[]> downloadExchange(String path, Map<String, ?> query, boolean retryOnUnauthorized) {
        URI uri = buildUri(path, query);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            log.info("调用LazyCraft接口 method={}, url={}, query={}", HttpMethod.GET, uri, toLogText(query));
            ResponseEntity<byte[]> response = lazyCraftRestTemplate.exchange(uri, HttpMethod.GET, entity, byte[].class);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.putAll(response.getHeaders());
            log.info("LazyCraft接口返回 method={}, url={}, status={}, bytes={}",
                    HttpMethod.GET, uri, response.getStatusCodeValue(), response.getBody() == null ? 0 : response.getBody().length);
            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
        } catch (HttpStatusCodeException ex) {
            log.warn("LazyCraft接口异常 method={}, url={}, status={}, body={}",
                    HttpMethod.GET, uri, ex.getStatusCode().value(), limitLogText(ex.getResponseBodyAsString()));
            if (retryOnUnauthorized && ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                clearToken();
                return downloadExchange(path, query, false);
            }
            throw new BizException("LazyCraft下载失败：" + ex.getResponseBodyAsString());
        }
    }

    private synchronized String getToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpireAt)) {
            return cachedToken;
        }
        cachedToken = login();
        tokenExpireAt = Instant.now().plusSeconds(lazyCraftProperties.getTokenTtlSeconds());
        return cachedToken;
    }

    private synchronized void clearToken() {
        cachedToken = null;
        tokenExpireAt = Instant.EPOCH;
    }

    @SuppressWarnings("unchecked")
    private String login() {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", lazyCraftProperties.getUsername());
            payload.put("password", lazyCraftProperties.getPassword());
            Map<String, Object> encrypted = encryptPayload(payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<Map> response = lazyCraftRestTemplate.exchange(
                    buildUri("/login", null),
                    HttpMethod.POST,
                    new HttpEntity<>(encrypted, headers),
                    Map.class);
            Object data = response.getBody() == null ? null : response.getBody().get("data");
            if (data == null) {
                throw new BizException("LazyCraft登录失败：未返回token");
            }
            return String.valueOf(data);
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("LazyCraft登录失败：" + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> encryptPayload(Map<String, Object> payload) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"), secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String frontendPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        Map<String, Object> exchangeBody = new HashMap<>();
        exchangeBody.put("frontend_public_key", frontendPublicKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> response = lazyCraftRestTemplate.exchange(
                buildUri("/key_exchange", null),
                HttpMethod.POST,
                new HttpEntity<>(exchangeBody, headers),
                Map.class);

        Map<String, Object> keyData = response.getBody();
        if (keyData != null && keyData.get("data") instanceof Map) {
            keyData = (Map<String, Object>) keyData.get("data");
        }
        if (keyData == null || keyData.get("backend_public_key") == null || keyData.get("session_id") == null) {
            throw new BizException("LazyCraft密钥交换失败");
        }

        byte[] backendKeyBytes = Base64.getDecoder().decode(String.valueOf(keyData.get("backend_public_key")));
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPublicKey backendPublicKey = (ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(backendKeyBytes));

        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(backendPublicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        byte[] aesKey = hkdfSha256(sharedSecret, HKDF_INFO.getBytes(StandardCharsets.UTF_8), 32);

        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(128, iv));
        byte[] plainText = objectMapper.writeValueAsBytes(payload);
        byte[] encrypted = cipher.doFinal(plainText);
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        Map<String, Object> result = new HashMap<>();
        result.put("encrypted_data", Base64.getEncoder().encodeToString(combined));
        result.put("session_id", keyData.get("session_id"));
        return result;
    }

    private byte[] hkdfSha256(byte[] inputKeyMaterial, byte[] info, int length) throws Exception {
        byte[] salt = new byte[32];
        Mac extractMac = Mac.getInstance("HmacSHA256");
        extractMac.init(new SecretKeySpec(salt, "HmacSHA256"));
        byte[] pseudoRandomKey = extractMac.doFinal(inputKeyMaterial);

        Mac expandMac = Mac.getInstance("HmacSHA256");
        expandMac.init(new SecretKeySpec(pseudoRandomKey, "HmacSHA256"));
        byte[] okm = new byte[length];
        byte[] previous = new byte[0];
        int offset = 0;
        int counter = 1;
        while (offset < length) {
            expandMac.reset();
            expandMac.update(previous);
            expandMac.update(info);
            expandMac.update((byte) counter);
            previous = expandMac.doFinal();
            int copyLength = Math.min(previous.length, length - offset);
            System.arraycopy(previous, 0, okm, offset, copyLength);
            offset += copyLength;
            counter++;
        }
        return okm;
    }

    private URI buildUri(String path, Map<String, ?> query) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(lazyCraftProperties.consoleApiBaseUrl() + ensureLeadingSlash(path));
        if (query != null) {
            for (Map.Entry<String, ?> entry : query.entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if (value instanceof Iterable) {
                    for (Object item : (Iterable<?>) value) {
                        builder.queryParam(entry.getKey(), item);
                    }
                } else {
                    builder.queryParam(entry.getKey(), value);
                }
            }
        }
        return builder.build(true).toUri();
    }

    private Map<String, Object> pageBody(LazyCraftPageQueryDTO query) {
        Map<String, Object> body = new HashMap<>();
        body.put("page", query.getPageNo());
        body.put("page_size", query.getPageSize());
        return body;
    }

    private List<String> defaultList(List<String> values) {
        return CollectionUtils.isEmpty(values) ? new ArrayList<>() : values;
    }

    private String defaultString(String value) {
        return defaultString(value, "");
    }

    private String defaultString(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String ensureLeadingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.startsWith("/") ? value : "/" + value;
    }

    private List<Map<String, Object>> fileSummaries(MultipartFile[] files) {
        List<Map<String, Object>> summaries = new ArrayList<>();
        if (files == null) {
            return summaries;
        }
        for (MultipartFile file : files) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("name", file.getOriginalFilename());
            summary.put("size", file.getSize());
            summary.put("contentType", file.getContentType());
            summaries.add(summary);
        }
        return summaries;
    }

    private String toLogText(Object value) {
        if (value == null) {
            return "null";
        }
        try {
            return limitLogText(objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException ex) {
            return limitLogText(String.valueOf(value));
        }
    }

    private String limitLogText(String value) {
        if (value == null || value.length() <= LOG_BODY_MAX_LENGTH) {
            return value;
        }
        return value.substring(0, LOG_BODY_MAX_LENGTH) + "...(truncated, length=" + value.length() + ")";
    }

    private static class MultipartInputStreamFileResource extends InputStreamResource {

        private final MultipartFile file;

        MultipartInputStreamFileResource(MultipartFile file) {
            super(getInputStream(file));
            this.file = file;
        }

        @Override
        public String getFilename() {
            return file.getOriginalFilename();
        }

        @Override
        public long contentLength() {
            return file.getSize();
        }

        private static InputStream getInputStream(MultipartFile file) {
            try {
                return file.getInputStream();
            } catch (IOException ex) {
                throw new BizException("读取上传文件失败：" + ex.getMessage());
            }
        }
    }
}
