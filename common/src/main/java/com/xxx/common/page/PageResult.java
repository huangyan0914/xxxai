package com.xxx.common.page;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页返回结果
 */
public class PageResult<T> implements Serializable {

    private long total;

    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}


