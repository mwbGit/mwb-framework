package com.mwb.framework.api.common.service.rs.api;

import javax.xml.bind.annotation.XmlElement;

public class PagingRequest {
	
	private boolean paged;
	
	private int pageNumber;
	private int pageSize;
	
	@XmlElement
	private String orderingBy;
	
	@XmlElement
	private String ordering;
	
	public PagingRequest() {
		this.paged = true;
		this.pageNumber = 1;
		this.pageSize = 10;
	}
	
	public boolean isPaged() {
		return paged;
	}

	public void setPaged(boolean paged) {
		this.paged = paged;
	}

	public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getOrderingBy() {
		return orderingBy;
	}

	public void setOrderingBy(String orderingBy) {
		this.orderingBy = orderingBy;
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public boolean ascOrdering() {
		if ("desc".equalsIgnoreCase(ordering)) {
			return false;
		} else {
			return true;
		}
    }

}
