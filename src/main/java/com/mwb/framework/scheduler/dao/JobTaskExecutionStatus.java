package com.mwb.framework.scheduler.dao;

import com.mwb.framework.model.Bool;
import com.mwb.framework.scheduler.JobTaskExecutionNotification;

import java.util.Date;

public class JobTaskExecutionStatus {
	private int id;
	private String name;
	private String alias;

	private String groupName;
	private String className;
	private String description;
	private String cronExpression;

	private Bool manualSupported;
	private Bool isLastExecutionSucess;
	private Bool isPersistentSupported;

	private int LastExecutionDuration;
	
	private Date executeTime;
	
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
	
	public Bool getManualSupported() {
		return manualSupported;
	}
	
	public void setManualSupported(Bool manualSupported) {
		this.manualSupported = manualSupported;
	}
	
	public Bool getIsLastExecutionSucess() {
		return isLastExecutionSucess;
	}
	
	public void setIsLastExecutionSucess(Bool isLastExecutionSucess) {
		this.isLastExecutionSucess = isLastExecutionSucess;
	}
	
	public Bool getIsPersistentSupported() {
		return isPersistentSupported;
	}
	
	public void setIsPersistentSupported(Bool isPersistentSupported) {
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

	public JobTaskExecutionStatus(JobTaskExecutionNotification jobTaskExecutionNotification) {
		this.setAlias(jobTaskExecutionNotification.getAlias());
		this.setClassName(jobTaskExecutionNotification.getClassName());
		this.setCronExpression(jobTaskExecutionNotification.getCronExpression());
		this.setDescription(jobTaskExecutionNotification.getDescription());
		this.setExecuteTime(jobTaskExecutionNotification.getExecuteTime());
		this.setGroupName(jobTaskExecutionNotification.getGroupName());
		this.setId(jobTaskExecutionNotification.getId());
		this.setIsLastExecutionSucess(Bool.fromValue(jobTaskExecutionNotification.isLastExecutionSucess()));
		this.setIsPersistentSupported(Bool.fromValue(jobTaskExecutionNotification.isPersistentSupported()));
		this.setLastExecutionDuration(jobTaskExecutionNotification.getLastExecutionDuration());
		this.setManualSupported(Bool.fromValue(jobTaskExecutionNotification.isManualSupported()));
		this.setName(jobTaskExecutionNotification.getName());
	} 
	
	public JobTaskExecutionStatus() {
		
	}
	
}
