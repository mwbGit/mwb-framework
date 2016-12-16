package com.mwb.framework.model.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mwb.framework.model.Bool;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private AbstractTaskContent content;
	private String contentStr;
	private Integer failureCount;
	private Bool inProgress; // is_being_syncup
	private Long timestamp;
	private Date nextTime; // next_syncup_time
	private Date endTime;
	private int typeId;
	private TaskType type;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AbstractTaskContent getContent() {
		return content;
	}

	public void setContent(AbstractTaskContent content) {
		this.content = content;
	}

	public String getContentStr() {
		contentStr = JSON.toJSONString(content, SerializerFeature.WriteClassName);
		return contentStr;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public Bool getInProgress() {
		return inProgress;
	}

	public void setInProgress(Bool inProgress) {
		this.inProgress = inProgress;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Date getNextTime() {
		return nextTime;
	}

	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

}
