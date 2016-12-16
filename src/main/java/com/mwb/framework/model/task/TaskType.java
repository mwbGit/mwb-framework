package com.mwb.framework.model.task;

import com.mwb.framework.model.Bool;

import java.io.Serializable;


public class TaskType implements Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
    private String code;
    private Bool errorAlert;
    private String description;
    private String timeIntervals;
    
    public int getId() {
    	return id;
    }
    
    public void setId(int id) {
    	this.id = id;
    }
    
    public String getCode() {
    	return code;
    }
    
    public void setCode(String code) {
    	this.code = code;
    }
    
    public Bool getErrorAlert() {
		return errorAlert;
	}

	public void setErrorAlert(Bool errorAlert) {
		this.errorAlert = errorAlert;
	}

	public String getDescription() {
    	return description;
    }
    
    public void setDescription(String description) {
    	this.description = description;
    }
    
    public String getTimeIntervals() {
    	return timeIntervals;
    }
    
    public void setTimeIntervals(String timeIntervals) {
    	this.timeIntervals = timeIntervals;
    }
}
