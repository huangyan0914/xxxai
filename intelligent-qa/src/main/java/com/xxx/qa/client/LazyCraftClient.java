package com.xxx.qa.client;

import com.xxx.common.exception.BizException;
import com.xxx.qa.config.LazyCraftProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LazyCraftClient {

    private static final Logger log = LoggerFactory.getLogger(LazyCraftClient.class);

    @Resource
    private LazyCraftProperties properties;

    @Resource
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 初始化对话用户并获取临时 Token
     *
     * @param existingToken 已有 token（可为 null）
     * @return 新的或续期的临时 token
     */
    public String initToken(String existingToken) {
        String url = properties.getBaseUrl() + "/console/api/conversation/" + properties.getAppId() + "/init";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (existingToken != null) {
            headers.set("TempToken", existingToken);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.info("[LazyCraft] initToken request => url={}, hasExistingToken={}", url, existingToken != null);
        try {
            String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            JsonNode root = objectMapper.readTree(response);
            String token = root.path("token").asText();
            log.info("[LazyCraft] initToken response => token={}", token);
            return token;
        } catch (Exception e) {
            log.error("LazyCraft initToken failed", e);
            throw new BizException("初始化对话 Token 失败");
        }
    }

    /**
     * 获取当前用户会话列表
     */
    public String listSessions(String tempToken) {
        String url = properties.getBaseUrl() + "/console/api/conversation/" + properties.getAppId() + "/sessions";
        HttpHeaders headers = buildHeaders(tempToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.info("[LazyCraft] listSessions request => url={}", url);
        try {
            String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            log.info("[LazyCraft] listSessions response => {}", response);
            return response;
        } catch (Exception e) {
            log.error("LazyCraft listSessions failed", e);
            throw new BizException("获取会话列表失败");
        }
    }

    /**
     * 获取某会话的历史消息
     */
    public String getHistory(String tempToken, String sessionId) {
        String url = properties.getBaseUrl() + "/console/api/conversation/" + properties.getAppId()
                + "/history?sessionid=" + sessionId;
        HttpHeaders headers = buildHeaders(tempToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.info("[LazyCraft] getHistory request => url={}, sessionId={}", url, sessionId);
        try {
            String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            log.info("[LazyCraft] getHistory response => sessionId={}, body={}", sessionId, response);
            return response;
        } catch (Exception e) {
            log.error("LazyCraft getHistory failed, sessionId={}", sessionId, e);
            throw new BizException("获取会话历史失败");
        }
    }

    /**
     * 发送消息并将 SSE 流转发给前端
     */
    public void sendMessageStream(String tempToken, String sessionId, String input,
                                   List<Object> files, SseEmitter emitter) {
        String url = properties.getBaseUrl() + "/console/api/conversation/" + properties.getAppId() + "/run";

        Map<String, Object> body = new HashMap<>();
        body.put("sessionid", sessionId);
        body.put("inputs", new String[]{input});
        body.put("files", files != null ? files : new Object[0]);
        body.put("mode", "publish");

        log.info("[LazyCraft] sendMessageStream request => url={}, sessionId={}, input={}, files={}",
                url, sessionId, input, files);

        RequestCallback requestCallback = request -> {
            request.getHeaders().set("TempToken", tempToken);
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            objectMapper.writeValue(request.getBody(), body);
        };

        ResponseExtractor<Void> responseExtractor = response -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data:")) {
                        String data = line.substring(5).trim();
                        log.debug("[LazyCraft] sendMessageStream SSE event => sessionId={}, data={}", sessionId, data);
                        emitter.send(SseEmitter.event().data(data));
                    }
                }
                log.info("[LazyCraft] sendMessageStream completed => sessionId={}", sessionId);
                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                emitter.complete();
            } catch (Exception e) {
                log.error("SSE stream error", e);
                emitter.completeWithError(e);
            }
            return null;
        };

        try {
            restTemplate.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
        } catch (Exception e) {
            log.error("LazyCraft sendMessageStream failed", e);
            emitter.completeWithError(new BizException("发送消息失败"));
        }
    }

    /**
     * 提交消息反馈（点赞 / 点踩）
     */
    public void submitFeedback(String tempToken, String sessionId, Long speakId,
                                Boolean isSatisfied, String userFeedback) {
        String url = properties.getBaseUrl() + "/console/api/conversation/" + properties.getAppId() + "/feedback";
        HttpHeaders headers = buildHeaders(tempToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("sessionid", sessionId);
        body.put("speak_id", speakId);
        body.put("is_satisfied", isSatisfied);
        body.put("user_feedback", userFeedback != null ? userFeedback : "");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        log.info("[LazyCraft] submitFeedback request => url={}, sessionId={}, speakId={}, isSatisfied={}, userFeedback={}",
                url, sessionId, speakId, isSatisfied, userFeedback);
        try {
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            log.info("[LazyCraft] submitFeedback response => sessionId={}, speakId={}, body={}", sessionId, speakId, response);
        } catch (Exception e) {
            log.error("LazyCraft submitFeedback failed", e);
            throw new BizException("提交反馈失败");
        }
    }

    private HttpHeaders buildHeaders(String tempToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("TempToken", tempToken);
        return headers;
    }
}

