package com.xxx.qa.dto.query;

import com.xxx.common.page.PageQuery;

public class ShareQueryDTO extends PageQuery {

    // 预留：后续可按 sessionTitle 等过滤
    private String sessionTitle;

    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
}

