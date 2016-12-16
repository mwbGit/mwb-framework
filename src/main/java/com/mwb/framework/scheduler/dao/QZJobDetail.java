package com.mwb.framework.scheduler.dao;

public class QZJobDetail {
	private String jobClassName;
	private String description;
	private String jobGroup;
	private String jobName;
	
	public String getJobClassName() {
		return jobClassName;
	}
	
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getJobGroup() {
		return jobGroup;
	}
	
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}
