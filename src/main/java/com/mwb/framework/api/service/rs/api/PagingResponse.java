package com.mwb.framework.api.service.rs.api;

import com.mwb.framework.api.model.PagingResult;


public class PagingResponse extends ServiceResponse {
    private static final long serialVersionUID = 1L;
    
    private int pageNumber;
    private int recordNumber;
    private int totalPage;
    
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
    
    public int getTotalPage() {
        return totalPage;
    }
    
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
    
    public void setPagingResult(PagingResult result) {
        if (result != null) {
            setPageNumber(result.getPageNumber());
            setRecordNumber(result.getRecordNumber());
            setTotalPage(result.getTotalPage());
        }
    }
}  
