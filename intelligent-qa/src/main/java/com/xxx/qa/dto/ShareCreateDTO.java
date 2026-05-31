package com.xxx.qa.dto;

import javax.validation.constraints.NotBlank;

public class ShareCreateDTO {

    @NotBlank(message = "sessionId 不能为空")
    private String sessionId;

    private String sessionTitle;

    @NotBlank(message = "接收方用户 ID 不能为空")
    private String toUserId;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }
}

