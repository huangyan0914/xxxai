package com.xxx.qa.service.impl;

import com.xxx.common.exception.BizException;
import com.xxx.qa.client.LazyCraftClient;
import com.xxx.qa.dto.FeedbackDTO;
import com.xxx.qa.dto.SendMessageDTO;
import com.xxx.qa.service.QaService;
import com.xxx.qa.service.TokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class QaServiceImpl implements QaService {

    private static final Logger log = LoggerFactory.getLogger(QaServiceImpl.class);

    @Resource
    private TokenService tokenService;

    @Resource
    private LazyCraftClient lazyCraftClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public List<Map<String, Object>> listSessions(Long userId) {
        String tempToken = tokenService.getOrInitToken(userId.toString());
        String raw = lazyCraftClient.listSessions(tempToken);
        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode data = root.path("data");
            if (data.isMissingNode() || !data.isArray()) {
                return new ArrayList<>();
            }
            return objectMapper.convertValue(data, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Parse sessions failed", e);
            throw new BizException("解析会话列表失败");
        }
    }

    @Override
    public List<Map<String, Object>> getHistory(Long userId, String sessionId) {
        String tempToken = tokenService.getOrInitToken(userId.toString());
        String raw = lazyCraftClient.getHistory(tempToken, sessionId);
        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode data = root.path("data");
            if (data.isMissingNode() || !data.isArray()) {
                return new ArrayList<>();
            }
            return objectMapper.convertValue(data, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Parse history failed", e);
            throw new BizException("解析会话历史失败");
        }
    }

    @Override
    public SseEmitter sendMessage(Long userId, SendMessageDTO dto) {
        SseEmitter emitter = new SseEmitter(120_000L);
        String tempToken = tokenService.getOrInitToken(userId.toString());

        executor.submit(() ->
            lazyCraftClient.sendMessageStream(
                    tempToken,
                    dto.getSessionId(),
                    dto.getInput(),
                    dto.getFiles(),
                    emitter
            )
        );

        return emitter;
    }

    @Override
    public void submitFeedback(Long userId, FeedbackDTO dto) {
        String tempToken = tokenService.getOrInitToken(userId.toString());
        lazyCraftClient.submitFeedback(
                tempToken,
                dto.getSessionId(),
                dto.getSpeakId(),
                dto.getIsSatisfied(),
                dto.getUserFeedback()
        );
    }
}

