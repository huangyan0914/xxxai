package com.xxx.qa.vo;

import java.util.List;

public class ShareDetailVO {

    private ShareVO share;
    private List<MessageVO> messages;

    public ShareVO getShare() { return share; }
    public void setShare(ShareVO share) { this.share = share; }

    public List<MessageVO> getMessages() { return messages; }
    public void setMessages(List<MessageVO> messages) { this.messages = messages; }
}

