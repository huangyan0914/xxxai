package com.xxx.qa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.common.exception.BizException;
import com.xxx.qa.dto.IssueReportDTO;
import com.xxx.qa.entity.QaIssueReport;
import com.xxx.qa.mapper.QaIssueReportMapper;
import com.xxx.qa.service.IssueReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class IssueReportServiceImpl extends ServiceImpl<QaIssueReportMapper, QaIssueReport>
        implements IssueReportService {

    private static final Logger log = LoggerFactory.getLogger(IssueReportServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReport(IssueReportDTO dto, Long userId) {
        if (isBlank(dto.getDoubtIssue()) && isBlank(dto.getWrongAnswer()) && isBlank(dto.getCorrectAnswer())) {
            throw new BizException("反馈内容不能全部为空");
        }

        QaIssueReport report = new QaIssueReport();
        report.setSessionId(dto.getSessionId());
        report.setSpeakId(dto.getSpeakId());
        report.setDoubtIssue(dto.getDoubtIssue());
        report.setWrongAnswer(dto.getWrongAnswer());
        report.setCorrectAnswer(dto.getCorrectAnswer());
        report.setUserId(String.valueOf(userId));
        report.setCreator(String.valueOf(userId));
        report.setCreateTime(LocalDateTime.now());

        save(report);
        log.info("用户 {} 提交问题反馈，speakId={}", userId, dto.getSpeakId());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

