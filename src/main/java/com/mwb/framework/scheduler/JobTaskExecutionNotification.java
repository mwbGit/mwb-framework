package com.mwb.framework.scheduler;

import com.mwb.framework.scheduler.dao.JobTaskExecutionStatus;

import java.util.Date;

public class JobTaskExecutionNotification {

	private int id;
	private String name;
	private String alias;

	private String groupName;
	private String className;
	private String description;
	private String cronExpression;

	private boolean manualSupported;
	private boolean isLastExecutionSucess;
	private boolean isPersistentSupported;

	private int LastExecutionDuration;
	private Date executeTime; 
	
	private String exception;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public boolean isManualSupported() {
		return manualSupported;
	}

	public void setManualSupported(boolean manualSupported) {
		this.manualSupported = manualSupported;
	}

	public boolean isLastExecutionSucess() {
		return isLastExecutionSucess;
	}

	public void setLastExecutionSucess(boolean isLastExecutionSucess) {
		this.isLastExecutionSucess = isLastExecutionSucess;
	}

	public boolean isPersistentSupported() {
		return isPersistentSupported;
	}

	public void setPersistentSupported(boolean isPersistentSupported) {
		this.isPersistentSupported = isPersistentSupported;
	}

	public int getLastExecutionDuration() {
		return LastExecutionDuration;
	}

	public void setLastExecutionDuration(int lastExecutionDuration) {
		LastExecutionDuration = lastExecutionDuration;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	public JobTaskExecutionNotification(JobTaskExecutionStatus jobTaskExecutionStatus) {
		
		this.setAlias(jobTaskExecutionStatus.getAlias());
		this.setClassName(jobTaskExecutionStatus.getClassName());
		this.setCronExpression(jobTaskExecutionStatus.getCronExpression());
		this.setDescription(jobTaskExecutionStatus.getDescription());
		this.setExecuteTime(jobTaskExecutionStatus.getExecuteTime());
		this.setGroupName(jobTaskExecutionStatus.getGroupName());
		this.setId(jobTaskExecutionStatus.getId());
		this.setLastExecutionDuration(jobTaskExecutionStatus.getLastExecutionDuration());
		this.setLastExecutionSucess(jobTaskExecutionStatus.getIsLastExecutionSucess().getValue());
		this.setManualSupported(jobTaskExecutionStatus.getManualSupported().getValue());
		this.setName(jobTaskExecutionStatus.getName());
	}

	public JobTaskExecutionNotification(String jobName, String jobGroup) {
		
		this.setGroupName(jobGroup);
		this.setName(jobName);
	}

	public JobTaskExecutionNotification() {

	}
}
