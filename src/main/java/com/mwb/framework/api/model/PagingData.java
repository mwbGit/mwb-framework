package com.mwb.framework.api.model;


public class PagingData {
    // 由于控制每一页最多的返回记录数，避免造成数据性能问题  2000
    public static final int MAX_PAGE_SIZE = 2000;
    
    private int pageNumber;
    private int pageSize;
    
    public PagingData(int pageNumber, int pageSize) {
        if (pageSize < 1) {
            pageSize = 1;
        }
        
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    
    public int getStartRecordNumber() {
        int startRecordNumber = (pageNumber - 1) * pageSize;
        return startRecordNumber;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public int getFromIndex() {
    	return  (pageNumber - 1) * pageSize;
    }
    
    public int getToIndex() {
    	return pageNumber * pageSize;
    }
    
    public int getToIndex(int maxIndex) {
    	int toIndex = pageNumber * pageSize;
    	toIndex = toIndex > maxIndex ? maxIndex : toIndex;
    	return toIndex;
    }
    
}
