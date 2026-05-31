package com.xxx.qa.service;

import com.xxx.qa.dto.FeedbackDTO;
import com.xxx.qa.dto.SendMessageDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface QaService {

    /**
     * 获取当前用户的会话列表（来自 LazyCraft）
     */
    List<Map<String, Object>> listSessions(Long userId);

    /**
     * 获取指定会话的历史消息（来自 LazyCraft）
     */
    List<Map<String, Object>> getHistory(Long userId, String sessionId);

    /**
     * 发送消息，以 SSE 流式返回 AI 回复
     */
    SseEmitter sendMessage(Long userId, SendMessageDTO dto);

    /**
     * 提交点赞 / 点踩反馈
     */
    void submitFeedback(Long userId, FeedbackDTO dto);
}

