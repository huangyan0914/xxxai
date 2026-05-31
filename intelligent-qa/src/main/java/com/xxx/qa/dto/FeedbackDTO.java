package com.xxx.qa.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FeedbackDTO {

    @NotBlank(message = "sessionId 不能为空")
    private String sessionId;

    @NotNull(message = "speakId 不能为空")
    private Long speakId;

    @NotNull(message = "满意度不能为空")
    private Boolean isSatisfied;

    private String userFeedback;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getSpeakId() { return speakId; }
    public void setSpeakId(Long speakId) { this.speakId = speakId; }

    public Boolean getIsSatisfied() { return isSatisfied; }
    public void setIsSatisfied(Boolean isSatisfied) { this.isSatisfied = isSatisfied; }

    public String getUserFeedback() { return userFeedback; }
    public void setUserFeedback(String userFeedback) { this.userFeedback = userFeedback; }
}

