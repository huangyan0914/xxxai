package com.xxx.qa.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_qa_issue_report")
public class QaIssueReport {

    @TableId
    private Long id;

    private String sessionId;

    private Long speakId;

    /** 反馈存疑：存疑问题描述 */
    private String doubtIssue;

    /** 反馈错误：错误答案描述 */
    private String wrongAnswer;

    /** 反馈错误：正确答案描述 */
    private String correctAnswer;

    private String userId;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getUpdater() { return updater; }
    public void setUpdater(String updater) { this.updater = updater; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

