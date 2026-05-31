package com.xxx.common.page;

/**
 * 通用分页查询参数
 */
public class PageQuery {

    private long pageNo = 1;

    private long pageSize = 10;

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}


