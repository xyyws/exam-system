package com.exam.common.model;

public class PageRequest {
    private int pageNum = 1;
    private int pageSize = 10;

    public PageRequest() {}
    public PageRequest(int pageNum, int pageSize) { this.pageNum = pageNum; this.pageSize = pageSize; }

    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = Math.max(1, pageNum); }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = Math.min(Math.max(1, pageSize), 100); }
    public int getOffset() { return (pageNum - 1) * pageSize; }
}
