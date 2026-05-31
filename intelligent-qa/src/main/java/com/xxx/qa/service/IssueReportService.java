package com.xxx.qa.service;

import com.xxx.qa.dto.IssueReportDTO;

public interface IssueReportService {

    /**
     * 提交问题反馈（存疑 / 错误）
     *
     * @param dto    反馈内容
     * @param userId 提交用户 ID
     */
    void submitReport(IssueReportDTO dto, Long userId);
}

