package com.mwb.framework.model.filter;

public class OrderingProperty {

    private int priority; 
    private String property;
    private boolean asc;
    
    public OrderingProperty(int priority, String property, boolean asc) {
    	this.priority = priority;
        this.property = property;
        this.asc = asc;
    }

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
    public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
