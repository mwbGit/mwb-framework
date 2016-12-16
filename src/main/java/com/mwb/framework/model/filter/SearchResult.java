package com.mwb.framework.model.filter;

import com.mwb.framework.api.common.model.PagingResult;

import java.util.List;

public class SearchResult <T> {
    private boolean paged;
    private PagingResult pagingResult;
    private List<T> result;
    
    public boolean isPaged() {
        return paged;
    }
    
    public void setPaged(boolean paged) {
        this.paged = paged;
    }
    
    public PagingResult getPagingResult() {
        return pagingResult;
    }
    
    public void setPagingResult(PagingResult pagingResult) {
        this.pagingResult = pagingResult;
    }

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
    
}
