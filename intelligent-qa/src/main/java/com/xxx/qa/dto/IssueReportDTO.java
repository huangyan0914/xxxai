package com.xxx.qa.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class IssueReportDTO {

    @NotBlank(message = "sessionId 不能为空")
    private String sessionId;

    @NotNull(message = "speakId 不能为空")
    private Long speakId;

    /** 反馈存疑：存疑问题描述 */
    private String doubtIssue;

    /** 反馈错误：错误答案描述 */
    private String wrongAnswer;

    /** 反馈错误：正确答案描述 */
    private String correctAnswer;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getSpeakId() { return speakId; }
    public void setSpeakId(Long speakId) { this.speakId = speakId; }

    public String getDoubtIssue() { return doubtIssue; }
    public void setDoubtIssue(String doubtIssue) { this.doubtIssue = doubtIssue; }

    public String getWrongAnswer() { return wrongAnswer; }
    public void setWrongAnswer(String wrongAnswer) { this.wrongAnswer = wrongAnswer; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}

