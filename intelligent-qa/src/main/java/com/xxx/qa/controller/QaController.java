package com.xxx.qa.controller;

import com.xxx.common.auth.UserContext;
import com.xxx.common.resp.Resp;
import com.xxx.qa.dto.FeedbackDTO;
import com.xxx.qa.dto.IssueReportDTO;
import com.xxx.qa.dto.SendMessageDTO;
import com.xxx.qa.service.IssueReportService;
import com.xxx.qa.service.QaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(name = "智能问答")
@RestController
@RequestMapping("/api/qa")
public class QaController {

    @Resource
    private QaService qaService;

    @Resource
    private IssueReportService issueReportService;

    @Operation(summary = "获取会话列表")
    @GetMapping("/sessions")
    public Resp<List<Map<String, Object>>> listSessions() {
        Long userId = UserContext.getUserId();
        return Resp.ok(qaService.listSessions(userId));
    }

    @Operation(summary = "获取会话历史消息")
    @GetMapping("/sessions/{sessionId}/messages")
    public Resp<List<Map<String, Object>>> getHistory(
            @PathVariable String sessionId) {
        Long userId = UserContext.getUserId();
        return Resp.ok(qaService.getHistory(userId, sessionId));
    }

    @Operation(summary = "发送消息（SSE 流式响应）")
    @PostMapping(value = "/sessions/{sessionId}/messages", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter sendMessage(
            @PathVariable String sessionId,
            @RequestBody @Valid SendMessageDTO dto) {
        Long userId = UserContext.getUserId();
        dto.setSessionId(sessionId);
        return qaService.sendMessage(userId, dto);
    }

    @Operation(summary = "提交消息反馈（点赞/点踩）")
    @PostMapping("/messages/feedback")
    public Resp<Void> submitFeedback(
            @RequestBody @Valid FeedbackDTO dto) {
        Long userId = UserContext.getUserId();
        qaService.submitFeedback(userId, dto);
        return Resp.ok();
    }

    @Operation(summary = "提交问题反馈（存疑/错误）")
    @PostMapping("/messages/report")
    public Resp<Void> submitReport(
            @RequestBody @Valid IssueReportDTO dto) {
        Long userId = UserContext.getUserId();
        issueReportService.submitReport(dto, userId);
        return Resp.ok();
    }
}

