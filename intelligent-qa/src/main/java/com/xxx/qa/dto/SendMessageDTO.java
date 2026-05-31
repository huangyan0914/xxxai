package com.xxx.qa.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class SendMessageDTO {

    @NotBlank(message = "sessionId 不能为空")
    private String sessionId;

    @NotBlank(message = "消息内容不能为空")
    private String input;

    private List<Object> files;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }

    public List<Object> getFiles() { return files; }
    public void setFiles(List<Object> files) { this.files = files; }
}

