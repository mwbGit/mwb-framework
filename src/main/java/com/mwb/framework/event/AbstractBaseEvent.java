package com.mwb.framework.event;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;

public abstract class AbstractBaseEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long createTimestamp;
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}
}
