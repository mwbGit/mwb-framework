package com.mwb.framework.api.common.model;


public class PagingResult {
    private int pageSize;
    private int pageNumber;
    private int recordNumber;
    
    public PagingResult() {
        
    }
    
    public PagingResult(int recordNumber, PagingData pagingData) {
        setRecordNumber(recordNumber);
        setPageSize(pagingData.getPageSize());
        setPageNumber(pagingData.getPageNumber());
    }
    
    public int getTotalPage() {
        return (int) Math.ceil(((double) recordNumber / pageSize));
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public int getRecordNumber() {
        return recordNumber;
    }
    
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

}
