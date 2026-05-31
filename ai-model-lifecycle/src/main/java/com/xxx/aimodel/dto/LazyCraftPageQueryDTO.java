package com.xxx.aimodel.dto;

import com.xxx.common.page.PageQuery;

import java.util.List;

public class LazyCraftPageQueryDTO extends PageQuery {

    private String keyword;

    private String qtype;

    private List<String> userId;

    private List<String> status;

    private List<String> tags;

    private List<String> modelType;

    private String modelKind;

    private List<String> dataType;

    public long getPageNum() {
        return getPageNo();
    }

    public void setPageNum(long pageNum) {
        setPageNo(pageNum);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getModelType() {
        return modelType;
    }

    public void setModelType(List<String> modelType) {
        this.modelType = modelType;
    }

    public String getModelKind() {
        return modelKind;
    }

    public void setModelKind(String modelKind) {
        this.modelKind = modelKind;
    }

    public List<String> getDataType() {
        return dataType;
    }

    public void setDataType(List<String> dataType) {
        this.dataType = dataType;
    }
}

